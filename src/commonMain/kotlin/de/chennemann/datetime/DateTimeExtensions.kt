package de.chennemann.datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

val LocalTime.Companion.MIN get() = LocalTime(0, 0)
val LocalTime.Companion.MAX get() = LocalTime(23, 59, 59, 999999999)

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
    Clock.System.now().toLocalDateTime(timeZone)

fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    LocalDateTime.now(timeZone).date

fun LocalDate.atStartOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime =
    this.atStartOfDayIn(timeZone).toLocalDateTime(timeZone)

fun LocalDate.atEndOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime {
    return this
        .plus(1, DateTimeUnit.DAY)
        .atStartOfDayIn(timeZone).minus(1.nanoseconds)
        .toLocalDateTime(timeZone)
}

fun LocalDateTime.evaluateDurationUntil(end: LocalDateTime, timeZone: TimeZone = TimeZone.currentSystemDefault()): Duration {
    if (end < this) throw IllegalArgumentException("end must be greater than start")
    return end.toInstant(timeZone) - this.toInstant(timeZone)
}

fun LocalDate.plusDays(days: Long): LocalDate = this.plus(days, DateTimeUnit.DAY)
fun LocalDate.plusWeeks(weeks: Long): LocalDate = this.plus(weeks, DateTimeUnit.WEEK)
fun LocalDate.plusMonths(months: Long): LocalDate = this.plus(months, DateTimeUnit.MONTH)
fun LocalDate.plusYears(years: Long): LocalDate = this.plus(years, DateTimeUnit.YEAR)

fun LocalDate.isAfter(other: LocalDate): Boolean = this > other
