package com.example.viewmodel

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.FirebaseNopiAiRepository
import com.example.ai.NopiAiRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "NopiChatViewModel"

data class NopiChatMessage(
    val id: Long,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false,
    val isGreeting: Boolean = false
)

enum class NopiChatRole(
    val label: String,
    val shortLabel: String,
    val modelName: String,
    val systemPrompt: String
) {
    COACH(
        label = "Digital Wellness Coach",
        shortLabel = "Wellness Coach",
        modelName = "gemini-3.8-flash",
        systemPrompt = """
            Kamu adalah Nopi, maskot berang-berang yang ramah, jenaka, dan penuh empati dari gerakan NPA (No Phones Around — Less Screen, More Connection).
            Peranmu adalah menjadi pelatih kebiasaan digital yang membantu pengguna mengurangi doomscrolling, menjaga kehadiran saat berkumpul bersama orang terdekat, serta mengevaluasi hasil Doomscrolling Test dan Digital Detox Timer mereka.
            Jawab dalam Bahasa Indonesia yang hangat, santai, praktis, dan tidak menghakimi (atau ikuti bahasa pengguna bila mereka bertanya dalam bahasa lain).
        """.trimIndent()
    ),
    QUICK_BUDDY(
        label = "Quick Tips Mode",
        shortLabel = "Quick Tips",
        modelName = "gemini-3.5-flash-lite",
        systemPrompt = """
            Kamu adalah Nopi si berang-berang ceria dari NPA (No Phones Around).
            Berikan tips anti-doomscrolling dan ide aktivitas seru tanpa HP yang sangat singkat, padat, dan langsung bisa dipraktikkan sekarang juga (maksimal 3 poin).
        """.trimIndent()
    )
}

data class NopiChatUiState(
    val isOpen: Boolean = false,
    val role: NopiChatRole = NopiChatRole.COACH,
    val messages: List<NopiChatMessage> = listOf(
        NopiChatMessage(
            id = 0L,
            isUser = false,
            text = "Haii! Aku Nopi 🦦✨ Mau curhat soal doomscrolling, bahas hasil tesmu, atau cari ide aktivitas seru tanpa HP? Tulis aja di bawah!",
            isGreeting = true
        )
    ),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NopiChatViewModel(
    private val aiRepository: NopiAiRepository = FirebaseNopiAiRepository()
) : ViewModel() {

    private var nextMsgId = 1L

    private val _uiState = MutableStateFlow(NopiChatUiState())
    val uiState: StateFlow<NopiChatUiState> = _uiState.asStateFlow()

    fun openChat() {
        _uiState.update { it.copy(isOpen = true) }
    }

    fun closeChat() {
        _uiState.update { it.copy(isOpen = false) }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null) }
    }

    fun switchRole(role: NopiChatRole) {
        if (_uiState.value.role == role || _uiState.value.isLoading) return
        aiRepository.resetSession()
        _uiState.update {
            it.copy(
                role = role,
                errorMessage = null,
                messages = listOf(
                    NopiChatMessage(
                        id = nextMsgId++,
                        isUser = false,
                        text = if (role == NopiChatRole.COACH) {
                            "Mode Digital Wellness Coach aktif 🦦! Ceritakan kebiasaan HP-mu atau hasil Doomscrolling Test tadi, yuk kita bahas bareng."
                        } else {
                            "Mode Quick Tips aktif ⚡! Tanya apa aja, aku kasih tips instan biar kamu bisa langsung taruh HP."
                        },
                        isGreeting = true
                    )
                )
            )
        }
    }

    fun resetConversation() {
        aiRepository.resetSession()
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                messages = listOf(
                    NopiChatMessage(
                        id = nextMsgId++,
                        isUser = false,
                        text = "Obrolan di-reset! Ada yang ingin kamu tanyakan ke Nopi sekarang? 🦦",
                        isGreeting = true
                    )
                )
            )
        }
    }

    fun sendMessage(promptOverride: String? = null) {
        val state = _uiState.value
        val text = (promptOverride ?: state.inputText).trim()
        if (text.isEmpty() || state.isLoading) return

        val priorCompletedMessages = state.messages
        val userMsg = NopiChatMessage(id = nextMsgId++, isUser = true, text = text)
        val botPlaceholderId = nextMsgId++
        val botPlaceholder = NopiChatMessage(
            id = botPlaceholderId,
            isUser = false,
            text = "",
            isStreaming = true
        )

        _uiState.update {
            it.copy(
                inputText = if (promptOverride == null) "" else it.inputText,
                isLoading = true,
                errorMessage = null,
                messages = it.messages + userMsg + botPlaceholder
            )
        }

        viewModelScope.launch {
            val accumulated = StringBuilder()
            try {
                withContext(Dispatchers.IO) {
                    var lastUiFlushMs = 0L
                    withTimeout(60_000L) {
                        aiRepository.streamReply(state.role, priorCompletedMessages, text).collect { part ->
                            if (part.isNotEmpty()) {
                                accumulated.append(part)
                                val now = SystemClock.elapsedRealtime()
                                if (now - lastUiFlushMs >= 60L) {
                                    lastUiFlushMs = now
                                    val partial = accumulated.toString()
                                    _uiState.update { cur ->
                                        cur.copy(
                                            messages = cur.messages.map { msg ->
                                                if (msg.id == botPlaceholderId) {
                                                    msg.copy(text = partial, isStreaming = true)
                                                } else msg
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (accumulated.isBlank()) {
                    throw IllegalStateException("Model mengembalikan respons kosong.")
                }

                val finalReply = accumulated.toString()
                _uiState.update { cur ->
                    cur.copy(
                        isLoading = false,
                        messages = cur.messages.map { msg ->
                            if (msg.id == botPlaceholderId) {
                                msg.copy(text = finalReply, isStreaming = false)
                            } else msg
                        }
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI chat timed out", e)
                failMessage(botPlaceholderId, "Permintaan timeout setelah 60 detik. Coba kirim lagi ya.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI chat failed", e)
                failMessage(
                    botPlaceholderId,
                    e.localizedMessage ?: "Gagal memproses respons AI."
                )
            }
        }
    }

    private fun failMessage(botPlaceholderId: Long, errorText: String) {
        _uiState.update { cur ->
            cur.copy(
                isLoading = false,
                errorMessage = errorText,
                messages = cur.messages.filterNot { it.id == botPlaceholderId && it.text.isBlank() }
                    .map { msg ->
                        if (msg.id == botPlaceholderId) msg.copy(isStreaming = false) else msg
                    }
            )
        }
    }
}
