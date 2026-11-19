package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.NpaData
import com.example.ui.components.NopiHdAvatar
import com.example.ui.theme.*
import com.example.viewmodel.HomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    homeStateFlow: StateFlow<HomeUiState>,
    liveCounterFlow: StateFlow<Int>,
    onNextQuote: () -> Unit,
    onDropPhone: () -> Unit,
    onResetHero: () -> Unit,
    onNavigateToChallenge: () -> Unit,
    onNavigateToLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeState by homeStateFlow.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(NpaCream)
            .testTag("home_feed"),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        // 01 Top Quote Strip
        item(key = "quote_top", contentType = "quote_banner") {
            QuoteStripBanner(
                quoteIndex = homeState.quoteIndex,
                isTop = true,
                onClick = onNextQuote
            )
        }

        // 02-03 Hero Section (Before / After Drop The Phone)
        item(key = "hero", contentType = "hero") {
            HeroSection(
                showAfter = homeState.heroShowAfter,
                hasToggled = homeState.heroHasToggled,
                onDropPhone = onDropPhone,
                onResetHero = onResetHero,
                onStartChallenge = onNavigateToChallenge
            )
        }

        // 04 Stats 2x2 Grid
        item(key = "stats", contentType = "stats") {
            SectionContainer {
                StatsGridSection()
            }
        }

        // 05 Education Interactive 2x2 Lab
        item(key = "education", contentType = "education") {
            SectionContainer {
                EducationSection()
            }
        }

        // 06 Tips (Dark Section)
        item(key = "tips", contentType = "tips") {
            TipsSection()
        }

        // 07 Myths vs Facts
        item(key = "myths", contentType = "myths") {
            SectionContainer {
                MythsSection()
            }
        }

        // 08 Data & Live Counter (Dark Section)
        item(key = "data", contentType = "data") {
            DataSection(liveCounterFlow = liveCounterFlow)
        }

        // 09 Moments
        item(key = "moments", contentType = "moments") {
            SectionContainer {
                MomentsSection()
            }
        }

        // 10 Board Game Coming Soon Banner
        item(key = "boardgame", contentType = "boardgame") {
            BoardGameTeaser(onQuickTryLab = onNavigateToLab)
        }

        // 11 Bottom Quote Strip
        item(key = "quote_bottom", contentType = "quote_banner") {
            QuoteStripBanner(
                quoteIndex = homeState.quoteIndex,
                isTop = false,
                onClick = onNextQuote
            )
        }

        // 12 Footer
        item(key = "footer", contentType = "footer") {
            FooterSection()
        }
    }
}

