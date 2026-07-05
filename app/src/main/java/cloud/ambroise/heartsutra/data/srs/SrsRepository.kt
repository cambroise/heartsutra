package cloud.ambroise.heartsutra.data.srs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val Context.srsDataStore: DataStore<Preferences> by preferencesDataStore(name = "srs")

/**
 * Persists spaced-repetition state as a single JSON blob (segment index → state)
 * in DataStore. The whole sutra is ~40 cards, so keeping the map in one key is
 * simpler than a database and still gives an atomic read/write.
 */
class SrsRepository(private val context: Context) {

    private val stateKey = stringPreferencesKey("card_states")
    private val json = Json { ignoreUnknownKeys = true }
    private val mapSerializer = MapSerializer(Int.serializer(), CardSrs.serializer())

    val states: Flow<Map<Int, CardSrs>> = context.srsDataStore.data.map { prefs ->
        prefs[stateKey]?.let { runCatching { json.decodeFromString(mapSerializer, it) }.getOrNull() }
            ?: emptyMap()
    }

    suspend fun grade(index: Int, grade: Grade, today: Long) {
        context.srsDataStore.edit { prefs ->
            val current = prefs[stateKey]
                ?.let { runCatching { json.decodeFromString(mapSerializer, it) }.getOrNull() }
                ?: emptyMap()
            val updated = current + (index to (current[index] ?: CardSrs()).grade(grade, today))
            prefs[stateKey] = json.encodeToString(mapSerializer, updated)
        }
    }

    suspend fun reset() {
        context.srsDataStore.edit { it.remove(stateKey) }
    }
}
