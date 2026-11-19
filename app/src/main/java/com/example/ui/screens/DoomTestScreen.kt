package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NpaData
import com.example.ui.components.NopiMascot
import com.example.ui.theme.*
import com.example.viewmodel.DoomUiState
import com.example.viewmodel.LabPhase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

private data class DoomFeedSlice(
    val phase: LabPhase = LabPhase.IDLE,
    val likedPostIndices: Set<Int> = emptySet(),
    val engagedPostIndices: Set<Int> = emptySet()
)

@Composable
fun DoomTestScreen(
    doomStateFlow: StateFlow<DoomUiState>,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    onReportScrollDelta: (deltaDp: Float, visibleEndIndex: Int) -> Unit,
    onEngagePost: (postIndex: Int, isLike: Boolean) -> Unit,
    onDismissToast: () -> Unit,
    onNavigateToDetox: () -> Unit = {},
    onNavigateToChallenge: () -> Unit = {}
) {
    // Only phase / likes / engaged indices drive the 120-item feed & phase switches (immune to 250ms speed/timer ticks)
    val feedSliceFlow = remember(doomStateFlow) {
        doomStateFlow
            .map { DoomFeedSlice(it.phase, it.likedPostIndices, it.engagedPostIndices) }
            .distinctUntilChanged()
    }
    val feedSlice by feedSliceFlow.collectAsStateWithLifecycle(initialValue = DoomFeedSlice())
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    var autoScrollDemo by remember { mutableStateOf(false) }
    val currentReportScroll by rememberUpdatedState(onReportScrollDelta)

    val totalFeedCount = if (feedSlice.phase == LabPhase.IDLE) 3 else 120

    val nestedScrollConnection = remember(feedSlice.phase, density) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (feedSlice.phase == LabPhase.PLAYING) {
                    if (consumed.y != 0f) {
                        val deltaDp = with(density) { consumed.y.toDp().value }
                        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        currentReportScroll(deltaDp, lastVisible)
                    }
                    // Prevent fling momentum from jerking the outer LabScreen while actively scrolling feed
                    if (source == NestedScrollSource.SideEffect && available.y != 0f) {
                        return Offset(0f, available.y)
                    }
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(feedSlice.phase, autoScrollDemo) {
        if (feedSlice.phase == LabPhase.PLAYING && autoScrollDemo) {
            var lastNanos = 0L
            while (true) {
                withFrameNanos { frameNanos ->
                    if (lastNanos != 0L) {
                        val dtSec = ((frameNanos - lastNanos).coerceAtMost(50_000_000L)) / 1_000_000_000f
                        val pxPerSec = with(density) { 260.dp.toPx() }
                        listState.dispatchRawDelta(pxPerSec * dtSec)
                    }
                    lastNanos = frameNanos
                }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Scroll feed FeedApp™ ini sealami mungkin (atau ketuk ❤️/💬). Lihat bagaimana kecepatan scroll memicu lonjakan notifikasi & engagement.",
            style = MaterialTheme.typography.bodyMedium,
            color = NpaNavy.copy(alpha = 0.74f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Isolated Live Telemetry Strip (only this card recomposes on 250ms telemetry ticks)
        DoomTelemetryStrip(doomStateFlow = doomStateFlow)

        // Simulated FeedApp™ Phone Frame
        NeoCard(
            containerColor = NpaNavy,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Phone Header
                FeedPhoneHeader(
                    doomStateFlow = doomStateFlow,
                    phase = feedSlice.phase,
                    autoScrollDemo = autoScrollDemo,
                    onToggleAutoScroll = { autoScrollDemo = !autoScrollDemo }
                )
                Box(Modifier.fillMaxWidth().height(1.dp).background(NpaWhite.copy(alpha = 0.15f)))

                if (feedSlice.phase != LabPhase.RESULT) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(370.dp)
                    ) {
                        LazyColumn(
                            state = listState,
                            userScrollEnabled = feedSlice.phase == LabPhase.PLAYING,
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(nestedScrollConnection)
                                .testTag("doom_feed_list")
                        ) {
                            items(
                                count = totalFeedCount,
                                key = { it },
                                contentType = { "feed_post" }
                            ) { index ->
                                val post = NpaData.fakePosts[index % NpaData.fakePosts.size]
                                val liked = index in feedSlice.likedPostIndices
                                val engaged = index in feedSlice.engagedPostIndices
                                val isPlaying = feedSlice.phase == LabPhase.PLAYING
                                FeedPostRow(
                                    index = index,
                                    post = post,
                                    liked = liked,
                                    engaged = engaged,
                                    isPlaying = isPlaying,
                                    onEngagePost = onEngagePost
                                )
                            }
                        }

                        // Isolated floating notification banner
                        FeedToastOverlay(
                            doomStateFlow = doomStateFlow,
                            onDismissToast = onDismissToast,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )

                        if (feedSlice.phase == LabPhase.IDLE) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, NpaNavy.copy(alpha = 0.94f))
                                        )
                                    )
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tekan Mulai Scroll lalu geser feed ke atas seperti biasa...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NpaCream.copy(alpha = 0.78f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    val doom by doomStateFlow.collectAsStateWithLifecycle()
                    val tier = remember(doom.seconds) {
                        NpaData.doomResults.firstOrNull {
                            doom.seconds >= it.minSec && doom.seconds < it.maxSec
                        } ?: NpaData.doomResults.last()
                    }
                    DoomTestResultsSummary(
                        doom = doom,
                        tier = tier,
                        onRetry = {
                            autoScrollDemo = false
                            onReset()
                        },
                        onNavigateToDetox = onNavigateToDetox,
                        onNavigateToChallenge = onNavigateToChallenge
                    )
                }

                // Bottom Action CTA Bar
                if (feedSlice.phase != LabPhase.RESULT) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        if (feedSlice.phase == LabPhase.IDLE) {
                            NeoButton(
                                onClick = {
                                    autoScrollDemo = false
                                    onStart()
                                },
                                containerColor = NpaCoral,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("doom_start_button")
                            ) {
                                Text(
                                    text = "▶ Mulai Scroll",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            NeoButton(
                                onClick = {
                                    autoScrollDemo = false
                                    onStop()
                                },
                                containerColor = NpaYellow,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("doom_stop_button")
                            ) {
                                Text(
                                    text = "✋ Stop! Aku sadar",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoomTelemetryStrip(doomStateFlow: StateFlow<DoomUiState>) {
    val doom by doomStateFlow.collectAsStateWithLifecycle()
    val speedDp = doom.scrollSpeedDpPerSec
    val speedRatio = (speedDp / 900f).coerceIn(0f, 1f)
    val animatedSpeedRatio by animateFloatAsState(
        targetValue = speedRatio,
        animationSpec = tween(220),
        label = "speedGauge"
    )
    val (speedLabel, speedColor) = when {
        speedDp >= 650f -> "Doomscroll Turbo 🔥" to NpaCoral
        speedDp >= 350f -> "FOMO Mode ⚡" to NpaYellow
        speedDp >= 120f -> "Mulai Kepo 👀" to NpaBlue
        else -> "Santai 🌱" to NpaMint
    }
    val animatedSpeedColor by animateColorAsState(targetValue = speedColor, label = "speedColor")
    val gaugeBrush = remember { Brush.horizontalGradient(listOf(NpaMint, NpaYellow, NpaCoral)) }

    NeoCard(
        containerColor = NpaWhite,
        contentPadding = PaddingValues(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "KECEPATAN SCROLL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NpaNavy.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${speedDp.roundToInt()} dp/s · $speedLabel",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = NpaNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoPill(
                        containerColor = if (doom.scrollScore >= 60) NpaCoral else NpaMint,
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {}
                            .testTag("doom_scroll_score_live")
                    ) {
                        Text(
                            text = "Score ${doom.scrollScore}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = NpaNavy
                        )
                    }
                    NeoPill(containerColor = animatedSpeedColor) {
                        Text(
                            text = "${doom.notificationRatePerMin.roundToInt()} notif/m",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = NpaNavy
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape)
                    .background(NpaCream)
                    .border(2.dp, NpaNavy, CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedSpeedRatio)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(gaugeBrush)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TelemetryChip(
                    label = "Scroll · Freq",
                    value = "${doom.scrollEvents}× (${doom.interactionFrequencyPerMin.roundToInt()}/m)",
                    bg = NpaLavenderSoft,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("doom_scroll_events_value")
                )
                TelemetryChip(
                    label = "Post · Notif",
                    value = "${doom.postsViewed} · ${doom.notificationsTriggered}🔔",
                    bg = NpaYellow.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                )
                TelemetryChip(
                    label = "Engage",
                    value = "${doom.engagementCount} ⚡",
                    bg = NpaPink.copy(alpha = 0.45f),
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
    }
}

@Composable
private fun FeedPhoneHeader(
    doomStateFlow: StateFlow<DoomUiState>,
    phase: LabPhase,
    autoScrollDemo: Boolean,
    onToggleAutoScroll: () -> Unit
) {
    val secondsFlow = remember(doomStateFlow) { doomStateFlow.map { it.seconds }.distinctUntilChanged() }
    val seconds by secondsFlow.collectAsStateWithLifecycle(initialValue = 0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(NpaCoral))
            Box(Modifier.size(8.dp).clip(CircleShape).background(NpaYellow))
            Box(Modifier.size(8.dp).clip(CircleShape).background(NpaMint))
            Spacer(Modifier.width(4.dp))
            Text("FeedApp™", style = MaterialTheme.typography.labelLarge, color = NpaWhite.copy(alpha = 0.85f))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (phase == LabPhase.PLAYING) {
                Surface(
                    onClick = onToggleAutoScroll,
                    shape = CircleShape,
                    color = if (autoScrollDemo) NpaMint else NpaWhite.copy(alpha = 0.14f),
                    border = BorderStroke(1.5.dp, NpaWhite.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = if (autoScrollDemo) "Auto ON" else "Auto",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (autoScrollDemo) NpaNavy else NpaWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "${seconds}s",
                    style = MaterialTheme.typography.titleLarge,
                    color = NpaCoral
                )
            }
        }
    }
}

@Composable
private fun FeedToastOverlay(
    doomStateFlow: StateFlow<DoomUiState>,
    onDismissToast: () -> Unit,
    modifier: Modifier = Modifier
) {
    val toastFlow = remember(doomStateFlow) {
        doomStateFlow.map { (it.phase == LabPhase.PLAYING) to it.activeToast }.distinctUntilChanged()
    }
    val (isPlaying, activeToast) = toastFlow.collectAsStateWithLifecycle(initialValue = false to null).value

    AnimatedVisibility(
        visible = isPlaying && activeToast != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            onClick = onDismissToast,
            shape = RoundedCornerShape(14.dp),
            color = NpaYellow,
            border = BorderStroke(2.dp, NpaNavy)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = activeToast.orEmpty(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NpaNavy,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text("✕", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
            }
        }
    }
}

@Composable
private fun FeedPostRow(
    index: Int,
    post: com.example.data.FakePostItem,
    liked: Boolean,
    engaged: Boolean,
    isPlaying: Boolean,
    onEngagePost: (Int, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (index % 2 == 0) NpaWhite.copy(alpha = 0.04f) else Color.Transparent)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(post.color)
                    .border(1.5.dp, NpaWhite.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(post.avatar, fontSize = 18.sp) }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(post.user, style = MaterialTheme.typography.labelSmall, color = post.color)
                    Text("#${index + 1}", style = MaterialTheme.typography.labelSmall, color = NpaWhite.copy(alpha = 0.35f))
                }
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NpaWhite.copy(alpha = 0.92f)
                )
            }
        }

        Row(
            modifier = Modifier.padding(start = 52.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { onEngagePost(index, true) },
                enabled = isPlaying,
                shape = CircleShape,
                color = if (liked) NpaCoral.copy(alpha = 0.28f) else NpaWhite.copy(alpha = 0.08f),
                modifier = Modifier.testTag("doom_like_$index")
            ) {
                Text(
                    text = "${if (liked) "💖" else "❤️"} ${post.likes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (liked) NpaCoral else NpaWhite.copy(alpha = 0.72f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
            Surface(
                onClick = { onEngagePost(index, false) },
                enabled = isPlaying,
                shape = CircleShape,
                color = if (engaged) NpaYellow.copy(alpha = 0.25f) else NpaWhite.copy(alpha = 0.08f),
                modifier = Modifier.testTag("doom_comment_$index")
            ) {
                Text(
                    text = "💬 Komentar",
                    style = MaterialTheme.typography.labelSmall,
                    color = NpaWhite.copy(alpha = 0.72f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
            Surface(
                onClick = { onEngagePost(index, false) },
                enabled = isPlaying,
                shape = CircleShape,
                color = NpaWhite.copy(alpha = 0.08f)
            ) {
                Text(
                    text = "🔁",
                    style = MaterialTheme.typography.labelSmall,
                    color = NpaWhite.copy(alpha = 0.72f),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(NpaWhite.copy(alpha = 0.08f)))
}

@Composable
private fun DoomTestResultsSummary(
    doom: DoomUiState,
    tier: com.example.data.DoomResultTier,
    onRetry: () -> Unit,
    onNavigateToDetox: () -> Unit,
    onNavigateToChallenge: () -> Unit
) {
    val context = LocalContext.current
    val score = doom.digitalHabitScore
    val avgSpeed = doom.averageSpeedDpPerSec
    val postsPerMin = ((doom.postsViewed.toFloat() / doom.seconds.coerceAtLeast(1)) * 60f).roundToInt()
    val scoreColor = when {
        score >= 85 -> NpaMint
        score >= 65 -> NpaBlue
        score >= 40 -> NpaYellow
        else -> NpaCoral
    }
    val animatedSweep by animateFloatAsState(
        targetValue = (score / 100f) * 260f,
        animationSpec = tween(durationMillis = 850),
        label = "habitScoreArc"
    )
    val deltaFromPrev = doom.previousHabitScore?.let { prev -> score - prev }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
            .testTag("doom_results_summary"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Personalized Digital Habit Score Gauge Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(NpaWhite.copy(alpha = 0.08f))
                .border(2.dp, NpaWhite.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "PERSONALIZED DIGITAL HABIT SCORE",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = NpaYellow
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Score Gauge Ring
                Box(
                    modifier = Modifier.size(116.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 14.dp.toPx()
                        drawArc(
                            color = NpaWhite.copy(alpha = 0.14f),
                            startAngle = 140f,
                            sweepAngle = 260f,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = scoreColor,
                            startAngle = 140f,
                            sweepAngle = animatedSweep,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 34.sp),
                            color = NpaCream
                        )
                        Text(
                            text = "/ 100",
                            style = MaterialTheme.typography.labelSmall,
                            color = NpaCream.copy(alpha = 0.6f)
                        )
                    }
                }

                NopiMascot(pose = tier.nopiPose, size = 86.dp)
            }

            NeoPill(containerColor = scoreColor) {
                Text(
                    text = doom.habitTitle.ifBlank { tier.label },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = NpaNavy
                )
            }

            if (deltaFromPrev != null) {
                val deltaText = when {
                    deltaFromPrev > 0 -> "📈 +$deltaFromPrev poin lebih sadar dari tes sebelumnya"
                    deltaFromPrev < 0 -> "📉 $deltaFromPrev poin dari tes sebelumnya"
                    else -> "⚖️ Sama dengan skor tes sebelumnya"
                }
                Text(
                    text = deltaText,
                    style = MaterialTheme.typography.labelSmall,
                    color = NpaCream.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "${doom.seconds} detik · ${tier.label}",
                style = MaterialTheme.typography.labelLarge,
                color = NpaYellow,
                textAlign = TextAlign.Center
            )
            Text(
                text = tier.desc,
                style = MaterialTheme.typography.bodyMedium,
                color = NpaCream.copy(alpha = 0.82f),
                textAlign = TextAlign.Center
            )
        }

        // Average Scroll Speed Spotlight Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NpaCream)
                .border(2.5.dp, NpaNavy, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RATA-RATA KECEPATAN SCROLL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NpaNavy.copy(alpha = 0.65f)
                    )
                    Text(
                        text = "${avgSpeed.roundToInt()} dp/s",
                        style = MaterialTheme.typography.headlineLarge,
                        color = NpaNavy
                    )
                }
                NeoPill(containerColor = NpaLavenderSoft) {
                    Text(
                        text = "~$postsPerMin post/mnt",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = NpaNavy
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultStatBox(
                    label = "Scroll Score",
                    value = "${doom.scrollScore}/100",
                    color = NpaCoral,
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) {}
                        .testTag("doom_scroll_score_result")
                )
                ResultStatBox(
                    label = "Scroll Event",
                    value = "${doom.scrollEvents}×",
                    color = NpaYellow,
                    modifier = Modifier.weight(1f)
                )
                ResultStatBox(
                    label = "Frekuensi",
                    value = "${doom.interactionFrequencyPerMin.roundToInt()}/mnt",
                    color = NpaMint,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultStatBox(
                    label = "Puncak Speed",
                    value = "${doom.peakSpeedDpPerSec.roundToInt()} dp/s",
                    color = NpaLavenderSoft,
                    modifier = Modifier.weight(1f)
                )
                ResultStatBox(
                    label = "Freq. Notif",
                    value = "${doom.notificationRatePerMin.roundToInt()}/mnt",
                    color = NpaCream,
                    modifier = Modifier.weight(1f)
                )
                ResultStatBox(
                    label = "Engagement",
                    value = "${doom.engagementCount}×",
                    color = NpaPink,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Scroll Speed History Sparkline with Average Line
        if (doom.speedHistory.size >= 2) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NpaWhite.copy(alpha = 0.07f))
                    .border(1.5.dp, NpaWhite.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Grafik Kecepatan Scroll",
                        style = MaterialTheme.typography.labelSmall,
                        color = NpaCream.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "— Rata-rata ${avgSpeed.roundToInt()} dp/s",
                        style = MaterialTheme.typography.labelSmall,
                        color = NpaYellow
                    )
                }
                SpeedSparkline(
                    samples = doom.speedHistory,
                    averageSpeed = avgSpeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                )
            }
        }

        // Personalized Habit Advice Box
        if (doom.habitAdvice.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NpaMint.copy(alpha = 0.16f))
                    .border(1.5.dp, NpaMint.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "💡 Insight Kebiasaan Digitalmu",
                    style = MaterialTheme.typography.labelLarge,
                    color = NpaMint
                )
                Text(
                    text = doom.habitAdvice,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NpaCream.copy(alpha = 0.9f)
                )
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeoButton(
                onClick = onRetry,
                containerColor = NpaMint,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "🔄 Coba lagi",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            NeoButton(
                onClick = {
                    val shareText = "Skor Kebiasaan Digital NPA-ku: $score/100 (${doom.habitTitle})! " +
                        "Rata-rata scroll ${avgSpeed.roundToInt()} dp/s selama ${doom.seconds}s. " +
                        "Yuk cek seberapa tahan kamu di #NoPhonesAround!"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Hasil Doomscrolling Test NPA")
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Bagikan Hasil Tes"))
                },
                containerColor = NpaPink,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "↗ Bagikan",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NeoButton(
                onClick = onNavigateToDetox,
                containerColor = NpaBlue,
                contentColor = NpaWhite,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "⏳ Detox Timer",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            NeoButton(
                onClick = onNavigateToChallenge,
                containerColor = NpaYellow,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "🌱 Ambil Challenge",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TelemetryChip(
    label: String,
    value: String,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.5.dp, NpaNavy, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = NpaNavy.copy(alpha = 0.7f),
            maxLines = 1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = NpaNavy,
            maxLines = 1
        )
    }
}

@Composable
private fun ResultStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(2.dp, NpaNavy, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = NpaNavy.copy(alpha = 0.75f), maxLines = 1)
        Text(value, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold), color = NpaNavy, maxLines = 1)
    }
}

@Composable
private fun SpeedSparkline(
    samples: List<Float>,
    averageSpeed: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val maxVal = maxOf(samples.maxOrNull() ?: 100f, averageSpeed, 100f)
        val avgY = size.height - ((averageSpeed / maxVal).coerceIn(0f, 1f) * (size.height - 8f)) - 4f
        drawLine(
            color = NpaYellow.copy(alpha = 0.7f),
            start = Offset(0f, avgY),
            end = Offset(size.width, avgY),
            strokeWidth = 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
        )

        val stepX = if (samples.size > 1) size.width / (samples.size - 1) else size.width
        val path = Path()
        samples.forEachIndexed { idx, value ->
            val x = idx * stepX
            val y = size.height - ((value / maxVal).coerceIn(0f, 1f) * (size.height - 8f)) - 4f
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
            drawCircle(
                color = if (value > 450f) NpaCoral else NpaYellow,
                radius = 4f,
                center = Offset(x, y)
            )
        }
        drawPath(
            path = path,
            color = NpaMint,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}
