package com.cryptoag.orderbook.service;

import com.cryptoag.orderbook.data.CryptoDB;
import com.cryptoag.orderbook.service.subscribers.OrderExecutionSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderExecution {
    
    @Autowired
    CryptoDB db;

    OrderExecutionSubscriber orderExecutionSubscriber;

    public OrderExecutionSubscriber getOrderExecutionSubscriberInstance(){
        if (orderExecutionSubscriber == null){
            orderExecutionSubscriber = new OrderExecutionSubscriber(db);
        }
        return orderExecutionSubscriber;
    }

}
