package com.meshkhal.app.ui.theme

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** User-selectable accent palettes; none relies on the former green identity. */
enum class MeshkhalPalette {
    AMETHYST,
    SAPPHIRE,
    SAND
}

object MeshkhalAppearance {
    private const val PREFS = "meshkhal_preferences"
    private const val KEY_PALETTE = "palette"

    fun current(context: Context): MeshkhalPalette = runCatching {
        MeshkhalPalette.valueOf(
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_PALETTE, MeshkhalPalette.AMETHYST.name)!!
        )
    }.getOrDefault(MeshkhalPalette.AMETHYST)

    fun set(activity: Activity, palette: MeshkhalPalette) {
        activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_PALETTE, palette.name).apply()
        activity.recreate()
    }
}

private data class PaletteColors(
    val lightPrimary: Color,
    val lightSecondary: Color,
    val lightBackground: Color,
    val lightSurface: Color,
    val lightOnSurface: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkOnSurface: Color
)

private fun colorsFor(palette: MeshkhalPalette) = when (palette) {
    MeshkhalPalette.AMETHYST -> PaletteColors(
        Color(0xFF6745B5), Color(0xFFD3A64B), Color(0xFFF7F4FC), Color(0xFFFFFDFF), Color(0xFF241D30),
        Color(0xFFC5A8FF), Color(0xFFE8C576), Color(0xFF15111D), Color(0xFF211B2B), Color(0xFFF1ECF8)
    )
    MeshkhalPalette.SAPPHIRE -> PaletteColors(
        Color(0xFF315DA8), Color(0xFFC99B48), Color(0xFFF2F6FC), Color(0xFFFCFDFF), Color(0xFF172338),
        Color(0xFF9DBDFF), Color(0xFFE7C575), Color(0xFF101722), Color(0xFF192436), Color(0xFFEAF0FC)
    )
    MeshkhalPalette.SAND -> PaletteColors(
        Color(0xFF89563E), Color(0xFFC49346), Color(0xFFFAF5ED), Color(0xFFFFFCF7), Color(0xFF2D211A),
        Color(0xFFE5AE86), Color(0xFFE6C276), Color(0xFF1B1512), Color(0xFF29201A), Color(0xFFF5EDE4)
    )
}

@Composable
fun MeshkhalTheme(
    palette: MeshkhalPalette = MeshkhalPalette.AMETHYST,
    content: @Composable () -> Unit
) {
    val p = colorsFor(palette)
    val dark = isSystemInDarkTheme()
    val scheme = if (dark) {
        darkColorScheme(
            primary = p.darkPrimary,
            onPrimary = Color(0xFF1A1422),
            secondary = p.darkSecondary,
            onSecondary = Color(0xFF2B2110),
            background = p.darkBackground,
            onBackground = p.darkOnSurface,
            surface = p.darkSurface,
            onSurface = p.darkOnSurface,
            primaryContainer = p.darkPrimary.copy(alpha = 0.22f),
            onPrimaryContainer = p.darkOnSurface,
            secondaryContainer = p.darkSecondary.copy(alpha = 0.18f),
            onSecondaryContainer = p.darkOnSurface,
            surfaceVariant = p.darkSurface.copy(alpha = 0.82f),
            onSurfaceVariant = p.darkOnSurface.copy(alpha = 0.72f),
            outline = p.darkSecondary.copy(alpha = 0.55f)
        )
    } else {
        lightColorScheme(
            primary = p.lightPrimary,
            onPrimary = Color.White,
            secondary = p.lightSecondary,
            onSecondary = Color.White,
            background = p.lightBackground,
            onBackground = p.lightOnSurface,
            surface = p.lightSurface,
            onSurface = p.lightOnSurface,
            primaryContainer = p.lightPrimary.copy(alpha = 0.12f),
            onPrimaryContainer = p.lightPrimary,
            secondaryContainer = p.lightSecondary.copy(alpha = 0.16f),
            onSecondaryContainer = p.lightOnSurface,
            surfaceVariant = p.lightPrimary.copy(alpha = 0.06f),
            onSurfaceVariant = p.lightOnSurface.copy(alpha = 0.68f),
            outline = p.lightSecondary.copy(alpha = 0.6f)
        )
    }
    MaterialTheme(colorScheme = scheme, typography = Typography(), content = content)
}
