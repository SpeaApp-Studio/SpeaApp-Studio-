package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.CelestialCategory
import com.example.data.model.CelestialObject
import com.example.data.model.LocalizedUiText
import com.example.ui.theme.CyanCosmic
import com.example.ui.theme.OrangeCosmic
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.StellarWhite
import com.example.ui.theme.TextMuted

@Composable
fun CelestialDetailDialog(
    obj: CelestialObject,
    texts: LocalizedUiText,
    language: AppLanguage,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onPlayFrequency: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val pointColor = Color(obj.pointColorHex)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SpaceCardBg),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, pointColor.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("celestial_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with glowing point, title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Glowing celestial emblem
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(pointColor, pointColor.copy(alpha = 0.2f))
                                    )
                                )
                                .border(1.dp, StellarWhite.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(StellarWhite)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = obj.getName(language),
                                color = StellarWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            val categoryText = when (obj.category) {
                                CelestialCategory.STAR -> texts.categoryStars
                                CelestialCategory.PLANET -> texts.categoryPlanets
                                CelestialCategory.BLACK_HOLE -> texts.categoryBlackHoles
                                CelestialCategory.GALAXY -> texts.categoryGalaxies
                                CelestialCategory.NEBULA -> texts.categoryNebulae
                            }
                            Text(
                                text = categoryText,
                                color = pointColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = texts.close,
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Cards (Distance, Size, Mass)
                StatRow(
                    icon = Icons.Default.Public,
                    label = texts.distance,
                    value = obj.getDistance(language),
                    accentColor = CyanCosmic
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatRow(
                    icon = Icons.Default.Straighten,
                    label = texts.size,
                    value = obj.getSize(language),
                    accentColor = OrangeCosmic
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatRow(
                    icon = Icons.Default.Scale,
                    label = texts.mass,
                    value = obj.getMass(language),
                    accentColor = pointColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Astronomical Description
                Text(
                    text = texts.physicsFact,
                    color = CyanCosmic,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = SpaceCardElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder)
                ) {
                    Text(
                        text = obj.getDescription(language),
                        color = StellarWhite.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // "What If?" Cosmic scenario card
                Text(
                    text = texts.whatIf,
                    color = OrangeCosmic,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = SpaceCardElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangeCosmic.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = obj.getWhatIf(language),
                        color = StellarWhite.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Resonant Frequency player button
                FilledTonalButton(
                    onClick = { onPlayFrequency(obj.resonantFrequencyHz) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_play_frequency"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CyanCosmic.copy(alpha = 0.15f),
                        contentColor = CyanCosmic
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${texts.resonantTone} (${obj.resonantFrequencyHz.toInt()} Hz)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action buttons: Bookmark + Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_bookmark"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isBookmarked) OrangeCosmic else SpaceBorder
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isBookmarked) OrangeCosmic else StellarWhite
                        )
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBookmarked) texts.favorited else texts.favorite,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_close_dialog"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                    ) {
                        Text(text = texts.close, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: Color
) {
    Surface(
        color = SpaceCardElevated,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = value,
                    color = StellarWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
