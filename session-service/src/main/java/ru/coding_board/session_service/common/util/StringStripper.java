package ru.coding_board.session_service.common.util;

public final class StringStripper {
    private StringStripper() {}

    public static String stripOrNull(String value) {
        return value != null ? value.strip() : null;
    }
}
