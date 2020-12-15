package com.cryptoag.orderbook.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;

import javax.annotation.PostConstruct;

import com.cryptoag.orderbook.data.CryptoDB;
import com.cryptoag.orderbook.model.Price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class PriceFeedService {

    @Autowired
    CryptoDB db;

    @Autowired
    OrderExecution orderExecution;


    final String FEED_URL_HOST = "http://127.0.0.1:5000";
    final String FEED_URL_PATH = "/btc-price";

    private WebClient webClient;

    SubmissionPublisher<Object> pricePublisher  = new SubmissionPublisher<Object>();

    Flux<Price> fluxPrice;

    public CompletableFuture<Void> completableFuture;

    public PriceFeedService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(FEED_URL_HOST).build();
    }
    
    @PostConstruct
    public void init() {
    	completableFuture = CompletableFuture.runAsync(() -> { this.getPriceFeed(); });
    }

    public void getPriceFeed() {
        log.info("Start Price feed");
        if (!pricePublisher.hasSubscribers())
            pricePublisher.subscribe(orderExecution.getOrderExecutionSubscriberInstance());

        fluxPrice = this.webClient.get()
        .uri(FEED_URL_PATH)
        .retrieve().
        bodyToFlux(Price.class).
        delayElements(Duration.ofSeconds(2)); // Delay call for 2 seconds

        int i = 0;
        while (i>-1) {
            try {
                Price p = fluxPrice.next().block();        // Get next Price
                pricePublisher.submit(p);    // Start trade execution asynchronously
                log.info("Price Feed "+i+++": "+p);
            } catch (RestClientException e) {
                log.error("Price feed server issue / Verify URL");
                break;
            } catch (Exception e) {
                return;
            }
        }
        pricePublisher.close();
    }

}
