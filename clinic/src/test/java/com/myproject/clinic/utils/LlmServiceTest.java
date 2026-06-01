package com.myproject.clinic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LlmServiceTest {

    private LlmService llmService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        llmService = new LlmService("dummy-token", objectMapper);
    }

    @Test
    void stripThinkTags_removesSimpleThinkBlock() throws Exception {
        // Access private method via reflection for testing
        var method = LlmService.class.getDeclaredMethod("stripThinkTags", String.class);
        method.setAccessible(true);

        String input = "<think>This is reasoning</think>This is the answer";
        String result = (String) method.invoke(llmService, input);
        assertEquals("This is the answer", result);
    }

    @Test
    void stripThinkTags_removesMultilineThinkBlock() throws Exception {
        var method = LlmService.class.getDeclaredMethod("stripThinkTags", String.class);
        method.setAccessible(true);

        String input = "<think>\nLine 1\nLine 2\n</think>Answer here";
        String result = (String) method.invoke(llmService, input);
        assertEquals("Answer here", result);
    }

    @Test
    void stripThinkTags_noThinkTags_returnsOriginal() throws Exception {
        var method = LlmService.class.getDeclaredMethod("stripThinkTags", String.class);
        method.setAccessible(true);

        String input = "Just a normal response";
        String result = (String) method.invoke(llmService, input);
        assertEquals("Just a normal response", result);
    }

    @Test
    void stripThinkTags_nullInput_returnsEmpty() throws Exception {
        var method = LlmService.class.getDeclaredMethod("stripThinkTags", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(llmService, (String) null);
        assertEquals("", result);
    }

    @Test
    void stripThinkTags_multipleThinkBlocks_removesAll() throws Exception {
        var method = LlmService.class.getDeclaredMethod("stripThinkTags", String.class);
        method.setAccessible(true);

        String input = "<think>First</think>Answer 1 <think>Second</think>Answer 2";
        String result = (String) method.invoke(llmService, input);
        assertEquals("Answer 1 Answer 2", result);
    }

    @Test
    void chatMessage_constructorAndGetters() {
        LlmService.ChatMessage msg = new LlmService.ChatMessage("user", "Hello");
        assertEquals("user", msg.getRole());
        assertEquals("Hello", msg.getContent());
    }

    @Test
    void chatMessage_setters() {
        LlmService.ChatMessage msg = new LlmService.ChatMessage();
        msg.setRole("system");
        msg.setContent("You are a bot");
        assertEquals("system", msg.getRole());
        assertEquals("You are a bot", msg.getContent());
    }
}
