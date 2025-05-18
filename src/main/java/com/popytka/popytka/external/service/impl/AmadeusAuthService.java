package com.popytka.popytka.external.service.impl;

import com.popytka.popytka.external.client.AmadeusAuthFeignClient;
import com.popytka.popytka.external.dto.auth.AmadeusAuthRequest;
import com.popytka.popytka.external.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmadeusAuthService {

    private final AmadeusAuthFeignClient amadeusAuthFeignClient;

    @Value("${amadeus.client.id}")
    private String clientId;

    @Value("${amadeus.client.secret}")
    private String clientSecret;

    @Value("${amadeus.grant.type}")
    private String grantType;

    private String accessToken;
    private long expirationTime;

    public String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() >= expirationTime) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        AmadeusAuthRequest amadeusAuthRequest = AmadeusAuthRequest.builder()
                .client_id(clientId)
                .client_secret(clientSecret)
                .grant_type(grantType)
                .build();
        TokenResponse response = amadeusAuthFeignClient.getAccessToken(amadeusAuthRequest);
        this.accessToken = response.getAccessToken();
        this.expirationTime = System.currentTimeMillis() + (response.getExpiresIn() * 1000L) - 10_000;
    }
}
