package de.chennemann.rrule

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test


class RRuleParserTest {

    @Test
    fun emptyString() {
        expectThat { parse("") }
            .failsWith<IllegalArgumentException>()
            .message shouldBeEqualTo "No RRule String Representation found"

        expectThat { parse(" ") }
            .failsWith<IllegalArgumentException>()
            .message shouldBeEqualTo "No RRule String Representation found"

        expectThat { parse("\n") }
            .failsWith<IllegalArgumentException>()
            .message shouldBeEqualTo "No RRule String Representation found"

        expectThat { parse("\t") }
            .failsWith<IllegalArgumentException>()
            .message shouldBeEqualTo "No RRule String Representation found"
    }

    @Test
    fun unknownProperty() {
        parse("SOME_UNKNOWN_PROPERTY") shouldBeEqualTo RRule()
    }

    @Test
    fun dtstart() {
        parse("DTSTART") shouldBeEqualTo RRule()
        parse("DTSTART:19970714T173000Z") shouldBeEqualTo RRule(
            startDateConfiguration = StartDateProperty(
                startDateTime = LocalDateTime(
                    year = 1997,
                    monthNumber = 7,
                    dayOfMonth = 14,
                    hour = 17,
                    minute = 30,
                    second = 0
                ),
                timeZone = TimeZone.UTC
            )
        )
        parse("DTSTART:19970714T173000") shouldBeEqualTo RRule(
            startDateConfiguration = StartDateProperty(
                startDateTime = LocalDateTime(
                    year = 1997,
                    monthNumber = 7,
                    dayOfMonth = 14,
                    hour = 17,
                    minute = 30,
                    second = 0
                ),
                timeZone = TimeZone.currentSystemDefault()
            )
        )
        parse("DTSTART;TZID=America/New_York:19970714T173000") shouldBeEqualTo RRule(
            startDateConfiguration = StartDateProperty(
                startDateTime = LocalDateTime(
                    year = 1997,
                    monthNumber = 7,
                    dayOfMonth = 14,
                    hour = 17,
                    minute = 30,
                    second = 0
                ),
                timeZone = TimeZone.of("America/New_York")
            )
        )
        parse("DTSTART;TZID=America/New_York:19970714T173000Z") shouldBeEqualTo RRule(
            startDateConfiguration = StartDateProperty(
                startDateTime = LocalDateTime(
                    year = 1997,
                    monthNumber = 7,
                    dayOfMonth = 14,
                    hour = 13,
                    minute = 30,
                    second = 0
                ),
                timeZone = TimeZone.of("America/New_York")
            )
        )
    }

