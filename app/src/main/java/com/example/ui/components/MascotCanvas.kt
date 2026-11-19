package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CharExpression
import com.example.data.MusicTrack
import com.example.data.NopiPose
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Custom Canvas mascot for Nopi the Otter with 7 expressive poses.
 */
@Composable
fun NopiMascot(
    pose: NopiPose = NopiPose.IDLE,
    size: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    val otterBody = Color(0xFFA9764F)
    val otterCream = Color(0xFFFFE9C7)
    val otterNose = Color(0xFF3B2A1D)

    val bob = if (pose == NopiPose.WIN) {
        val infinite = rememberInfiniteTransition(label = "nopiFloat")
        infinite.animateFloat(
            initialValue = -2f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
            label = "bob"
        ).value
    } else {
        0f
    }

    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "Maskot berang-berang Nopi" }
    ) {
        val w = this.size.width
        val h = this.size.height
        val strokeW = w * 0.032f
        val yShift = bob * (h / 100f)

        // Tail
        val tailPath = Path().apply {
            moveTo(w * 0.28f, h * 0.72f + yShift)
            quadraticTo(w * 0.08f, h * 0.78f + yShift, w * 0.14f, h * 0.60f + yShift)
            quadraticTo(w * 0.22f, h * 0.62f + yShift, w * 0.30f, h * 0.66f + yShift)
            close()
        }
        drawPath(tailPath, otterBody)
        drawPath(tailPath, NpaNavy, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Body oval
        drawOval(
            color = otterBody,
            topLeft = Offset(w * 0.26f, h * 0.48f + yShift),
            size = Size(w * 0.48f, h * 0.40f)
        )
        drawOval(
            color = NpaNavy,
            topLeft = Offset(w * 0.26f, h * 0.48f + yShift),
            size = Size(w * 0.48f, h * 0.40f),
            style = Stroke(strokeW)
        )

        // Cream belly
        drawOval(
            color = otterCream,
            topLeft = Offset(w * 0.34f, h * 0.55f + yShift),
            size = Size(w * 0.32f, h * 0.29f)
        )

        // Ears
        drawCircle(otterBody, radius = w * 0.085f, center = Offset(w * 0.26f, h * 0.21f + yShift))
        drawCircle(NpaNavy, radius = w * 0.085f, center = Offset(w * 0.26f, h * 0.21f + yShift), style = Stroke(strokeW))
        drawCircle(otterBody, radius = w * 0.085f, center = Offset(w * 0.74f, h * 0.21f + yShift))
        drawCircle(NpaNavy, radius = w * 0.085f, center = Offset(w * 0.74f, h * 0.21f + yShift), style = Stroke(strokeW))

        // Head
        drawOval(
            color = otterBody,
            topLeft = Offset(w * 0.20f, h * 0.14f + yShift),
            size = Size(w * 0.60f, h * 0.44f)
        )
        drawOval(
            color = NpaNavy,
            topLeft = Offset(w * 0.20f, h * 0.14f + yShift),
            size = Size(w * 0.60f, h * 0.44f),
            style = Stroke(strokeW)
        )

        // Cream muzzle
        drawOval(
            color = otterCream,
            topLeft = Offset(w * 0.31f, h * 0.33f + yShift),
            size = Size(w * 0.38f, h * 0.22f)
        )

        // Cheeks
        drawOval(
            color = NpaCoral.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.24f, h * 0.37f + yShift),
            size = Size(w * 0.10f, h * 0.055f)
        )
        drawOval(
            color = NpaCoral.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.66f, h * 0.37f + yShift),
            size = Size(w * 0.10f, h * 0.055f)
        )

        // Eyes by pose
        when (pose) {
            NopiPose.WIN -> {
                val leftArc = Path().apply {
                    moveTo(w * 0.33f, h * 0.31f + yShift)
                    quadraticTo(w * 0.38f, h * 0.25f + yShift, w * 0.43f, h * 0.31f + yShift)
                }
                val rightArc = Path().apply {
                    moveTo(w * 0.57f, h * 0.31f + yShift)
                    quadraticTo(w * 0.62f, h * 0.25f + yShift, w * 0.67f, h * 0.31f + yShift)
                }
                drawPath(leftArc, NpaNavy, style = Stroke(strokeW, cap = StrokeCap.Round))
                drawPath(rightArc, NpaNavy, style = Stroke(strokeW, cap = StrokeCap.Round))
            }
            NopiPose.DROP, NopiPose.CROSSED -> {
                // Determined slanted brows + eyes
                drawCircle(NpaNavy, radius = w * 0.042f, center = Offset(w * 0.38f, h * 0.31f + yShift))
                drawCircle(NpaNavy, radius = w * 0.042f, center = Offset(w * 0.62f, h * 0.31f + yShift))
                drawLine(NpaNavy, Offset(w * 0.32f, h * 0.24f + yShift), Offset(w * 0.43f, h * 0.27f + yShift), strokeW, StrokeCap.Round)
                drawLine(NpaNavy, Offset(w * 0.68f, h * 0.24f + yShift), Offset(w * 0.57f, h * 0.27f + yShift), strokeW, StrokeCap.Round)
            }
            else -> {
                drawCircle(NpaWhite, radius = w * 0.055f, center = Offset(w * 0.38f, h * 0.30f + yShift))
                drawCircle(NpaNavy, radius = w * 0.036f, center = Offset(w * 0.38f, h * 0.305f + yShift))
                drawCircle(NpaWhite, radius = w * 0.015f, center = Offset(w * 0.365f, h * 0.29f + yShift))

                drawCircle(NpaWhite, radius = w * 0.055f, center = Offset(w * 0.62f, h * 0.30f + yShift))
                drawCircle(NpaNavy, radius = w * 0.036f, center = Offset(w * 0.62f, h * 0.305f + yShift))
                drawCircle(NpaWhite, radius = w * 0.015f, center = Offset(w * 0.605f, h * 0.29f + yShift))
            }
        }

        // Nose & smile
        drawOval(
            color = otterNose,
            topLeft = Offset(w * 0.44f, h * 0.36f + yShift),
            size = Size(w * 0.12f, h * 0.075f)
        )
        val mouth = Path().apply {
            moveTo(w * 0.43f, h * 0.45f + yShift)
            quadraticTo(w * 0.50f, h * 0.51f + yShift, w * 0.57f, h * 0.45f + yShift)
        }
        drawPath(mouth, NpaNavy, style = Stroke(strokeW * 0.85f, cap = StrokeCap.Round))

        // Pose-specific arms / accessories
        when (pose) {
            NopiPose.WIN -> {
                // Raised celebrating arms + golden star
                drawLine(otterBody, Offset(w * 0.28f, h * 0.56f + yShift), Offset(w * 0.15f, h * 0.38f + yShift), w * 0.10f, StrokeCap.Round)
                drawLine(otterBody, Offset(w * 0.72f, h * 0.56f + yShift), Offset(w * 0.85f, h * 0.38f + yShift), w * 0.10f, StrokeCap.Round)
                drawCircle(NpaYellow, radius = w * 0.07f, center = Offset(w * 0.86f, h * 0.24f + yShift))
                drawCircle(NpaNavy, radius = w * 0.07f, center = Offset(w * 0.86f, h * 0.24f + yShift), style = Stroke(strokeW * 0.7f))
            }
            NopiPose.CROSSED -> {
                // Crossed arms
                drawLine(NpaNavy, Offset(w * 0.32f, h * 0.62f + yShift), Offset(w * 0.68f, h * 0.66f + yShift), strokeW * 1.4f, StrokeCap.Round)
                drawLine(NpaNavy, Offset(w * 0.68f, h * 0.62f + yShift), Offset(w * 0.32f, h * 0.66f + yShift), strokeW * 1.4f, StrokeCap.Round)
            }
            NopiPose.DROP -> {
                // Phone dropping away on right
                rotate(25f, pivot = Offset(w * 0.84f, h * 0.66f + yShift)) {
                    drawRoundRect(
                        color = NpaNavy,
                        topLeft = Offset(w * 0.78f, h * 0.58f + yShift),
                        size = Size(w * 0.11f, h * 0.16f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    drawRoundRect(
                        color = NpaCoral,
                        topLeft = Offset(w * 0.80f, h * 0.60f + yShift),
                        size = Size(w * 0.07f, h * 0.11f),
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                }
            }
            else -> {
                // Cute paws on tummy
                drawOval(otterBody, topLeft = Offset(w * 0.20f, h * 0.55f + yShift), size = Size(w * 0.14f, h * 0.10f))
                drawOval(NpaNavy, topLeft = Offset(w * 0.20f, h * 0.55f + yShift), size = Size(w * 0.14f, h * 0.10f), style = Stroke(strokeW * 0.8f))
                drawOval(otterBody, topLeft = Offset(w * 0.66f, h * 0.55f + yShift), size = Size(w * 0.14f, h * 0.10f))
                drawOval(NpaNavy, topLeft = Offset(w * 0.66f, h * 0.55f + yShift), size = Size(w * 0.14f, h * 0.10f), style = Stroke(strokeW * 0.8f))
            }
        }
    }
}

/**
 * Anime-inspired expressive character head shared across Hero TableScene and NPA Lab.
 */
@Composable
fun ExpressiveCharacter(
    expression: CharExpression,
    skin: Color = Color(0xFFFFDFCD),
    hair: Color = Color(0xFF1A133B),
    hairHighlight: Color = NpaBlue,
    eyeColor: Color = NpaBlue,
    shirt: Color = NpaMint,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "Karakter ekspresi ${expression.name}" }
    ) {
        val w = this.size.width
        val h = this.size.height
        val stroke = w * 0.042f

        // Shoulders / shirt
        drawRoundRect(
            color = shirt,
            topLeft = Offset(w * 0.22f, h * 0.72f),
            size = Size(w * 0.56f, h * 0.26f),
            cornerRadius = CornerRadius(w * 0.2f, w * 0.2f)
        )
        drawRoundRect(
            color = NpaNavy,
            topLeft = Offset(w * 0.22f, h * 0.72f),
            size = Size(w * 0.56f, h * 0.26f),
            cornerRadius = CornerRadius(w * 0.2f, w * 0.2f),
            style = Stroke(stroke)
        )

        // Hair back
        drawCircle(hair, radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.42f))

        // Face
        drawCircle(skin, radius = w * 0.26f, center = Offset(w * 0.5f, h * 0.48f))
        drawCircle(NpaNavy, radius = w * 0.26f, center = Offset(w * 0.5f, h * 0.48f), style = Stroke(stroke))

        // Bangs / hair highlight
        val bangs = Path().apply {
            moveTo(w * 0.24f, h * 0.40f)
            quadraticTo(w * 0.38f, h * 0.22f, w * 0.52f, h * 0.34f)
            quadraticTo(w * 0.66f, h * 0.22f, w * 0.76f, h * 0.40f)
            quadraticTo(w * 0.50f, h * 0.16f, w * 0.24f, h * 0.40f)
            close()
        }
        drawPath(bangs, hair)
        drawCircle(hairHighlight, radius = w * 0.04f, center = Offset(w * 0.34f, h * 0.28f))

        // Blush on positive states
        if (expression == CharExpression.HAPPY || expression == CharExpression.RELIEVED || expression == CharExpression.WARM) {
            drawOval(NpaCoral.copy(alpha = 0.45f), Offset(w * 0.27f, h * 0.52f), Size(w * 0.11f, h * 0.06f))
            drawOval(NpaCoral.copy(alpha = 0.45f), Offset(w * 0.62f, h * 0.52f), Size(w * 0.11f, h * 0.06f))
        }

        // Eyes & Mouth
        when (expression) {
            CharExpression.HAPPY, CharExpression.RELIEVED -> {
                val eyeL = Path().apply {
                    moveTo(w * 0.34f, h * 0.48f)
                    quadraticTo(w * 0.39f, h * 0.42f, w * 0.44f, h * 0.48f)
                }
                val eyeR = Path().apply {
                    moveTo(w * 0.56f, h * 0.48f)
                    quadraticTo(w * 0.61f, h * 0.42f, w * 0.66f, h * 0.48f)
                }
                drawPath(eyeL, NpaNavy, style = Stroke(stroke, cap = StrokeCap.Round))
                drawPath(eyeR, NpaNavy, style = Stroke(stroke, cap = StrokeCap.Round))
                val smile = Path().apply {
                    moveTo(w * 0.42f, h * 0.58f)
                    quadraticTo(w * 0.50f, h * 0.66f, w * 0.58f, h * 0.58f)
                }
                drawPath(smile, NpaNavy, style = Stroke(stroke, cap = StrokeCap.Round))
            }
            CharExpression.WARM, CharExpression.NEUTRAL -> {
                drawCircle(eyeColor, radius = w * 0.045f, center = Offset(w * 0.39f, h * 0.47f))
                drawCircle(NpaNavy, radius = w * 0.025f, center = Offset(w * 0.39f, h * 0.47f))
                drawCircle(eyeColor, radius = w * 0.045f, center = Offset(w * 0.61f, h * 0.47f))
                drawCircle(NpaNavy, radius = w * 0.025f, center = Offset(w * 0.61f, h * 0.47f))
                if (expression == CharExpression.WARM) {
                    val smile = Path().apply {
                        moveTo(w * 0.44f, h * 0.58f)
                        quadraticTo(w * 0.50f, h * 0.63f, w * 0.56f, h * 0.58f)
                    }
                    drawPath(smile, NpaNavy, style = Stroke(stroke * 0.9f, cap = StrokeCap.Round))
                } else {
                    drawLine(NpaNavy, Offset(w * 0.44f, h * 0.59f), Offset(w * 0.56f, h * 0.59f), stroke * 0.9f, StrokeCap.Round)
                }
            }
            CharExpression.SLIGHTLY_HURT, CharExpression.DISAPPOINTED, CharExpression.SAD -> {
                drawCircle(NpaNavy, radius = w * 0.036f, center = Offset(w * 0.39f, h * 0.49f))
                drawCircle(NpaNavy, radius = w * 0.036f, center = Offset(w * 0.61f, h * 0.49f))
                // Sad brows
                drawLine(NpaNavy, Offset(w * 0.33f, h * 0.43f), Offset(w * 0.43f, h * 0.40f), stroke * 0.8f, StrokeCap.Round)
                drawLine(NpaNavy, Offset(w * 0.67f, h * 0.43f), Offset(w * 0.57f, h * 0.40f), stroke * 0.8f, StrokeCap.Round)
                val frown = Path().apply {
                    moveTo(w * 0.43f, h * 0.62f)
                    quadraticTo(w * 0.50f, h * 0.56f, w * 0.57f, h * 0.62f)
                }
                drawPath(frown, NpaNavy, style = Stroke(stroke * 0.9f, cap = StrokeCap.Round))
            }
        }
    }
}

