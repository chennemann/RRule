package de.chennemann.rrule

import de.chennemann.datetime.atStartOfDay
import de.chennemann.datetime.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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
    fun property() {
        parse("SOME_PROPERTY") shouldBeEqualTo RRule()
        parse("DTSTART") shouldBeEqualTo RRule(
            startDateConfiguration = StartDateProperty(
                startDateTime = LocalDate.now().atStartOfDay(TimeZone.UTC),
                timeZone = TimeZone.UTC
            )
        )
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
    DTSTART
}


fun parse(rrule: String): RRule {

    require(rrule.trim().isNotEmpty()) { "No RRule String Representation found" }

    val contentLines = rrule.unfold().map(::parseContentLine)

    return rrule {
        contentLines.forEach { line ->
            when {
                line isFor PropertyType.DTSTART -> parseRecurrenceStart(line)
            }
        }
    }
}


class RRuleBuilder() {
    var timeZone: TimeZone = TimeZone.UTC
    var start: LocalDateTime? = null

    fun parseRecurrenceStart(dtstartContentline: ContentLine) {
        
    }

    fun build(): RRule {
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


data class RRule(
    private val startDateConfiguration: StartDateProperty? = null
) {
    val startDate = startDateConfiguration?.startDateTime?.toInstant(startDateConfiguration.timeZone)
}

data class StartDateProperty(
    val startDateTime: LocalDateTime = LocalDateTime.parse(""),
    val timeZone: TimeZone = TimeZone.UTC
)

data class ContentLine(
    val name: String,
    val params: List<String> = emptyList(),
    val values: List<String> = emptyList()
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
            val (propertyConfiguration, propertyValue) = contentLineString.split(PROPERTY_VALUE_DELIMITER, limit = 2)
            val values = propertyValue.split(VALUE_DELIMITER)
            when {
                propertyConfiguration.contains(PARAM_DELIMITER) -> {
                    val propertyParams = propertyConfiguration.splitToSequence(PARAM_DELIMITER)
                    val propertyName = propertyParams.first()
                    val params = propertyParams.drop(1).toList()
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