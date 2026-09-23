package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.CelestialObject
import com.example.ui.theme.CyanCosmic
import com.example.ui.theme.OrangeCosmic
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.StellarWhite
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun CosmicBubbleCanvas(
    objects: List<CelestialObject>,
    language: AppLanguage,
    onObjectClick: (CelestialObject) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    val starTwinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star_twinkle"
    )

    // Pre-calculated deterministic background star coordinates
    val backgroundStars = remember {
        List(40) { index ->
            val angle = (index * 47.3f) % 360f
            val rad = Math.toRadians(angle.toDouble())
            val dist = 0.15f + ((index * 23) % 75) / 100f
            Pair(
                (0.5f + (cos(rad) * dist * 0.45f)).toFloat(),
                (0.5f + (sin(rad) * dist * 0.45f)).toFloat()
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("cosmic_bubble_container"),
        contentAlignment = Alignment.Center
    ) {
        val availableSize = min(maxWidth.value, maxHeight.value).dp
        val bubbleSize = availableSize * 0.92f

        // Center Cosmic Bubble
        Box(
            modifier = Modifier
                .size(bubbleSize)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x14FFFFFF),
                            Color(0x0600E5FF),
                            Color(0x02000000)
                        )
                    )
                )
                .drawBehind {
                    val radius = size.minDimension / 2f * pulseScale
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Draw outer glowing rim of the observable universe
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                CyanCosmic.copy(alpha = 0.10f * glowAlpha),
                                StellarWhite.copy(alpha = glowAlpha)
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )

                    // Draw outer border ring (The 2px white border from original HTML)
                    drawCircle(
                        color = StellarWhite.copy(alpha = 0.25f + glowAlpha * 0.25f),
                        radius = radius - 2f,
                        center = center,
                        style = Stroke(width = 2.5f)
                    )

                    // Concentric cosmic distance reference rings (Observable horizons)
                    drawCircle(
                        color = Color(0x2200E5FF),
                        radius = radius * 0.66f,
                        center = center,
                        style = Stroke(width = 1f)
                    )
                    drawCircle(
                        color = Color(0x18FFFFFF),
                        radius = radius * 0.33f,
                        center = center,
                        style = Stroke(width = 1f)
                    )

                    // Draw crosshair axes for coordinate orientation
                    drawLine(
                        color = Color(0x15FFFFFF),
                        start = Offset(center.x - radius, center.y),
                        end = Offset(center.x + radius, center.y),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color(0x15FFFFFF),
                        start = Offset(center.x, center.y - radius),
                        end = Offset(center.x, center.y + radius),
                        strokeWidth = 1f
                    )

                    // Draw twinkling background deep stars
                    backgroundStars.forEachIndexed { i, coord ->
                        val alpha = if (i % 2 == 0) starTwinkle else (1.2f - starTwinkle)
                        drawCircle(
                            color = StellarWhite.copy(alpha = (alpha * 0.6f).coerceIn(0.1f, 0.9f)),
                            radius = if (i % 5 == 0) 2.2f else 1.2f,
                            center = Offset(size.width * coord.first, size.height * coord.second)
                        )
                    }
                }
        ) {
            // Render interactive celestial points (The blue and orange points from the prompt)
            objects.forEach { obj ->
                CosmicPointItem(
                    obj = obj,
                    language = language,
                    pulseAlpha = glowAlpha,
                    bubbleWidthPx = bubbleSize.value,
                    bubbleHeightPx = bubbleSize.value,
                    onClick = { onObjectClick(obj) }
                )
            }
        }
    }
}

@Composable
private fun CosmicPointItem(
    obj: CelestialObject,
    language: AppLanguage,
    pulseAlpha: Float,
    bubbleWidthPx: Float,
    bubbleHeightPx: Float,
    onClick: () -> Unit
) {
    val pointColor = Color(obj.pointColorHex)
    var isHovered by remember { mutableStateOf(false) }

    // Position mapping inside the circular bubble:
    // We map obj.bubbleXPercent & obj.bubbleYPercent (0..1) directly to the box
    val xOffset = (obj.bubbleXPercent * bubbleWidthPx).dp - 12.dp
    val yOffset = (obj.bubbleYPercent * bubbleHeightPx).dp - 12.dp

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (obj.bubbleXPercent * bubbleWidthPx * 2.5f).toInt(),
                    y = (obj.bubbleYPercent * bubbleHeightPx * 2.5f).toInt()
                )
            }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        // We use offset relative to center of bubble for reliable cross-screen geometry
    }

    // Direct placement using BoxWithConstraints coordinates
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = xOffset, y = yOffset)
                .size(if (isHovered) 36.dp else 26.dp)
                .clickable { onClick() }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isHovered = true
                            tryAwaitRelease()
                            isHovered = false
                        },
                        onTap = { onClick() }
                    )
                }
                .testTag("point_${obj.id}"),
            contentAlignment = Alignment.Center
        ) {
            // Outer glowing aura
            Box(
                modifier = Modifier
                    .size(if (isHovered) 32.dp else 22.dp)
                    .clip(CircleShape)
                    .background(pointColor.copy(alpha = 0.35f + pulseAlpha * 0.3f))
            )

            // Inner solid luminous core
            Box(
                modifier = Modifier
                    .size(if (isHovered) 16.dp else 12.dp)
                    .clip(CircleShape)
                    .background(pointColor)
            )

            // Small badge label indicator next to key points
            if (isHovered || obj.id == "earth" || obj.id == "stephenson_2_18" || obj.id == "ton_618") {
                Surface(
                    color = SpaceBlack.copy(alpha = 0.85f),
                    shape = CircleShape,
                    modifier = Modifier
                        .offset(y = 18.dp)
                ) {
                    Text(
                        text = obj.getName(language).take(14),
                        color = pointColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
