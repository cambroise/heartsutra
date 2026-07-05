package cloud.ambroise.heartsutra.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** How the app resolves light vs dark. SYSTEM follows the device setting. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {

    private val appContext = context.applicationContext
    private val themeKey = stringPreferencesKey("theme_mode")

    val themeMode: Flow<ThemeMode> = appContext.settingsDataStore.data.map { prefs ->
        prefs[themeKey]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        appContext.settingsDataStore.edit { it[themeKey] = mode.name }
    }
}
