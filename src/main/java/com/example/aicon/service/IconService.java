package com.example.aicon.service;

import com.example.aicon.dto.GenerateIconRequest;
import com.example.aicon.dto.GenerateIconResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class IconService {

    @Value("${image.provider:PLACEHOLDER}")
    private String imageProvider;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    @Value("${openai.image.model:dall-e-3}")
    private String openaiImageModel;

    private final RestTemplate restTemplate;

    public IconService(RestTemplateBuilder builder) {
        // 非推奨API（setConnectTimeout/ReadTimeout）を使わず、リクエストファクトリでタイムアウト設定
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout((int) Duration.ofSeconds(15).toMillis());
        rf.setReadTimeout((int) Duration.ofSeconds(60).toMillis());

        this.restTemplate = builder
                .requestFactory(() -> rf)
                .build();
    }

    public GenerateIconResponse generateIcons(GenerateIconRequest req) {
        GenerateIconResponse res = new GenerateIconResponse();

        String prompt = Optional.ofNullable(req.getPrompt()).orElse("").trim();
        if (prompt.isEmpty()) {
            res.setError("プロンプトが空です。左のチャットから英語プロンプトを反映してください。");
            res.setIcons(Collections.emptyList());
            return res;
        }
        res.setPrompt(prompt);

        int count = (req.getCount() == null || req.getCount() < 1) ? 4 : req.getCount();
        if (count > 4) count = 4;

        String uiSize = Optional.ofNullable(req.getSize()).orElse("1024x1024");
        String apiSize = mapToOpenAiSize(uiSize);

        String style = Optional.ofNullable(req.getStyle()).orElse("").trim().toLowerCase();

        // プロバイダ分岐（OPENAI 以外はダミー）
        if (!"OPENAI".equalsIgnoreCase(imageProvider)) {
            log.warn("image.provider が OPENAI 以外 or 未設定のため、ダミー画像を返します: {}", imageProvider);
            res.setIcons(buildPlaceholderIcons(count));
            res.setError("OpenAI画像生成は現在無効です（image.provider=" + imageProvider + "）。application.properties を確認してください。");
            return res;
        }

        if (openaiApiKey == null || openaiApiKey.isBlank()) {
            log.error("openai.api.key が設定されていません。");
            res.setIcons(buildPlaceholderIcons(count));
            res.setError("OpenAI APIキーが未設定のため、ダミー画像を表示しています。");
            return res;
        }

        // OpenAI Images API 呼び出し
        try {
            // dalle-3 / gpt-image 系は n=1 を推奨/制限されることがある
            int n = 1;

            Map<String, Object> body = new HashMap<>();
            body.put("model", openaiImageModel);
            body.put("prompt", buildPromptWithStyle(prompt, style));
            body.put("n", n);
            body.put("size", apiSize);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            log.info("Calling OpenAI Images API. model={}, size={}, n={}", openaiImageModel, apiSize, n);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.openai.com/v1/images/generations",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object dataObj = response.getBody().get("data");
                if (dataObj instanceof List<?> list && !list.isEmpty()) {
                    List<GenerateIconResponse.IconData> icons = new ArrayList<>();
                    int index = 1;
                    for (Object o : list) {
                        if (!(o instanceof Map)) continue;
                        Map<?, ?> m = (Map<?, ?>) o;
                        Object urlObj = m.get("url");
                        if (urlObj == null) continue;
                        String url = urlObj.toString();

                        GenerateIconResponse.IconData icon = new GenerateIconResponse.IconData();
                        icon.setId("openai-" + index);
                        icon.setImageUrl(url);
                        icons.add(icon);
                        index++;
                    }
                    if (!icons.isEmpty()) {
                        res.setIcons(icons);
                        res.setError(null);
                        return res;
                    }
                }
            }

            log.error("OpenAI Images API から有効な画像URLが得られませんでした: status={}, body={}",
                    response.getStatusCode(), response.getBody());

            res.setIcons(buildPlaceholderIcons(count));
            res.setError("OpenAI画像生成に失敗したため、ダミー画像を表示しています。");
            return res;

        } catch (RestClientResponseException e) {
            log.error("OpenAI API request failed: {} {} {}", e.getRawStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
            res.setIcons(buildPlaceholderIcons(count));
            res.setError("OpenAI画像生成に失敗したため、ダミー画像を表示しています。: "
                    + e.getRawStatusCode() + " " + e.getStatusText() + " "
                    + e.getResponseBodyAsString());
            return res;

        } catch (Exception e) {
            log.error("OpenAI画像生成中に予期せぬエラーが発生しました", e);
            res.setIcons(buildPlaceholderIcons(count));
            res.setError("OpenAI画像生成に失敗したため、ダミー画像を表示しています。");
            return res;
        }
    }

    private String buildPromptWithStyle(String prompt, String style) {
        if (style.isEmpty()) return prompt;
        return prompt + " Style: " + style + " icon design.";
    }

    /** UI の size 値を OpenAI API 対応サイズにマップ */
    private String mapToOpenAiSize(String uiSize) {
        if (uiSize == null) return "1024x1024";
        switch (uiSize) {
            case "1024x1024":
                return "1024x1024";
            case "1024x1536":
                return "1024x1536";
            case "1536x1024":
                return "1536x1024";
            default:
                return "1024x1024"; // 512/768 等は 1024 に寄せる
        }
    }

    private List<GenerateIconResponse.IconData> buildPlaceholderIcons(int count) {
        List<GenerateIconResponse.IconData> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            GenerateIconResponse.IconData icon = new GenerateIconResponse.IconData();
            icon.setId("placeholder-" + i);
            icon.setImageUrl("https://via.placeholder.com/512x512.png?text=AIcon+" + i);
            list.add(icon);
        }
        return list;
    }
}