enum class OtterMood { IDLE, WAVE, SPIN, BLUSH, WINK, JUMP, SHY }

@Composable
fun NopiHdAvatar(
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val painter = rememberCrispBitmapPainter(
        resId = R.drawable.img_nopi_mascot,
        maxDimPx = 220,
        placeholderColor = NpaYellow
    )
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(NpaYellow),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Maskot berang-berang Nopi HD",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    scaleX = 1.34f
                    scaleY = 1.34f
                }
        )
    }
}

/**
 * Interactive floating OtterPet companion from OtterPet.tsx.
 */
@Composable
fun FloatingOtterPet(
    onOpenChat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var mood by remember { mutableStateOf(OtterMood.IDLE) }

    LaunchedEffect(mood) {
        if (mood != OtterMood.IDLE) {
            delay(1600)
            mood = OtterMood.IDLE
        }
    }

    val bubbleMap = remember {
        mapOf(
            OtterMood.WAVE to "Haii! Tanya Nopi AI yuk 👋",
            OtterMood.SPIN to "Wuss~ siap bantu fokus 🌀",
            OtterMood.BLUSH to "Yuk ngobrol bareng Nopi! 🥰",
            OtterMood.WINK to "Hehe, ada yang mau ditanya? 😉",
            OtterMood.JUMP to "Yeay!! Buka Gemini Chat 🎉",
            OtterMood.SHY to "Aku di sini buat kamu~ 🦦"
        )
    }

    val spinDeg by animateFloatAsState(
        targetValue = if (mood == OtterMood.SPIN) 360f else 0f,
        animationSpec = tween(500),
        label = "otterSpin"
    )
    val jumpOffset by animateFloatAsState(
        targetValue = if (mood == OtterMood.JUMP) -12f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "otterJump"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        AnimatedVisibility(
            visible = mood != OtterMood.IDLE,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NpaWhite,
                border = BorderStroke(2.dp, NpaNavy),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = bubbleMap[mood] ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = NpaNavy,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Box {
            Surface(
                onClick = {
                    val actions = listOf(
                        OtterMood.WAVE,
                        OtterMood.SPIN,
                        OtterMood.BLUSH,
                        OtterMood.WINK,
                        OtterMood.JUMP,
                        OtterMood.SHY
                    )
                    mood = actions.filterNot { it == mood }.random()
                    onOpenChat()
                },
                shape = CircleShape,
                color = NpaYellow,
                border = BorderStroke(2.5.dp, NpaNavy),
                modifier = Modifier
                    .size(58.dp)
                    .graphicsLayer {
                        translationY = jumpOffset.dp.toPx()
                        rotationZ = spinDeg
                    }
                    .testTag("floating_otter_chat_button")
                    .semantics { contentDescription = "Klik logo kartun Nopi untuk membuka chatbot Gemini" }
            ) {
                NopiHdAvatar(size = 58.dp)
            }

            Surface(
                shape = CircleShape,
                color = NpaCoral,
                border = BorderStroke(1.5.dp, NpaNavy),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = NpaWhite,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Floating Music Player matching MusicPlayer.tsx (playlist popup + spinning disc + quick play/pause).
 */
@Composable
fun FloatingMusicPlayer(
    tracks: List<MusicTrack>,
    activeIndex: Int?,
    isPlaying: Boolean,
    drawerOpen: Boolean,
    onToggleDrawer: () -> Unit,
    onSelectTrack: (Int) -> Unit,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onOpenYouTube: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTrack = activeIndex?.let { tracks.getOrNull(it) }
    val discAngle = if (isPlaying) {
        val infinite = rememberInfiniteTransition(label = "discSpin")
        infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
            label = "discAngle"
        ).value
    } else {
        0f
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = drawerOpen,
            enter = fadeIn() + scaleIn(initialScale = 0.94f),
            exit = fadeOut() + scaleOut(targetScale = 0.94f)
        ) {
            NeoCard(
                containerColor = NpaCream,
                contentPadding = PaddingValues(14.dp),
                modifier = Modifier
                    .width(276.dp)
                    .padding(bottom = 10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🎧 Temani sambil hadir",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
                        color = NpaNavy
                    )
                    tracks.forEachIndexed { index, track ->
                        val isActive = activeIndex == index
                        Surface(
                            onClick = { onSelectTrack(index) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isActive) track.color.copy(alpha = 0.25f) else NpaWhite,
                            border = BorderStroke(
                                width = 2.dp,
                                color = if (isActive) NpaNavy else NpaNavy.copy(alpha = 0.16f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("music_track_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(track.color)
                                        .border(2.dp, NpaNavy, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isActive && isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = if (isActive && isPlaying) "Jeda" else "Putar",
                                        tint = NpaNavy,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = NpaNavy,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = track.artist,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NpaNavy.copy(alpha = 0.65f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = { onOpenYouTube(track.videoId) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Buka ${track.title} di YouTube",
                                        tint = NpaNavy.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (activeTrack != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Diputar: ${activeTrack.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NpaNavy.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                onClick = onStop,
                                shape = CircleShape,
                                color = NpaWhite,
                                border = BorderStroke(2.dp, NpaNavy),
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("music_stop_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Close, contentDescription = "Hentikan musik", tint = NpaNavy, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activeTrack != null) {
                Surface(
                    onClick = onTogglePlayPause,
                    shape = CircleShape,
                    color = NpaWhite,
                    border = BorderStroke(2.5.dp, NpaNavy),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("music_play_pause_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Jeda musik" else "Putar musik",
                            tint = NpaNavy
                        )
                    }
                }
            }

            Box {
                Surface(
                    onClick = onToggleDrawer,
                    shape = CircleShape,
                    color = activeTrack?.color ?: NpaMint,
                    border = BorderStroke(2.5.dp, NpaNavy),
                    modifier = Modifier
                        .size(58.dp)
                        .testTag("music_drawer_toggle")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.graphicsLayer { rotationZ = discAngle }
                    ) {
                        Text("🎵", fontSize = 24.sp)
                    }
                }
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(NpaCoral)
                            .border(2.dp, NpaNavy, CircleShape)
                    )
                }
            }
        }
    }
}
