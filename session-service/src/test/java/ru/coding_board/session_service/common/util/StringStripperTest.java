package ru.coding_board.session_service.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringStripperTest {
    @Test
    void shouldStripString() {
        // given
        String givenString = " JAVA ";

        // when
        String result = StringStripper.stripOrNull(givenString);

        // then
        assertEquals("JAVA", result);
    }
}
