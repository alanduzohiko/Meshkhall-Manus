package com.meshkhal.app.data

import android.content.Context
import com.meshkhal.app.locale.MeshkhalLocale

data class Surah(val id:Int,val name:String,val ayahCount:Int)
data class Ayah(val surahId:Int,val number:Int,val arabic:String,val translation:String,val tafsir:String)
data class Hadith(val title:String,val text:String,val source:String)
data class Prayer(val name:String,val time:String)

/** Unified production repository. Khira is an additional content pack; Meshkhal databases remain available. */
class MeshkhalRepository(context: Context) {
    private val appContext = context.applicationContext
    private val meshkhal = AssetSqliteContentSource(appContext)
    private val khira = KhiraContentSource(appContext)

    fun surahs(): List<Surah> = khira.surahs()
    fun ayahs(surahId:Int): List<Ayah> {
        val language = MeshkhalLocale.current(appContext).tag
        val translations = meshkhal.translations(surahId, language)
        return khira.ayahs(surahId).map { ayah ->
            val translation = when (language) {
                "ar" -> ""
                else -> translations[ayah.number] ?: ayah.translation
            }
            ayah.copy(translation = translation)
        }
    }
    fun search(query:String): List<Ayah> = khira.search(query)
    fun athkar(): List<Dhikr> = khira.athkar()
    fun duas(): List<Dua> = khira.duas()
    fun tafsirSources(): List<TafsirSource> = khira.tafsirSources()
    fun hadiths(): List<Hadith> = meshkhal.hadiths()
    fun prayers(): List<Prayer> = meshkhal.prayers()
}
