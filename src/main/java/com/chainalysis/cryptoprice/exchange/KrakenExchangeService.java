package com.chainalysis.cryptoprice.exchange;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class KrakenExchangeService implements ExchangeService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getPrice(String coinSymbol) {
        URI price = ExchangeService.buildURI("https://api.kraken.com/0/public/Ticker?pair=" + coinSymbol + "USD");
        String result = restTemplate.getForObject(price, String.class);

        JSONObject priceObj = new JSONObject(result);
        JSONArray lastSoldPriceArr = priceObj.getJSONObject("result")
                .getJSONObject("X" + coinSymbol + "ZUSD")
                .getJSONArray("c");

        return lastSoldPriceArr.getString(0);
    }

    @Override
    public Map<String, String> getFees(String coinSymbol) {
        Map<String, String> fees = new HashMap<>();
        URI krakenFeesUri = ExchangeService.buildURI("https://api.kraken.com/0/public/AssetPairs?pair=X" + coinSymbol + "ZUSD");
        String result = restTemplate.getForObject(krakenFeesUri, String.class);

        JSONObject resultObj = new JSONObject(result);
        JSONObject feesObj = new JSONObject(resultObj)
                .getJSONObject("result")
                .getJSONObject("X" + coinSymbol + "ZUSD");

        String takerFees = feesObj
                .getJSONArray("fees")
                .getString(0);
        String makerFees = feesObj
                .getJSONArray("fees_maker")
                .getString(0);

        fees.put("takerFees", takerFees);
        fees.put("makerFees", makerFees);

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
