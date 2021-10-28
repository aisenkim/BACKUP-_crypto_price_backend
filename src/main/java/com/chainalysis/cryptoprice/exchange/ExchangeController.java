package com.chainalysis.cryptoprice.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeController {

    private final KrakenExchangeService krakenExchangeService;
    private final BlockchainExchangeService blockchainExchangeService;


    @GetMapping
    public ResponseEntity<Map<String, String>> getBuySellPrice(
            @RequestParam String exchange,
            @RequestParam String coinSymbol
    ) {
        if(exchange.equals("kraken"))
            return ResponseEntity.ok().body(krakenExchangeService.getBuySellPrice(coinSymbol));
        else
            return ResponseEntity.ok().body(blockchainExchangeService.getBuySellPrice(coinSymbol));
    }

}
