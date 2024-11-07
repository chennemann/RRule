package de.chennemann.rrule

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.char
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus


open class RRuleLegacy() {

    private val name = "RRULE"

    var freq: Frequency = Frequency.DAILY

    var wkst: Weekday? = null
    var until: LocalDateTime? = null
    var count = 0
    var interval = 0

    val byDay = arrayListOf<WeekdayNum>()
    val byMonth = arrayListOf<Int>() // in +/-[1-12]
    val byMonthDay = arrayListOf<Int>()  // in +/-[1-31]
    val byWeekNo = arrayListOf<Int>()  // in +/-[1-53]
    val byYearDay = arrayListOf<Int>()  // in +/-[1-366]
    val bySetPos = arrayListOf<Int>()  // in +/-[1-366]

    constructor(rfc5545String: String) : this() {
        val components = rfc5545String.replace("$name:", "").split(";", "=")
        var i = 0
        while (i < components.size) {
            val component = components[i]
            if (component == "FREQ") {
                i += 1
                freq = when (components[i]) {
                    "DAILY" -> Frequency.DAILY
                    "WEEKLY" -> Frequency.WEEKLY
                    "MONTHLY" -> Frequency.MONTHLY
                    "YEARLY" -> Frequency.YEARLY
                    else -> Frequency.DAILY
                }
            }
            if (component == "INTERVAL") {
                i += 1
                interval = components[i].toIntOrNull() ?: 1
            }
            if (component == "BYDAY") {
                i += 1
                val dayStrings = components[i].split(",")
                for (dayString in dayStrings) {
                    val weekDay = weekDayFromString(dayString)

                    if (weekDay != null) {
                        if (dayString.length > 2) {
                            val number = dayString.replace(Regex("[^-?0-9]+"), "").toIntOrNull() ?: 0
                            byDay.add(WeekdayNum(number, weekDay))
                        } else {
                            byDay.add(WeekdayNum(0, weekDay))
                        }
                    }
                }
            }

            if (component == "BYMONTHDAY") {
                i += 1
                val dayStrings = components[i].split(",")
                for (dayString in dayStrings) {
                    val monthDay = dayString.toIntOrNull()
                    if (monthDay != null) {
                        byMonthDay.add(monthDay)
                    }
                }
            }

            if (component == "BYMONTH") {
                i += 1
                val monthStrings = components[i].split(",")
                for (monthString in monthStrings) {
                    val month = monthString.toIntOrNull()
                    if (month != null) {
                        byMonth.add(month)
                    }
                }
            }

            if (component == "BYWEEKNO") {
                i += 1
                val weekStrings = components[i].split(",")
                for (weekString in weekStrings) {
                    val week = weekString.toIntOrNull()
                    if (week != null) {
                        byWeekNo.add(week)
                    }
                }
            }

            if (component == "BYYEARDAY") {
                i += 1
                val dayStrings = components[i].split(",")
                for (dayString in dayStrings) {
                    val yearDay = dayString.toIntOrNull()
                    if (yearDay != null) {
                        byYearDay.add(yearDay)
                    }
                }
            }

            if (component == "BYSETPOS") {
                i += 1
                val posStrings = components[i].split(",")
                for (posString in posStrings) {
                    val pos = posString.toIntOrNull()
                    if (pos != null) {
                        bySetPos.add(pos)
                    }
                }
            }

            if (component == "COUNT") {
                i += 1
                count = components[i].toIntOrNull() ?: 1
            } else if (component == "UNTIL") {
                i += 1
                until = LocalDateTime.parse(components[i], dateFormatter)
            }

            if (component == "WKST") {
                i += 1
                wkst = weekDayFromString(components[i])
            }
            i++
        }
    }