    /*
    //    @Test
    //    fun contentLine() {
    //        parse("PROPERTY_NAME") shouldBeEqualTo RRule(ContentLine(name = "PROPERTY_NAME"))
    //        parse("PROPERTY_NAME:PROPERTY_VALUE") shouldBeEqualTo RRule(
    //            ContentLine(
    //                name = "PROPERTY_NAME",
    //                values = listOf("PROPERTY_VALUE")
    //            )
    //        )
    //        parse("PROPERTY_NAME;PARAM1=PARAM1_VALUE;PARAM2=PARAM2_VALUE:PROPERTY_VALUE") shouldBeEqualTo RRule(
    //            ContentLine(
    //                name = "PROPERTY_NAME",
    //                params = listOf("PARAM1=PARAM1_VALUE", "PARAM2=PARAM2_VALUE"),
    //                values = listOf("PROPERTY_VALUE")
    //            )
    //        )
    //        parse("PROPERTY_NAME;PARAM1=PARAM1_VALUE;PARAM2=PARAM2_VALUE:PROPERTY_VALUE1,PROPERTY_VALUE2") shouldBeEqualTo RRule(
    //            ContentLine(
    //                name = "PROPERTY_NAME",
    //                params = listOf("PARAM1=PARAM1_VALUE", "PARAM2=PARAM2_VALUE"),
    //                values = listOf("PROPERTY_VALUE1", "PROPERTY_VALUE2")
    //            )
    //        )
    //    }
    //
    //    @Test
    //    fun multipleContentLines() {
    //        parse("PROPERTY_NAME1\r\nPROPERTY_NAME2") shouldBeEqualTo RRule(ContentLine(name = "PROPERTY_NAME1"), ContentLine(name = "PROPERTY_NAME2"))
    //        parse("PROPERTY_NAME1:PROPERTY_VALUE\r\nPROPERTY_NAME2:PROPERTY_VALUE") shouldBeEqualTo RRule(
    //            ContentLine(
    //                name = "PROPERTY_NAME1",
    //                values = listOf("PROPERTY_VALUE")
    //            ),
    //            ContentLine(
    //                name = "PROPERTY_NAME2",
    //                values = listOf("PROPERTY_VALUE")
    //            )
    //        )
    //        parse("PROPERTY_NAME1;PARAM1=PARAM1_VALUE;PARAM2=PARAM2_VALUE:PROPERTY_VALUE\r\nPROPERTY_NAME2\r\nPROPERTY_NAME3;PARAM1=PARAM1_VALUE;PARAM2=PARAM2_VALUE:PROPERTY_VALUE1,PROPERTY_VALUE2") shouldBeEqualTo RRule(
    //            ContentLine(
    //                name = "PROPERTY_NAME1",
    //                params = listOf("PARAM1=PARAM1_VALUE", "PARAM2=PARAM2_VALUE"),
    //                values = listOf("PROPERTY_VALUE")
    //            ),
    //            ContentLine(name = "PROPERTY_NAME2"),
    //            ContentLine(
    //                name = "PROPERTY_NAME3",
    //                params = listOf("PARAM1=PARAM1_VALUE", "PARAM2=PARAM2_VALUE"),
    //                values = listOf("PROPERTY_VALUE1", "PROPERTY_VALUE2")
    //            )
    //        )
    //    }
    //
    //    @Test
    //    fun multilineContentLine() {
    //        parse("PROPER\r\n TY_NAME") shouldBeEqualTo RRule(ContentLine(name = "PROPERTY_NAME"))
    //        parse("PROPER\r\n\tTY_NAME") shouldBeEqualTo RRule(ContentLine(name = "PROPERTY_NAME"))
    //
    //        // Trim only a singular whitespace and respect additional whitespace
    //        parse("PROPER\r\n  TY_NAME") shouldBeEqualTo RRule(ContentLine(name = "PROPER TY_NAME"))
    //        parse("PROPER\r\n\t TY_NAME") shouldBeEqualTo RRule(ContentLine(name = "PROPER TY_NAME"))
    //
    //
    //        parse("PROPERTY_NAME1;PA\r\n RAM1\r\n\t=PARAM1_VALUE;PARAM2=PARAM2_VALUE:PROPERTY_VALUE\r\nPROPERTY_NAME2\r\nPROPERTY_NAME3;PARAM1=P\r\n ARAM1_VALUE;\r\n\tPARAM2=PARAM2_VALUE:PROPERTY_VALUE1,PROPERTY_VALUE2") shouldBeEqualTo RRule(
    //            ContentLine(
    //                name = "PROPERTY_NAME1",
    //                params = listOf("PARAM1=PARAM1_VALUE", "PARAM2=PARAM2_VALUE"),
    //                values = listOf("PROPERTY_VALUE")
    //            ),
    //            ContentLine(name = "PROPERTY_NAME2"),
    //            ContentLine(
    //                name = "PROPERTY_NAME3",
    //                params = listOf("PARAM1=PARAM1_VALUE", "PARAM2=PARAM2_VALUE"),
    //                values = listOf("PROPERTY_VALUE1", "PROPERTY_VALUE2")
    //            )
    //        )
    //    }
    */
}


