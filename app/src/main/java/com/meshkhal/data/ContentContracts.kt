package com.meshkhal.app.data

/** Stable contracts for replacing demo data with licensed offline content. */
data class ContentSource(val id: String, val name: String, val author: String?, val language: String, val version: String, val license: String, val url: String?)
data class Bookmark(val surahId: Int, val ayahNumber: Int, val note: String?, val createdAt: Long)
data class ReadingProgress(val surahId: Int, val ayahNumber: Int, val updatedAt: Long)
data class AudioTrack(val surahId: Int, val ayahNumber: Int?, val reciter: String, val url: String, val license: String)
data class PrayerSettings(val city: String, val latitude: Double?, val longitude: Double?, val method: String, val madhab: String, val notificationsEnabled: Boolean)
data class AppPreferences(val language: String = "ckb", val darkMode: Boolean = false, val fontScale: Float = 1f, val audioAutoPlay: Boolean = false)

interface LicensedContentImporter {
    fun validate(source: ContentSource): Result<Unit>
    fun importQuran(databaseFile: String): Result<Int>
    fun importTafsir(databaseFile: String): Result<Int>
    fun importHadith(databaseFile: String): Result<Int>
}

interface BackupService {
    fun exportEncrypted(destination: String, passphrase: CharArray): Result<Unit>
    fun importEncrypted(source: String, passphrase: CharArray): Result<Unit>
}

interface PrayerTimesEngine {
    fun calculate(settings: PrayerSettings, epochDay: Long): List<Prayer>
}
