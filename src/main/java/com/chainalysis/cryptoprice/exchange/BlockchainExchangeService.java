package com.chainalysis.cryptoprice.exchange;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

@Service
public class BlockchainExchangeService implements ExchangeService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getPrice(String coinSymbol) {
        URI price = ExchangeService.buildURI("https://api.blockchain.com/v3/exchange/tickers/" + coinSymbol + "-USD");
        String result = restTemplate.getForObject(price, String.class);

        JSONObject resultObj = new JSONObject(result);

        BigDecimal lastTradePrice = (BigDecimal) resultObj.get("last_trade_price");

        return lastTradePrice.toString();
    }

    /**
     * NOTE - Used fixed rate from website due to api keys
     */
    @Override
    public Map<String, String> getFees(String coinSymbol) {
        return Map.of(
                "takerFees", "0.0024",
                "makerFees", "0.0014"
        );
    }

    @Override
    public Map<String, String> getBuySellPrice(String coinSymbol) {
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
