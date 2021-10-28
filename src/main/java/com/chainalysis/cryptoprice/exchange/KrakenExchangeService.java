package com.chainalysis.cryptoprice.exchange;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KrakenExchangeService implements ExchangeService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getPrice(String coinSymbol) {
        URI price = ExchangeService.buildURI("https://api.kraken.com/0/public/Ticker?pair=" + coinSymbol + "USD");
        String result = restTemplate.getForObject(price, String.class);

        JSONObject resultObj = new JSONObject(result);

        JSONArray lastSoldPriceArr = resultObj.getJSONObject("result")
                .getJSONObject("X" + coinSymbol + "ZUSD")
                .getJSONArray("c");

        return lastSoldPriceArr.getString(0);
    }

    @Override
    public Map<String, String> getFees(String coinSymbol) {
        Map<String, String> fees = new HashMap<>();
        URI krakenFeesUri = ExchangeService.buildURI("https://api.kraken.com/0/public/AssetPairs?pair=X" + coinSymbol + "ZUSD");
        String result = restTemplate.getForObject(krakenFeesUri, String.class);

        JSONObject feesObj = new JSONObject(result)
                .getJSONObject("result")
                .getJSONObject("X" + coinSymbol + "ZUSD");

        JSONArray takerFees = (JSONArray) feesObj
                .getJSONArray("fees")
                .get(0);
        JSONArray makerFees = (JSONArray) feesObj
                .getJSONArray("fees_maker")
                .get(0);

        fees.put("takerFees", String.valueOf(takerFees.get(1)));
        fees.put("makerFees", String.valueOf(makerFees.get(1)));

        return fees;
    }

    @Override
    public Map<String, String> getBuySellPrice(String coinSymbol) {
        // Kraken uses different symbol
        if(coinSymbol.equals("BTC")) {
            coinSymbol = "XBT";
        }

        BigDecimal price = new BigDecimal(getPrice(coinSymbol));
        Map<String, String> fees = getFees(coinSymbol);

        BigDecimal buyersFee = new BigDecimal(fees.get("takerFees"));
        BigDecimal sellersFee = new BigDecimal(fees.get("makerFees"));

        String buyPrice = ExchangeService.calculatePrice(price, buyersFee);
        String sellPrice = ExchangeService.calculatePrice(price, sellersFee);

        return Map.of(
                "buyPrice", buyPrice,
                "sellPrice", sellPrice
        );
    }


}
