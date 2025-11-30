package com.example.aicon.service;

public enum ImageProvider {
    OPENAI,
    STABILITY,
    NOVITA,
    PLACEHOLDER;

    public static ImageProvider from(String v) {
        if (v == null) return PLACEHOLDER;
        return switch (v.trim().toUpperCase()) {
            case "OPENAI" -> OPENAI;
            case "STABILITY" -> STABILITY;
            case "NOVITA" -> NOVITA;
            default -> PLACEHOLDER;
        };
    }
}