    /**
     * Transforms this RRule to a RFC5545 standard iCal String.
     */
    fun toRFC5545String(): String {

        val buf = StringBuilder()
        buf.append("$name:")
        buf.append("FREQ=").append(freq.toString())
        if (interval > 0) {
            buf.append(";INTERVAL=").append(interval)
        }
        if (until != null) {
            buf.append(";UNTIL=").append(dateFormatter.format(until!!))
        }
        if (count > 0) {
            buf.append(";COUNT=").append(count)
        }
        if (byYearDay.isNotEmpty()) {
            buf.append(";BYYEARDAY=")
            writeIntList(byYearDay, buf)
        }
        if (byMonth.isNotEmpty()) {
            buf.append(";BYMONTH=")
            writeIntList(byMonth, buf)
        }
        if (byMonthDay.isNotEmpty()) {
            buf.append(";BYMONTHDAY=")
            writeIntList(byMonthDay, buf)
        }
        if (byWeekNo.isNotEmpty()) {
            buf.append(";BYWEEKNO=")
            writeIntList(byWeekNo, buf)
        }
        if (byDay.isNotEmpty()) {
            buf.append(";BYDAY=")
            var first = true
            for (day in byDay) {
                if (!first) {
                    buf.append(',')
                } else {
                    first = false
                }
                buf.append(day.toICalString())
            }
        }
        if (bySetPos.isNotEmpty()) {
            buf.append(";BYSETPOS=")
            writeIntList(bySetPos, buf)
        }
        if (wkst != null) {
            buf.append(";WKST=").append(wkst?.initials)
        }

        return buf.toString()
    }

    // Generating date sequence
    fun generateOccurrences(start: LocalDate): Sequence<LocalDate> {
        var currentCount = 0
        return generateSequence(start) { current ->
            if (count != null && currentCount >= count) return@generateSequence null
            if (until != null && current > until!!.date ) return@generateSequence null

            currentCount++
            when (freq) {
                Frequency.DAILY -> current.plusDays(interval.toLong())
                Frequency.WEEKLY -> {
                    var nextDate = current.plusWeeks(interval.toLong())
                    byDay?.let {
                        nextDate = it.firstOrNull()?.let { day ->
                            nextDate.nextOrSame(DayOfWeek.valueOf(day.weekday.name))
                        } ?: nextDate
                    }
                    nextDate
                }
                Frequency.MONTHLY -> current.plusMonths(interval.toLong())
                Frequency.YEARLY -> current.plusYears(interval.toLong())
            }
        }.filter { date ->
            (byMonth == null || byMonth.contains(date.monthNumber)) &&
                    (byMonthDay == null || byMonthDay.contains(date.dayOfMonth)) &&
                    (byDay == null || byDay.any { it.weekday.name == date.dayOfWeek.name })
        }
    }

    private fun LocalDate.nextOrSame(dayOfWeek: DayOfWeek): LocalDate {
        val currentDay = this.dayOfWeek
        val daysToAdd = if (currentDay <= dayOfWeek) {
            dayOfWeek.isoDayNumber - currentDay.isoDayNumber
        } else {
            7 - (currentDay.isoDayNumber - dayOfWeek.isoDayNumber)
        }
        return this.plusDays(daysToAdd.toLong())
    }

    private fun writeIntList(integers: List<Int>, out: StringBuilder) {
        for (i in integers.indices) {
            if (0 != i) {
                out.append(',')
            }
            out.append(integers[i])
        }
    }

    private fun weekDayFromString(dayString: String): Weekday? {
        return when {
            dayString.contains("SU") -> Weekday.SUNDAY
            dayString.contains("MO") -> Weekday.MONDAY
            dayString.contains("TU") -> Weekday.TUESDAY
            dayString.contains("WE") -> Weekday.WEDNESDAY
            dayString.contains("TH") -> Weekday.THURSDAY
            dayString.contains("FR") -> Weekday.FRIDAY
            dayString.contains("SA") -> Weekday.SATURDAY
            else -> null
        }
    }

    companion object {
        private val dateFormatter = LocalDateTime.Format {
            year()
            monthNumber()
            dayOfMonth()
            char('T')
            hour()
            minute()
            second()
            char('Z')
        }
    }
}




fun LocalDate.plusDays(days: Long): LocalDate = this.plus(days, DateTimeUnit.DAY)
fun LocalDate.plusWeeks(weeks: Long): LocalDate = this.plus(weeks, DateTimeUnit.WEEK)
fun LocalDate.plusMonths(months: Long): LocalDate = this.plus(months, DateTimeUnit.MONTH)
fun LocalDate.plusYears(years: Long): LocalDate = this.plus(years, DateTimeUnit.YEAR)