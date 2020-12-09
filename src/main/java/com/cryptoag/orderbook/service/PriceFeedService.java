package com.cryptoag.orderbook.service;



import com.cryptoag.orderbook.model.Price;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PriceFeedService {

    final String FEED_URL = "http://127.0.0.1:5000/btc-price";

    RestTemplate restTemplate;

    public PriceFeedService() {
        restTemplate = new RestTemplateBuilder().build();
    }

    public Price getPriceFeed() {
        return restTemplate.getForObject(FEED_URL, Price.class);
    }

}
