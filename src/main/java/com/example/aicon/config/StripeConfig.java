package com.example.aicon.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret-key:}")
    private String secretKey;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            System.out.println("[StripeConfig] stripe.secret-key 未設定のため、Stripe連携は無効です。");
            return;
        }
        Stripe.apiKey = secretKey;
        System.out.println("[StripeConfig] Stripe initialized.");
    }
}