package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// 1. Reading Progress (to continue reading where you left off)
@Entity(tableName = "reading_progress")
data class ReadingProgress(
    @PrimaryKey val scriptureId: String,
    val scriptureName: String,
    val lastChapter: Int,
    val lastVerse: Int = 1,
    val progressPercent: Float = 0f,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)

// 2. Bookmark (favorited/bookmarked verses)
@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scriptureId: String,
    val scriptureName: String,
    val chapter: Int,
    val verseNumber: Int,
    val verseText: String,
    val translationText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// 3. Journal Entry (reflexions, teachings, gratitude list)
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "Reflection", // Reflection, Gratitude, Teachings, Collection
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)

// 4. Prayer & Meditation Sessions (streak tracking/history logs)
@Entity(tableName = "prayer_sessions")
data class PrayerSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationMinutes: Int,
    val sessionType: String, // Silent Focus, Guided Breathing, Temple Chants
    val completionTimestamp: Long = System.currentTimeMillis()
)

// 5. Imported Documents (PDF, TXT, CSV, etc. loaded into offline reader state)
@Entity(tableName = "imported_docs")
data class ImportedDoc(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileType: String, // PDF, TXT, CSV, DOCX, EPUB
    val content: String,
    val importTimestamp: Long = System.currentTimeMillis()
)

// 6. Verse Notes (Custom margin notes added in reader)
@Entity(tableName = "verse_notes")
data class VerseNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val scriptureId: String,
    val className: String, // e.g. "Bhagavad Gita"
    val chapter: Int,
    val verseNumber: Int,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface AppDao {
    // --- Reading Progress ---
    @Query("SELECT * FROM reading_progress ORDER BY lastReadTimestamp DESC")
    fun getAllReadingProgress(): Flow<List<ReadingProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgress)

    @Query("SELECT * FROM reading_progress WHERE scriptureId = :scriptureId LIMIT 1")
    suspend fun getProgressForScripture(scriptureId: String): ReadingProgress?

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE scriptureId = :scriptureId AND chapter = :chapter AND verseNumber = :verseNumber")
    suspend fun deleteBookmarkByCoordinates(scriptureId: String, chapter: Int, verseNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE scriptureId = :scriptureId AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1)")
    fun isBookmarkedFlow(scriptureId: String, chapter: Int, verseNumber: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE scriptureId = :scriptureId AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1)")
    suspend fun isBookmarkedSync(scriptureId: String, chapter: Int, verseNumber: Int): Boolean

    // --- Journal Entries ---
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntryById(id: Int)

    // --- Prayer Sessions ---
    @Query("SELECT * FROM prayer_sessions ORDER BY completionTimestamp DESC")
    fun getAllPrayerSessions(): Flow<List<PrayerSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerSession(session: PrayerSession)

    // --- Imported Documents ---
    @Query("SELECT * FROM imported_docs ORDER BY importTimestamp DESC")
    fun getAllImportedDocs(): Flow<List<ImportedDoc>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportedDoc(doc: ImportedDoc)

    @Query("DELETE FROM imported_docs WHERE id = :id")
    suspend fun deleteImportedDocById(id: Int)

    // --- Verse Notes ---
    @Query("SELECT * FROM verse_notes ORDER BY timestamp DESC")
    fun getAllVerseNotes(): Flow<List<VerseNote>>

    @Query("SELECT * FROM verse_notes WHERE scriptureId = :scriptureId AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1")
    fun getNoteForVerseFlow(scriptureId: String, chapter: Int, verseNumber: Int): Flow<VerseNote?>

    @Query("SELECT * FROM verse_notes WHERE scriptureId = :scriptureId AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getNoteForVerseSync(scriptureId: String, chapter: Int, verseNumber: Int): VerseNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerseNote(note: VerseNote)

    @Query("DELETE FROM verse_notes WHERE id = :id")
    suspend fun deleteVerseNoteById(id: Int)
}

@Database(
    entities = [
        ReadingProgress::class,
        Bookmark::class,
        JournalEntry::class,
        PrayerSession::class,
        ImportedDoc::class,
        VerseNote::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