@Composable
private fun SectionContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun QuoteStripBanner(
    quoteIndex: Int,
    isTop: Boolean,
    onClick: () -> Unit
) {
    val quote = NpaData.quotes[quoteIndex % NpaData.quotes.size]
    val brush = remember(isTop) {
        if (isTop) {
            Brush.linearGradient(listOf(NpaYellow, NpaPink, NpaMint, NpaBlue))
        } else {
            Brush.linearGradient(listOf(NpaBlue, NpaMint, NpaPink, NpaYellow))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush)
            .clickable(onClick = onClick)
            .testTag(if (isTop) "quote_strip_top" else "quote_strip_bottom")
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                NeoPill(containerColor = NpaWhite.copy(alpha = 0.72f)) {
                    Text(
                        text = "QUOTE OF THE MOMENT",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                        color = NpaNavy
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    NpaData.quotes.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (idx == quoteIndex) NpaNavy else NpaNavy.copy(alpha = 0.25f)
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            AnimatedContent(targetState = quote, label = "quoteText") { q ->
                Column {
                    Text(
                        text = "\"${q.text}\"",
                        style = MaterialTheme.typography.titleLarge,
                        color = NpaNavy
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = q.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = NpaNavy.copy(alpha = 0.72f)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tap untuk quote berikutnya →",
                style = MaterialTheme.typography.labelSmall,
                color = NpaNavy.copy(alpha = 0.6f)
            )
        }
        // Bottom/Top ink divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(if (isTop) Alignment.BottomCenter else Alignment.TopCenter)
                .background(NpaNavy)
        )
    }
}

@Composable
private fun HeroSection(
    showAfter: Boolean,
    hasToggled: Boolean,
    onDropPhone: () -> Unit,
    onResetHero: () -> Unit,
    onStartChallenge: () -> Unit
) {
    val bgTopState = animateColorAsState(
        targetValue = if (showAfter) NpaAfterGreen else NpaNavy,
        animationSpec = tween(420),
        label = "heroBgTop"
    )
    val bgBottomState = animateColorAsState(
        targetValue = if (showAfter) NpaAfterPeach else NpaNavySoft,
        animationSpec = tween(420),
        label = "heroBgBottom"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Brush.verticalGradient(listOf(bgTopState.value, bgBottomState.value)))
            }
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tag badge rotated -3deg
            Box(
                modifier = Modifier
                    .align(Alignment.Start)
                    .rotate(-3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NpaYellow)
                    .border(2.5.dp, NpaNavy, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "No Phone Around",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = NpaNavy
                )
            }

            Spacer(Modifier.height(16.dp))

            // Hero Headline
            if (!showAfter) {
                Text(
                    text = buildAnnotatedString {
                        append("Semua ")
                        withStyle(SpanStyle(color = NpaYellow)) { append("\"hadir\"") }
                        append(",\nga ada yang beneran ada.")
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = NpaWhite,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append("6 orang, 1 meja, 0 kontak mata. ")
                        withStyle(SpanStyle(color = NpaPink, fontWeight = FontWeight.Bold)) {
                            append("Familiar?")
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = NpaWhite.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        append("Less Screen,\n")
                        withStyle(SpanStyle(color = NpaBlue)) { append("More Connection.") }
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = NpaNavy,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Karena momen terbaik ga butuh notifikasi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NpaNavy.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(22.dp))

            // 6-person Table Scene
            HeroTableScene(showAfter = showAfter)

            if (showAfter) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, NpaNavy, CircleShape)
                    ) {
                        NopiHdAvatar(size = 56.dp)
                    }
                    NeoPill(containerColor = NpaMint) {
                        Text(
                            text = "ya gitu dong! 🎉",
                            style = MaterialTheme.typography.labelLarge,
                            color = NpaNavy
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // CTA Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!showAfter) {
                    NeoButton(
                        onClick = onDropPhone,
                        containerColor = NpaCoral,
                        contentColor = NpaNavy,
                        modifier = Modifier.testTag("drop_phone_button")
                    ) {
                        Text(
                            text = "📵 DROP THE PHONE",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                } else {
                    NeoButton(
                        onClick = onStartChallenge,
                        containerColor = NpaYellow,
                        contentColor = NpaNavy,
                        modifier = Modifier.testTag("start_npa_button")
                    ) {
                        Text(
                            text = "✨ Mulai NPA Experience",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                    NeoButton(
                        onClick = onResetHero,
                        containerColor = NpaWhite,
                        contentColor = NpaNavy,
                        modifier = Modifier.testTag("reset_hero_button")
                    ) {
                        Text(
                            text = "← lihat lagi",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            if (!showAfter) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = if (!hasToggled) "↑ klik DROP THE PHONE untuk lihat perbedaannya"
                    else "\"Put the phone down. Pick the moment up.\"",
                    style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                    color = NpaWhite.copy(alpha = 0.65f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HeroTableScene(showAfter: Boolean) {
    val beforePainter = rememberCrispBitmapPainter(
        resId = R.drawable.img_hero_before,
        maxDimPx = 960,
        placeholderColor = NpaNavySoft
    )
    val afterPainter = rememberCrispBitmapPainter(
        resId = R.drawable.img_hero_after,
        maxDimPx = 960,
        placeholderColor = NpaAfterGreen
    )

    NeoCard(
        containerColor = if (showAfter) NpaWhite else NpaNavySoft,
        borderColor = if (showAfter) NpaNavy else NpaYellow,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AnimatedContent(
                targetState = showAfter,
                label = "heroSceneArtwork"
            ) { after ->
                Image(
                    painter = if (after) afterPainter else beforePainter,
                    contentDescription = if (after) {
                        "Ilustrasi 6 teman tertawa bersama tanpa HP di atas meja"
                    } else {
                        "Ilustrasi 6 teman duduk bersama namun sibuk menatap layar HP masing-masing"
                    },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                )
            }

            NeoPill(
                containerColor = if (showAfter) NpaMint else NpaNavy.copy(alpha = 0.88f),
                borderColor = if (showAfter) NpaNavy else NpaYellow,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = if (showAfter) "✨ 100% Hadir Bareng" else "📵 0 Kontak Mata",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (showAfter) NpaNavy else NpaYellow
                )
            }
        }
    }
}

@Composable
private fun StatsGridSection() {
    val statRows = remember { NpaData.stats.chunked(2) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        statRows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { stat ->
                    NeoCard(
                        containerColor = stat.bg,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(stat.icon, fontSize = 24.sp)
                            Text(
                                text = stat.value,
                                style = MaterialTheme.typography.headlineLarge,
                                color = NpaNavy
                            )
                            Text(
                                text = stat.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NpaNavy.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EducationSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "KENAPA NPA?",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = NpaCoral
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "4 hal yang pelan-pelan hilang tanpa kita sadar",
                style = MaterialTheme.typography.headlineLarge,
                color = NpaNavy,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Bukan soal anti-teknologi. Soal hadir di momen yang benar-benar penting.",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaNavy.copy(alpha = 0.72f),
                textAlign = TextAlign.Center
            )
        }

        // 5a Compare Card
        CompareEduCard()

        // 5b Brain Overload Card
        BrainOverloadEduCard()

        // 5c Empathy Card
        EmpathyEduCard()

        // 5d Moment Quality Card
        MomentQualityEduCard()
    }
}

@Composable
private fun CompareEduCard() {
    var scrollMode by remember { mutableStateOf(true) }
    val data = if (scrollMode) NpaData.compareScroll else NpaData.compareNgobrol

    NeoCard(containerColor = NpaWhite) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERBANDINGAN",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = NpaNavy.copy(alpha = 0.65f)
                )
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NpaLavenderSoft)
                        .border(2.dp, NpaNavy, CircleShape)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(true to "📱 Scroll", false to "💬 Ngobrol").forEach { (isScroll, label) ->
                        val active = scrollMode == isScroll
                        Surface(
                            onClick = { scrollMode = isScroll },
                            shape = CircleShape,
                            color = if (active) NpaYellow else Color.Transparent,
                            modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.chars.forEach { ch ->
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(data.chipBg)
                            .border(2.5.dp, NpaNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ch, fontSize = 24.sp)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(data.title, style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
                Text(data.subtitle, style = MaterialTheme.typography.labelSmall, color = NpaNavy.copy(alpha = 0.6f))
                Spacer(Modifier.height(4.dp))
                data.items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(data.accent)
                        )
                        Text(item, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = NpaNavy)
                    }
                }
            }
        }
    }
}

@Composable
private fun BrainOverloadEduCard() {
    var exploded by remember { mutableStateOf(false) }
    val radiusDp by animateDpAsState(targetValue = if (exploded) 72.dp else 0.dp, label = "brainRadius")

    NeoCard(
        containerColor = NpaWhite,
        onClick = { exploded = !exploded },
        modifier = Modifier.testTag("brain_overload_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "ATTENTION SPAN",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = NpaNavy.copy(alpha = 0.65f)
            )
            Text("Otakmu lagi overload.", style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
            Text(
                text = "Setiap notifikasi merobek fokusmu. Tap area otak untuk lihat bedanya.",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaNavy.copy(alpha = 0.7f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                contentAlignment = Alignment.Center
            ) {
                NpaData.brainTabs.forEachIndexed { idx, tab ->
                    val angleRad = (idx.toFloat() / NpaData.brainTabs.size) * 2.0 * PI
                    val cosAngle = remember(idx) { cos(angleRad).toFloat() }
                    val sinAngle = remember(idx) { sin(angleRad).toFloat() }
                    if (exploded) {
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    val px = radiusDp.toPx()
                                    translationX = cosAngle * px
                                    translationY = sinAngle * px
                                }
                                .clip(CircleShape)
                                .background(NpaYellow)
                                .border(2.dp, NpaNavy, CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(tab, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(if (exploded) 72.dp else 82.dp)
                        .clip(CircleShape)
                        .background(if (exploded) NpaCoral else NpaMint)
                        .border(3.dp, NpaNavy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 36.sp)
                }
            }
        }
    }
}

@Composable
private fun EmpathyEduCard() {
    var phoneVisible by remember { mutableStateOf(true) }

    NeoCard(containerColor = NpaWhite) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "EMPATI",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = NpaNavy.copy(alpha = 0.65f)
            )
            Text("HP sebagai tembok tak kasat mata.", style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
            Text(
                text = "Kehadiran HP saja sudah cukup untuk memutus koneksi emosional.",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaNavy.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(NpaBlue)
                            .border(2.5.dp, NpaNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("😊", fontSize = 26.sp) }
                    Spacer(Modifier.height(4.dp))
                    Text("Kamu", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                }

                Box(
                    modifier = Modifier.width(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (phoneVisible) NpaCoral.copy(alpha = 0.45f) else NpaMint)
                    )
                    if (phoneVisible) {
                        Box(
                            modifier = Modifier
                                .size(34.dp, 46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NpaNavy)
                                .border(2.dp, NpaNavy, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("📱", fontSize = 16.sp) }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (phoneVisible) NpaPink.copy(alpha = 0.65f) else NpaPink)
                            .border(2.5.dp, NpaNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text(if (phoneVisible) "😶" else "😄", fontSize = 26.sp) }
                    Spacer(Modifier.height(4.dp))
                    Text("Dia", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                NeoButton(
                    onClick = { phoneVisible = true },
                    containerColor = if (phoneVisible) NpaCoral else NpaNavy,
                    contentColor = if (phoneVisible) NpaNavy else NpaWhite,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Text("📱 Munculin HP", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
                NeoButton(
                    onClick = { phoneVisible = false },
                    containerColor = NpaMint,
                    contentColor = NpaNavy,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Text("👀 Balikin Fokus", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MomentQualityEduCard() {
    var value by remember { mutableIntStateOf(20) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        if (running) {
            while (value < 88) {
                delay(50)
                value = (value + 2).coerceAtMost(88)
            }
        }
    }

    val progressBrush = remember { Brush.horizontalGradient(listOf(NpaCoral, NpaYellow, NpaMint)) }
    val activeStage = NpaData.momentStages.lastOrNull { value >= it.first }?.second ?: "Semua pegang HP"

    NeoCard(containerColor = NpaWhite) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "KUALITAS MOMEN",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                color = NpaNavy.copy(alpha = 0.65f)
            )
            Text("Dari 20% ke 88%.", style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
            Text(
                text = "Kualitas momen saat semua taruh HP vs pas semua pegang HP.",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaNavy.copy(alpha = 0.7f)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$value%", style = MaterialTheme.typography.labelLarge, color = NpaNavy)
                Text(activeStage, style = MaterialTheme.typography.labelLarge, color = NpaMint)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(CircleShape)
                    .background(NpaCream)
                    .border(2.5.dp, NpaNavy, CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(value / 100f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(progressBrush)
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NpaData.momentStages.forEach { (pct, label) ->
                    val reached = value >= pct
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (reached) NpaMint else NpaNavy.copy(alpha = 0.08f))
                            .border(
                                width = if (reached) 2.dp else 0.dp,
                                color = if (reached) NpaNavy else Color.Transparent,
                                shape = CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                    }
                }
            }

            NeoButton(
                onClick = {
                    if (running && value >= 88) {
                        value = 20
                        running = false
                    } else if (!running) {
                        running = true
                    }
                },
                containerColor = NpaYellow
            ) {
                Text(
                    text = when {
                        running && value >= 88 -> "🔄 Ulang"
                        running -> "sedang berjalan..."
                        else -> "▶ Mulai simulasi"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun TipsSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NpaNavy)
            .padding(horizontal = 16.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "KEBIASAAN KECIL",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = NpaMint
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "4 kebiasaan yang mulai bisa kamu coba hari ini",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NpaCream,
                    textAlign = TextAlign.Center
                )
            }

            val tipRows = remember { NpaData.tips.chunked(2) }
            tipRows.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { tip ->
                        NeoCard(
                            containerColor = tip.bg,
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(tip.icon, fontSize = 28.sp)
                                Text(tip.title, style = MaterialTheme.typography.titleLarge, color = NpaNavy)
                                Text(
                                    text = tip.desc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NpaNavy.copy(alpha = 0.85f)
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
private fun MythsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "MITOS VS FAKTA",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = NpaCoral
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Kamu yakin itu cuma mitos?",
                style = MaterialTheme.typography.headlineLarge,
                color = NpaNavy,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tap kartu untuk lihat faktanya, atau klik \"Ganti Mitos\" untuk mitos lain.",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaNavy.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }

        NpaData.myths.indices.forEach { startIndex ->
            MythFlipCard(startIndex = startIndex)
        }
    }
}

@Composable
private fun MythFlipCard(startIndex: Int) {
    var index by remember { mutableIntStateOf(startIndex) }
    var showFact by remember { mutableStateOf(false) }
    val current = NpaData.myths[index]
    val bg by animateColorAsState(
        targetValue = if (showFact) current.factBg else current.mythBg,
        animationSpec = tween(250),
        label = "mythBg"
    )

    NeoCard(
        containerColor = bg,
        onClick = { showFact = !showFact },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("myth_card_$startIndex")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoPill(containerColor = NpaWhite.copy(alpha = 0.6f)) {
                    Text(
                        text = if (showFact) "FAKTA" else "MITOS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = NpaNavy
                    )
                }
                Surface(
                    onClick = {
                        index = (index + 1) % NpaData.myths.size
                        showFact = false
                    },
                    shape = CircleShape,
                    color = NpaWhite.copy(alpha = 0.8f),
                    border = BorderStroke(2.dp, NpaNavy)
                ) {
                    Text(
                        text = "⟳ Ganti Mitos",
                        style = MaterialTheme.typography.labelSmall,
                        color = NpaNavy,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Text(
                text = if (showFact) current.fact else "\"${current.myth}\"",
                style = if (showFact) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.titleLarge,
                color = NpaNavy
            )

            Text(
                text = if (showFact) "← tap untuk kembali" else "tap untuk lihat faktanya →",
                style = MaterialTheme.typography.labelSmall,
                color = NpaNavy.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
private fun DataSection(liveCounterFlow: StateFlow<Int>) {
    val maxHours = remember { NpaData.screenTimeChart.maxOf { it.hours } }
    val shockStatRows = remember { NpaData.shockStats.chunked(2) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NpaNavy)
            .padding(horizontal = 16.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "DATA YANG BICARA",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = NpaYellow
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Rata-rata screen time Indonesia",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NpaCream,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Sumber: DataReportal, Sensor Tower (2025–2026)",
                    style = MaterialTheme.typography.labelSmall,
                    color = NpaCream.copy(alpha = 0.55f)
                )
            }

            // Screen time bar chart card
            NeoCard(containerColor = NpaCream) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        NpaData.screenTimeChart.forEach { point ->
                            val barHeight = (120f * (point.hours / maxHours)).dp
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = point.displayLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = NpaNavy
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                        .background(point.color)
                                        .border(
                                            2.dp,
                                            NpaNavy,
                                            RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                        )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = point.year,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NpaNavy
                                )
                            }
                        }
                    }
                    Text(
                        text = "Rata-rata jam online mobile per hari orang Indonesia",
                        style = MaterialTheme.typography.labelSmall,
                        color = NpaNavy.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Shock stats 2x2
            shockStatRows.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { s ->
                        NeoCard(
                            containerColor = s.bg,
                            contentPadding = PaddingValues(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(s.value, style = MaterialTheme.typography.headlineLarge, color = NpaNavy)
                                Text(s.label, style = MaterialTheme.typography.labelSmall, color = NpaNavy.copy(alpha = 0.85f))
                            }
                        }
                    }
                }
            }

            // Isolated Live NPA Counter — only this card recomposes on tick
            LiveNpaCounterCard(liveCounterFlow = liveCounterFlow)
        }
    }
}

@Composable
private fun LiveNpaCounterCard(liveCounterFlow: StateFlow<Int>) {
    val liveCounter by liveCounterFlow.collectAsStateWithLifecycle()
    val numberFormatter = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")) }
    val formattedCounter = remember(liveCounter) { numberFormatter.format(liveCounter) }

    NeoCard(containerColor = NpaYellow) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Orang sedang NPA mode hari ini 🌱",
                style = MaterialTheme.typography.labelLarge,
                color = NpaNavy
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formattedCounter,
                style = MaterialTheme.typography.displayLarge,
                color = NpaNavy
            )
            Text(
                text = "dan bertambah setiap detiknya",
                style = MaterialTheme.typography.labelSmall,
                color = NpaNavy.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
private fun MomentsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "MOMENTS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = NpaCoral
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Yang ga bisa di-screenshot.",
                style = MaterialTheme.typography.headlineLarge,
                color = NpaNavy,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Ini yang hilang kalau kamu terus pegang HP.",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaNavy.copy(alpha = 0.7f)
            )
        }

        NpaData.moments.forEach { m ->
            NeoCard(containerColor = m.bg) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(m.icon, fontSize = 32.sp)
                    Text(m.title, style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
                    Text(m.desc, style = MaterialTheme.typography.bodyMedium, color = NpaNavy.copy(alpha = 0.85f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(28.dp)
                                .background(NpaNavy)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "\"${m.quote}\"",
                            style = MaterialTheme.typography.labelSmall.copy(fontStyle = FontStyle.Italic),
                            color = NpaNavy.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardGameTeaser(onQuickTryLab: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NpaYellow)
            .border(width = 3.dp, color = NpaNavy)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("🎲", fontSize = 40.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COMING SOON",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                        color = NpaNavy.copy(alpha = 0.65f)
                    )
                    Text(
                        text = "NPA: The Board Game",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NpaNavy
                    )
                    Text(
                        text = "Versi offline dari gerakan ini. Dimainkan IRL, bersama orang-orang nyata.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NpaNavy.copy(alpha = 0.8f)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NeoPill(containerColor = NpaWhite.copy(alpha = 0.55f)) {
                    Text("Notify me 🔔 (Segera)", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                }
                NeoButton(
                    onClick = onQuickTryLab,
                    containerColor = NpaWhite,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("🎮 Mainkan NPA Lab dulu", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun FooterSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NpaNavy)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NpaCream),
                    contentAlignment = Alignment.Center
                ) {
                    Text("N", style = MaterialTheme.typography.titleLarge, color = NpaNavy)
                }
                Text("NPA", style = MaterialTheme.typography.headlineMedium, color = NpaCream)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NpaMint)
                )
            }
            Text(
                text = "Less Screen, More Connection.\n\"Put the phone down. Pick the moment up.\"",
                style = MaterialTheme.typography.bodyMedium,
                color = NpaCream.copy(alpha = 0.7f)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SUMBER DATA",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = NpaCream.copy(alpha = 0.5f)
                )
                NpaData.footerSources.forEach { src ->
                    Text(
                        text = "• $src",
                        style = MaterialTheme.typography.labelSmall,
                        color = NpaCream.copy(alpha = 0.5f)
                    )
                }
            }
            NeoCard(
                containerColor = NpaWhite.copy(alpha = 0.08f),
                borderColor = NpaWhite.copy(alpha = 0.25f),
                shadowOffset = 0.dp
            ) {
                Text(
                    text = "Kampanye edukasi. Bukan anti-teknologi. Yang dilawan adalah ketiadaan kehadiran.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NpaCream.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Text(
                text = "© 2024 NPA — No Phones Around. Dibuat dengan ❤️ dan sedikit gangguan HP.",
                style = MaterialTheme.typography.labelSmall,
                color = NpaCream.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
