package com.popytka.popytka.external.client;

import com.popytka.popytka.external.config.AmadeusFeignConfig;
import com.popytka.popytka.external.dto.AmadeusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "amadeusClient",
        url = "https://test.api.amadeus.com",
        configuration = AmadeusFeignConfig.class
)
public interface AmadeusFeignClient {

    @GetMapping("/v1/shopping/activities")
    AmadeusResponse getActivities(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("radius") int radius
    );
}