const val LINE_DELIMITER = "\r\n"
const val PROPERTY_VALUE_DELIMITER = ":"
const val PARAM_DELIMITER = ";"
const val VALUE_DELIMITER = ","
val ALLOWED_CONTINUATION_WHITESPACES = setOf(" ", "\t")


enum class PropertyType {
    DTSTART, RRULE
}

enum class Modifier {
    TZID, FREQ, INTERVAL, COUNT, UNTIL, BYDAY, BYMONTH
}


fun parse(rrule: String): RRule {

    require(rrule.trim().isNotEmpty()) { "No RRule String Representation found" }

    val contentLines = rrule.unfold().map(::parseContentLine)

    return rrule {
        contentLines.forEach { line ->
            when {
                line isFor PropertyType.DTSTART -> parseRecurrenceStart(line)
                line isFor PropertyType.RRULE -> parseRRule(line)
            }
        }
    }
}

// Formatter for datetime strings without timezone information
//val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
val LOCAL_DATE_TIME_FORMATTER = LocalDateTime.Format {
    year();monthNumber();dayOfMonth();char('T');hour();minute();second()
}

class RRuleBuilder() {
    var timeZone: TimeZone = TimeZone.UTC
    var start: LocalDateTime? = null
    var frequency: Frequency? = null
    var interval: Int = 1 // default to 1 if unspecified
    var count: Int? = null
    var until: LocalDateTime? = null

    internal fun parseRecurrenceStart(dtstart: ContentLine) {
        val dtstartValue = dtstart.values.propertyValue() // Strip 'Z' if present
        if (dtstartValue != null) {

            start = LocalDateTime.parse(dtstartValue.removeSuffix("Z"), LOCAL_DATE_TIME_FORMATTER)

            val isUTCValue = dtstartValue.endsWith("Z")
            val tzid = dtstart.params.paramValue(Modifier.TZID)
            val hasTZID = tzid != null

            when {
                hasTZID && isUTCValue -> {
                    timeZone = TimeZone.of(tzid)
                    start = start?.toInstant(TimeZone.UTC)?.toLocalDateTime(TimeZone.of(tzid))
                }

                hasTZID && !isUTCValue -> {
                    timeZone = TimeZone.of(tzid)
                }

                !hasTZID && isUTCValue -> {
                    timeZone = TimeZone.UTC
                }

                // Default to system timezone or UTC if no TZID and no Z
                else -> timeZone =TimeZone.currentSystemDefault()

            }
        }
    }

    internal fun parseRRule(line: ContentLine) {
    }

    internal fun build(): RRule {
        return RRule(
            startDateConfiguration = start?.let { StartDateProperty(startDateTime = it, timeZone = timeZone) }
        )
    }
}

fun rrule(init: RRuleBuilder.() -> Unit): RRule {
    val builder = RRuleBuilder()
    builder.init()
    return builder.build()
}

fun RRuleBuilder.frequency(frequency: Frequency) = apply { this.frequency = frequency }
fun RRuleBuilder.interval(interval: Int) = apply { this.interval = interval }
fun RRuleBuilder.count(count: Int) = apply { this.count = count }
fun RRuleBuilder.until(until: LocalDateTime) = apply { this.until = until }
fun RRuleBuilder.timezone(timeZone: TimeZone) = apply { this.timeZone = timeZone }


data class RRule(
    private val startDateConfiguration: StartDateProperty? = null,
    val frequency: Frequency? = null,
    val interval: Int = 1,
    val count: Int? = null,
    val until: LocalDateTime? = null
) {
    val startDate = startDateConfiguration?.startDateTime?.toInstant(startDateConfiguration.timeZone)

    override fun toString(): String {
        val components = mutableListOf<String>()

        startDateConfiguration?.let {
            components.add("DTSTART;TZID=${it.timeZone.id}:${it.startDateTime}")
        }
        frequency?.let { components.add("FREQ=${it.name}") }
        if (interval > 1) components.add("INTERVAL=$interval")
        count?.let { components.add("COUNT=$it") }
        until?.let { components.add("UNTIL=$it") }

        return components.joinToString(";")
    }
}

