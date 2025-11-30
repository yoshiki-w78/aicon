package com.example.aicon.controller;

import com.example.aicon.model.User;
import com.example.aicon.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/me")
    public MeResponse me(@AuthenticationPrincipal OAuth2User principal) {
        MeResponse res = new MeResponse();

        if (principal == null) {
            res.setAuthenticated(false);
            res.setPlan("FREE");
            return res;
        }

        Long userId = principal.getAttribute("userId");
        if (userId == null) {
            res.setAuthenticated(false);
            res.setPlan("FREE");
            return res;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            res.setAuthenticated(false);
            res.setPlan("FREE");
            return res;
        }

        res.setAuthenticated(true);
        res.setName(user.getName());
        res.setPlan(user.getSubscriptionStatus());
        return res;
    }

    public static class MeResponse {
        private boolean authenticated;
        private String name;
        private String plan;

        public boolean isAuthenticated() {
            return authenticated;
        }
        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPlan() {
            return plan;
        }
        public void setPlan(String plan) {
            this.plan = plan;
        }
    }
}