package com.example.rateyourrecord.service;

import com.example.rateyourrecord.client.DiscogsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DiscogsServiceTest {

    private DiscogsService discogsService;

    @BeforeEach
    void setUp() {
        DiscogsClient mockClient = mock(DiscogsClient.class);
        discogsService = new DiscogsService(mockClient);
    }

    @ParameterizedTest
    @CsvSource({
            "'1:23', 83",
            "'10:05', 605",
            "'0:00', 0",
            "'0:59', 59"
    })
    void parseDurationToSeconds_shouldReturnCorrectSeconds_forValidFormat(String duration, long expectedSeconds) {
        try {
            java.lang.reflect.Method method = DiscogsService.class.getDeclaredMethod("parseDurationToSeconds", String.class);
            method.setAccessible(true);
            long actualSeconds = (long) method.invoke(discogsService, duration);
            assertEquals(expectedSeconds, actualSeconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parseDurationToSeconds_shouldThrowException_forInvalidFormat() {
        // Verifiera att en exception kastas för ogiltigt format
        try {
            java.lang.reflect.Method method = DiscogsService.class.getDeclaredMethod("parseDurationToSeconds", String.class);
            method.setAccessible(true);

            Exception exception = assertThrows(java.lang.reflect.InvocationTargetException.class, () -> {
                method.invoke(discogsService, "invalid");
            });

            assertEquals(IllegalArgumentException.class, exception.getCause().getClass());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void parseDurationToSeconds_shouldReturnZero_forNullOrEmptyDuration(String duration) {
        // Detta test verifierar att både null och tomma strängar hanteras säkert och returnerar 0.
        try {
            java.lang.reflect.Method method = DiscogsService.class.getDeclaredMethod("parseDurationToSeconds", String.class);
            method.setAccessible(true);

            long actualSeconds = (long) method.invoke(discogsService, duration);
            assertEquals(0, actualSeconds);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
