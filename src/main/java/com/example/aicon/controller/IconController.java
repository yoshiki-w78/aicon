package com.example.aicon.controller;

import com.example.aicon.dto.GenerateIconRequest;
import com.example.aicon.dto.GenerateIconResponse;
import com.example.aicon.service.IconService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/icons")
public class IconController {

    private final IconService iconService;

    public IconController(IconService iconService) {
        this.iconService = iconService;
    }

    // 画像生成エンドポイントだけ
    @PostMapping("/generate")
    public GenerateIconResponse generate(@RequestBody GenerateIconRequest req) {
        try {
            return iconService.generateIcons(req);
        } catch (Exception e) {
            e.printStackTrace();
            GenerateIconResponse res = new GenerateIconResponse();
            res.setError("サーバーエラーにより生成できませんでした: " + e.getMessage());
            return res;
        }
    }
}