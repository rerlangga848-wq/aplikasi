package com.example.ai

import com.example.viewmodel.NopiChatMessage
import com.example.viewmodel.NopiChatRole
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface NopiAiRepository {
    fun streamReply(
        role: NopiChatRole,
        history: List<NopiChatMessage>,
        userPrompt: String
    ): Flow<String>

    fun resetSession()
}

class FirebaseNopiAiRepository : NopiAiRepository {

    private val coachModel by lazy {
        Firebase.ai.generativeModel(
            modelName = NopiChatRole.COACH.modelName,
            systemInstruction = content { text(NopiChatRole.COACH.systemPrompt) }
        )
    }

    private val quickModel by lazy {
        Firebase.ai.generativeModel(
            modelName = NopiChatRole.QUICK_BUDDY.modelName,
            systemInstruction = content { text(NopiChatRole.QUICK_BUDDY.systemPrompt) }
        )
    }

    private var activeChat: Chat? = null
    private var activeChatRole: NopiChatRole? = null

    override fun resetSession() {
        activeChat = null
        activeChatRole = null
    }

    override fun streamReply(
        role: NopiChatRole,
        history: List<NopiChatMessage>,
        userPrompt: String
    ): Flow<String> = flow {
        val chatSession = getOrCreateChat(role, history)
        try {
            chatSession.sendMessageStream(userPrompt).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    emit(part)
                }
            }
        } catch (e: Exception) {
            resetSession()
            throw e
        }
    }

    private fun getOrCreateChat(
        role: NopiChatRole,
        priorCompletedMessages: List<NopiChatMessage>
    ): Chat {
        val existing = activeChat
        if (existing != null && activeChatRole == role) return existing

        val model = if (role == NopiChatRole.COACH) coachModel else quickModel
        val priorHistory = priorCompletedMessages
            .filter { !it.isGreeting && it.text.isNotBlank() && !it.isStreaming }
            .map { msg ->
                content(role = if (msg.isUser) "user" else "model") {
                    text(msg.text)
                }
            }

        return model.startChat(history = priorHistory).also {
            activeChat = it
            activeChatRole = role
        }
    }
}
