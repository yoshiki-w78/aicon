package com.example.aicon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeminiModelInfo {
    private String modelId;       // APIに渡す実際のモデル名
    private String displayName;   // 画面表示用
    private boolean recommended;  // 推奨モデルかどうか
}