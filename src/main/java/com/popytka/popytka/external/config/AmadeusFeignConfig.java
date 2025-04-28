package com.popytka.popytka.external.config;

import com.popytka.popytka.external.service.impl.AmadeusAuthService;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmadeusFeignConfig {

    private final AmadeusAuthService amadeusAuthService;

    @Bean
    public RequestInterceptor amadeusRequestInterceptor() {
        return requestTemplate -> {
            String accessToken = amadeusAuthService.getAccessToken();
            requestTemplate.header("Authorization", "Bearer " + accessToken);
        };
    }
}
