//package com.example.aicon.service;
//
//import com.example.aicon.dto.GeminiPromptRequest;
//import com.example.aicon.dto.GeminiPromptResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class GeminiBackendService {
//
//    @Value("${gemini.api.key:}")
//    private String geminiApiKey;
//
//    @Value("${gemini.model:gemini-2.0-flash}")
//    private String geminiModel;
//
//    private final RestClient client = RestClient.builder()
//            .baseUrl("https://generativelanguage.googleapis.com")
//            .build();
//
//    public GeminiPromptResponse generatePrompt(GeminiPromptRequest req) {
//
//        String user = safe(req.getMessage());
//        if (user.isEmpty()) {
//            return new GeminiPromptResponse(
//                    "作りたいアイコンのイメージを教えてください。（例：青系／ミニマル／AIアシスタント用）",
//                    null,
//                    false
//            );
//        }
//
//        // APIキー未設定 → フォールバック
//        if (geminiApiKey == null || geminiApiKey.isBlank()) {
//            log.warn("Gemini API key is not set. Using fallback prompt.");
//            return fallback(user, "Gemini APIキーが未設定のため、サーバー側で用意した汎用プロンプトを返します。");
//        }
//
//        try {
//            String promptText = buildSystemPrompt(user);
//
//            Map<String, Object> body = Map.of(
//                    "contents", List.of(
//                            Map.of(
//                                    "parts", List.of(
//                                            Map.of("text", promptText)
//                                    )
//                            )
//                    )
//            );
//
//            Map<String, Object> res = client.post()
//                    .uri(uriBuilder -> uriBuilder
//                            .path("/v1/models/{model}:generateContent")
//                            .queryParam("key", geminiApiKey)
//                            .build(geminiModel)
//                    )
//                    .body(body)
//                    .retrieve()
//                    .body(Map.class);
//
//            String english = extractText(res);
//            if (english == null || english.isBlank()) {
//                log.warn("Gemini response did not contain text. Response={}", res);
//                return fallback(user, "うまく応答が取得できなかったため、代わりに汎用プロンプトを提案します。");
//            }
//
//            // できるだけ短く・そのまま右に使える形にしたいので整形
//            english = english
//                    .replaceAll("^```[a-zA-Z]*", "")
//                    .replaceAll("```$", "")
//                    .trim();
//
//            return new GeminiPromptResponse(
//                    "ご要望に基づいて英語プロンプト案を生成しました。右側で確認して「アイコンを生成」を押してください。",
//                    english,
//                    false
//            );
//
//        } catch (Exception e) {
//            log.error("Gemini API error", e);
//            return fallback(user, "Geminiへの接続でエラーが発生したため、代わりに汎用プロンプトを提案します。");
//        }
//    }
//
//    private String safe(String s) {
//        return s == null ? "" : s.trim();
//    }
//
//    private GeminiPromptResponse fallback(String user, String msgJa) {
//        String english = "A clean, modern minimalist icon based on: \"" + user +
//                "\". Flat design, simple shapes, balanced composition, suitable as an app or SaaS logo.";
//        return new GeminiPromptResponse(msgJa, english, false);
//    }
//
//    private String buildSystemPrompt(String userMessage) {
//        // 実際にGeminiへ渡す指示（短くシンプル）
//        return """
//                You are an assistant that writes one concise English prompt for AI icon generation.
//
//                Requirements:
//                - Output ONLY the English prompt.
//                - Do not include explanations, bullet points, or Japanese.
//                - Focus on a clean, modern app/SaaS/icon style.
//                - Use 1-3 sentences maximum.
//
//                User request:
//                """ + userMessage;
//    }
//
//    @SuppressWarnings("unchecked")
//    private String extractText(Map<String, Object> res) {
//        if (res == null) return null;
//        var candidates = (List<Map<String, Object>>) res.get("candidates");
//        if (candidates == null || candidates.isEmpty()) return null;
//
//        var content = (Map<String, Object>) candidates.get(0).get("content");
//        if (content == null) return null;
//
//        var parts = (List<Map<String, Object>>) content.get("parts");
//        if (parts == null || parts.isEmpty()) return null;
//
//        Object text = parts.get(0).get("text");
//        return text == null ? null : text.toString();
//    }
//}
