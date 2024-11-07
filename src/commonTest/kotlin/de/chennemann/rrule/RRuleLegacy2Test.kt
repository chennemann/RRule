package de.chennemann.rrule

import org.junit.Test
import kotlin.test.assertEquals

class RRuleLegacy2Test {

    @Test
    fun unfolding() {
        val unfoldedRRule = "PROPERTY:this\r\n is a test\r\nANOTHER_PROPERTY: well, well, well".unfoldLegacy()
        unfoldedRRule.size shouldBeEqualTo 2
        unfoldedRRule[0] shouldBeEqualTo "PROPERTY:this is a test"
        unfoldedRRule[1] shouldBeEqualTo "ANOTHER_PROPERTY: well, well, well"
    }

    @Test
    fun propertyDetection() {
        val unfoldedRRule = "PROPERTY_NAME;TZID=America/New_York:VALUE1,VALUE2"
    }

}

const val TIMEZONE_ID = "TZID"

const val VALUE_SEPARATOR = ","
const val PROPERTY_SEPARATOR = ";"

fun String.unfoldLegacy(): List<String> {
    return this.split("\r\n").fold(mutableListOf()) { acc, line ->
        if (line.startsWith(" ") || line.startsWith("\t")) {
            acc[acc.size - 1] += " ${line.trimStart()}"
        } else {
            acc.add(line)
        }
        acc
    }
}

//infix fun <T> T.shouldBeEqualTo(expected: T) {
//    assertEquals(expected, this)
//}

