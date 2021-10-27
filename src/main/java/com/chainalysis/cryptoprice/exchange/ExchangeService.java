package com.chainalysis.cryptoprice.exchange;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public interface ExchangeService {
    String getPrice(String coinSymbol);

    Map<String, String> getFees();


    static URI buildURI(String url) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        return builder.build().encode().toUri();
    }

    Map<String, String> getBuySellPrice(String coinSymbol);
}
