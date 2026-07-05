package cloud.ambroise.heartsutra

import cloud.ambroise.heartsutra.data.srs.CardSrs
import cloud.ambroise.heartsutra.data.srs.Grade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardSrsTest {

    private val today = 1000L

    @Test
    fun newCardIsDue() {
        assertTrue(CardSrs().isDue(today))
    }

    @Test
    fun goodProgressionFollows1Then6Days() {
        val first = CardSrs().grade(Grade.GOOD, today)
        assertEquals(1, first.intervalDays)
        assertEquals(today + 1, first.dueEpochDay)
        assertFalse(first.isDue(today))

        val second = first.grade(Grade.GOOD, today + 1)
        assertEquals(6, second.intervalDays)
    }

    @Test
    fun againResetsRepetitionsAndSchedulesTomorrow() {
        val learned = CardSrs().grade(Grade.GOOD, today).grade(Grade.GOOD, today + 1)
        val lapsed = learned.grade(Grade.AGAIN, today + 7)
        assertEquals(0, lapsed.repetitions)
        assertEquals(1, lapsed.intervalDays)
    }

    @Test
    fun easeNeverDropsBelowFloor() {
        var card = CardSrs()
        repeat(20) { card = card.grade(Grade.AGAIN, today) }
        assertTrue(card.ease >= 1.3)
    }

    @Test
    fun easyAdvancesFasterThanGood() {
        val good = CardSrs().grade(Grade.GOOD, today)
        val easy = CardSrs().grade(Grade.EASY, today)
        assertTrue(easy.intervalDays > good.intervalDays)
    }
}
