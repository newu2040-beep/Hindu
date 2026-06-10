package com.example.data

import com.example.data.local.AppDao
import com.example.data.local.Bookmark
import com.example.data.local.ImportedDoc
import com.example.data.local.JournalEntry
import com.example.data.local.PrayerSession
import com.example.data.local.ReadingProgress
import com.example.data.local.VerseNote
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allReadingProgress: Flow<List<ReadingProgress>> = appDao.getAllReadingProgress()
    val allBookmarks: Flow<List<Bookmark>> = appDao.getAllBookmarks()
    val allJournalEntries: Flow<List<JournalEntry>> = appDao.getAllJournalEntries()
    val allPrayerSessions: Flow<List<PrayerSession>> = appDao.getAllPrayerSessions()
    val allImportedDocs: Flow<List<ImportedDoc>> = appDao.getAllImportedDocs()
    val allVerseNotes: Flow<List<VerseNote>> = appDao.getAllVerseNotes()

    suspend fun saveReadingProgress(progress: ReadingProgress) {
        appDao.saveReadingProgress(progress)
    }

    suspend fun getProgressForScripture(scriptureId: String): ReadingProgress? {
        return appDao.getProgressForScripture(scriptureId)
    }

    suspend fun insertBookmark(bookmark: Bookmark) {
        appDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmarkByCoordinates(scriptureId: String, chapter: Int, verseNumber: Int) {
        appDao.deleteBookmarkByCoordinates(scriptureId, chapter, verseNumber)
    }

    fun isBookmarkedFlow(scriptureId: String, chapter: Int, verseNumber: Int): Flow<Boolean> {
        return appDao.isBookmarkedFlow(scriptureId, chapter, verseNumber)
    }

    suspend fun isBookmarkedSync(scriptureId: String, chapter: Int, verseNumber: Int): Boolean {
        return appDao.isBookmarkedSync(scriptureId, chapter, verseNumber)
    }

    suspend fun insertJournalEntry(entry: JournalEntry) {
        appDao.insertJournalEntry(entry)
    }

    suspend fun deleteJournalEntryById(id: Int) {
        appDao.deleteJournalEntryById(id)
    }

    suspend fun insertPrayerSession(session: PrayerSession) {
        appDao.insertPrayerSession(session)
    }

    suspend fun insertImportedDoc(doc: ImportedDoc) {
        appDao.insertImportedDoc(doc)
    }

    suspend fun deleteImportedDocById(id: Int) {
        appDao.deleteImportedDocById(id)
    }

    fun getNoteForVerseFlow(scriptureId: String, chapter: Int, verseNumber: Int): Flow<VerseNote?> {
        return appDao.getNoteForVerseFlow(scriptureId, chapter, verseNumber)
    }

    suspend fun getNoteForVerseSync(scriptureId: String, chapter: Int, verseNumber: Int): VerseNote? {
        return appDao.getNoteForVerseSync(scriptureId, chapter, verseNumber)
    }

    suspend fun insertVerseNote(note: VerseNote) {
        appDao.insertVerseNote(note)
    }

    suspend fun deleteVerseNoteById(id: Int) {
        appDao.deleteVerseNoteById(id)
    }
}
