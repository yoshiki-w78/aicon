//package com.example.aicon.service;
//
//import com.example.aicon.dto.GeminiModelInfo;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpStatusCodeException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class GeminiService {
//
//    @Value("${gemini.api.key:}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * settings.html から呼ばれるモデル一覧API用。
//     * 必要に応じて増やす or Googleの models.list を叩く形に発展可能。
//     * ここでは AIcon 向けに使いやすい候補をサーバー側で定義。
//     */
//    public List<GeminiModelInfo> getAvailableModels() {
//        List<GeminiModelInfo> list = new ArrayList<>();
//
//        // 最新の公式モデル一覧は docs を参照して適宜更新してください。 [oai_citation:0‡Google AI for Developers](https://ai.google.dev/gemini-api/docs/models?utm_source=chatgpt.com)
//
//        list.add(new GeminiModelInfo(
//                "gemini-2.5-flash",
//                "gemini-2.5-flash（推奨・高速）",
//                true
//        ));
//        list.add(new GeminiModelInfo(
//                "gemini-2.5-pro",
//                "gemini-2.5-pro（高品質）",
//                false
//        ));
//        // 必要ならここに他モデルを追加
//
//        return list;
//    }
//
//    /**
//     * アイコン用の英語プロンプトを1つ生成。
//     * - 成功時: Geminiの返却結果
//     * - 失敗時 or 未設定時: Fallbackの簡易プロンプト
//     */
//    public String generateIconPrompt(String userMessage, String clientModelId) {
//        if (userMessage == null || userMessage.isBlank()) {
//            return "";
//        }
//
//        // APIキー未設定 → Fallback即返し（UIを止めない）
//        if (apiKey == null || apiKey.isBlank()) {
//            return buildFallbackPrompt(userMessage);
//        }
//
//        String model = chooseModel(clientModelId);
//
//        String url = "https://generativelanguage.googleapis.com/v1/models/"
//                + model + ":generateContent?key=" + apiKey;
//
//        String systemPrompt = """
//                You are an assistant that creates ONE concise English prompt
//                for generating a modern application or service icon.
//
//                Requirements:
//                - Output only ONE prompt.
//                - English only.
//                - No explanation, no bullet points, no code block, no markdown.
//                - Reflect the user's desired style, color, theme, and concept.
//                - Make it suitable as an app/service icon (clean, simple, vector friendly).
//                """.trim();
//
//        Map<String, Object> body = Map.of(
//                "contents", List.of(
//                        Map.of(
//                                "role", "user",
//                                "parts", List.of(
//                                        Map.of("text", systemPrompt + "\n\nUser request: " + userMessage)
//                                )
//                        )
//                )
//        );
//
//        try {
//            String json = restTemplate.postForObject(url, body, String.class);
//            if (json == null) {
//                return buildFallbackPrompt(userMessage);
//            }
//
//            JsonNode root = objectMapper.readTree(json);
//            JsonNode candidates = root.path("candidates");
//            if (!candidates.isArray() || candidates.isEmpty()) {
//                return buildFallbackPrompt(userMessage);
//            }
//
//            JsonNode parts = candidates.get(0)
//                    .path("content")
//                    .path("parts");
//
//            StringBuilder sb = new StringBuilder();
//            if (parts.isArray()) {
//                for (JsonNode p : parts) {
//                    String t = p.path("text").asText(null);
//                    if (t != null) sb.append(t);
//                }
//            }
//
//            String result = sb.toString().trim();
//            if (result.isEmpty()) {
//                return buildFallbackPrompt(userMessage);
//            }
//
//            // 念のため改行・バッククォートを削る
//            result = result.replaceAll("[\\r\\n`]+", " ").trim();
//            return result;
//
//        } catch (HttpStatusCodeException e) {
//            System.err.println("Gemini API error: " + e.getStatusCode());
//            System.err.println(e.getResponseBodyAsString());
//            return buildFallbackPrompt(userMessage);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return buildFallbackPrompt(userMessage);
//        }
//    }
//
//    /**
//     * クライアント指定モデルが有効ならそれを使い、
//     * なければサーバー側で決めた推奨モデルを使う。
//     */
//    private String chooseModel(String clientModelId) {
//        List<GeminiModelInfo> available = getAvailableModels();
//
//        if (clientModelId != null && !clientModelId.isBlank()) {
//            boolean ok = available.stream()
//                    .anyMatch(m -> m.getModelId().equals(clientModelId));
//            if (ok) {
//                return clientModelId;
//            }
//        }
//
//        // 推奨マーク付きがあればそれ
//        return available.stream()
//                .filter(GeminiModelInfo::isRecommended)
//                .map(GeminiModelInfo::getModelId)
//                .findFirst()
//                // 万が一リスト空でも最低限これ
//                .orElse("gemini-2.5-flash");
//    }
//
//    /**
//     * Geminiが使えない場合用のバックアッププロンプト。
//     */
//    private String buildFallbackPrompt(String userMessage) {
//        return ("A clean, modern, minimalist icon representing: "
//                + userMessage
//                + ". Flat design, simple geometric shapes, clear contrast, suitable as an app logo.")
//                .replaceAll("\\s+", " ")
//                .trim();
//    }
//}