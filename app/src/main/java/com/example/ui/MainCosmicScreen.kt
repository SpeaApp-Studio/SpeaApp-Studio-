package com.example.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CelestialCategory
import com.example.ui.components.CelestialDetailDialog
import com.example.ui.components.CosmicBubbleCanvas
import com.example.ui.components.CreditsDialog
import com.example.ui.components.ExitConfirmDialog
import com.example.ui.components.FreeCatalogDialog
import com.example.ui.components.PremiumSubscriptionDialog
import com.example.ui.components.SideMenuPanel
import com.example.ui.components.UniverseInfoDialog
import com.example.ui.theme.CyanCosmic
import com.example.ui.theme.OrangeCosmic
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.SpaceBorder
import com.example.ui.theme.SpaceCardBg
import com.example.ui.theme.SpaceCardElevated
import com.example.ui.theme.SpacePanelBg
import com.example.ui.theme.StellarWhite
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCosmicScreen(
    viewModel: CosmicViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showQuickSettingsSheet by remember { mutableStateOf(false) }

    val layoutDirection = if (uiState.language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(SpaceBlack)) {
            val isWideScreen = maxWidth >= 680.dp

            if (isWideScreen) {
                // Dual pane layout matching the HTML web container (Side Panel 32% + Main Display 68%)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                ) {
                    SideMenuPanel(
                        texts = uiState.texts,
                        currentLanguage = uiState.language,
                        musicVolume = uiState.musicVolume,
                        sfxVolume = uiState.sfxVolume,
                        isMusicPlaying = uiState.isMusicPlaying,
                        dailyCount = uiState.dailyExplorationCount,
                        onLanguageSelect = { viewModel.changeLanguage(it) },
                        onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                        onSfxVolumeChange = { viewModel.setSfxVolume(it) },
                        onToggleMusic = { viewModel.toggleMusic() },
                        onOpenDialog = { viewModel.openDialog(it) },
                        modifier = Modifier.width(320.dp)
                    )

                    CosmicMainDisplay(
                        viewModel = viewModel,
                        uiState = uiState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            } else {
                // Mobile layout with drawer & quick action top bar
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = SpacePanelBg,
                            modifier = Modifier.width(320.dp)
                        ) {
                            SideMenuPanel(
                                texts = uiState.texts,
                                currentLanguage = uiState.language,
                                musicVolume = uiState.musicVolume,
                                sfxVolume = uiState.sfxVolume,
                                isMusicPlaying = uiState.isMusicPlaying,
                                dailyCount = uiState.dailyExplorationCount,
                                onLanguageSelect = {
                                    viewModel.changeLanguage(it)
                                    coroutineScope.launch { drawerState.close() }
                                },
                                onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                                onSfxVolumeChange = { viewModel.setSfxVolume(it) },
                                onToggleMusic = { viewModel.toggleMusic() },
                                onOpenDialog = { dialog ->
                                    viewModel.openDialog(dialog)
                                    coroutineScope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = uiState.texts.title,
                                            color = CyanCosmic,
                                            fontSize = 19.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = Color(0xFFFF5722),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Alpha",
                                                color = StellarWhite,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { coroutineScope.launch { drawerState.open() } },
                                        modifier = Modifier.testTag("btn_open_drawer")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menu",
                                            tint = CyanCosmic
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = { showQuickSettingsSheet = true },
                                        modifier = Modifier.testTag("btn_quick_settings")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = "Settings",
                                            tint = StellarWhite
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = SpaceBlack,
                                    titleContentColor = StellarWhite
                                )
                            )
                        },
                        containerColor = SpaceBlack
                    ) { innerPadding ->
                        CosmicMainDisplay(
                            viewModel = viewModel,
                            uiState = uiState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }

            // Quick Settings Modal BottomSheet for Mobile
            if (showQuickSettingsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showQuickSettingsSheet = false },
                    containerColor = SpaceCardBg,
                    sheetState = rememberModalBottomSheetState()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = uiState.texts.changeLanguage,
                            color = CyanCosmic,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            com.example.data.model.AppLanguage.values().forEach { lang ->
                                val isSelected = uiState.language == lang
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CyanCosmic else Color(0xFF252538))
                                        .clickable { viewModel.changeLanguage(lang) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang.displayName,
                                        color = if (isSelected) Color.Black else StellarWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = uiState.texts.interstellarMusic,
                            color = StellarWhite,
                            fontSize = 13.sp
                        )
                        androidx.compose.material3.Slider(
                            value = uiState.musicVolume.toFloat(),
                            onValueChange = { viewModel.setMusicVolume(it.toInt()) },
                            valueRange = 0f..100f
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = uiState.texts.soundFx,
                            color = StellarWhite,
                            fontSize = 13.sp
                        )
                        androidx.compose.material3.Slider(
                            value = uiState.sfxVolume.toFloat(),
                            onValueChange = { viewModel.setSfxVolume(it.toInt()) },
                            valueRange = 0f..100f
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Dialogs
            when (uiState.activeDialog) {
                ActiveDialogType.FREE_CATALOG -> {
                    FreeCatalogDialog(
                        texts = uiState.texts,
                        onDismiss = { viewModel.closeDialog() }
                    )
                }
                ActiveDialogType.PREMIUM_SUB -> {
                    PremiumSubscriptionDialog(
                        texts = uiState.texts,
                        isSupporterUnlocked = uiState.isSupporterUnlocked,
                        onUnlockDemo = { viewModel.activateSupporterDemo() },
                        onDismiss = { viewModel.closeDialog() }
                    )
                }
                ActiveDialogType.UNIVERSE_INFO -> {
                    UniverseInfoDialog(
                        texts = uiState.texts,
                        onDismiss = { viewModel.closeDialog() }
                    )
                }
                ActiveDialogType.CREDITS -> {
                    CreditsDialog(
                        texts = uiState.texts,
                        onDismiss = { viewModel.closeDialog() }
                    )
                }
                ActiveDialogType.EXIT_CONFIRM -> {
                    ExitConfirmDialog(
                        texts = uiState.texts,
                        onConfirmExit = {
                            viewModel.closeDialog()
                            (context as? Activity)?.finish()
                        },
                        onDismiss = { viewModel.closeDialog() }
                    )
                }
                ActiveDialogType.CELESTIAL_DETAIL -> {
                    uiState.selectedCelestialObject?.let { obj ->
                        CelestialDetailDialog(
                            obj = obj,
                            texts = uiState.texts,
                            language = uiState.language,
                            isBookmarked = uiState.bookmarkedIds.contains(obj.id),
                            onBookmarkToggle = { viewModel.toggleBookmark(obj.id) },
                            onPlayFrequency = { viewModel.playObjectFrequency(it) },
                            onDismiss = { viewModel.closeDialog() }
                        )
                    }
                }
                null -> {}
            }
        }
    }
}

@Composable
private fun CosmicMainDisplay(
    viewModel: CosmicViewModel,
    uiState: CosmicUiState,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val filteredObjects = viewModel.getFilteredObjects()

    Box(
        modifier = modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF101020),
                        Color(0xFF05050A)
                    )
                )
            )
            .testTag("main_display")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Search Bar & Filter Row (Matching the .search-box in original HTML)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = uiState.texts.searchPlaceholder,
                            color = TextDim,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = CyanCosmic,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF161622),
                        unfocusedContainerColor = Color(0xFF161622),
                        focusedBorderColor = CyanCosmic,
                        unfocusedBorderColor = SpaceBorder,
                        focusedTextColor = StellarWhite,
                        unfocusedTextColor = StellarWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("searchBar")
                )
            }

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterCategoryChip(
                    title = uiState.texts.categoryAll,
                    isSelected = uiState.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) }
                )
                FilterCategoryChip(
                    title = uiState.texts.categoryStars,
                    isSelected = uiState.selectedCategory == CelestialCategory.STAR,
                    color = OrangeCosmic,
                    onClick = { viewModel.selectCategory(CelestialCategory.STAR) }
                )
                FilterCategoryChip(
                    title = uiState.texts.categoryPlanets,
                    isSelected = uiState.selectedCategory == CelestialCategory.PLANET,
                    color = CyanCosmic,
                    onClick = { viewModel.selectCategory(CelestialCategory.PLANET) }
                )
                FilterCategoryChip(
                    title = uiState.texts.categoryBlackHoles,
                    isSelected = uiState.selectedCategory == CelestialCategory.BLACK_HOLE,
                    color = Color(0xFFFF5252),
                    onClick = { viewModel.selectCategory(CelestialCategory.BLACK_HOLE) }
                )
                FilterCategoryChip(
                    title = uiState.texts.categoryGalaxies,
                    isSelected = uiState.selectedCategory == CelestialCategory.GALAXY,
                    color = Color(0xFFB388FF),
                    onClick = { viewModel.selectCategory(CelestialCategory.GALAXY) }
                )
                FilterCategoryChip(
                    title = uiState.texts.categoryNebulae,
                    isSelected = uiState.selectedCategory == CelestialCategory.NEBULA,
                    color = CyanCosmic,
                    onClick = { viewModel.selectCategory(CelestialCategory.NEBULA) }
                )
            }

            // Center Cosmic Canvas containing the Observable Universe Bubble and luminous points
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CosmicBubbleCanvas(
                    objects = filteredObjects,
                    language = uiState.language,
                    onObjectClick = { obj -> viewModel.onCelestialObjectTapped(obj) },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom subtle instruction
            Text(
                text = "✨ انقر على أي نقطة زرقاء أو برتقالية لاستكشاف تفاصيل الجرم والتردد الكوني",
                color = TextDim,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun FilterCategoryChip(
    title: String,
    isSelected: Boolean,
    color: Color = CyanCosmic,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF161622),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) color else SpaceBorder
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            color = if (isSelected) color else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
