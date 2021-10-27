package com.chainalysis.cryptoprice.exchange;

import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

public interface ExchangeService {
    String getPrice(String coinSymbol);

    Map<String, String> getFees(String coinSymbol);


    static URI buildURI(String url) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        return builder.build().encode().toUri();
    }

    Map<String, String> getBuySellPrice(String coinSymbol);

    /**
     * Calculates price with fees combined
     * @param price - featured coin price
     * @param fees - adding a makers or takers fee
     * @return - total price in String format
     */
    static String calculatePrice(BigDecimal price, BigDecimal fees) {
        BigDecimal feePrice = price.multiply(fees);
        return price.add(feePrice).toString();
    }

}
