package com.example.ui.theme

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val NpaLightColorScheme = lightColorScheme(
    primary = NpaCoral,
    onPrimary = NpaNavy,
    secondary = NpaYellow,
    onSecondary = NpaNavy,
    tertiary = NpaMint,
    onTertiary = NpaNavy,
    background = NpaCream,
    onBackground = NpaNavy,
    surface = NpaCream,
    onSurface = NpaNavy,
    surfaceVariant = NpaLavenderSoft,
    onSurfaceVariant = NpaNavy,
    outline = NpaNavy
)

@Composable
fun NpaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NpaLightColorScheme,
        typography = NpaTypography,
        content = content
    )
}

object NpaBitmapCache {
    // 24 MB in-memory bitmap cache (downsampled bitmaps total <8 MB across entire app)
    private val cache = object : LruCache<String, ImageBitmap>(24 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap): Int {
            return ((value.width.toLong() * value.height.toLong() * 4L) / 1024L).toInt().coerceAtLeast(1)
        }
    }

    fun get(resId: Int, maxDimPx: Int): ImageBitmap? = cache.get("$resId@$maxDimPx")

    fun decodeAndCache(resources: Resources, resId: Int, maxDimPx: Int): ImageBitmap? {
        val key = "$resId@$maxDimPx"
        cache.get(key)?.let { return it }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(resources, resId, bounds)
        val srcW = bounds.outWidth
        val srcH = bounds.outHeight
        if (srcW <= 0 || srcH <= 0) return null

        var inSampleSize = 1
        while (srcW / (inSampleSize * 2) >= maxDimPx && srcH / (inSampleSize * 2) >= maxDimPx) {
            inSampleSize *= 2
        }
        // Keep high quality without exceeding maxDimPx * 1.35 to prevent multi-4K OOM / GPU stalls
        while ((srcW / inSampleSize) > (maxDimPx * 1.4f) || (srcH / inSampleSize) > (maxDimPx * 1.4f)) {
            inSampleSize *= 2
        }

        val decodeOpts = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize.coerceAtLeast(1)
            this.inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val decoded = BitmapFactory.decodeResource(resources, resId, decodeOpts) ?: return null
        val imageBitmap = decoded.asImageBitmap()
        cache.put(key, imageBitmap)
        return imageBitmap
    }
}

/**
 * Loads an appropriately downsampled ImageBitmap off the Main Thread (with instant LruCache hit)
 * and renders via BitmapPainter(FilterQuality.High) for sharp visuals without UI thread stalls or OOM.
 */
@Composable
fun rememberCrispBitmapPainter(
    @DrawableRes resId: Int,
    maxDimPx: Int = 720,
    placeholderColor: Color = NpaCream
): Painter {
    val resources = LocalContext.current.resources
    val cached = remember(resId, maxDimPx) { NpaBitmapCache.get(resId, maxDimPx) }
    val bitmap by produceState(initialValue = cached, resId, maxDimPx) {
        if (value == null) {
            value = withContext(Dispatchers.IO) {
                NpaBitmapCache.decodeAndCache(resources, resId, maxDimPx)
            }
        }
    }
    val resolved = bitmap
    return remember(resolved, placeholderColor) {
        if (resolved != null) {
            BitmapPainter(resolved, filterQuality = FilterQuality.High)
        } else {
            ColorPainter(placeholderColor)
        }
    }
}

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    containerColor: Color = NpaWhite,
    gradient: Brush? = null,
    borderColor: Color = NpaNavy,
    borderWidth: Dp = 2.5.dp,
    cornerRadius: Dp = 20.dp,
    shadowOffset: Dp = 4.dp,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val shadowColor = remember { NpaNavy.copy(alpha = 0.18f) }

    Box(
        modifier = modifier
            .then(
                if (shadowOffset > 0.dp) {
                    Modifier.drawBehind {
                        val offsetPx = shadowOffset.toPx()
                        val radiusPx = cornerRadius.toPx()
                        drawRoundRect(
                            color = shadowColor,
                            topLeft = Offset(offsetPx, offsetPx),
                            size = size,
                            cornerRadius = CornerRadius(radiusPx, radiusPx)
                        )
                    }
                } else Modifier
            )
            .clip(shape)
            .then(
                if (gradient != null) Modifier.background(gradient)
                else Modifier.background(containerColor)
            )
            .border(borderWidth, borderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = NpaCoral,
    contentColor: Color = NpaNavy,
    borderColor: Color = NpaNavy,
    shape: Shape = CircleShape,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit
) {
    val shadowColor = remember(enabled) { NpaNavy.copy(alpha = if (enabled) 0.20f else 0.08f) }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        contentColor = contentColor,
        border = BorderStroke(2.5.dp, borderColor),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .drawBehind {
                val offsetPx = 3.dp.toPx()
                val radiusPx = if (shape == CircleShape) size.height / 2f else 16.dp.toPx()
                drawRoundRect(
                    color = shadowColor,
                    topLeft = Offset(offsetPx, offsetPx),
                    size = size,
                    cornerRadius = CornerRadius(radiusPx, radiusPx)
                )
            }
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun NeoPill(
    modifier: Modifier = Modifier,
    containerColor: Color = NpaWhite.copy(alpha = 0.7f),
    borderColor: Color = NpaNavy,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .border(2.dp, borderColor, CircleShape)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
