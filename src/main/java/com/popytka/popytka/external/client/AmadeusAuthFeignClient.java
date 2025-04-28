package com.popytka.popytka.external.client;

import com.popytka.popytka.external.dto.auth.AmadeusAuthRequest;
import com.popytka.popytka.external.dto.auth.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "amadeusAuthClient",
        url = "https://test.api.amadeus.com"
)
public interface AmadeusAuthFeignClient {

    @PostMapping(value = "/v1/security/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenResponse getAccessToken(@RequestBody AmadeusAuthRequest amadeusAuthRequest);
}

