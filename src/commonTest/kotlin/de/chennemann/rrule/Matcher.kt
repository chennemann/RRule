package de.chennemann.rrule

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

data class Matcher<T>(val valueFactory: () -> T) {
    infix fun shouldBeEqualTo(expected: T) {
        assertEquals(expected, valueFactory())
    }

    infix fun shouldNotBeEqualTo(expected: T) {
        assertNotEquals(expected, valueFactory())
    }

    fun fails() = assertFails { valueFactory() }
    inline fun <reified E : Throwable> failsWith(): E {
        return assertFailsWith<E> { valueFactory() }
    }
}

fun <T> expectThat(value: () -> T): Matcher<T> = Matcher(value)
fun <T> expectThat(value: T): Matcher<T> = Matcher { value }
infix fun <T> T.shouldBeEqualTo(expected: T) = expectThat { this } shouldBeEqualTo expected
infix fun <T> T.shouldNotBeEqualTo(expected: T) = expectThat { this } shouldNotBeEqualTo expected


class MatcherTest {

    @Test
    fun shouldBeEqualTo() {
        1 shouldBeEqualTo 1
        "1" shouldBeEqualTo "1"
        expectThat { 1 } shouldBeEqualTo 1
        expectThat { "1" } shouldBeEqualTo "1"

        assertFails {
            expectThat { 1 } shouldBeEqualTo 27
        }
        assertFails {
            expectThat { "1" } shouldBeEqualTo "27"
        }
    }

    @Test
    fun shouldNotBeEqualTo() {
        expectThat { 1 } shouldNotBeEqualTo 27
        expectThat { "1" } shouldNotBeEqualTo "27"

        assertFails {
            expectThat { 1 } shouldNotBeEqualTo 1
        }
        assertFails {
            expectThat { "1" } shouldNotBeEqualTo "1"
        }
    }

    @Test
    fun fails() {
        expectThat { throw IllegalArgumentException() }.fails()

        assertFails {
            expectThat { "Nothing happens" }.fails()
        }
    }

    @Test
    fun failsWith() {
        expectThat { throw IllegalArgumentException() }.failsWith<IllegalArgumentException>()

        assertFails {
            expectThat { throw IllegalArgumentException() }.failsWith<IllegalStateException>()
        }

        assertFails {
            expectThat { "Nothing happens" }.fails()
        }
    }


}