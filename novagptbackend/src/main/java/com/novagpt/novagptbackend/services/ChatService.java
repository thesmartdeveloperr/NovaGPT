package com.novagpt.novagptbackend.services;

import com.novagpt.novagptbackend.models.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class ChatService {

    @Value("${ai.provider}")
    private String provider;

    @Value("${openai.api.key}")
    private String openaiKey;

    @Value("${gemini.api.key}")
    private String geminiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponse getOpenAIResponse(String prompt) {
        if ("gemini".equalsIgnoreCase(provider)) {
            return useGeminiAPI(prompt);
        } else {
            return useOpenAIAPI(prompt);
        }
    }

    private ChatResponse useOpenAIAPI(String prompt) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openaiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");
            return new ChatResponse(content.trim());
        } catch (Exception e) {
            return new ChatResponse("OpenAI Error: " + e.getMessage());
        }
    }

    private ChatResponse useGeminiAPI(String prompt) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> messagePart = new HashMap<>();
        messagePart.put("text", prompt);

        Map<String, Object> part = new HashMap<>();
        part.put("parts", List.of(messagePart));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(part));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String text = (String) parts.get(0).get("text");
            return new ChatResponse(text.trim());
        } catch (Exception e) {
            return new ChatResponse("Gemini Error: " + e.getMessage());
        }
    }
}
