package com.meshkhal.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshkhal.app.data.Ayah
import com.meshkhal.app.data.MeshkhalRepository
import com.meshkhal.app.data.Surah
import com.meshkhal.app.locale.MeshkhalLanguage
import com.meshkhal.app.locale.MeshkhalLocale
import com.meshkhal.app.ui.theme.MeshkhalAppearance
import com.meshkhal.app.ui.theme.MeshkhalPalette
import com.meshkhal.app.ui.theme.MeshkhalTheme

private enum class Tab(val label: Int) {
    HOME(R.string.home), QURAN(R.string.quran), HADITH(R.string.hadith), ATHKAR(R.string.athkar), MORE(R.string.more)
}

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(MeshkhalLocale.apply(newBase))
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContent {
            MeshkhalTheme(palette = MeshkhalAppearance.current(this)) {
                MeshkhalApp(MeshkhalRepository(applicationContext), this)
            }
        }
    }
}

@Composable
private fun MeshkhalApp(repo: MeshkhalRepository, activity: MainActivity) {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var selected by remember { mutableStateOf<Surah?>(null) }
    val rtl = MeshkhalLocale.current(activity) != MeshkhalLanguage.ENGLISH

    CompositionLocalProvider(LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Tab.values().forEach { item ->
                        NavigationBarItem(
                            selected = tab == item,
                            onClick = { tab = item; selected = null },
                            icon = {
                                Icon(
                                    imageVector = when (item) {
                                        Tab.QURAN -> Icons.Default.MenuBook
                                        Tab.HADITH -> Icons.Default.Book
                                        Tab.ATHKAR -> Icons.Default.Favorite
                                        Tab.MORE -> Icons.Default.Settings
                                        Tab.HOME -> Icons.Default.Home
                                    },
                                    contentDescription = stringResource(item.label)
                                )
                            },
                            label = { Text(stringResource(item.label)) }
                        )
                    }
                }
            }
        ) { padding ->
            AnimatedContent(
                targetState = tab,
                modifier = Modifier.fillMaxSize().padding(padding),
                transitionSpec = {
                    fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 28 } togetherWith fadeOut(tween(140))
                },
                label = "meshkhal-tab-transition"
            ) { visibleTab ->
                when (visibleTab) {
                    Tab.HOME -> Home(repo, onOpen = { tab = it })
                    Tab.QURAN -> if (selected == null) Quran(repo) { selected = it } else Reader(repo, selected!!) { selected = null }
                    Tab.HADITH -> Hadiths(repo)
                    Tab.ATHKAR -> Athkar(repo)
                    Tab.MORE -> More(repo, activity)
                }
            }
        }
    }
}

@Composable
private fun Home(repo: MeshkhalRepository, onOpen: (Tab) -> Unit) {
    val verse = remember { repo.ayahs(1).firstOrNull() }
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AnimatedVisibility(visible = entered, enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 12 }) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                BrandHeader()
                WelcomeCard()
                TodayAyahCard(verse)
                Text(stringResource(R.string.quick_access), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ShortcutCard(
                        title = R.string.continue_reading,
                        subtitle = R.string.quran,
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpen(Tab.QURAN) }
                    )
                    ShortcutCard(
                        title = R.string.athkar_duas,
                        subtitle = R.string.athkar,
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpen(Tab.ATHKAR) }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                    Text(stringResource(R.string.home_footer), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        ImageLogo()
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.app_tagline), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.Explore, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(25.dp))
    }
}

@Composable
private fun ImageLogo() {
    Box(
        modifier = Modifier.size(66.dp).shadow(8.dp, CircleShape).clip(CircleShape)
            .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.78f)))),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.ic_meshkhal_mark),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
