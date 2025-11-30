//package com.example.aicon.controller;
//
//import com.example.aicon.entity.User;
//import com.example.aicon.repository.UserRepository;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/billing")
//@RequiredArgsConstructor
//public class BillingController {
//
//    private final UserRepository userRepository;
//
//    @Value("${stripe.price-id-pro}")
//    private String priceIdPro;
//
//    @PostMapping("/create-checkout-session")
//    public String createCheckoutSession(@AuthenticationPrincipal OAuth2User principal) throws Exception {
//        if (principal == null) {
//            throw new RuntimeException("Unauthorized");
//        }
//
//        Long userId = principal.getAttribute("userId");
//        if (userId == null) {
//            throw new RuntimeException("User not found in session");
//        }
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 成功/キャンセル後に戻したいURL（まずはローカル用）
//        String successUrl = "http://localhost:8080/?billing=success";
//        String cancelUrl = "http://localhost:8080/?billing=cancel";
//
//        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
//                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
//                .setSuccessUrl(successUrl)
//                .setCancelUrl(cancelUrl)
//                .addLineItem(
//                        new SessionCreateParams.LineItem.Builder()
//                                .setPrice(priceIdPro)
//                                .setQuantity(1L)
//                                .build()
//                )
//                .putMetadata("userId", String.valueOf(user.getId()));
//
//        // 既にStripe Customerを持っていたらセット（今回まだなので省略可）
//        if (user.getStripeCustomerId() != null) {
//            builder.setCustomer(user.getStripeCustomerId());
//        }
//
//        Session session = Session.create(builder.build());
//        return session.getUrl();
//    }
//}
