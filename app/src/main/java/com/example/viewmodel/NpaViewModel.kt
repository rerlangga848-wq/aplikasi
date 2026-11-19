package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.audio.AmbientMusicEngine
import com.example.data.NpaData
import com.example.data.NpaPreferencesRepository
import com.example.data.PersistedNpaState
import com.example.ui.theme.NpaBitmapCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class LabPhase { IDLE, PLAYING, RESULT }

data class TemptationBubble(
    val id: Long,
    val xFraction: Float,
    val yFraction: Float,
    val icon: String
)

data class DoomUiState(
    val phase: LabPhase = LabPhase.IDLE,
    val seconds: Int = 0,
    val scrollSpeedDpPerSec: Float = 0f,
    val averageSpeedDpPerSec: Float = 0f,
    val peakSpeedDpPerSec: Float = 0f,
    val postsViewed: Int = 0,
    val scrollEvents: Int = 0,
    val interactionFrequencyPerMin: Float = 0f,
    val scrollScore: Int = 0,
    val notificationsTriggered: Int = 0,
    val notificationRatePerMin: Float = 0f,
    val engagementCount: Int = 0,
    val digitalHabitScore: Int = 100,
    val habitTitle: String = "",
    val habitAdvice: String = "",
    val previousHabitScore: Int? = null,
    val activeToast: String? = null,
    val speedHistory: List<Float> = emptyList(),
    val likedPostIndices: Set<Int> = emptySet(),
    val engagedPostIndices: Set<Int> = emptySet()
)

data class FocusUiState(
    val phase: LabPhase = LabPhase.IDLE,
    val timeLeft: Int = 25,
    val integrity: Float = 100f,
    val temptations: List<TemptationBubble> = emptyList(),
    val caved: Int = 0,
    val resisted: Int = 0
)

data class PresenceUiState(
    val phase: LabPhase = LabPhase.IDLE,
    val value: Float = 60f,
    val timeLeft: Int = 30,
    val goodSeconds: Float = 0f,
    val pulseLabel: String? = null
)

data class HomeUiState(
    val quoteIndex: Int = 0,
    val heroShowAfter: Boolean = false,
    val heroHasToggled: Boolean = false
)

data class StoryUiState(
    val connPhase: LabPhase = LabPhase.IDLE,
    val connStep: Int = 0,
    val connPresentCount: Int = 0,
    val connReaction: String? = null,
    val connLastPresent: Boolean? = null,
    val storyKey: String = "start",
    val storyHistory: List<String> = emptyList()
)

data class ChallengeUiState(
    val selectedChallengeId: String? = null,
    val pledgerName: String = "",
    val pledged: Boolean = false
)

data class MusicUiState(
    val musicDrawerOpen: Boolean = false,
    val activeTrackIndex: Int? = null,
    val isMusicPlaying: Boolean = false
)

class NpaViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val repo = NpaPreferencesRepository(appContext)
    private val musicEngine = AmbientMusicEngine()

    val persistedState: StateFlow<PersistedNpaState> = repo.stateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersistedNpaState())

    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    private val _storyState = MutableStateFlow(StoryUiState())
    val storyState: StateFlow<StoryUiState> = _storyState.asStateFlow()

    private val _challengeState = MutableStateFlow(ChallengeUiState())
    val challengeState: StateFlow<ChallengeUiState> = _challengeState.asStateFlow()

    private val _detoxRunning = MutableStateFlow(false)
    val detoxRunning: StateFlow<Boolean> = _detoxRunning.asStateFlow()

    private val _musicState = MutableStateFlow(MusicUiState())
    val musicState: StateFlow<MusicUiState> = _musicState.asStateFlow()

    // Isolated high-frequency / localized StateFlows prevent root-level recompositions
    private val _liveNpaCounter = MutableStateFlow(12847)
    val liveNpaCounter: StateFlow<Int> = _liveNpaCounter.asStateFlow()

    private val _detoxSeconds = MutableStateFlow(0)
    val detoxSeconds: StateFlow<Int> = _detoxSeconds.asStateFlow()

    private val _doomState = MutableStateFlow(DoomUiState())
    val doomState: StateFlow<DoomUiState> = _doomState.asStateFlow()

    private val _focusState = MutableStateFlow(FocusUiState())
    val focusState: StateFlow<FocusUiState> = _focusState.asStateFlow()

    private val _presenceState = MutableStateFlow(PresenceUiState())
    val presenceState: StateFlow<PresenceUiState> = _presenceState.asStateFlow()

    private var doomJob: Job? = null
    private var focusJob: Job? = null
    private var presenceJob: Job? = null
    private var presencePulseJob: Job? = null
    private var detoxJob: Job? = null
    private var nextTemptId = 0L

    init {
        viewModelScope.launch {
            repo.stateFlow.collect { saved ->
                _challengeState.update { current ->
                    if (!current.pledged && saved.isPledged && saved.selectedChallengeId != null) {
                        current.copy(
                            selectedChallengeId = saved.selectedChallengeId,
                            pledgerName = saved.pledgerName,
                            pledged = true
                        )
                    } else current
                }
            }
        }

        // Pre-warm downsampled hero & mascot bitmaps off the Main Thread
        viewModelScope.launch(Dispatchers.IO) {
            val res = appContext.resources
            NpaBitmapCache.decodeAndCache(res, R.drawable.img_nopi_mascot, 220)
            NpaBitmapCache.decodeAndCache(res, R.drawable.img_hero_before, 960)
            NpaBitmapCache.decodeAndCache(res, R.drawable.img_hero_after, 960)
        }

        // Lightweight isolated ticker: only LiveNpaCounterCard recomposes
        viewModelScope.launch {
            while (isActive) {
                delay(2400)
                _liveNpaCounter.update { it + Random.nextInt(1, 4) }
            }
        }
    }

    fun nextQuote() {
        _homeState.update { it.copy(quoteIndex = (it.quoteIndex + 1) % NpaData.quotes.size) }
    }

    fun dropPhoneHero() {
        _homeState.update { it.copy(heroShowAfter = true, heroHasToggled = true) }
    }

    fun resetHero() {
        _homeState.update { it.copy(heroShowAfter = false) }
    }

    // --- DOOMSCROLLING TEST ---
    private var doomScrollAccumDp = 0f
    private var doomTotalScrollDp = 0f
    private var doomElapsedMs = 0L
    private var doomTickMs = 0L
    private var doomScrollEventCount = 0

    private fun computeInteractionMetrics(
        scrollEvents: Int,
        engagementCount: Int,
        avgSpeedDpPerSec: Float,
        elapsedMs: Long
    ): Pair<Float, Int> {
        val elapsedMin = (elapsedMs.coerceAtLeast(1000L) / 60_000f)
        val totalInteractions = scrollEvents + (engagementCount * 2)
        val freqPerMin = totalInteractions / elapsedMin
        // Scroll Score (0-100) reflects interaction frequency & scroll intensity
        val freqComponent = (freqPerMin / 1.8f).coerceAtMost(60f)
        val speedComponent = (avgSpeedDpPerSec / 18f).coerceAtMost(25f)
        val engagementComponent = (engagementCount * 3f).coerceAtMost(15f)
        val score = if (totalInteractions == 0 && avgSpeedDpPerSec < 1f) {
            0
        } else {
            (freqComponent + speedComponent + engagementComponent).toInt().coerceIn(1, 100)
        }
        return freqPerMin to score
    }

    fun startDoomTest() {
        doomJob?.cancel()
        doomScrollAccumDp = 0f
        doomTotalScrollDp = 0f
        doomElapsedMs = 0L
        doomTickMs = 0L
        doomScrollEventCount = 0
        val previousScore = persistedState.value.lastDoomHabitScore
        _doomState.value = DoomUiState(
            phase = LabPhase.PLAYING,
            postsViewed = 3,
            notificationsTriggered = 1,
            notificationRatePerMin = 12f,
            previousHabitScore = previousScore,
            activeToast = NpaData.simulatedNotifications.first(),
            speedHistory = listOf(0f)
        )

        doomJob = viewModelScope.launch {
            while (isActive) {
                delay(250)
                doomTickMs += 250
                val tickMs = doomTickMs
                doomElapsedMs = tickMs
                val currentAccum = doomScrollAccumDp
                doomScrollAccumDp = 0f
                val instantSpeed = currentAccum * 4f
                val avgSpeed = if (tickMs > 0L) (doomTotalScrollDp / (tickMs / 1000f)) else 0f
                val capturedScrollEvents = doomScrollEventCount

                _doomState.update { state ->
                    if (state.phase != LabPhase.PLAYING) return@update state
                    val smoothedSpeed = (state.scrollSpeedDpPerSec * 0.45f) + (instantSpeed * 0.55f)
                    val peak = maxOf(state.peakSpeedDpPerSec, smoothedSpeed)
                    val nextSec = if (tickMs % 1000L == 0L) state.seconds + 1 else state.seconds

                    val speedFactor = (smoothedSpeed / 600f).coerceIn(0f, 1.5f)
                    val engageFactor = (state.engagementCount * 0.08f).coerceAtMost(0.6f)
                    val shouldSpawnNotif = (tickMs % 1000L == 0L) &&
                        (Random.nextFloat() < (0.28f + speedFactor * 0.42f + engageFactor))

                    val newNotifCount = if (shouldSpawnNotif) state.notificationsTriggered + 1 else state.notificationsTriggered
                    val newToast = if (shouldSpawnNotif) NpaData.simulatedNotifications.random() else state.activeToast

                    val elapsedMinutes = (nextSec.coerceAtLeast(1) / 60f)
                    val dynamicRate = (newNotifCount / elapsedMinutes).coerceAtLeast(
                        4f + (smoothedSpeed / 35f) + (state.engagementCount * 2.5f)
                    )
                    val (interactionFreq, liveScrollScore) = computeInteractionMetrics(
                        scrollEvents = capturedScrollEvents,
                        engagementCount = state.engagementCount,
                        avgSpeedDpPerSec = avgSpeed,
                        elapsedMs = tickMs
                    )

                    val nextHistory = if (tickMs % 1000L == 0L) {
                        (state.speedHistory + smoothedSpeed).takeLast(30)
                    } else state.speedHistory

                    state.copy(
                        seconds = nextSec,
                        scrollSpeedDpPerSec = smoothedSpeed,
                        averageSpeedDpPerSec = avgSpeed,
                        peakSpeedDpPerSec = peak,
                        scrollEvents = capturedScrollEvents,
                        interactionFrequencyPerMin = interactionFreq,
                        scrollScore = liveScrollScore,
                        notificationsTriggered = newNotifCount,
                        notificationRatePerMin = dynamicRate,
                        activeToast = newToast,
                        speedHistory = nextHistory
                    )
                }
            }
        }
    }

    fun reportDoomScrollDelta(deltaDp: Float, visibleEndIndex: Int) {
        val current = _doomState.value
        if (current.phase != LabPhase.PLAYING) return
        val absDp = kotlin.math.abs(deltaDp)
        doomScrollAccumDp += absDp
        doomTotalScrollDp += absDp
        if (absDp > 0.5f) {
            doomScrollEventCount++
        }
        val viewedCandidate = visibleEndIndex + 1
        // Only emit StateFlow update when visible item count actually increases (zero per-frame scroll churn)
        if (viewedCandidate > current.postsViewed) {
            _doomState.update { state ->
                if (viewedCandidate > state.postsViewed) state.copy(postsViewed = viewedCandidate)
                else state
            }
        }
    }

    fun engageDoomPost(postIndex: Int, isLike: Boolean) {
        if (_doomState.value.phase != LabPhase.PLAYING) return
        _doomState.update { state ->
            val nextLikes = if (isLike) {
                if (postIndex in state.likedPostIndices) state.likedPostIndices - postIndex
                else state.likedPostIndices + postIndex
            } else state.likedPostIndices
            val nextEngaged = state.engagedPostIndices + postIndex
            val extraNotif = NpaData.simulatedNotifications.random()
            state.copy(
                likedPostIndices = nextLikes,
                engagedPostIndices = nextEngaged,
                engagementCount = state.engagementCount + 1,
                notificationsTriggered = state.notificationsTriggered + 1,
                activeToast = "⚡ Engagement terdeteksi! $extraNotif"
            )
        }
    }

    fun dismissDoomToast() {
        _doomState.update { it.copy(activeToast = null) }
    }

    fun stopDoomTest() {
        doomJob?.cancel()
        val current = _doomState.value
        val elapsedSec = (doomElapsedMs / 1000f).coerceAtLeast(1f)
        val finalAvgSpeed = doomTotalScrollDp / elapsedSec
        val sec = current.seconds
        val finalScrollEvents = doomScrollEventCount
        val (finalFreqPerMin, finalScrollScore) = computeInteractionMetrics(
            scrollEvents = finalScrollEvents,
            engagementCount = current.engagementCount,
            avgSpeedDpPerSec = finalAvgSpeed,
            elapsedMs = doomElapsedMs
        )

        val durationPenalty = (sec * 1.1f).coerceAtMost(32f)
        val speedPenalty = (finalAvgSpeed / 18f).coerceAtMost(30f)
        val engagementPenalty = (current.engagementCount * 4.5f).coerceAtMost(20f)
        val notifPenalty = ((current.notificationsTriggered - 1).coerceAtLeast(0) * 2.4f).coerceAtMost(18f)
        val habitScore = (100f - durationPenalty - speedPenalty - engagementPenalty - notifPenalty)
            .toInt()
            .coerceIn(0, 100)

        val habitTitle = when {
            habitScore >= 85 -> "Mindful Navigator 🌱"
            habitScore >= 65 -> "Balanced Explorer ⚖️"
            habitScore >= 40 -> "Algorithm Magnet ⚡"
            else -> "Autopilot Scroller 💀"
        }

        val habitAdvice = when {
            finalAvgSpeed >= 320f ->
                "Kecepatan scroll rata-ratamu (${finalAvgSpeed.toInt()} dp/s) menunjukkan refleks swipe cepat sebelum sempat berpikir. Coba aturan 'tahan 3 detik' sebelum buka aplikasi feed."
            current.engagementCount >= 3 ->
                "Kamu terpancing ${current.engagementCount}× interaksi yang langsung memicu banjir notifikasi. Matikan notifikasi like & rekomendasi."
            sec >= 15 ->
                "Kamu bertahan $sec detik di feed sebelum sadar. Pasang pengingat Detox 5 menit agar momen nyatamu tidak terserap layar."
            else ->
                "Kesadaranmu cepat! Kamu segera sadar begitu feed mulai menarik perhatianmu. Pertahankan kebiasaan taruh HP saat berkumpul."
        }

        _doomState.update {
            it.copy(
                phase = LabPhase.RESULT,
                averageSpeedDpPerSec = finalAvgSpeed,
                scrollEvents = finalScrollEvents,
                interactionFrequencyPerMin = finalFreqPerMin,
                scrollScore = finalScrollScore,
                digitalHabitScore = habitScore,
                habitTitle = habitTitle,
                habitAdvice = habitAdvice,
                activeToast = null
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveDoomResult(sec, habitScore, finalAvgSpeed.toInt())
        }
    }

    fun resetDoomTest() {
        doomJob?.cancel()
        doomScrollAccumDp = 0f
        doomTotalScrollDp = 0f
        doomElapsedMs = 0L
        doomTickMs = 0L
        doomScrollEventCount = 0
        _doomState.value = DoomUiState()
    }

    fun resetDoomScrollScore() {
        doomScrollAccumDp = 0f
        doomTotalScrollDp = 0f
        doomElapsedMs = 0L
        doomTickMs = 0L
        doomScrollEventCount = 0
        _doomState.update { state ->
            state.copy(
                scrollSpeedDpPerSec = 0f,
                averageSpeedDpPerSec = 0f,
                peakSpeedDpPerSec = 0f,
                scrollEvents = 0,
                interactionFrequencyPerMin = 0f,
                scrollScore = 0,
                engagementCount = 0,
                speedHistory = listOf(0f)
            )
        }
    }

    // --- FOCUS SHIELD ---
    fun startFocusShield() {
        focusJob?.cancel()
        _focusState.value = FocusUiState(phase = LabPhase.PLAYING)
        val icons = listOf("📱", "🔔", "💬", "❤️", "📩", "🔁")
        focusJob = viewModelScope.launch {
            var elapsedMs = 0L
            var lastSpawnMs = 0L
            var lastSecTickMs = 0L
            while (isActive) {
                delay(33)
                elapsedMs += 33

                _focusState.update { state ->
                    var resistedAdd = 0
                    var integrityBoost = 0f
                    val updated = state.temptations.mapNotNull { t ->
                        val nextY = t.yFraction + 0.011f
                        if (nextY > 1.05f) {
                            resistedAdd++
                            integrityBoost += 2f
                            null
                        } else {
                            t.copy(yFraction = nextY)
                        }
                    }.toMutableList()

                    if (elapsedMs - lastSpawnMs >= 1200) {
                        lastSpawnMs = elapsedMs
                        updated.add(
                            TemptationBubble(
                                id = nextTemptId++,
                                xFraction = 0.14f + Random.nextFloat() * 0.72f,
                                yFraction = -0.06f,
                                icon = icons.random()
                            )
                        )
                    }

                    var timeLeft = state.timeLeft
                    var nextPhase = state.phase
                    if (elapsedMs - lastSecTickMs >= 1000) {
                        lastSecTickMs = elapsedMs
                        timeLeft = (timeLeft - 1).coerceAtLeast(0)
                        if (timeLeft == 0) {
                            nextPhase = LabPhase.RESULT
                        }
                    }

                    state.copy(
                        temptations = updated,
                        resisted = state.resisted + resistedAdd,
                        integrity = (state.integrity + integrityBoost).coerceIn(0f, 100f),
                        timeLeft = timeLeft,
                        phase = nextPhase
                    )
                }

                if (_focusState.value.phase != LabPhase.PLAYING) break
            }
        }
    }

    fun tapTemptation(id: Long) {
        _focusState.update { state ->
            state.copy(
                temptations = state.temptations.filterNot { it.id == id },
                caved = state.caved + 1,
                integrity = (state.integrity - 16f).coerceIn(0f, 100f)
            )
        }
    }

    // --- PRESENCE METER ---
    fun startPresenceMeter() {
        presenceJob?.cancel()
        _presenceState.value = PresenceUiState(phase = LabPhase.PLAYING)
        presenceJob = viewModelScope.launch {
            var elapsedMs = 0L
            while (isActive) {
                delay(350)
                elapsedMs += 350
                var finished = false
                _presenceState.update { state ->
                    val nextPresence = (state.value - 2.4f).coerceIn(0f, 100f)
                    val goodAdd = if (nextPresence >= 55f) 0.35f else 0f
                    val remaining = (30 - (elapsedMs / 1000).toInt()).coerceAtLeast(0)
                    val nextPhase = if (remaining == 0) {
                        finished = true
                        LabPhase.RESULT
                    } else LabPhase.PLAYING
                    state.copy(
                        value = nextPresence,
                        goodSeconds = state.goodSeconds + goodAdd,
                        timeLeft = remaining,
                        phase = nextPhase
                    )
                }
                if (finished) break
            }
        }
    }

    fun triggerPresenceAction(label: String, boost: Float) {
        _presenceState.update {
            it.copy(
                value = (it.value + boost).coerceIn(0f, 100f),
                pulseLabel = label
            )
        }
        presencePulseJob?.cancel()
        presencePulseJob = viewModelScope.launch {
            delay(350)
            _presenceState.update { cur ->
                if (cur.pulseLabel == label) cur.copy(pulseLabel = null) else cur
            }
        }
    }

    // --- CONNECTION STORY ---
    fun startConnectionStory() {
        _storyState.update {
            it.copy(
                connPhase = LabPhase.PLAYING,
                connStep = 0,
                connPresentCount = 0,
                connReaction = null,
                connLastPresent = null
            )
        }
    }

    fun chooseConnectionOption(present: Boolean, reaction: String) {
        _storyState.update {
            it.copy(
                connReaction = reaction,
                connLastPresent = present,
                connPresentCount = if (present) it.connPresentCount + 1 else it.connPresentCount
            )
        }
    }

    fun nextConnectionStep() {
        _storyState.update { state ->
            if (state.connStep + 1 >= NpaData.connectionStory.size) {
                state.copy(connPhase = LabPhase.RESULT, connReaction = null)
            } else {
                state.copy(connStep = state.connStep + 1, connReaction = null, connLastPresent = null)
            }
        }
    }

    // --- STORYTELLING ---
    fun chooseStoryNode(nextKey: String) {
        if (nextKey == "restart") {
            _storyState.update { it.copy(storyKey = "start", storyHistory = emptyList()) }
        } else {
            _storyState.update {
                it.copy(
                    storyHistory = it.storyHistory + it.storyKey,
                    storyKey = nextKey
                )
            }
        }
    }

    // --- CHALLENGE ---
    fun selectChallenge(id: String) {
        _challengeState.update { it.copy(selectedChallengeId = id, pledged = false) }
    }

    fun updatePledgerName(name: String) {
        _challengeState.update { it.copy(pledgerName = name) }
    }

    fun submitPledge() {
        val state = _challengeState.value
        val id = state.selectedChallengeId ?: return
        val today = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        _challengeState.update { it.copy(pledged = true) }
        viewModelScope.launch(Dispatchers.IO) {
            repo.savePledge(id, state.pledgerName.trim(), today, true)
        }
    }

    fun resetChallengePledge() {
        _challengeState.update { it.copy(selectedChallengeId = null, pledged = false, pledgerName = "") }
        viewModelScope.launch(Dispatchers.IO) { repo.clearPledge() }
    }

    // --- DETOX TIMER ---
    fun startDetoxTimer() {
        if (_detoxRunning.value) return
        detoxJob?.cancel()
        _detoxRunning.value = true
        detoxJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _detoxSeconds.update { it + 1 }
            }
        }
    }

    fun pauseDetoxTimer() {
        detoxJob?.cancel()
        val elapsed = _detoxSeconds.value
        _detoxRunning.value = false
        if (elapsed > 0) {
            viewModelScope.launch(Dispatchers.IO) { repo.recordDetoxSession(elapsed) }
        }
    }

    fun resetDetoxTimer() {
        detoxJob?.cancel()
        val elapsed = _detoxSeconds.value
        if (_detoxRunning.value && elapsed > 0) {
            viewModelScope.launch(Dispatchers.IO) { repo.recordDetoxSession(elapsed) }
        }
        _detoxRunning.value = false
        _detoxSeconds.value = 0
    }

    // --- MUSIC PLAYER ---
    fun toggleMusicDrawer() {
        _musicState.update { it.copy(musicDrawerOpen = !it.musicDrawerOpen) }
    }

    fun closeMusicDrawer() {
        _musicState.update { it.copy(musicDrawerOpen = false) }
    }

    fun selectTrack(index: Int) {
        val state = _musicState.value
        if (state.activeTrackIndex == index) {
            togglePlayPause()
        } else {
            val track = NpaData.tracks[index]
            musicEngine.play(track.baseFreqHz)
            _musicState.update {
                it.copy(activeTrackIndex = index, isMusicPlaying = true, musicDrawerOpen = false)
            }
        }
    }

    fun togglePlayPause() {
        val state = _musicState.value
        val idx = state.activeTrackIndex ?: 0
        if (state.isMusicPlaying) {
            musicEngine.stop()
            _musicState.update { it.copy(isMusicPlaying = false) }
        } else {
            val track = NpaData.tracks[idx]
            musicEngine.play(track.baseFreqHz)
            _musicState.update { it.copy(activeTrackIndex = idx, isMusicPlaying = true) }
        }
    }

    fun stopMusic() {
        musicEngine.stop()
        _musicState.update { it.copy(activeTrackIndex = null, isMusicPlaying = false) }
    }

    override fun onCleared() {
        super.onCleared()
        doomJob?.cancel()
        focusJob?.cancel()
        presenceJob?.cancel()
        presencePulseJob?.cancel()
        detoxJob?.cancel()
        musicEngine.stop()
    }
}
