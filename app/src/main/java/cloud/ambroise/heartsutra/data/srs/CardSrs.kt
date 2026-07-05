package cloud.ambroise.heartsutra.data.srs

import kotlinx.serialization.Serializable

/** The memorisation grade the user gives a card, in ascending quality. */
enum class Grade { AGAIN, HARD, GOOD, EASY }

/**
 * Per-card spaced-repetition state (a lightweight SM-2 variant). A card that has
 * never been reviewed has [dueEpochDay] == 0, which is always in the past and so
 * counts as due.
 */
@Serializable
data class CardSrs(
    val repetitions: Int = 0,
    val intervalDays: Int = 0,
    val ease: Double = 2.5,
    val dueEpochDay: Long = 0L,
    val lastGrade: Grade? = null,
) {
    fun isDue(todayEpochDay: Long): Boolean = dueEpochDay <= todayEpochDay

    /** Returns the next state after grading this card on [today]. */
    fun grade(grade: Grade, today: Long): CardSrs {
        val newEase = (ease + EASE_DELTA.getValue(grade)).coerceAtLeast(MIN_EASE)
        val newInterval = when (grade) {
            Grade.AGAIN -> 1
            Grade.HARD -> (intervalDays.coerceAtLeast(1) * HARD_FACTOR).roundToIntAtLeast(1)
            Grade.GOOD -> when (repetitions) {
                0 -> 1
                1 -> 6
                else -> (intervalDays * ease).roundToIntAtLeast(1)
            }
            Grade.EASY -> when (repetitions) {
                0 -> 4
                else -> (intervalDays * ease * EASY_BONUS).roundToIntAtLeast(1)
            }
        }
        val newReps = if (grade == Grade.AGAIN) 0 else repetitions + 1
        return CardSrs(
            repetitions = newReps,
            intervalDays = newInterval,
            ease = newEase,
            dueEpochDay = today + newInterval,
            lastGrade = grade,
        )
    }

    private fun Double.roundToIntAtLeast(min: Int): Int =
        Math.round(this).toInt().coerceAtLeast(min)

    companion object {
        private const val MIN_EASE = 1.3
        private const val HARD_FACTOR = 1.2
        private const val EASY_BONUS = 1.3
        private val EASE_DELTA = mapOf(
            Grade.AGAIN to -0.20,
            Grade.HARD to -0.15,
            Grade.GOOD to 0.0,
            Grade.EASY to 0.15,
        )
    }
}
