package com.popytka.popytka.external.client;

import com.popytka.popytka.external.dto.MyMemoryTranslateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "myMemoryTranslateClient", url = "https://api.mymemory.translated.net")
public interface MyMemoryTranslateClient {

    @GetMapping("/get")
    MyMemoryTranslateResponse translate(
            @RequestParam("q") String text,
            @RequestParam("langpair") String langPair
    );
}
