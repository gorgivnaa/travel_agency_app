package com.popytka.popytka.external.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class AmadeusAuthRequest {

    private String client_id;
    private String client_secret;
    private String grant_type;
}
