package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CharExpression
import com.example.data.NpaData
import com.example.ui.components.ExpressiveCharacter
import com.example.ui.theme.*
import com.example.viewmodel.DoomUiState
import com.example.viewmodel.FocusUiState
import com.example.viewmodel.LabPhase
import com.example.viewmodel.PresenceUiState
import com.example.viewmodel.StoryUiState
import com.example.viewmodel.TemptationBubble
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

private enum class LabSectionTab(val label: String) {
    DOOM_TEST("📱 Doom Test"),
    NPA_LAB("🛡️ NPA Lab"),
    STORYTELLING("🧭 Pilih Jalanmu")
}

private enum class MiniGameMode(val label: String) {
    FOCUS("🛡️ Focus Shield"),
    PRESENCE("🌡️ Presence Meter"),
    STORY("📖 Connection Story")
}

@Composable
fun LabScreen(
    storyStateFlow: StateFlow<StoryUiState>,
    doomStateFlow: StateFlow<DoomUiState>,
    focusStateFlow: StateFlow<FocusUiState>,
    presenceStateFlow: StateFlow<PresenceUiState>,
    onStartDoomTest: () -> Unit,
    onStopDoomTest: () -> Unit,
    onResetDoomTest: () -> Unit,
    onResetDoomScrollScore: () -> Unit,
    onReportDoomScrollDelta: (Float, Int) -> Unit,
    onEngageDoomPost: (Int, Boolean) -> Unit,
    onDismissDoomToast: () -> Unit,
    onStartFocusShield: () -> Unit,
    onTapTemptation: (Long) -> Unit,
    onStartPresenceMeter: () -> Unit,
    onPresenceAction: (String, Float) -> Unit,
    onStartConnectionStory: () -> Unit,
    onChooseConnectionOption: (Boolean, String) -> Unit,
    onNextConnectionStep: () -> Unit,
    onChooseStoryNode: (String) -> Unit,
    onNavigateToDetox: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(LabSectionTab.DOOM_TEST) }
    var miniGameMode by remember { mutableStateOf(MiniGameMode.FOCUS) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NpaCream)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "TES & NPA LAB",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = NpaCoral
                )
                Text(
                    text = "Latih kehadiranmu, bukan cuma teorinya.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NpaNavy,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LabSectionTab.entries.forEach { tab ->
                        val selected = activeTab == tab
                        Surface(
                            onClick = { activeTab = tab },
                            shape = CircleShape,
                            color = if (selected) NpaYellow else NpaWhite,
                            border = BorderStroke(2.5.dp, NpaNavy),
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("lab_tab_${tab.name}")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NpaNavy,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth()) {
                when (activeTab) {
                    LabSectionTab.DOOM_TEST -> DoomTestScreen(
                        doomStateFlow = doomStateFlow,
                        onStart = onStartDoomTest,
                        onStop = onStopDoomTest,
                        onReset = onResetDoomTest,
                        onReportScrollDelta = onReportDoomScrollDelta,
                        onEngagePost = onEngageDoomPost,
                        onDismissToast = onDismissDoomToast,
                        onNavigateToDetox = onNavigateToDetox,
                        onNavigateToChallenge = onNavigateToChallenge
                    )
                    LabSectionTab.NPA_LAB -> Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MiniGameMode.entries.forEach { mode ->
                                val selected = miniGameMode == mode
                                Surface(
                                    onClick = { miniGameMode = mode },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (selected) NpaMint else NpaWhite,
                                    border = BorderStroke(2.dp, NpaNavy),
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                                        Text(
                                            text = mode.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NpaNavy,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        when (miniGameMode) {
                            MiniGameMode.FOCUS -> FocusShieldSection(
                                focusStateFlow = focusStateFlow,
                                onStart = onStartFocusShield,
                                onTapTemptation = onTapTemptation,
                                onGoDetox = onNavigateToDetox,
                                onGoChallenge = onNavigateToChallenge
                            )
                            MiniGameMode.PRESENCE -> PresenceMeterSection(
                                presenceStateFlow = presenceStateFlow,
                                onStart = onStartPresenceMeter,
                                onAction = onPresenceAction,
                                onGoDetox = onNavigateToDetox,
                                onGoChallenge = onNavigateToChallenge
                            )
                            MiniGameMode.STORY -> ConnectionStorySection(
                                storyStateFlow = storyStateFlow,
                                onStart = onStartConnectionStory,
                                onChoose = onChooseConnectionOption,
                                onNext = onNextConnectionStep,
                                onGoDetox = onNavigateToDetox,
                                onGoChallenge = onNavigateToChallenge
                            )
                        }
                    }
                    LabSectionTab.STORYTELLING -> StorytellingSection(
                        storyStateFlow = storyStateFlow,
                        onChoose = onChooseStoryNode
                    )
                }
            }
        }
        }

        if (activeTab == LabSectionTab.DOOM_TEST) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 14.dp, bottom = 84.dp)
            ) {
                FloatingActionButton(
                    onClick = onResetDoomScrollScore,
                    shape = CircleShape,
                    containerColor = NpaYellow,
                    contentColor = NpaNavy,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    ),
                    modifier = Modifier
                        .border(2.5.dp, NpaNavy, CircleShape)
                        .testTag("doom_reset_score_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = "Reset Scroll Score ke nol"
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusShieldSection(
    focusStateFlow: StateFlow<FocusUiState>,
    onStart: () -> Unit,
    onTapTemptation: (Long) -> Unit,
    onGoDetox: () -> Unit,
    onGoChallenge: () -> Unit
) {
    val focus by focusStateFlow.collectAsStateWithLifecycle()
    val cardGradient = remember { Brush.verticalGradient(listOf(NpaLavenderSoft, NpaCream)) }
    val expr = when {
        focus.phase == LabPhase.IDLE -> CharExpression.NEUTRAL
        focus.integrity >= 90f -> CharExpression.RELIEVED
        focus.integrity >= 75f -> CharExpression.HAPPY
        focus.integrity >= 55f -> CharExpression.WARM
        focus.integrity >= 40f -> CharExpression.NEUTRAL
        focus.integrity >= 20f -> CharExpression.SLIGHTLY_HURT
        else -> CharExpression.DISAPPOINTED
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Godaan notifikasi akan lewat. Tugasmu: jangan disentuh. Biarkan lewat begitu saja.",
            style = MaterialTheme.typography.bodyMedium,
            color = NpaNavy.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        NeoCard(
            gradient = cardGradient,
            contentPadding = PaddingValues(0.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                val boxWPx = with(androidx.compose.ui.platform.LocalDensity.current) { maxWidth.toPx() }
                val boxHPx = with(androidx.compose.ui.platform.LocalDensity.current) { maxHeight.toPx() }

                // Falling temptations positioned via graphicsLayer (zero layout passes at 30fps)
                if (focus.phase == LabPhase.PLAYING) {
                    FallingTemptationsOverlay(
                        temptations = focus.temptations,
                        boxWidthPx = boxWPx,
                        boxHeightPx = boxHPx,
                        onTapTemptation = onTapTemptation
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NeoPill(containerColor = NpaCream) {
                            Text("${focus.resisted} lolos aman", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                        }
                        NeoPill(containerColor = if (focus.timeLeft <= 6) NpaCoral else NpaYellow) {
                            Text("${focus.timeLeft}s", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                        }
                    }
                }

                // Bottom Character + Meter
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExpressiveCharacter(expression = expr, size = 86.dp)
                    if (focus.phase == LabPhase.PLAYING) {
                        MeterProgressBar(value = focus.integrity, label = "Ketenangan")
                    }
                }

                if (focus.phase == LabPhase.IDLE) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NpaNavy.copy(alpha = 0.86f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "Diam itu kekuatan. Siap coba?",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NpaCream,
                                textAlign = TextAlign.Center
                            )
                            NeoButton(onClick = onStart, containerColor = NpaCoral) {
                                Text("▶ Mulai Focus Shield", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }

                if (focus.phase == LabPhase.RESULT) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NpaNavy.copy(alpha = 0.90f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            ExpressiveCharacter(expression = expr, size = 80.dp)
                            Text(
                                text = "${focus.integrity.roundToInt()}% Tenang",
                                style = MaterialTheme.typography.headlineMedium,
                                color = NpaYellow
                            )
                            Text(
                                text = "${focus.resisted} godaan dibiarkan lewat · ${focus.caved} godaan tersentuh",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NpaCream.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        if (focus.phase == LabPhase.RESULT) {
            LabResultCtaRow(onReplay = onStart, onGoDetox = onGoDetox, onGoChallenge = onGoChallenge)
        }
    }
}

@Composable
private fun FallingTemptationsOverlay(
    temptations: List<TemptationBubble>,
    boxWidthPx: Float,
    boxHeightPx: Float,
    onTapTemptation: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        temptations.forEach { t ->
            androidx.compose.runtime.key(t.id) {
                Surface(
                    onClick = { onTapTemptation(t.id) },
                    shape = CircleShape,
                    color = NpaWhite,
                    border = BorderStroke(2.dp, NpaNavy),
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = (boxWidthPx * t.xFraction) - 24.dp.toPx()
                            translationY = boxHeightPx * t.yFraction
                        }
                        .size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(t.icon, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresenceMeterSection(
    presenceStateFlow: StateFlow<PresenceUiState>,
    onStart: () -> Unit,
    onAction: (String, Float) -> Unit,
    onGoDetox: () -> Unit,
    onGoChallenge: () -> Unit
) {
    val presence by presenceStateFlow.collectAsStateWithLifecycle()
    val expr = when {
        presence.phase == LabPhase.IDLE -> CharExpression.NEUTRAL
        presence.value >= 90f -> CharExpression.RELIEVED
        presence.value >= 72f -> CharExpression.HAPPY
        presence.value >= 52f -> CharExpression.WARM
        presence.value >= 35f -> CharExpression.NEUTRAL
        presence.value >= 18f -> CharExpression.SLIGHTLY_HURT
        else -> CharExpression.DISAPPOINTED
    }
    val finalScore = ((presence.goodSeconds / 30f) * 100f).roundToInt().coerceIn(0, 100)
    val actions = remember {
        listOf(
            Triple("Ngobrol", "💬", 9f),
            Triple("Eye Contact", "👀", 7f),
            Triple("Dengerin", "👂", 8f),
            Triple("I'm Here", "🤝", 11f)
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Presence-mu turun sendiri seiring waktu. Isi lagi lewat aksi kehadiran nyata.",
            style = MaterialTheme.typography.bodyMedium,
            color = NpaNavy.copy(alpha = 0.72f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        NeoCard(containerColor = NpaWhite) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    ExpressiveCharacter(expression = expr, size = 72.dp)
                    Box(modifier = Modifier.weight(1f)) {
                        MeterProgressBar(value = presence.value, label = "Presence")
                    }
                }

                if (presence.phase == LabPhase.IDLE) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        NeoButton(onClick = onStart, containerColor = NpaBlue, contentColor = NpaWhite) {
                            Text("▶ Mulai Presence Meter", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                if (presence.phase == LabPhase.PLAYING) {
                    Text(
                        text = "⏱ ${presence.timeLeft}s tersisa",
                        style = MaterialTheme.typography.labelLarge,
                        color = NpaNavy,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    actions.chunked(2).forEach { pair ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            pair.forEach { (label, icon, boost) ->
                                val pulsed = presence.pulseLabel == label
                                NeoButton(
                                    onClick = { onAction(label, boost) },
                                    containerColor = if (pulsed) NpaMint else NpaCream,
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "$icon $label",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                if (presence.phase == LabPhase.RESULT) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("$finalScore% Hadir", style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = when {
                                finalScore >= 70 -> "Kamu konsisten menjaga kehadiranmu. Orang di depanmu pasti ngerasain itu."
                                finalScore >= 40 -> "Kehadiranmu naik-turun — sama seperti kebanyakan orang. Terus dilatih, ya."
                                else -> "Presence gampang pudar kalau nggak dijaga aktif. Sekarang kamu tahu rasanya."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = NpaNavy.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (presence.phase == LabPhase.RESULT) {
            LabResultCtaRow(onReplay = onStart, onGoDetox = onGoDetox, onGoChallenge = onGoChallenge)
        }
    }
}

@Composable
private fun ConnectionStorySection(
    storyStateFlow: StateFlow<StoryUiState>,
    onStart: () -> Unit,
    onChoose: (Boolean, String) -> Unit,
    onNext: () -> Unit,
    onGoDetox: () -> Unit,
    onGoChallenge: () -> Unit
) {
    val uiState by storyStateFlow.collectAsStateWithLifecycle()
    val stepData = NpaData.connectionStory.getOrNull(uiState.connStep)
    val expr = when {
        uiState.connLastPresent == true -> CharExpression.HAPPY
        uiState.connLastPresent == false -> CharExpression.SLIGHTLY_HURT
        else -> CharExpression.WARM
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NeoCard(containerColor = NpaWhite) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ExpressiveCharacter(expression = expr, size = 76.dp)

                when (uiState.connPhase) {
                    LabPhase.IDLE -> {
                        Text("Makan Malam Keluarga", style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
                        Text(
                            text = "5 pilihan kecil di meja makan. Lihat bagaimana setiap pilihan mengubah suasana.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NpaNavy.copy(alpha = 0.72f),
                            textAlign = TextAlign.Center
                        )
                        NeoButton(onClick = onStart, containerColor = NpaYellow) {
                            Text("▶ Mulai Cerita", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    LabPhase.PLAYING -> if (stepData != null) {
                        NeoPill(containerColor = NpaLavenderSoft) {
                            Text("Bab ${uiState.connStep + 1} / ${NpaData.connectionStory.size}", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                        }
                        Text(
                            text = stepData.narration,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = NpaNavy,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.connReaction == null) {
                            stepData.choices.forEach { ch ->
                                NeoButton(
                                    onClick = { onChoose(ch.present, ch.reaction) },
                                    containerColor = NpaCream,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(ch.text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        } else {
                            NeoCard(
                                containerColor = if (uiState.connLastPresent == true) NpaMint else NpaCoral,
                                shadowOffset = 0.dp
                            ) {
                                Text(
                                    text = uiState.connReaction.orEmpty(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NpaNavy
                                )
                            }
                            NeoButton(onClick = onNext, containerColor = NpaYellow) {
                                Text("Lanjut →", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    LabPhase.RESULT -> {
                        Text(
                            text = "${uiState.connPresentCount} / ${NpaData.connectionStory.size} Momen Hadir",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NpaNavy
                        )
                        Text(
                            text = when {
                                uiState.connPresentCount >= 4 -> "Keluargamu merasakan kehadiranmu malam ini. Momen kecil seperti ini yang paling diingat."
                                uiState.connPresentCount >= 2 -> "Ada momen yang nyambung, ada yang terlewat. Satu pilihan kecil sudah cukup mengubah suasana."
                                else -> "Malam itu lewat begitu saja. Nggak ada yang menghakimi — cuma pengingat kecil buat lain kali."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = NpaNavy.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (uiState.connPhase == LabPhase.RESULT) {
            LabResultCtaRow(onReplay = onStart, onGoDetox = onGoDetox, onGoChallenge = onGoChallenge)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StorytellingSection(
    storyStateFlow: StateFlow<StoryUiState>,
    onChoose: (String) -> Unit
) {
    val uiState by storyStateFlow.collectAsStateWithLifecycle()
    val scene = NpaData.storyNodes[uiState.storyKey] ?: NpaData.storyNodes.getValue("start")
    val cardColor by animateColorAsState(
        targetValue = when (uiState.storyKey) {
            "good_ending" -> NpaMint
            "check", "apologize" -> NpaCoral
            else -> NpaYellow
        },
        label = "storyCardColor"
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Pilih jalanmu.",
            style = MaterialTheme.typography.headlineMedium,
            color = NpaNavy,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.storyHistory.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.storyHistory.forEach { h ->
                    NeoPill(containerColor = NpaLavenderSoft) {
                        Text(h, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                    }
                }
                NeoPill(containerColor = cardColor) {
                    Text(uiState.storyKey, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                }
            }
        }

        NeoCard(containerColor = cardColor) {
            Text(
                text = scene.text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = NpaNavy
            )
        }

        scene.choices.forEachIndexed { idx, choice ->
            NeoButton(
                onClick = { onChoose(choice.next) },
                containerColor = if (idx == 0) NpaWhite else NpaLavenderSoft,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(choice.label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun MeterProgressBar(value: Float, label: String) {
    val barColor by animateColorAsState(
        targetValue = when {
            value >= 70f -> NpaMint
            value >= 40f -> NpaYellow
            else -> NpaCoral
        },
        label = "meterColor"
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
            Text("${value.roundToInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = NpaNavy)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CircleShape)
                .background(NpaWhite)
                .border(2.dp, NpaNavy, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value / 100f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LabResultCtaRow(
    onReplay: () -> Unit,
    onGoDetox: () -> Unit,
    onGoChallenge: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NeoButton(onClick = onReplay, containerColor = NpaMint) {
            Text("🔄 Coba Lagi", style = MaterialTheme.typography.labelSmall)
        }
        NeoButton(onClick = onGoDetox, containerColor = NpaBlue, contentColor = NpaWhite) {
            Text("⏳ Mulai Detox", style = MaterialTheme.typography.labelSmall)
        }
        NeoButton(onClick = onGoChallenge, containerColor = NpaCoral) {
            Text("🌱 Ambil Pledge", style = MaterialTheme.typography.labelSmall)
        }
    }
}
