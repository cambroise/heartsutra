package cloud.ambroise.heartsutra.data.kanji

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Loads KanjiVG stroke-order data bundled at `assets/kanjivg/strokes.json`
 * (a map from character to its ordered list of SVG path `d` strings, in a
 * 109×109 viewBox). Parsed once and cached. See `assets/kanjivg/NOTICE.txt`
 * for the CC BY-SA 3.0 attribution.
 */
class KanjiStrokeRepository(context: Context) {

    private val appContext = context.applicationContext
    @Volatile private var cache: Map<Char, List<String>>? = null

    suspend fun load(): Map<Char, List<String>> = cache ?: withContext(Dispatchers.IO) {
        val text = appContext.assets.open("kanjivg/strokes.json")
            .bufferedReader().use { it.readText() }
        val byString: Map<String, List<String>> = Json.decodeFromString(text)
        byString.entries
            .mapNotNull { (k, v) -> k.firstOrNull()?.let { it to v } }
            .toMap()
            .also { cache = it }
    }
}
