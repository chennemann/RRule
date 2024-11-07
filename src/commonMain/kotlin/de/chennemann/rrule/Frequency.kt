package de.chennemann.rrule


enum class Frequency {

    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY;

    override fun toString(): String {
        return name.uppercase()
    }
}