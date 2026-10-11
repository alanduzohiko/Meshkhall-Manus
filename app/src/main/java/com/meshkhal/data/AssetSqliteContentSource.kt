package com.meshkhal.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File

/** Opens the real content databases copied from app/src/main/assets. */
class AssetSqliteContentSource(private val context: Context) {
    private val dbDir = File(context.filesDir, "meshkhal-content").apply { mkdirs() }

    private fun openAsset(assetName: String): SQLiteDatabase {
        val target = File(dbDir, assetName.substringAfterLast('/'))
        if (!target.exists() || target.length() == 0L) {
            context.assets.open(assetName).use { input -> target.outputStream().use { output -> input.copyTo(output) } }
        }
        return SQLiteDatabase.openDatabase(target.path, null, SQLiteDatabase.OPEN_READONLY)
    }

    fun surahs(): List<Surah> = query("quran.db") { db ->
        db.rawQuery("SELECT surah_no, name_arabic, ayah_number FROM surah_name ORDER BY surah_no", null).use { c ->
            buildList { while (c.moveToNext()) add(Surah(c.getInt(0), c.getString(1), c.getInt(2))) }
        }
    }

    fun ayahs(surahId: Int): List<Ayah> = query("quran.db") { db ->
        db.rawQuery("SELECT verse_id, quran_arabic, translation_puxta FROM quran WHERE surah_id = ? ORDER BY verse_id", arrayOf(surahId.toString())).use { c ->
            buildList { while (c.moveToNext()) add(Ayah(surahId, c.getInt(0), c.getString(1), c.getString(2).orEmpty(), "")) }
        }
    }

    /** Returns a real content translation when the selected language has one in the bundled databases. */
    fun translations(surahId: Int, language: String): Map<Int, String> {
        if (language == "ar") return emptyMap() // The Quran is already rendered in Arabic.
        val asset = if (language == "en") "tafseer_sahih_international.db" else "quran.db"
        val sql = if (language == "en") {
            "SELECT verse_id, text FROM quran WHERE surah_id = ?"
        } else {
            "SELECT verse_id, translation_puxta FROM quran WHERE surah_id = ?"
        }
        return query(asset) { db ->
            db.rawQuery(sql, arrayOf(surahId.toString())).use { c ->
                buildMap { while (c.moveToNext()) put(c.getInt(0), c.getString(1).orEmpty()) }
            }
        }
    }

    fun tafsir(surahId: Int, verseId: Int, database: String = "tafseeri_noor.db"): String? = query(database) { db ->
        db.rawQuery("SELECT text FROM quran WHERE surah_id = ? AND verse_id = ?", arrayOf(surahId.toString(), verseId.toString())).use { c -> if (c.moveToFirst()) c.getString(0) else null }
    }

    fun hadiths(): List<Hadith> = query("db_hadith.db") { db ->
        db.rawQuery("SELECT COALESCE(text_ar, text), COALESCE(text_ar, text), 'db_hadith.db' FROM hadiths ORDER BY sort LIMIT 200", null).use { c ->
            buildList { while (c.moveToNext()) add(Hadith("حديث", c.getString(0).orEmpty(), c.getString(2))) }
        }
    }

    fun prayers(): List<Prayer> = query("prayertimes.db") { db ->
        db.rawQuery("SELECT fajr, sunrise, dhuhr, asr, maghrib, isha FROM staticprayertimes LIMIT 1", null).use { c ->
            buildList {
                if (c.moveToFirst()) {
                    add(Prayer("الفجر", c.getString(0))); add(Prayer("الشروق", c.getString(1))); add(Prayer("الظهر", c.getString(2))); add(Prayer("العصر", c.getString(3))); add(Prayer("المغرب", c.getString(4))); add(Prayer("العشاء", c.getString(5)))
                }
            }
        }
    }

    private fun <T> query(assetName: String, block: (SQLiteDatabase) -> T): T {
        val db = openAsset(assetName)
        return try { block(db) } finally { db.close() }
    }
}
