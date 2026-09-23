package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.LocalizedUiText
import com.example.ui.ActiveDialogType
import com.example.ui.theme.CyanCosmic
import com.example.ui.theme.DarkRedBg
import com.example.ui.theme.GoldPremium
import com.example.ui.theme.OrangeCosmic
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedHover
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.SpacePanelBg
import com.example.ui.theme.StellarWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted

@Composable
fun SideMenuPanel(
    texts: LocalizedUiText,
    currentLanguage: AppLanguage,
    musicVolume: Int,
    sfxVolume: Int,
    isMusicPlaying: Boolean,
    dailyCount: Int,
    onLanguageSelect: (AppLanguage) -> Unit,
    onMusicVolumeChange: (Int) -> Unit,
    onSfxVolumeChange: (Int) -> Unit,
    onToggleMusic: () -> Unit,
    onOpenDialog: (ActiveDialogType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SpacePanelBg,
        modifier = modifier
            .fillMaxHeight()
            .testTag("side_menu_panel")
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper Header Zone & Action Menu
            Column {
                // App Title (Cyan, 28px in HTML)
                Text(
                    text = texts.title,
                    color = CyanCosmic,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.testTag("txt_app_title")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Alpha Badge (#ff5722)
                Surface(
                    color = Color(0xFFFF5722),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = texts.badge,
                        color = StellarWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("txt_badge")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Menu Buttons
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Start exploration
                    CosmicMenuButton(
                        text = texts.startExploration,
                        icon = Icons.Default.RocketLaunch,
                        borderColor = SpaceBorder,
                        textColor = StellarWhite,
                        testTag = "btn_start",
                        onClick = { onOpenDialog(ActiveDialogType.FREE_CATALOG) }
                    )

                    // Premium Subscription ($2/month)
                    CosmicMenuButton(
                        text = texts.premiumSub,
                        icon = Icons.Default.Star,
                        borderColor = GoldPremium,
                        textColor = GoldPremium,
                        testTag = "btn_sub",
                        onClick = { onOpenDialog(ActiveDialogType.PREMIUM_SUB) }
                    )

                    // Universe Info
                    CosmicMenuButton(
                        text = texts.universeInfo,
                        icon = Icons.Default.Info,
                        borderColor = SpaceBorder,
                        textColor = StellarWhite,
                        testTag = "btn_info",
                        onClick = { onOpenDialog(ActiveDialogType.UNIVERSE_INFO) }
                    )

                    // Credits
                    CosmicMenuButton(
                        text = texts.credits,
                        icon = Icons.Default.Bookmark,
                        borderColor = SpaceBorder,
                        textColor = StellarWhite,
                        testTag = "btn_credits",
                        onClick = { onOpenDialog(ActiveDialogType.CREDITS) }
                    )

                    // Exit App
                    CosmicMenuButton(
                        text = texts.exitApp,
                        icon = Icons.Default.ExitToApp,
                        containerColor = DarkRedBg,
                        borderColor = RedBorder,
                        textColor = RedHover,
                        testTag = "btn_exit",
                        onClick = { onOpenDialog(ActiveDialogType.EXIT_CONFIRM) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lower Settings Zone (Audio and Languages)
            Surface(
                color = SpaceCardBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Interstellar Music Slider + Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = texts.interstellarMusic,
                            color = StellarWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = onToggleMusic,
                            modifier = Modifier.size(28.dp).testTag("btn_toggle_music")
                        ) {
                            Icon(
                                imageVector = if (isMusicPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                                contentDescription = "Music Toggle",
                                tint = if (isMusicPlaying) CyanCosmic else TextDim,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Slider(
                        value = musicVolume.toFloat(),
                        onValueChange = { onMusicVolumeChange(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanCosmic,
                            activeTrackColor = CyanCosmic,
                            inactiveTrackColor = SpaceBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_music_vol")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // SFX Volume Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = texts.soundFx,
                            color = StellarWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$sfxVolume%",
                            color = OrangeCosmic,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = sfxVolume.toFloat(),
                        onValueChange = { onSfxVolumeChange(it.toInt()) },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = OrangeCosmic,
                            activeTrackColor = OrangeCosmic,
                            inactiveTrackColor = SpaceBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_sfx_vol")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language Selector Pills
                    Text(
                        text = texts.changeLanguage,
                        color = StellarWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = currentLanguage == lang
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) CyanCosmic else Color(0xFF252538))
                                    .clickable { onLanguageSelect(lang) }
                                    .padding(vertical = 6.dp)
                                    .testTag("lang_btn_${lang.code}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang.displayName,
                                    color = if (isSelected) Color.Black else StellarWhite,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Daily Exploration Usage Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 ${texts.dailyCounter} ",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$dailyCount",
                            color = CyanCosmic,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("txt_visit_num")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CosmicMenuButton(
    text: String,
    icon: ImageVector,
    containerColor: Color = Color(0xFF161622),
    borderColor: Color,
    textColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
