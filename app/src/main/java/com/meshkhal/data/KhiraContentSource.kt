package com.meshkhal.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.util.zip.InflaterInputStream

/** Reads the Khira content set stored under assets/khira without replacing Meshkhal databases. */
class KhiraContentSource(private val context: Context) {
    private val prefix = "khira/"
    private val ayahs by lazy { compressedArray("mushaf/ayahs.bin") }
    private val tafsir by lazy { compressedArray("mushaf/tafsir.bin") }
    private val surahRows by lazy { JSONArray(text("data/surahs.json")) }
    private val athkarRows by lazy { JSONObject(text("data/athkar.json")).getJSONObject("entries") }
    private val duaRows by lazy { JSONObject(text("data/duas.json")).getJSONObject("entries") }

    fun surahs(): List<Surah> {
        val counts = HashMap<Int, Int>()
        for (i in 0 until ayahs.length()) { val id = ayahs.getJSONObject(i).getInt("sora"); counts[id] = (counts[id] ?: 0) + 1 }
        return (0 until surahRows.length()).map { i -> val o=surahRows.getJSONObject(i); Surah(o.getInt("number"), o.getString("arabicName"), counts[o.getInt("number")] ?: 0) }
    }

    fun ayahs(surahId: Int): List<Ayah> {
        val explanations = HashMap<Int, String>()
        for (i in 0 until tafsir.length()) { val o=tafsir.getJSONObject(i); if (o.getInt("surah")==surahId) explanations[o.getInt("ayah")]=o.getString("text") }
        return (0 until ayahs.length()).mapNotNull { i -> val o=ayahs.getJSONObject(i); if(o.getInt("sora")!=surahId) null else Ayah(surahId,o.getInt("aya_no"),o.getString("aya_text"),o.getString("aya_text_emlaey"),explanations[o.getInt("aya_no")].orEmpty()) }
    }

    fun search(query: String): List<Ayah> { val q=normalize(query); if(q.isBlank()) return emptyList(); return (0 until ayahs.length()).mapNotNull { i -> val o=ayahs.getJSONObject(i); if(normalize(o.getString("aya_text_emlaey")).contains(q)) Ayah(o.getInt("sora"),o.getInt("aya_no"),o.getString("aya_text"),o.getString("aya_text_emlaey"),"") else null }.take(100) }
    fun athkar(): List<Dhikr> = athkarRows.keys().asSequence().map { id -> val o=athkarRows.getJSONObject(id); Dhikr(id,o.getString("text"),o.optInt("repetitions",1),o.optString("source")) }.toList()
    fun duas(): List<Dua> = duaRows.keys().asSequence().map { id -> val o=duaRows.getJSONObject(id); Dua(id,o.getString("text"),o.optString("source")) }.toList()
    fun tafsirSources(): List<TafsirSource> { val a=JSONArray(text("tafsir/catalog.json")); return (0 until a.length()).map { val o=a.getJSONObject(it); TafsirSource(o.getString("id"),o.getString("title"),o.getString("author"),o.optString("edition")) } }
    private fun text(name:String)=context.assets.open(prefix+name).bufferedReader(Charsets.UTF_8).use{it.readText()}
    private fun compressedArray(name:String)=InflaterInputStream(BufferedInputStream(context.assets.open(prefix+name))).bufferedReader(Charsets.UTF_8).use{JSONArray(it.readText())}
    private fun normalize(s:String)=s.replace("َ","").replace("ً","").replace("ُ","").replace("ٌ","").replace("ِ","").replace("ٍ","").replace("ْ","").replace("ّ","").trim()
}

data class Dhikr(val id:String,val text:String,val repetitions:Int,val source:String)
data class Dua(val id:String,val text:String,val source:String)
data class TafsirSource(val id:String,val title:String,val author:String,val edition:String)
