package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.local.Bookmark
import com.example.data.local.ImportedDoc
import com.example.data.local.JournalEntry
import com.example.data.local.PrayerSession
import com.example.data.local.ReadingProgress
import com.example.data.local.VerseNote
import com.example.data.model.Scripture
import com.example.data.model.StaticScriptureProvider
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("hinduss_prefs", Context.MODE_PRIVATE)

    // --- Onboarding & Settings States ---
    private val _themePreset = MutableStateFlow(
        ThemePreset.valueOf(prefs.getString("theme_preset", ThemePreset.NATURAL_TONES.name) ?: ThemePreset.NATURAL_TONES.name)
    )
    val themePreset: StateFlow<ThemePreset> = _themePreset.asStateFlow()

    private val _themeMode = MutableStateFlow(
        ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _lang = MutableStateFlow(prefs.getString("lang", "en") ?: "en")
    val lang: StateFlow<String> = _lang.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_completed", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    // --- Reader Preference States ---
    private val _fontSize = MutableStateFlow(prefs.getFloat("reader_font_size", 18f))
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    private val _lineSpacing = MutableStateFlow(prefs.getFloat("reader_line_spacing", 1.4f))
    val lineSpacing: StateFlow<Float> = _lineSpacing.asStateFlow()

    private val _fontFamily = MutableStateFlow(prefs.getString("reader_font_family", "Serif") ?: "Serif")
    val fontFamily: StateFlow<String> = _fontFamily.asStateFlow()

    private val _eyeComfort = MutableStateFlow(prefs.getBoolean("reader_eye_comfort", false))
    val eyeComfort: StateFlow<Boolean> = _eyeComfort.asStateFlow()

    private val _readingTheme = MutableStateFlow(prefs.getString("reader_theme", "Soft Paper") ?: "Soft Paper")
    val readingTheme: StateFlow<String> = _readingTheme.asStateFlow()

    // --- Search & Contents States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedScriptureIndex = MutableStateFlow<String?>("gita")
    val selectedScriptureId: StateFlow<String?> = _selectedScriptureIndex.asStateFlow()

    private val _currentChapterNumber = MutableStateFlow(1)
    val currentChapterNumber: StateFlow<Int> = _currentChapterNumber.asStateFlow()

    // --- Room Database Flows ---
    val bookmarks = repository.allBookmarks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val journalEntries = repository.allJournalEntries.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val prayerSessions = repository.allPrayerSessions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val importedDocs = repository.allImportedDocs.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val verseNotes = repository.allVerseNotes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val readingHistory = repository.allReadingProgress.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // --- Search results across ALL models ---
    val searchResults = combine(
        _searchQuery,
        bookmarks,
        journalEntries,
        importedDocs
    ) { query, bms, journals, docs ->
        if (query.trim().isEmpty()) return@combine SearchResultSets()

        val lowercaseQuery = query.lowercase()

        // 1. Search in scriptures and verses
        val scriptureMatches = mutableListOf<ScriptureSearchResult>()
        StaticScriptureProvider.scriptures.forEach { s ->
            val matchScripture = s.titleEn.lowercase().contains(lowercaseQuery) ||
                    s.titleHi.contains(lowercaseQuery) ||
                    s.category.lowercase().contains(lowercaseQuery)

            s.chapters.forEach { c ->
                c.verses.forEach { v ->
                    val textEnMatch = v.textEn.lowercase().contains(lowercaseQuery) || v.translationEn.lowercase().contains(lowercaseQuery)
                    val textHiMatch = v.textHi.contains(lowercaseQuery) || v.translationHi.contains(lowercaseQuery)
                    val textNeMatch = v.textNe.contains(lowercaseQuery) || v.translationNe.contains(lowercaseQuery)

                    if (matchScripture || textEnMatch || textHiMatch || textNeMatch) {
                        scriptureMatches.add(
                            ScriptureSearchResult(
                                scriptureId = s.id,
                                scriptureName = s.titleEn,
                                chapterNum = c.number,
                                verseNum = v.number,
                                textEn = v.textEn,
                                translationEn = v.translationEn
                            )
                        )
                    }
                }
            }
        }

        // 2. Search in journal entries
        val journalMatches = journals.filter {
            it.title.lowercase().contains(lowercaseQuery) || it.content.lowercase().contains(lowercaseQuery) || it.category.lowercase().contains(lowercaseQuery)
        }

        // 3. Search in bookmarks
        val bookmarkMatches = bms.filter {
            it.verseText.lowercase().contains(lowercaseQuery) || it.scriptureName.lowercase().contains(lowercaseQuery)
        }

        // 4. Search in imported documents
        val docMatches = docs.filter {
            it.fileName.lowercase().contains(lowercaseQuery) || it.content.lowercase().contains(lowercaseQuery)
        }

        SearchResultSets(
            scriptureResults = scriptureMatches.take(15),
            bookmarkResults = bookmarkMatches,
            journalResults = journalMatches,
            documentResults = docMatches
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchResultSets())

    // --- Dynamic Stats ---
    val stats = combine(readingHistory, prayerSessions) { history, prayers ->
        val totalDaysRead = history.size
        val totalSessionsCount = prayers.size
        val totalMinutesMeditated = prayers.sumOf { it.durationMinutes }
        
        // Dynamic badges based on progress
        val badges = mutableListOf<String>()
        if (totalMinutesMeditated >= 10) badges.add("Peace Seeker")
        if (totalMinutesMeditated >= 30) badges.add("Mindful Yogi")
        if (totalMinutesMeditated >= 60) badges.add("Zen Master")
        if (totalDaysRead >= 1) badges.add("Dharma Beginner")
        if (totalDaysRead >= 3) badges.add("Wisdom Sage")
        if (badges.isEmpty()) {
            badges.add("Spiritual Seeker")
        }

        DashboardStats(
            daysReadStreak = if (totalDaysRead > 0) totalDaysRead + 2 else 0, // friendly streak buffer
            meditationMinutes = totalMinutesMeditated,
            meditationSessions = totalSessionsCount,
            badges = badges
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats()
    )

    // --- Methods ---

    fun onThemeSelected(preset: ThemePreset) {
        _themePreset.value = preset
        prefs.edit().putString("theme_preset", preset.name).apply()
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun onLanguageSelected(code: String) {
        _lang.value = code
        prefs.edit().putString("lang", code).apply()
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    fun resetOnboarding() {
        _isOnboardingCompleted.value = false
        prefs.edit().putBoolean("onboarding_completed", false).apply()
    }

    // Reader Options Adjustments
    fun setFontSize(size: Float) {
        _fontSize.value = size
        prefs.edit().putFloat("reader_font_size", size).apply()
    }

    fun setLineSpacing(spacing: Float) {
        _lineSpacing.value = spacing
        prefs.edit().putFloat("reader_line_spacing", spacing).apply()
    }

    fun setFontFamily(family: String) {
        _fontFamily.value = family
        prefs.edit().putString("reader_font_family", family).apply()
    }

    fun setEyeComfort(enabled: Boolean) {
        _eyeComfort.value = enabled
        prefs.edit().putBoolean("reader_eye_comfort", enabled).apply()
    }

    fun setReadingTheme(theme: String) {
        _readingTheme.value = theme
        prefs.edit().putString("reader_theme", theme).apply()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectScripture(id: String?) {
        _selectedScriptureIndex.value = id
        _currentChapterNumber.value = 1
    }

    fun setChapter(chapter: Int) {
        _currentChapterNumber.value = chapter
    }

    // --- Save progress ---
    fun updateReadingProgress(scriptureId: String, name: String, chNum: Int, verseNum: Int) {
        viewModelScope.launch {
            repository.saveReadingProgress(
                ReadingProgress(
                    scriptureId = scriptureId,
                    scriptureName = name,
                    lastChapter = chNum,
                    lastVerse = verseNum,
                    progressPercent = 100f
                )
            )
        }
    }

    // --- Bookmarking ---
    fun toggleBookmark(scriptureId: String, name: String, chNum: Int, verseNum: Int, body: String, trans: String) {
        viewModelScope.launch {
            val bookmarked = repository.isBookmarkedSync(scriptureId, chNum, verseNum)
            if (bookmarked) {
                repository.deleteBookmarkByCoordinates(scriptureId, chNum, verseNum)
            } else {
                repository.insertBookmark(
                    Bookmark(
                        scriptureId = scriptureId,
                        scriptureName = name,
                        chapter = chNum,
                        verseNumber = verseNum,
                        verseText = body,
                        translationText = trans
                    )
                )
            }
        }
    }

    fun isBookmarkedFlow(scriptureId: String, chNum: Int, verseNum: Int): Flow<Boolean> {
        return repository.isBookmarkedFlow(scriptureId, chNum, verseNum)
    }

    // --- Journaling ---
    fun addJournalEntry(title: String, content: String, category: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            repository.insertJournalEntry(
                JournalEntry(
                    title = title,
                    content = content,
                    category = category,
                    dateString = dateStr
                )
            )
        }
    }

    fun deleteJournalEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteJournalEntryById(id)
        }
    }

    // --- Meditation timer ---
    fun addMeditationSession(minutes: Int, modeName: String) {
        viewModelScope.launch {
            repository.insertPrayerSession(
                PrayerSession(
                    durationMinutes = minutes,
                    sessionType = modeName
                )
            )
        }
    }

    // --- Document Importing ---
    fun importDocument(title: String, content: String, type: String) {
        viewModelScope.launch {
            repository.insertImportedDoc(
                ImportedDoc(
                    fileName = title,
                    fileType = type,
                    content = content
                )
            )
        }
    }

    fun deleteImportedDoc(id: Int) {
        viewModelScope.launch {
            repository.deleteImportedDocById(id)
        }
    }

    // --- Verse Margins Note ---
    fun saveVerseNote(scriptureId: String, className: String, chNum: Int, vNum: Int, text: String) {
        viewModelScope.launch {
            val existing = repository.getNoteForVerseSync(scriptureId, chNum, vNum)
            if (text.trim().isEmpty()) {
                if (existing != null) {
                    repository.deleteVerseNoteById(existing.id)
                }
            } else {
                repository.insertVerseNote(
                    VerseNote(
                        id = existing?.id ?: 0,
                        scriptureId = scriptureId,
                        className = className,
                        chapter = chNum,
                        verseNumber = vNum,
                        noteText = text
                    )
                )
            }
        }
    }

    fun getNoteForVerseFlow(scriptureId: String, chNum: Int, vNum: Int): Flow<VerseNote?> {
        return repository.getNoteForVerseFlow(scriptureId, chNum, vNum)
    }
}

// Data holder classes
data class ScriptureSearchResult(
    val scriptureId: String,
    val scriptureName: String,
    val chapterNum: Int,
    val verseNum: Int,
    val textEn: String,
    val translationEn: String
)

data class SearchResultSets(
    val scriptureResults: List<ScriptureSearchResult> = emptyList(),
    val bookmarkResults: List<Bookmark> = emptyList(),
    val journalResults: List<JournalEntry> = emptyList(),
    val documentResults: List<ImportedDoc> = emptyList()
)

data class DashboardStats(
    val daysReadStreak: Int = 0,
    val meditationMinutes: Int = 0,
    val meditationSessions: Int = 0,
    val badges: List<String> = emptyList()
)