data class StartDateProperty(
    val startDateTime: LocalDateTime = LocalDateTime.parse(""),
    val timeZone: TimeZone = TimeZone.UTC
)

sealed interface Param {
    val name: String

    data class MarkerParam(override val name: String) : Param
    data class ConfigParam(override val name: String, val value: String) : Param
}

fun List<Param>.paramValue(enum: Modifier) = this
    .filterIsInstance<Param.ConfigParam>()
    .firstOrNull { it.name == enum.name }
    ?.value

sealed interface Value {

    val value: String

    data class StandaloneValue(override val value: String) : Value
    data class ConfigValue(val name: String, override val value: String) : Value
}

fun List<Value>.valueModifierValue(enum: Modifier) = this
    .filterIsInstance<Value.ConfigValue>()
    .firstOrNull { it.name == enum.name }
    ?.value

fun List<Value>.propertyValue() = this
    .filterIsInstance<Value.StandaloneValue>()
    .firstOrNull()
    ?.value


data class ContentLine(
    val name: String,
    val params: List<Param> = emptyList(),
    val values: List<Value> = emptyList()
) {

    infix fun isFor(propertyType: PropertyType): Boolean {
        return propertyType.name.equals(name, ignoreCase = true)
    }
}


private fun String.unfold(): List<String> = this
    .split(LINE_DELIMITER)
    .fold(emptyList()) { contentLines, nextLine ->
        when {
            nextLine.startsWithAnyOf(ALLOWED_CONTINUATION_WHITESPACES) -> {
                contentLines.replaceLast { currentValue ->
                    currentValue + nextLine.drop(1)
                }
            }

            else -> contentLines + nextLine
        }
    }

private fun parseContentLine(contentLineString: String): ContentLine {
    return when {
        contentLineString.contains(PROPERTY_VALUE_DELIMITER) -> {
            val (propertyConfiguration, propertyValue) = contentLineString.split(
                PROPERTY_VALUE_DELIMITER,
                limit = 2
            )
            @Suppress("NAME_SHADOWING") val values = propertyValue.split(VALUE_DELIMITER).map { value ->
                when {
                    value.contains("=") -> {
                        val (name, value) = value.split("=", limit = 2)
                        Value.ConfigValue(name, value)
                    }

                    else -> Value.StandaloneValue(value)
                }
            }
            when {
                propertyConfiguration.contains(PARAM_DELIMITER) -> {
                    val propertyParams = propertyConfiguration.split(PARAM_DELIMITER)
                    val propertyName = propertyParams.first()
                    val params = propertyParams.drop(1).map { param ->
                        when {
                            param.contains("=") -> {
                                val (name, value) = param.split("=", limit = 2)
                                Param.ConfigParam(name, value)
                            }

                            else -> Param.MarkerParam(param)
                        }
                    }
                    ContentLine(name = propertyName, params = params, values = values)
                }

                else -> ContentLine(name = propertyConfiguration, values = values)
            }
        }

        else -> ContentLine(contentLineString)
    }
}


private fun <T> List<T>.replaceAt(index: Int, transformation: (T) -> T): List<T> {
    val elementToReplace = this.elementAt(index)
    return this.minus(elementToReplace).plus(transformation(elementToReplace))
}

private fun <T> List<T>.replaceFirst(transformation: (T) -> T): List<T> = replaceAt(0, transformation)
private fun <T> List<T>.replaceLast(transformation: (T) -> T): List<T> = replaceAt(lastIndex, transformation)

private fun String.startsWithAnyOf(prefixes: Set<String>, startIndex: Int = 0, ignoreCase: Boolean = true) =
    prefixes.any { this.startsWith(it, startIndex, ignoreCase) }
