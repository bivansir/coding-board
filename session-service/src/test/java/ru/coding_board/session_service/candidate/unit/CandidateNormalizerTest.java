package ru.coding_board.session_service.candidate.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.coding_board.session_service.candidate.CandidateNormalizer;

@ExtendWith(MockitoExtension.class)
public class CandidateNormalizerTest {
    @InjectMocks
    CandidateNormalizer normalizer;

    @Test
    void shouldNormalizeFioPart() {
        // given
        String name = " иЛьЯ   ";

        // when
        String result = normalizer.normalizeFioPart(name);

        // then
        assertEquals("Илья", result);
    }

    @Test
    void shouldNormalizeCombinedSurnames() {
        // given
        String surname1 = " салтыков-щедрин";
        String surname2 = " ван  Бетховен";
        String surname3 = "фон  шварц-линден";

        // when
        String result1 = normalizer.normalizeFioPart(surname1);
        String result2 = normalizer.normalizeFioPart(surname2);
        String result3 = normalizer.normalizeFioPart(surname3);

        // then
        assertEquals("Салтыков-Щедрин", result1);
        assertEquals("Ван Бетховен", result2);
        assertEquals("Фон Шварц-Линден", result3);
    }

    @Test
    void shouldReturnNullIfNullOrBlank() {
        // given
        String blank = "";
        String nulla = null;

        // when
        String result1 = normalizer.normalizeFioPart(blank);
        String result2 = normalizer.normalizeFioPart(nulla);

        // then
        assertEquals(null, result1);
        assertEquals(null, result2);
    }
}
