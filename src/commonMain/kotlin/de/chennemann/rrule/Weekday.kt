package de.chennemann.rrule

enum class Weekday(val initials: String) {

    MONDAY("MO"),
    TUESDAY("TU"),
    WEDNESDAY("WE"),
    THURSDAY("TH"),
    FRIDAY("FR"),
    SATURDAY("SA"),
    SUNDAY("SU");

    companion object {
        fun fromString(string: String?): Weekday? {
            return if (string.isNullOrEmpty() || string.length < 2) {
                null
            } else {
                try {
                    string.lowercase()
                    string[0].uppercase()
                    valueOf(string)
                } catch (e: Exception) {
                    val dayInitials = string.substring(0, 2)

                    for (value in entries) {
                        if (value.initials.equals(dayInitials, true)) {
                            return value
                        }
                    }
                    null
                }
            }
        }
    }
}