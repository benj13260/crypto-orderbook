package com.cryptoag.orderbook;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.SubmissionPublisher;

import com.cryptoag.Exceptions.BadRequestException;
import com.cryptoag.orderbook.data.CryptoDB;
import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.model.Order;
import com.cryptoag.orderbook.model.Price;
import com.cryptoag.orderbook.service.OrderExecution;
import com.cryptoag.orderbook.service.OrderbookService;
import com.cryptoag.orderbook.service.PriceFeedService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = OrderbookApp.class)
public class OrderbookServiceTests {

    @Autowired
    OrderbookService orderbookService;

    @Autowired
    OrderExecution orderExecution;

    @Autowired
    CryptoDB db;

    @Autowired
    PriceFeedService pf;

    @Test
    void Orders() throws JsonProcessingException, BadRequestException, InterruptedException {
        
        // Stop automatic price feed
        try {
            pf.completableFuture.complete(null);
        } catch (Exception e) {}

        SubmissionPublisher<String> pricePublisher = new SubmissionPublisher<String>();
        pricePublisher.subscribe(orderExecution.getOrderExecutionSubscriberInstance());
 
        Float balance =6000.0f, orderPrice= 3100.0f, price1 = 3200f, price2= 300f; 

        // Create account
        Account acc = Account.builder().name("test").usd_balance(balance).build();  
        orderbookService.createAccount(acc);

        // Create order;
        Order o = Order.builder().account_id(acc.getId()).price_limit(orderPrice).amount(2.0f).build();
        orderbookService.createLimitOrder(o);

        Price p = new Price();

        // Set first price above limit
        p.setPrice(price1);
        db.currentPrice = p;
        pricePublisher.submit(p.toString());
        Thread.sleep(1000); // Wait execution of async
        assertEquals(orderbookService.fetchOrderDetails(o.getId()).getStatus(), Order.Status.Pending);


        orderbookService.fetchAccountDetails(acc.getId());
        
        // Set second price below limit, must trigger Order execution
        p.setPrice(price2);
        db.currentPrice = p;
        pricePublisher.submit(p.toString());
        Thread.sleep(1000); // Wait execution of async
        assertEquals(orderbookService.fetchOrderDetails(o.getId()).getStatus(), Order.Status.Executed);


        pricePublisher.close();

    }
    
}
