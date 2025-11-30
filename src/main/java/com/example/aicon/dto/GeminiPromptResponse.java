package com.example.aicon.dto;

public class GeminiPromptResponse {

    private String prompt;

    public GeminiPromptResponse() {
    }

    public GeminiPromptResponse(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}