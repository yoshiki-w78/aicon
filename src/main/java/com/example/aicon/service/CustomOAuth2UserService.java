//package com.example.aicon.service;
//
//import com.example.aicon.model.User;
//import com.example.aicon.repository.UserRepository;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    public CustomOAuth2UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "github"
//        Map<String, Object> attrs = oAuth2User.getAttributes();
//
//        String providerUserId;
//        String name = null;
//        String email = null;
//
//        if ("github".equals(registrationId)) {
//            providerUserId = String.valueOf(attrs.get("id"));
//            name = (String) attrs.get("login");
//            email = (String) attrs.get("email");
//        } else {
//            providerUserId = oAuth2User.getName();
//        }
//
//        User user = userRepository
//                .findByProviderAndProviderUserId(registrationId, providerUserId)
//                .orElseGet(() -> {
//                    User u = new User();
//                    u.setProvider(registrationId);
//                    u.setProviderUserId(providerUserId);
//                    u.setName(name);
//                    u.setEmail(email);
//                    u.setSubscriptionStatus("FREE");
//                    return userRepository.save(u);
//                });
//
//        Map<String, Object> newAttrs = new HashMap<>(attrs);
//        newAttrs.put("userId", user.getId());
//        newAttrs.put("subscriptionStatus", user.getSubscriptionStatus());
//
//        return new DefaultOAuth2User(
//                oAuth2User.getAuthorities(),
//                newAttrs,
//                "id" // GitHubは"id"でOK
//        );
//    }
//}