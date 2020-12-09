package com.cryptoag.orderbook.controller;

import java.util.concurrent.CompletableFuture;

import com.cryptoag.orderbook.model.Order;
import com.cryptoag.orderbook.model.Price;
import com.cryptoag.orderbook.service.OrderbookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OrderController {

    @Autowired
    OrderbookService orderbookService;

    @GetMapping("/orders/{id}")
    Order fetchOrderDetails(@PathVariable Integer id) {
        return orderbookService.fetchOrderDetails(id);
    }

    @PostMapping("/order")
    public Order createLimitOrder(@RequestBody Order account) {
        log.info(account.toString());
        return orderbookService.createLimitOrder(account);
    }

    @GetMapping("/start")
    public void startPriceFeed() { 
        CompletableFuture.runAsync(() -> {
            while (1 != 0) {
                Price p = orderbookService.getPriceFeedService().getPriceFeed();
                log.info("Price :"+p.getPrice());
                orderbookService.executeOrders(p);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}