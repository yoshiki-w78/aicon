package com.example.aicon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRFいったんOFF（ローカル開発 & シンプルAPI用）
                .csrf(csrf -> csrf.disable())

                // 全部基本的に許可
                .authorizeHttpRequests(auth -> auth
                        // 静的ファイル & メイン画面 & 設定画面
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/settings.html",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // APIも一旦全部許可（本気運用時にここを締める）
                        .requestMatchers(
                                "/api/**"
                        ).permitAll()

                        .anyRequest().permitAll()
                )

                // フォームログインは使わない
                .formLogin(form -> form.disable())

                // ログアウト機能もいったん無効化（実装してないので）
                .logout(logout -> logout.disable())

                // OAuth2 Loginも今回は無効化
                .oauth2Login(oauth2 -> oauth2.disable());

        return http.build();
    }
}
