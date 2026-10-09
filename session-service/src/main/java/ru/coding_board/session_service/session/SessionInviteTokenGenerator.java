package ru.coding_board.session_service.session;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component 
public class SessionInviteTokenGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
