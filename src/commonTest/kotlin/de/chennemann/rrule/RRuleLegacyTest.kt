package de.chennemann.rrule

import kotlinx.datetime.LocalDate
import java.time.Month
import kotlin.test.Test
import kotlin.test.assertEquals


class RRuleLegacyTest {

//    private val ruleStrings = listOf(
//        "RRULE:FREQ=DAILY;INTERVAL=1",
//        "RRULE:FREQ=WEEKLY;INTERVAL=2",
//        "RRULE:FREQ=MONTHLY;INTERVAL=3",
//        "RRULE:FREQ=YEARLY;INTERVAL=4",
//        "RRULE:FREQ=WEEKLY;INTERVAL=2;BYDAY=FR;WKST=SU",
//        "RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=FR,SU",
//        "RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20201009T220000Z",
//        "RRULE:FREQ=DAILY;INTERVAL=10;COUNT=5",
//        "RRULE:FREQ=MONTHLY;INTERVAL=2;COUNT=5;BYDAY=2FR",
//        "RRULE:FREQ=MONTHLY;INTERVAL=2;COUNT=10;BYDAY=1SU,-1SU",
//        "RRULE:FREQ=MONTHLY;INTERVAL=1;COUNT=4;BYMONTHDAY=10,11,15",
//        "RRULE:FREQ=YEARLY;INTERVAL=2;COUNT=10;BYMONTH=2,6"
//    )
//
//    @Test
//    fun testRRule() {
//
//        for (ruleString in ruleStrings) {
//            val rule = RRule(ruleString)
//            assertEquals(ruleString, rule.toRFC5545String())
//        }
//    }
//
//    @Test
//    fun testWeekdayFromString() {
//
//        assertEquals(Weekday.Sunday, Weekday.fromString("Sunday"))
//        assertEquals(Weekday.Monday, Weekday.fromString("mo"))
//        assertEquals(Weekday.Thursday, Weekday.fromString("th"))
//        assertEquals(Weekday.Friday, Weekday.fromString("FRIDAY"))
//        assertEquals(Weekday.Wednesday, Weekday.fromString("wednesday"))
//        assertNull(Weekday.fromString(""))
//        assertNull(Weekday.fromString(null))
//        assertNull(Weekday.fromString("s"))
//    }

    @Test
    fun daily10Occurrences() {
        val rrule = """
           DTSTART;TZID=America/New_York:19970902T090000
           RRULE:FREQ=DAILY;COUNT=10
        """.trimIndent()

        val recurrenceRule = RRuleLegacy(rrule)
        assertEquals(10, recurrenceRule.count)
        val occurrences = recurrenceRule.generateOccurrences(LocalDate(2024, Month.APRIL, 3)).toList()

        assertEquals(
            listOf(
                LocalDate(2024, Month.APRIL, 3),
                LocalDate(2024, Month.APRIL, 4),
                LocalDate(2024, Month.APRIL, 5),
                LocalDate(2024, Month.APRIL, 6),
                LocalDate(2024, Month.APRIL, 7),
                LocalDate(2024, Month.APRIL, 8),
                LocalDate(2024, Month.APRIL, 9),
                LocalDate(2024, Month.APRIL, 10),
            ),
            occurrences
        )
    }


}