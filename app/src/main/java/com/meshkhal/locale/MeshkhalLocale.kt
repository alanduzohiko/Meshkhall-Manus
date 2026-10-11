package com.meshkhal.app.locale

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class MeshkhalLanguage(val tag: String) { ARABIC("ar"), SORANI("ckb"), ENGLISH("en") }

object MeshkhalLocale {
    private const val PREFS = "meshkhal_preferences"
    private const val KEY_LANGUAGE = "language"
    fun current(context: Context): MeshkhalLanguage = runCatching { MeshkhalLanguage.valueOf(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_LANGUAGE, MeshkhalLanguage.SORANI.name)!!) }.getOrDefault(MeshkhalLanguage.SORANI)
    fun apply(context: Context): Context { val lang = current(context); val c = Configuration(context.resources.configuration); c.setLocale(Locale.forLanguageTag(lang.tag)); return context.createConfigurationContext(c) }
    fun set(activity: Activity, language: MeshkhalLanguage) { activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_LANGUAGE, language.name).apply(); activity.recreate() }
}
