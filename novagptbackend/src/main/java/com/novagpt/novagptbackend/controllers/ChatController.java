package com.novagpt.novagptbackend.controllers;
import com.novagpt.novagptbackend.models.ChatRequest;
import com.novagpt.novagptbackend.models.ChatResponse;
import com.novagpt.novagptbackend.services.ChatService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.getOpenAIResponse(request.getPrompt());
    }
}
