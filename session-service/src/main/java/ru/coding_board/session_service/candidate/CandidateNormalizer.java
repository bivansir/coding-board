package ru.coding_board.session_service.candidate;

import org.springframework.stereotype.Component;

@Component
public class CandidateNormalizer {
    public String normalizeFioPart(String fioPart) {
        if (fioPart == null || fioPart.isBlank()) {
            return null;
        }

        String normalized = fioPart.trim().replaceAll("\\s{2,}", " ");
        char[] chars = normalized.toCharArray();

        chars[0] = Character.toUpperCase(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            if (chars[i - 1] == ' ' || chars[i - 1] == '-') {
                chars[i] = Character.toUpperCase(chars[i]);
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
            }
        }

        return new String(chars);
    }
}
