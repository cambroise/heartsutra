package cloud.ambroise.heartsutra.data

/**
 * One line of the sutra in its three parallel forms. [index] is the stable
 * position in the recitation order and doubles as the SRS key.
 */
data class Segment(
    val index: Int,
    val original: String,
    val reading: String,
    val english: String,
)
