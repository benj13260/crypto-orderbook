package com.cryptoag.orderbook;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cryptoag.Exceptions.BadRequestException;
import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.model.Order;
import com.cryptoag.orderbook.model.Price;
import com.cryptoag.orderbook.service.OrderbookService;
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

    @Test
    void Orders() throws JsonProcessingException, BadRequestException {
       
 
        // Create account
        Account acc = Account.builder().name("test").usd_balance(6000.0f).build();  
        orderbookService.createAccount(acc);

        // Create order;
        Order o = Order.builder().account_id(acc.getId()).price_limit(3100.0f).amount(2.0f).build();
        orderbookService.createLimitOrder(o);

        Price p = new Price();
        p.setPrice(3200);
        orderbookService.executeOrders(p);
        assertEquals(orderbookService.fetchOrderDetails(o.getId()).getStatus(), Order.Status.Pending);

        p.setPrice(300);
        orderbookService.fetchAccountDetails(acc.getId());
        orderbookService.executeOrders(p);
        assertEquals(orderbookService.fetchOrderDetails(o.getId()).getStatus(), Order.Status.Executed);
        orderbookService.fetchAccountDetails(acc.getId());



    }
    
}