private fun WelcomeCard() {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = primary),
        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(28.dp))
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(22.dp)) {
            Canvas(modifier = Modifier.size(180.dp).align(Alignment.TopEnd)) {
                val center = Offset(size.width * 0.78f, size.height * 0.12f)
                for (ring in 0..4) {
                    drawCircle(
                        color = secondary.copy(alpha = 0.16f - ring * 0.018f),
                        radius = 28f + ring * 19f,
                        center = center,
                        style = Stroke(width = 1.4f)
                    )
                }
                drawCircle(color = secondary.copy(alpha = 0.25f), radius = 3.5f, center = Offset(size.width * 0.24f, size.height * 0.76f))
                drawCircle(color = Color.White.copy(alpha = 0.30f), radius = 2.5f, center = Offset(size.width * 0.38f, size.height * 0.62f))
            }
            Column(modifier = Modifier.fillMaxWidth(0.78f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(stringResource(R.string.welcome_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text(stringResource(R.string.welcome_subtitle), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.86f), lineHeight = 23.sp)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = secondary, modifier = Modifier.size(19.dp))
                    Text(stringResource(R.string.offline_content), color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun TodayAyahCard(verse: Ayah?) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(19.dp))
                }
                Text(stringResource(R.string.today_ayah), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                verse?.arabic ?: stringResource(R.string.loading),
                fontSize = 25.sp,
                lineHeight = 43.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Text(stringResource(R.string.verse_card_note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ShortcutCard(title: Int, subtitle: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(22.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(15.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Text(stringResource(title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(stringResource(subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Header(title: Int, subtitle: Int) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(stringResource(title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(stringResource(subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Quran(repo: MeshkhalRepository, select: (Surah) -> Unit) {
    val list = remember { repo.surahs() }
    Column {
        Header(R.string.quran, R.string.quran_subtitle)
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(list) { surah ->
                Card(
                    onClick = { select(surah) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    ListItem(
                        headlineContent = { Text(surah.name, style = MaterialTheme.typography.titleLarge) },
                        supportingContent = { Text(stringResource(R.string.ayah_count, surah.ayahCount)) },
                        leadingContent = {
                            Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                Text(surah.id.toString(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Reader(repo: MeshkhalRepository, surah: Surah, back: () -> Unit) {
    val list = remember(surah.id) { repo.ayahs(surah.id) }
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = back) { Icon(Icons.Default.ArrowForward, contentDescription = stringResource(R.string.back)) }
            Text(surah.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(list) { ayah ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text(stringResource(R.string.ayah_number, ayah.number), color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge)
                        Text(ayah.arabic, fontSize = 25.sp, lineHeight = 42.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        if (ayah.translation.isNotBlank()) Text(ayah.translation, fontSize = 17.sp, lineHeight = 27.sp)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Text(stringResource(R.string.tafsir), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        Text(ayah.tafsir.ifBlank { stringResource(R.string.no_content) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Hadiths(repo: MeshkhalRepository) {
    val list = remember { repo.hadiths() }
    Column {
        Header(R.string.hadith, R.string.hadith_subtitle)
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(list) { hadith ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), shape = RoundedCornerShape(20.dp)) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(hadith.text, fontSize = 19.sp, lineHeight = 31.sp)
                        Text(hadith.source, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun Athkar(repo: MeshkhalRepository) {
    val list = remember { repo.athkar() }
    Column {
        Header(R.string.athkar, R.string.athkar_subtitle)
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(list) { dhikr ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), shape = RoundedCornerShape(20.dp)) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(dhikr.text, fontSize = 20.sp, lineHeight = 33.sp)
                        Text(stringResource(R.string.repetitions, dhikr.repetitions, dhikr.source), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun More(repo: MeshkhalRepository, activity: MainActivity) {
    var showLanguage by remember { mutableStateOf(false) }
    var showPalette by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header(R.string.more, R.string.more_subtitle)
        SettingsSectionTitle(R.string.personalization)
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp)) {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.language)) },
                    supportingContent = { Text(stringResource(R.string.choose_language)) },
                    leadingContent = { SettingsIcon(Icons.Default.Language) },
                    modifier = Modifier.clickable { showLanguage = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                ListItem(
                    headlineContent = { Text(stringResource(R.string.appearance)) },
                    supportingContent = { Text(stringResource(R.string.appearance_subtitle)) },
                    leadingContent = { SettingsIcon(Icons.Default.Palette) },
                    modifier = Modifier.clickable { showPalette = true }
                )
            }
        }
        SettingsSectionTitle(R.string.content_section)
        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(22.dp)) {
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.sources)) },
                    supportingContent = { Text("${repo.tafsirSources().size} ${stringResource(R.string.merged_sources)}") },
                    leadingContent = { SettingsIcon(Icons.Default.MenuBook) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                ListItem(
                    headlineContent = { Text(stringResource(R.string.duas)) },
                    supportingContent = { Text("${repo.duas().size} ${stringResource(R.string.duas)}") },
                    leadingContent = { SettingsIcon(Icons.Default.VolunteerActivism) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                ListItem(headlineContent = { Text(stringResource(R.string.prayer_qibla)) }, leadingContent = { SettingsIcon(Icons.Default.Explore) })
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f))
                ListItem(headlineContent = { Text(stringResource(R.string.settings_privacy)) }, leadingContent = { SettingsIcon(Icons.Default.Settings) })
            }
        }
        Spacer(Modifier.height(24.dp))
    }
    if (showLanguage) LanguageDialog(activity) { showLanguage = false }
    if (showPalette) PaletteDialog(activity) { showPalette = false }
}

@Composable
private fun SettingsSectionTitle(text: Int) {
    Text(
        stringResource(text),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 10.dp)
    )
}

@Composable
private fun SettingsIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(21.dp))
    }
}

@Composable
private fun LanguageDialog(activity: MainActivity, close: () -> Unit) {
    AlertDialog(
        onDismissRequest = close,
        title = { Text(stringResource(R.string.choose_language), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LanguageChoice(stringResource(R.string.arabic)) { MeshkhalLocale.set(activity, MeshkhalLanguage.ARABIC) }
                LanguageChoice(stringResource(R.string.sorani)) { MeshkhalLocale.set(activity, MeshkhalLanguage.SORANI) }
                LanguageChoice(stringResource(R.string.english)) { MeshkhalLocale.set(activity, MeshkhalLanguage.ENGLISH) }
            }
        },
        confirmButton = { TextButton(onClick = close) { Text(stringResource(R.string.close)) } },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun LanguageChoice(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
    }
}

@Composable
private fun PaletteDialog(activity: MainActivity, close: () -> Unit) {
    AlertDialog(
        onDismissRequest = close,
        title = { Text(stringResource(R.string.choose_palette), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                PaletteChoice(R.string.palette_amethyst, Color(0xFF6745B5), Color(0xFFD3A64B)) { MeshkhalAppearance.set(activity, MeshkhalPalette.AMETHYST) }
                PaletteChoice(R.string.palette_sapphire, Color(0xFF315DA8), Color(0xFFC99B48)) { MeshkhalAppearance.set(activity, MeshkhalPalette.SAPPHIRE) }
                PaletteChoice(R.string.palette_sand, Color(0xFF89563E), Color(0xFFC49346)) { MeshkhalAppearance.set(activity, MeshkhalPalette.SAND) }
            }
        },
        confirmButton = { TextButton(onClick = close) { Text(stringResource(R.string.close)) } },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun PaletteChoice(title: Int, primary: Color, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(vertical = 9.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(primary), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(accent))
        }
        Text(stringResource(title), style = MaterialTheme.typography.bodyLarge)
    }
}
