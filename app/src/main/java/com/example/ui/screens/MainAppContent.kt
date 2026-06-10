package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Bookmark
import com.example.data.local.ImportedDoc
import com.example.data.local.JournalEntry
import com.example.data.model.Chapter
import com.example.data.model.Scripture
import com.example.data.model.StaticScriptureProvider
import com.example.data.model.Verse
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemePreset
import com.example.ui.theme.LocalThemeManager
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

// Simple safe enum for Hamburger Menu Drawer routing
enum class DrawerScreen(val displayName: String) {
    HOME("Home"),
    SCRIPTURES("Scriptures"),
    DAILY_WISDOM("Daily Wisdom"),
    MEDITATION_TIMER("Prayer & Meditation"),
    JOURNAL("Wisdom Journal"),
    IMPORTS("Imported Documents"),
    EXPORTS("Export Center"),
    SETTINGS("Personalization Settings"),
    ABOUT("About Hinduss")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: MainViewModel,
    onboardingCompleted: Boolean,
    currentLang: String,
    themePreset: ThemePreset,
    themeMode: ThemeMode
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Active navigational states
    var currentScreen by remember { mutableStateOf(DrawerScreen.HOME) }
    var activeScriptureForReader by remember { mutableStateOf<Scripture?>(null) }
    var activeChapterForReader by remember { mutableStateOf<Chapter?>(null) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Check language texts helper
    fun translate(en: String, hi: String, ne: String): String {
        return when (currentLang) {
            "hi" -> hi
            "ne" -> ne
            else -> en
        }
    }

    if (!onboardingCompleted) {
        OnboardingScreen(viewModel = viewModel, currentLang = currentLang, onTranslate = { en, hi, ne -> translate(en, hi, ne) })
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(310.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    
                    // App Branding Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ॐ",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Hinduss",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Serif,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = translate(
                                    "Your Sacred Spiritual Companion",
                                    "आपका पवित्र आध्यात्मिक साथी",
                                    "तपाईंको पवित्र आध्यात्मिक साथी"
                                ),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = androidx.compose.ui.text.TextStyle(fontStyle = FontStyle.Italic)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    // Drawer Options
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(DrawerScreen.values()) { screen ->
                            val isSelected = currentScreen == screen
                            val icon = when (screen) {
                                DrawerScreen.HOME -> Icons.Default.Home
                                DrawerScreen.SCRIPTURES -> Icons.Default.Book
                                DrawerScreen.DAILY_WISDOM -> Icons.Default.AutoAwesome
                                DrawerScreen.MEDITATION_TIMER -> Icons.Default.Timer
                                DrawerScreen.JOURNAL -> Icons.Default.EditNote
                                DrawerScreen.IMPORTS -> Icons.Default.CloudUpload
                                DrawerScreen.EXPORTS -> Icons.Default.FileDownload
                                DrawerScreen.SETTINGS -> Icons.Default.Settings
                                DrawerScreen.ABOUT -> Icons.Default.Info
                            }

                            NavigationDrawerItem(
                                icon = { Icon(imageVector = icon, contentDescription = screen.displayName) },
                                label = {
                                    Text(
                                        text = when (screen) {
                                            DrawerScreen.HOME -> translate("Home Dashboard", "होम डैशबोर्ड", "गृह ड्यासबोर्ड")
                                            DrawerScreen.SCRIPTURES -> translate("Scripture Library", "शास्त्र पुस्तकालय", "शास्त्र पुस्तकालय")
                                            DrawerScreen.DAILY_WISDOM -> translate("Daily Wisdom", "दैनिक ज्ञान", "दैनिक ज्ञान")
                                            DrawerScreen.MEDITATION_TIMER -> translate("Prayer & Meditation", "प्रार्थना और ध्यान", "प्रार्थना र ध्यान")
                                            DrawerScreen.JOURNAL -> translate("Personal Journal", "मेरा जर्नल", "मेरो जर्नल")
                                            DrawerScreen.IMPORTS -> translate("Import Center", "दस्तावेज़ आयात", "कागजात आयात")
                                            DrawerScreen.EXPORTS -> translate("Export Hub", "निर्यात केंद्र", "निर्यात केन्द्र")
                                            DrawerScreen.SETTINGS -> translate("Personalization", "थीम और सेटिंग्स", "थीम र सेटिंग्स")
                                            DrawerScreen.ABOUT -> translate("About & Author", "हमारे बारे में", "हाम्रो बारेमा")
                                        },
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    currentScreen = screen
                                    // Reset active reader if switching screeen
                                    if (screen != DrawerScreen.SCRIPTURES) {
                                        activeScriptureForReader = null
                                        activeChapterForReader = null
                                    }
                                    coroutineScope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    unselectedContainerColor = Color.Transparent,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .testTag("drawer_item_${screen.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = when {
                                    activeChapterForReader != null -> activeScriptureForReader?.let { translate(it.titleEn, it.titleHi, it.titleNe) } ?: "Reader"
                                    else -> when (currentScreen) {
                                        DrawerScreen.HOME -> translate("Hinduss", "हिंदुस", "हिन्दुस")
                                        DrawerScreen.SCRIPTURES -> translate("Scriptures", "धर्मग्रंथ पुस्तकालय", "धर्मग्रन्थ")
                                        DrawerScreen.DAILY_WISDOM -> translate("Daily Wisdom", "दैनिक ज्ञान धारा", "दैनिक ज्ञान")
                                        DrawerScreen.MEDITATION_TIMER -> translate("Prayer Center", "प्रार्थना और ध्यान", "प्रार्थना र ध्यान")
                                        DrawerScreen.JOURNAL -> translate("Thoughts Journal", "ज्ञान जर्नल", "ज्ञान जर्नल")
                                        DrawerScreen.IMPORTS -> translate("Import Center", "दस्तावेज़ आयात", "आयात केन्द्र")
                                        DrawerScreen.EXPORTS -> translate("Export Data", "निर्यात केंद्र", "निर्यात केन्द्र")
                                        DrawerScreen.SETTINGS -> translate("Divine Harmony Theme", "थीम अनुकूलन", "थीम सेटिंग्स")
                                        DrawerScreen.ABOUT -> translate("About Author", "लेखक के बारे में", "हाम्रो बारेमा")
                                    }
                                },
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            if (activeChapterForReader != null) {
                                IconButton(onClick = { activeChapterForReader = null }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            } else {
                                IconButton(
                                    onClick = { coroutineScope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("drawer_hamburger_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Open menu")
                                }
                            }
                        },
                        actions = {
                            // Quick Action language switch
                            IconButton(onClick = {
                                val nextLang = when (currentLang) {
                                    "en" -> "hi"
                                    "hi" -> "ne"
                                    else -> "en"
                                }
                                viewModel.onLanguageSelected(nextLang)
                                Toast.makeText(context, "Language: $nextLang", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (activeChapterForReader != null && activeScriptureForReader != null) {
                        AdvancedReaderScreen(
                            viewModel = viewModel,
                            scripture = activeScriptureForReader!!,
                            chapter = activeChapterForReader!!,
                            currentLang = currentLang,
                            onClose = { activeChapterForReader = null },
                            onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                        )
                    } else {
                        when (currentScreen) {
                            DrawerScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToScreen = { screen -> currentScreen = screen },
                                onReadChapter = { scripture, chapter ->
                                    activeScriptureForReader = scripture
                                    activeChapterForReader = chapter
                                },
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.SCRIPTURES -> ScriptureLibraryScreen(
                                viewModel = viewModel,
                                onReadChapter = { scripture, chapter ->
                                    activeScriptureForReader = scripture
                                    activeChapterForReader = chapter
                                },
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.DAILY_WISDOM -> DailyWisdomScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.MEDITATION_TIMER -> MeditationTimerScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.JOURNAL -> JournalScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.IMPORTS -> DocumentsImportScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.EXPORTS -> ExportCenterScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                            DrawerScreen.ABOUT -> AboutScreen(
                                viewModel = viewModel,
                                onTranslate = { en, hi, ne -> translate(en, hi, ne) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ONBOARDING SCREEN
// ==========================================
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    currentLang: String,
    onTranslate: (String, String, String) -> String
) {
    var selectedPreset by remember { mutableStateOf(ThemePreset.NATURAL_TONES) }
    var selectedMode by remember { mutableStateOf(ThemeMode.LIGHT) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "ॐ",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hinduss",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = onTranslate(
                    "Welcome to Divine Harmony UI",
                    "दिव्य हार्मनी यूआई में आपका स्वागत है",
                    "दिव्य हार्मोनी यूआई मा स्वागत छ"
                ),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = onTranslate(
                    "Tailor your sacred reading and meditation environment instantly.",
                    "अपने पवित्र पठन और ध्यान वातावरण को तुरंत अनुकूलित करें।",
                    "आफ्नो पवित्र अध्ययन र ध्यान वातावरण तुरुन्तै मिलाउनुहोस्।"
                ),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Language quick select
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose App Language / भाषा चुनिए:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("en" to "English", "hi" to "हिंदी", "ne" to "नेपाली").forEach { (code, label) ->
                            val isSelected = currentLang == code
                            Button(
                                onClick = { viewModel.onLanguageSelected(code) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Theme Preset select
        item {
            Text(
                text = onTranslate("Select Spiritual Color Theme:", "आध्यात्मिक रंग विषय चुनें:", "आध्यात्मिक रंग विषय रोज्नुहोस्:"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }

        item {
            // Flow Row simulation
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ThemePreset.values().toList().chunked(2).forEach { rowPresets ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowPresets.forEach { preset ->
                            val isSelected = selectedPreset == preset
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedPreset = preset
                                        viewModel.onThemeSelected(preset)
                                    },
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (preset) {
                                                    ThemePreset.NATURAL_TONES -> Color(0xFFFF9933)
                                                    ThemePreset.SACRED_SAFFRON -> Color(0xFFE65100)
                                                    ThemePreset.LOTUS_PINK -> Color(0xFFD81B60)
                                                    ThemePreset.TULSI_GREEN -> Color(0xFF1B5E20)
                                                    ThemePreset.GANGA_BLUE -> Color(0xFF01579B)
                                                    ThemePreset.HIMALAYAN_WHITE -> Color(0xFF607D8B)
                                                    ThemePreset.TEMPLE_SAND -> Color(0xFF4E342E)
                                                    ThemePreset.DIVINE_PURPLE -> Color(0xFF4A148C)
                                                    ThemePreset.PEACOCK_BLUE -> Color(0xFF004D40)
                                                    ThemePreset.GOLDEN_DHARMA -> Color(0xFFF57F17)
                                                    ThemePreset.SUNSET_PRAYER -> Color(0xFFD84315)
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = preset.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ambient Mode select
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = onTranslate("Screen Mode preset:", "स्क्रीन चमक मोड:", "स्क्रिन मोडहरू:"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isSelected = selectedMode == mode
                            Button(
                                onClick = {
                                    selectedMode = mode
                                    viewModel.onThemeModeSelected(mode)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mode.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.completeOnboarding() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("begin_journey_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = onTranslate("Begin Your Sacred Journey", "अपनी पवित्र यात्रा प्रारंभ करें", "आफ्नो पवित्र यात्रा सुरु गर्नुहोस्"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ==========================================
// HOME DASHBOARD
// ==========================================
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToScreen: (DrawerScreen) -> Unit,
    onReadChapter: (Scripture, Chapter) -> Unit,
    onTranslate: (String, String, String) -> String
) {
    val stats by viewModel.stats.collectAsState()
    val history by viewModel.readingHistory.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    // Random wisdom trigger for Hero Section
    val verseOfTheDay = StaticScriptureProvider.scriptures[0].chapters[1].verses[1] // Bhagavad Gita 2.47
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = onTranslate("Namaste & Welcome", "नमस्ते और स्वागत है", "नमस्ते र स्वागत छ"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = onTranslate("Divine Harmony", "दिव्य सामंजस्य", "दिव्य सामञ्जस्य"),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Serif,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "ॐ", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Streak Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFFF6D00),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = onTranslate(
                                    "Reading Streak: ${stats.daysReadStreak} Days",
                                    "पठन निरंतरता: ${stats.daysReadStreak} दिन",
                                    "अध्ययन निरन्तरता: ${stats.daysReadStreak} दिन"
                                ),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = onTranslate(
                                    "Continue seeking wisdom everyday to develop good habits.",
                                    "अच्छी आदतें विकसित करने के लिए प्रतिदिन ज्ञान की खोज करें।",
                                    "राम्रो बानी बिकास गर्न दैनिक ज्ञानको खोजी गरौं।"
                                ),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = onTranslate("Quick Actions", "त्वरित विकल्प", "क्विक अप्सन"),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val actions = listOf(
                        Triple(Icons.Default.Book, onTranslate("Scriptures", "धर्मग्रंथ", "शास्त्र"), DrawerScreen.SCRIPTURES),
                        Triple(Icons.Default.Timer, onTranslate("Timer", "ध्यानtimer", "ध्यान समय"), DrawerScreen.MEDITATION_TIMER),
                        Triple(Icons.Default.EditNote, onTranslate("Journal", "जर्नल", "जर्नल"), DrawerScreen.JOURNAL),
                        Triple(Icons.Default.CloudUpload, onTranslate("Import", "आयात", "भित्र्याउनु"), DrawerScreen.IMPORTS)
                    )

                    actions.forEach { (icon, label, screen) ->
                        Card(
                            onClick = { onNavigateToScreen(screen) },
                            modifier = Modifier
                                .weight(1f)
                                .height(85.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }

        // Verse of the Day Card
        item {
            val themePreset = LocalThemeManager.current.currentPreset
            val isNaturalTones = themePreset == ThemePreset.NATURAL_TONES
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNaturalTones) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = if (isNaturalTones) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                val backgroundModifier = if (isNaturalTones) {
                    Modifier.background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(0xFFFF9933), Color(0xFFFF6B35))
                        )
                    )
                } else Modifier

                Column(modifier = Modifier.then(backgroundModifier).padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Featured",
                                tint = if (isNaturalTones) Color.White else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = onTranslate("Verse of the Day", "आज का अनमोल श्लोक", "आजको विशेष श्लोक"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNaturalTones) Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "BG 2.47",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNaturalTones) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = onTranslate(verseOfTheDay.textEn, verseOfTheDay.textHi, verseOfTheDay.textNe),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = if (isNaturalTones) Color.White else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = onTranslate(verseOfTheDay.translationEn, verseOfTheDay.translationHi, verseOfTheDay.translationNe),
                        fontSize = 13.sp,
                        color = if (isNaturalTones) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            // Navigation to Gita chapter 2
                            val gita = StaticScriptureProvider.scriptures[0]
                            val ch2 = gita.chapters[1]
                            onReadChapter(gita, ch2)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNaturalTones) Color.White else MaterialTheme.colorScheme.primary,
                            contentColor = if (isNaturalTones) Color(0xFFFF6B35) else MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = onTranslate("Read Full Chapter", "पूरा अध्याय पढ़ें", "पूरा अध्याय पढ्नुहोस्"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Streak & Meditation Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = onTranslate("Spiritual Practice Milestones", "योग और ध्यान उपलब्धियां", "योग र ध्यान उपलब्धिहरू"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${stats.meditationMinutes}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = onTranslate("Minutes", "कुल मिनट", "कुल मिनेट"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${stats.meditationSessions}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = onTranslate("Sessions", "कुल सत्र", "सत्रहरू"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${bookmarks.size}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = onTranslate("Bookmarks", "बुकमार्क", "बुकमार्क"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = onTranslate("Unlocked Achievements / Badges:", "अर्जित बैज / उपलब्धियां:", "अर्जित ब्याजहरू:"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        stats.badges.forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = "Badge",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Continue reading section
        if (history.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = onTranslate("Continue Reading", "पठन जारी रखें", "अध्ययन जारी राख्नुहोस्"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    history.forEach { progress ->
                        val scripture = StaticScriptureProvider.scriptures.find { it.id == progress.scriptureId }
                        if (scripture != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    val targetCh = scripture.chapters.find { it.number == progress.lastChapter } ?: scripture.chapters[0]
                                    onReadChapter(scripture, targetCh)
                                },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Bookmark", tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = onTranslate(scripture.titleEn, scripture.titleHi, scripture.titleNe), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = "Chapter ${progress.lastChapter}, Verse ${progress.lastVerse}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Static Spiritual Calm Tip
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "“Dharmo Rakshati Rakshitah”",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Dharma protects those who walk in alignment with it.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ==========================================
// SCRIPTURES SCREEN
// ==========================================
@Composable
fun ScriptureLibraryScreen(
    viewModel: MainViewModel,
    onReadChapter: (Scripture, Chapter) -> Unit,
    onTranslate: (String, String, String) -> String
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Epic / Smriti", "Vedas", "Devotional Hymn")

    val filteredScriptures = remember(searchQuery, selectedCategory) {
        StaticScriptureProvider.scriptures.filter { s ->
            val matchQuery = s.titleEn.lowercase().contains(searchQuery.lowercase()) ||
                    s.category.lowercase().contains(searchQuery.lowercase())
            val matchCat = selectedCategory == "All" || s.category == selectedCategory
            matchQuery && matchCat
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(text = onTranslate("Search Scriptures, stories...", "शास्त्र, कहानियाँ खोजें...", "शास्त्र, कथाहरू खोज्नुहोस्...")) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        // Horizontal Category Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Bible or scriptures List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredScriptures.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = "None", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.secondary)
                        Text(text = "No scriptures match your search.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            } else {
                items(filteredScriptures) { scripture ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = scripture.category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = "ॐ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = onTranslate(scripture.titleEn, scripture.titleHi, scripture.titleNe),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = onTranslate(scripture.descriptionEn, scripture.descriptionHi, scripture.descriptionNe),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Chapter listings
                            Text(
                                text = onTranslate("Select Chapter / भाग चुनें:", "अध्याय चुनें:", "अध्याय रोज्नुहोस्:"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                scripture.chapters.forEach { chapter ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                            .clickable { onReadChapter(scripture, chapter) }
                                            .padding(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "Ch ${chapter.number}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// ADVANCED READER SCREEN
// ==========================================
@Composable
fun AdvancedReaderScreen(
    viewModel: MainViewModel,
    scripture: Scripture,
    chapter: Chapter,
    currentLang: String,
    onClose: () -> Unit,
    onTranslate: (String, String, String) -> String
) {
    val fontSize by viewModel.fontSize.collectAsState()
    val lineSpacing by viewModel.lineSpacing.collectAsState()
    val fontFamilyName by viewModel.fontFamily.collectAsState()
    val eyeComfortEnabled by viewModel.eyeComfort.collectAsState()
    val readingTheme by viewModel.readingTheme.collectAsState()

    var showConfigPanel by remember { mutableStateOf(false) }

    // Map font name to system Compose families
    val resolvedFontFamily = when (fontFamilyName) {
        "Serif" -> FontFamily.Serif
        "Sans" -> FontFamily.SansSerif
        else -> FontFamily.Monospace
    }

    // Colors mapping based on selected theme
    val readerBackground = when (readingTheme) {
        "Temple Sand" -> Color(0xFFF5EBE6)
        "Mystic Saffron" -> Color(0xFFFFF2E6)
        "Quiet Sepia" -> Color(0xFFF4ECD8)
        "Deep AMOLED" -> Color.Black
        else -> MaterialTheme.colorScheme.background // Soft Paper background
    }

    val readerTextColor = when (readingTheme) {
        "Deep AMOLED" -> Color.White
        else -> Color(0xFF2F211A)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBackground)
    ) {
        // eye comfort warming tint overlay
        if (eyeComfortEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFB74D).copy(alpha = 0.08f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = onTranslate(chapter.titleEn, chapter.titleHi, chapter.titleNe),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = readerTextColor
                )
                Row {
                    IconButton(onClick = { showConfigPanel = !showConfigPanel }) {
                        Icon(imageVector = Icons.Default.FontDownload, contentDescription = "Font settings", tint = readerTextColor)
                    }
                    IconButton(onClick = { onClose() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Exit reader", tint = readerTextColor)
                    }
                }
            }

            HorizontalDivider(color = readerTextColor.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

            // Subtitle
            if (!chapter.descriptionEn.isNullOrEmpty()) {
                Text(
                    text = onTranslate(chapter.descriptionEn, "संक्षिप्त विवरण श्लोक व्याख्या", "संक्षिप्त अध्याय वर्णन श्लोक व्याख्या"),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = readerTextColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Interactive configurations panel overlay
            AnimatedVisibility(visible = showConfigPanel) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "Customize Reader Preferences", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        
                        // Text Size Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Size: ${fontSize.toInt()}sp", fontSize = 12.sp, modifier = Modifier.width(70.dp))
                            Slider(
                                value = fontSize,
                                onValueChange = { viewModel.setFontSize(it) },
                                valueRange = 12f..28f,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Spacing Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Spacing: ${"%.1f".format(lineSpacing)}", fontSize = 12.sp, modifier = Modifier.width(70.dp))
                            Slider(
                                value = lineSpacing,
                                onValueChange = { viewModel.setLineSpacing(it) },
                                valueRange = 1.0f..2.0f,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Font Family select
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Font:", fontSize = 12.sp, modifier = Modifier.width(70.dp))
                            listOf("Serif", "Sans", "Mono").forEach { fn ->
                                val isSelected = fontFamilyName == fn
                                Button(
                                    onClick = { viewModel.setFontFamily(fn) },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = fn, fontSize = 10.sp, color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        // Eye comfort and Reading local themes choice
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Comfort Mask:", fontSize = 11.sp, modifier = Modifier.width(90.dp))
                            Switch(
                                checked = eyeComfortEnabled,
                                onCheckedChange = { viewModel.setEyeComfort(it) }
                            )
                        }

                        // Reading theme selections
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Soft Paper", "Temple Sand", "Mystic Saffron", "Quiet Sepia", "Deep AMOLED").forEach { thm ->
                                val selected = readingTheme == thm
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { viewModel.setReadingTheme(thm) }
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(text = thm, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Color.Unspecified)
                                }
                            }
                        }
                    }
                }
            }

            // Verses reading layout
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chapter.verses) { verse ->
                    var showUserNoteField by remember { mutableStateOf(false) }
                    var noteTextVal by remember { mutableStateOf("") }
                    val isBookmarked by viewModel.isBookmarkedFlow(scripture.id, chapter.number, verse.number).collectAsState(false)
                    val associatedNote by viewModel.getNoteForVerseFlow(scripture.id, chapter.number, verse.number).collectAsState(null)

                    // Update local progress with current verse
                    LaunchedEffect(verse.number) {
                        viewModel.updateReadingProgress(scripture.id, scripture.titleEn, chapter.number, verse.number)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (readingTheme == "Deep AMOLED") Color(0xFF1E1E1E) else Color.White.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Verse ${verse.number}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = readerTextColor.copy(alpha = 0.8f)
                                )
                                Row {
                                    // Bookmark button
                                    IconButton(onClick = {
                                        viewModel.toggleBookmark(
                                            scriptureId = scripture.id,
                                            name = scripture.titleEn,
                                            chNum = chapter.number,
                                            verseNum = verse.number,
                                            body = onTranslate(verse.textEn, verse.textHi, verse.textNe),
                                            trans = onTranslate(verse.translationEn, verse.translationHi, verse.translationNe)
                                        )
                                    }) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (isBookmarked) Color(0xFFFF9100) else readerTextColor
                                        )
                                    }
                                    
                                    // Margin notes button
                                    IconButton(onClick = {
                                        showUserNoteField = !showUserNoteField
                                        associatedNote?.let { noteTextVal = it.noteText }
                                    }) {
                                        Icon(
                                            imageVector = if (associatedNote != null) Icons.Default.EditNote else Icons.Default.StickyNote2,
                                            contentDescription = "Notes",
                                            tint = if (associatedNote != null) MaterialTheme.colorScheme.primary else readerTextColor
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Verse Core Text
                            Text(
                                text = onTranslate(verse.textEn, verse.textHi, verse.textNe),
                                fontSize = fontSize.sp,
                                fontFamily = resolvedFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = readerTextColor,
                                lineHeight = (fontSize * lineSpacing).sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Translation
                            Text(
                                text = onTranslate(verse.translationEn, verse.translationHi, verse.translationNe),
                                fontSize = (fontSize - 3).sp,
                                fontFamily = resolvedFontFamily,
                                color = readerTextColor.copy(alpha = 0.8f),
                                lineHeight = ((fontSize - 3) * lineSpacing).sp
                            )

                            // User marginal Note rendering
                            if (associatedNote != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Check", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "Your Reflection Note:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Text(text = associatedNote!!.noteText, fontSize = 12.sp, fontStyle = FontStyle.Italic)
                                    }
                                }
                            }

                            // Interactive input for verse reflection
                            AnimatedVisibility(visible = showUserNoteField) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    OutlinedTextField(
                                        value = noteTextVal,
                                        onValueChange = { noteTextVal = it },
                                        placeholder = { Text(text = "Reflections, meditations on this verse...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = {
                                                viewModel.saveVerseNote(scripture.id, scripture.titleEn, chapter.number, verse.number, noteTextVal)
                                                showUserNoteField = false
                                            }
                                        ) {
                                            Text(text = "Save Note")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// DAILY WISDOM SCREEN
// ==========================================
@Composable
fun DailyWisdomScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Pick dynamic items for daily quotes
    val dailyQuote = StaticScriptureProvider.dailyWisdomQuotes[0]
    val buddhaThought = StaticScriptureProvider.buddhaThoughts[1]
    val moralStory = StaticScriptureProvider.moralStories[0]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero stack item
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(text = "ॐ", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = onTranslate("Daily Wisdom Stream", "दैनिक ज्ञान धारा", "दैनिक ज्ञान धारा"),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = onTranslate("Meditate upon these scriptures & moral instructions daily.", "प्रतिदिन इन मूल्यों और शिक्षाओं पर ध्यान केंद्रित करें।", "दैनिक रुपमा यी शास्त्र र नैतिक निर्देशनहरूको मनन गर्नुहोस्।"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Quote card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Source", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = onTranslate("Daily Wisdom Quote", "दैनिक अमूल्य विचार", "दैनिक अनमोल वचन"), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = "“${onTranslate(dailyQuote.textEn, dailyQuote.textHi, dailyQuote.textNe)}”",
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— ${onTranslate(dailyQuote.sourceEn, dailyQuote.sourceHi, dailyQuote.sourceNe)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${dailyQuote.textEn} - ${dailyQuote.sourceEn}")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Buddha Thought
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = "Buddha", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Lord Buddha Mindful Thought", fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = "“${onTranslate(buddhaThought.quoteEn, buddhaThought.quoteHi, buddhaThought.quoteNe)}”",
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Life Application / जीवन में उपयोग:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = onTranslate(buddhaThought.applicationEn, buddhaThought.applicationHi, buddhaThought.applicationNe),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Moral Story Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Subject, contentDescription = "Story", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = onTranslate("Spiritual & Moral Story", "सत्य एवं नैतिक कहानी", "नैतिक कथा"), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = onTranslate(moralStory.titleEn, moralStory.titleHi, moralStory.titleNe),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = onTranslate(moralStory.contentEn, moralStory.contentHi, moralStory.contentNe),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Moral Lesson / सीख:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = onTranslate(moralStory.moralEn, moralStory.moralHi, moralStory.moralNe), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PRAYER TIMER & MEDITATION
// ==========================================
@Composable
fun MeditationTimerScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeTimerMinutes by remember { mutableStateOf(10) }
    var selectedMethod by remember { mutableStateOf("Guided Breathing") } // Guided Breathing, Silent Focus, Temple Chants

    var isRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableStateOf(600) }

    // breathing animations
    var breathInstruction by remember { mutableStateOf("Inhale") }
    var breathScaleVal by remember { mutableStateOf(1.0f) }

    LaunchedEffect(activeTimerMinutes) {
        if (!isRunning) {
            secondsLeft = activeTimerMinutes * 60
        }
    }

    // Main countdown timer loop
    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft -= 1
            if (secondsLeft == 0) {
                isRunning = false
                viewModel.addMeditationSession(activeTimerMinutes, selectedMethod)
                Toast.makeText(context, "Sadhana Session Completed! Temple Bell Echoes.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Breathing loop loop
    LaunchedEffect(isRunning, selectedMethod) {
        if (selectedMethod == "Guided Breathing") {
            while (isRunning) {
                // Inhale 4s
                breathInstruction = "Breathe In (Inhale)"
                animate(1.0f, 1.8f, animationSpec = tween(4000, easing = LinearEasing)) { value, _ ->
                    breathScaleVal = value
                }
                delay(1000)
                // Hold 4s
                breathInstruction = "Hold Breath (Kumbhaka)"
                delay(3000)
                // Exhale 4s
                breathInstruction = "Breathe Out (Exhale)"
                animate(1.8f, 1.0f, animationSpec = tween(4000, easing = LinearEasing)) { value, _ ->
                    breathScaleVal = value
                }
                delay(1000)
            }
        } else {
            breathScaleVal = 1.0f
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = onTranslate("Prayer & Sadhana Timer", "प्रार्थना एवं साधना टाइमर", "प्रार्थना र साधना"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = onTranslate("Practice quiet focus to attain tranquil consciousness.", "शांत एकाग्रता और आत्म-नियंत्रण का अभ्यास करें।", "शान्त एकाग्रता र आत्म-नियन्त्रण अभ्यास गरौं।"),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Meditation technique picker
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Guided Breathing", "Silent Focus", "Temple Chants").forEach { tech ->
                    val isSelected = selectedMethod == tech
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedMethod = tech }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tech,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Rounded interactive Timer interface
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Pulse visualization for Breathing Yoga
                Box(
                    modifier = Modifier
                        .size(100.dp * breathScaleVal)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = secondsLeft / 60
                    val seconds = secondsLeft % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isRunning && selectedMethod == "Guided Breathing") {
                        Text(
                            text = breathInstruction,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        Text(
                            text = selectedMethod,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Duration select list
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(5, 10, 15, 20, 30, 45).forEach { mins ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeTimerMinutes == mins) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable {
                                if (!isRunning) {
                                    activeTimerMinutes = mins
                                }
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${mins}M", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Control Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) Color(0xFFD84315) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isRunning) "Pause Session" else "Begin Sadhana Timer",
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        secondsLeft = activeTimerMinutes * 60
                    }
                ) {
                    Text(text = "Reset")
                }
            }
        }
    }
}

// ==========================================
// THOUGHTS JOURNAL SCREEN
// ==========================================
@Composable
fun JournalScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    val journalEntries by viewModel.journalEntries.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteBody by remember { mutableStateOf("") }
    var noteCategory by remember { mutableStateOf("Reflection") } // Reflection, Gratitude, Teachings

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = onTranslate("Wisdom & Reflection Journal", "पवित्र चिंतन डायरी", "ज्ञान र प्रतिबिम्ब डायरी"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = onTranslate("Write your spiritual reflections and logs here.", "आत्म-चिंतन और आभार के विषय में लिखें।", "आफ्नो आध्यात्मिक चिन्तन र आभार यहाँ लेख्नुहोस्।"),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Reflection")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (journalEntries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.EditNote, contentDescription = "Empty", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                        Text(
                            text = "Your Spiritual Journal is empty.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to write down your reflections on Lord's teachings.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(journalEntries) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = entry.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = entry.dateString, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.deleteJournalEntry(entry.id) }) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = entry.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = entry.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(text = "Add Daily Journal Reflection") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text(text = "Reflection Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noteBody,
                        onValueChange = { noteBody = it },
                        label = { Text(text = "What is your main spiritual learn detail...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    
                    Text(text = "Reflection Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Reflection", "Gratitude", "Teachings").forEach { cat ->
                            val selected = noteCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { noteCategory = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = cat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Color.Unspecified)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.trim().isNotEmpty()) {
                            viewModel.addJournalEntry(noteTitle, noteBody, noteCategory)
                            noteTitle = ""
                            noteBody = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text(text = "Save Entry")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

// ==========================================
// DOCUMENTS IMPORT SCREEN
// ==========================================
@Composable
fun DocumentsImportScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    val context = LocalContext.current
    val docs by viewModel.importedDocs.collectAsState()

    var showImportForm by remember { mutableStateOf(false) }
    var importTitle by remember { mutableStateOf("") }
    var importContent by remember { mutableStateOf("") }
    var importType by remember { mutableStateOf("TXT") } // TXT, CSV, PDF, EPUB, DOCX

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = onTranslate("Import Center", "दस्तावेज़ आयात केंद्र", "कागजात आयात"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = onTranslate("Paste scripture files or TXT snippets to read in-app.", "बाहरी गीता पाठ या ज्ञान नोट्स को आयात करें।", "बाहिरी फाइल वा ज्ञान नोट इम्पोर्ट गरि पढौ।"),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showImportForm = !showImportForm },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (showImportForm) "Close Form" else "Import New")
            }
        }

        // Simple Copy Paste Import panel
        AnimatedVisibility(visible = showImportForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Import Snippet or Book Detail", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = importTitle,
                        onValueChange = { importTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Document File Name / Title") }
                    )
                    OutlinedTextField(
                        value = importContent,
                        onValueChange = { importContent = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(text = "Paste text contents of document") },
                        minLines = 4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("TXT", "PDF", "CSV", "EPUB").forEach { t ->
                            val isSel = importType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { importType = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = t, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color.Unspecified)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (importTitle.trim().isNotEmpty() && importContent.trim().isNotEmpty()) {
                                viewModel.importDocument(importTitle, importContent, importType)
                                importTitle = ""
                                importContent = ""
                                showImportForm = false
                                Toast.makeText(context, "Successfully Converted Content!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Convert into Reader experience")
                    }
                }
            }
        }

        // List of Imported items
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (docs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.FolderOpen, contentDescription = "None", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "No imported books yet.", fontWeight = FontWeight.Bold)
                        Text(text = "Paste quotes/Sanskrit notes above to render.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(docs) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Description, contentDescription = "Doc", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = doc.fileName, fontWeight = FontWeight.Bold)
                                }
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = doc.fileType, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { viewModel.deleteImportedDoc(doc.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = doc.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// EXPORT CENTER SCREEN
// ==========================================
@Composable
fun ExportCenterScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    val context = LocalContext.current
    var exportStatusMessage by remember { mutableStateOf("") }

    val bookmarks by viewModel.bookmarks.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()
    val verseNotes by viewModel.verseNotes.collectAsState()

    // Export formats picker
    fun triggerTextExport(format: String) {
        val buildStr = StringBuilder()
        buildStr.append("=== HINDUSS DEVOTIONAL DATA EXPORT ===\n\n")

        buildStr.append("--- FAVORITE BOOKMARKS ---\n")
        bookmarks.forEach {
            buildStr.append("[${it.scriptureName} - Ch ${it.chapter} V ${it.verseNumber}]: ${it.verseText}\n")
        }
        buildStr.append("\n")

        buildStr.append("--- PERSONAL JOURNAL ENTRIES ---\n")
        journalEntries.forEach {
            buildStr.append("[${it.dateString} - ${it.category}]: ${it.title} - ${it.content}\n")
        }

        buildStr.append("\n")
        buildStr.append("--- MARGIN NOTES ---\n")
        verseNotes.forEach {
            buildStr.append("[${it.className} Ch ${it.chapter}]: ${it.noteText}\n")
        }

        try {
            val fileName = "hinduss_exported_data.txt"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use {
                it.write(buildStr.toString().toByteArray())
            }

            // Share local file
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, buildStr.toString())
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export Harmony data via:")
            context.startActivity(shareIntent)

            exportStatusMessage = "Status: Successfully Shared ${format} Export Data!"
        } catch (e: Exception) {
            exportStatusMessage = "Error: " + e.localizedMessage
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = onTranslate("Wisdom Export Center", "डेटा निर्यात केंद्र", "निर्यात केन्द्र"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = onTranslate("Seamlessly backup your saved notes, favorite quotes and reflexions.", "अपने सहेजे गए नोट्स, पसंदीदा उद्धरणों का बैकअप लें।", "तपाईंको सेभ गरिएका टिप्पणी वा उद्धरणहरू ब्याकअप गर्नुहोस्।"),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Backup / Export stats:", fontWeight = FontWeight.Bold)
                    Text(text = "• Bookmarks to Export: ${bookmarks.size}", fontSize = 13.sp)
                    Text(text = "• Journal Reflections: ${journalEntries.size}", fontSize = 13.sp)
                    Text(text = "• Margin Verses Notes: ${verseNotes.size}", fontSize = 13.sp)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Select Export Format:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { triggerTextExport("TXT") }, modifier = Modifier.weight(1f)) {
                        Text(text = "Export as TXT")
                    }
                    Button(onClick = { triggerTextExport("CSV") }, modifier = Modifier.weight(1f)) {
                        Text(text = "Export as CSV")
                    }
                    Button(onClick = { triggerTextExport("PDF") }, modifier = Modifier.weight(1f)) {
                        Text(text = "Export as PDF")
                    }
                }
            }
        }

        if (exportStatusMessage.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp)
                ) {
                    Text(text = exportStatusMessage, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

// ==========================================
// SETTINGS / PERSONALIZATION SCREEN
// ==========================================
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    val preset by viewModel.themePreset.collectAsState()
    val mode by viewModel.themeMode.collectAsState()
    val currentLang by viewModel.lang.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App settings
        item {
            Text(
                text = "Divine Harmony UI Personalization",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Instantly change presets and configurations throughout Hinduss.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Themes Preset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Configure Theme Presets:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    
                    ThemePreset.values().forEach { prs ->
                        val selected = preset == prs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { viewModel.onThemeSelected(prs) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (prs) {
                                                ThemePreset.NATURAL_TONES -> Color(0xFFFF9933)
                                                ThemePreset.SACRED_SAFFRON -> Color(0xFFE65100)
                                                ThemePreset.LOTUS_PINK -> Color(0xFFD81B60)
                                                ThemePreset.TULSI_GREEN -> Color(0xFF1B5E20)
                                                ThemePreset.GANGA_BLUE -> Color(0xFF01579B)
                                                ThemePreset.HIMALAYAN_WHITE -> Color(0xFF607D8B)
                                                ThemePreset.TEMPLE_SAND -> Color(0xFF4E342E)
                                                ThemePreset.DIVINE_PURPLE -> Color(0xFF4A148C)
                                                ThemePreset.PEACOCK_BLUE -> Color(0xFF004D40)
                                                ThemePreset.GOLDEN_DHARMA -> Color(0xFFF57F17)
                                                ThemePreset.SUNSET_PRAYER -> Color(0xFFD84315)
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = prs.displayName, fontSize = 14.sp)
                            }
                            if (selected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Mode choices
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Display Mode Presets:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    ThemeMode.values().forEach { md ->
                        val active = mode == md
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { viewModel.onThemeModeSelected(md) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = md.name, fontSize = 14.sp)
                            if (active) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // Onboarding reset
        item {
            Button(
                onClick = { viewModel.resetOnboarding() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reset App Configurations (Onboarding)")
            }
        }
    }
}

// ==========================================
// ABOUT SCREEN
// ==========================================
@Composable
fun AboutScreen(
    viewModel: MainViewModel,
    onTranslate: (String, String, String) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ॐ",
            fontSize = 62.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "About Hinduss",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Version 1.0.0 (Divine Release)",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "A tranquil Hindu spiritual experience designed to help you integrate scriptures, mindfulness, meditation and moral habits into today's modern life schedules.",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Love", tint = Color.Red)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Made with love by Rahul Shah",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Dedicated to preserving wisdom and cultural heritage.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
