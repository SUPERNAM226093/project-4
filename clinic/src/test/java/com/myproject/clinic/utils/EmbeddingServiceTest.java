package com.myproject.clinic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddingServiceTest {

    private EmbeddingService embeddingService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Using a dummy token since we're testing utility methods, not API calls
        embeddingService = new EmbeddingService("dummy-token", objectMapper);
    }

    @Test
    void cosineSimilarity_identicalVectors_returns1() {
        List<Double> a = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> b = Arrays.asList(1.0, 0.0, 0.0);
        double result = embeddingService.cosineSimilarity(a, b);
        assertEquals(1.0, result, 0.0001);
    }

    @Test
    void cosineSimilarity_orthogonalVectors_returns0() {
        List<Double> a = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> b = Arrays.asList(0.0, 1.0, 0.0);
        double result = embeddingService.cosineSimilarity(a, b);
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    void cosineSimilarity_oppositeVectors_returnsNeg1() {
        List<Double> a = Arrays.asList(1.0, 0.0, 0.0);
        List<Double> b = Arrays.asList(-1.0, 0.0, 0.0);
        double result = embeddingService.cosineSimilarity(a, b);
        assertEquals(-1.0, result, 0.0001);
    }

    @Test
    void cosineSimilarity_emptyVectors_returns0() {
        double result = embeddingService.cosineSimilarity(List.of(), List.of());
        assertEquals(0.0, result);
    }

    @Test
    void cosineSimilarity_differentSizes_returns0() {
        List<Double> a = Arrays.asList(1.0, 0.0);
        List<Double> b = Arrays.asList(1.0, 0.0, 0.0);
        double result = embeddingService.cosineSimilarity(a, b);
        assertEquals(0.0, result);
    }

    @Test
    void cosineSimilarity_similarVectors_returnsHighValue() {
        List<Double> a = Arrays.asList(1.0, 2.0, 3.0);
        List<Double> b = Arrays.asList(1.1, 2.1, 3.1);
        double result = embeddingService.cosineSimilarity(a, b);
        assertTrue(result > 0.99, "Similar vectors should have high cosine similarity");
    }

    @Test
    void embeddingToJson_andBack_roundTrip() {
        List<Double> original = Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5);
        String json = embeddingService.embeddingToJson(original);
        assertNotNull(json);
        assertFalse(json.isEmpty());

        List<Double> restored = embeddingService.jsonToEmbedding(json);
        assertEquals(original.size(), restored.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i), restored.get(i), 0.0001);
        }
    }

    @Test
    void jsonToEmbedding_nullInput_returnsEmpty() {
        List<Double> result = embeddingService.jsonToEmbedding(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void jsonToEmbedding_blankInput_returnsEmpty() {
        List<Double> result = embeddingService.jsonToEmbedding("  ");
        assertTrue(result.isEmpty());
    }

    @Test
    void jsonToEmbedding_invalidJson_returnsEmpty() {
        List<Double> result = embeddingService.jsonToEmbedding("not-json");
        assertTrue(result.isEmpty());
    }

    @Test
    void embeddingToJson_emptyList_returnsEmptyArray() {
        String json = embeddingService.embeddingToJson(List.of());
        assertEquals("[]", json);
    }
}
