package zielu.gittoolbox.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import zielu.gittoolbox.formatter.RegExpFormatter;

class RegExpFormatterTest {

    @DisplayName("Formatting with pattern")
    @ParameterizedTest(name = "''{1}'' formatted with ''{0}'' should return ''{2}''")
    @CsvSource({
        "(.*), abc, abc",
        "(.*)b(.*), abc, ac",
        "aaa, abc, abc",
        ", abc, abc"
    })
    void formatShouldReturnExpectedResult(String pattern, String input, String expected) {
        RegExpFormatter formatter = RegExpFormatter.create(pattern);
        assertEquals(expected, formatter.format(input).text);
    }

    @DisplayName("Formatting with empty")
    @ParameterizedTest(name = "Input formatted with ''{0}'' should return input")
    @ValueSource(strings = {"   ", ""})
    void formatShouldReturnInputIfPatternEmpty(String pattern) {
        final String input = "abc";
        assertEquals(input, RegExpFormatter.create(pattern).format(input).text);
    }
}