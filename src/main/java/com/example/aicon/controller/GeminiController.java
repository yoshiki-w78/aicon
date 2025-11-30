package com.example.aicon.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    @PostMapping("/prompt")
    public Map<String, String> generatePrompt(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "").trim();

        String base =
                "A modern, clean, minimalist icon for an AI assistant, blue color scheme, flat design, high resolution, no text";

        String prompt;
        if (message.isEmpty()) {
            prompt = base;
        } else {
            prompt = base + ", based on the concept: " + message;
        }

        Map<String, String> res = new HashMap<>();
        res.put("prompt", prompt);
        return res;
    }
}