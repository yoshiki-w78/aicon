//package com.example.aicon.controller;
//
//import com.example.aicon.entity.User;
//import com.example.aicon.repository.UserRepository;
//import com.stripe.exception.SignatureVerificationException;
//import com.stripe.model.Event;
//import com.stripe.model.checkout.Session;
//import com.stripe.net.Webhook;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/webhook/stripe")
//@RequiredArgsConstructor
//public class StripeWebhookController {
//
//    private final UserRepository userRepository;
//
//    @Value("${stripe.webhook-secret}")
//    private String webhookSecret;
//
//    @PostMapping
//    public ResponseEntity<String> handleStripeEvent(
//            @RequestHeader("Stripe-Signature") String sigHeader,
//            @RequestBody String payload
//    ) {
//        Event event;
//        try {
//            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
//        } catch (SignatureVerificationException e) {
//            return ResponseEntity.badRequest().body("Invalid signature");
//        }
//
//        String type = event.getType();
//
//        try {
//            switch (type) {
//                case "checkout.session.completed" -> handleCheckoutCompleted(event);
//                // 必要に応じて "customer.subscription.updated" などもここに追加
//                default -> {
//                    // ログだけでもOK
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.ok("Webhook processed with error");
//        }
//
//        return ResponseEntity.ok("success");
//    }
//
//    private void handleCheckoutCompleted(Event event) {
//        Session session = (Session) event.getDataObjectDeserializer()
//                .getObject()
//                .orElse(null);
//
//        if (session == null) return;
//
//        String userIdStr = session.getMetadata().get("userId");
//        if (userIdStr == null) return;
//
//        Long userId = Long.valueOf(userIdStr);
//        Optional<User> optUser = userRepository.findById(userId);
//        if (optUser.isEmpty()) return;
//
//        User user = optUser.get();
//
//        // 本来は session.getCustomer() を保存しておくとより良い
//        if (user.getStripeCustomerId() == null && session.getCustomer() != null) {
//            user.setStripeCustomerId(session.getCustomer());
//        }
//
//        // サブスク有効化
//        user.setSubscriptionStatus("ACTIVE");
//        userRepository.save(user);
//    }
//}
