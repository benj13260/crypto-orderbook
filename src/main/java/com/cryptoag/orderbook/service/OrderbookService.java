package com.cryptoag.orderbook.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;

import com.cryptoag.Exceptions.BadRequestException;
import com.cryptoag.orderbook.data.CryptoDB;
import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.model.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderbookService {

    @Autowired
    CryptoDB db;
    
    @Autowired
    OrderExecution orderExecution;

    SubmissionPublisher<Object> orderPublisher = new SubmissionPublisher<Object>();
    

    // Verify validation
    public void validAccount(Account a) throws BadRequestException{
        //TODO: create validation class
        if (a.getName()==null){
            throw new BadRequestException("Name must not be null");
        }
    }

    // Creates an account on the application with 0 BTC.
    public Account createAccount(Account a) throws BadRequestException {
        validAccount(a);
        a.setId(a.hashCode());
        a.setBtc_balance(0.0f);
        log.info(a.toString());
        db.accounts.put(a.getId(), a);
        return a;
    }

    // Fetches account detail
    public Account fetchAccountDetails(Integer account_id) {
        return db.accounts.get(account_id);
    }

    public List<Account> ListAccounts() {
        return new ArrayList<Account>(db.accounts.values());
    }

    public List<Order> ListOrders() {
        return new ArrayList<Order>(db.orders.values());
    }

    /**
     *  Creates a limit order, publish a new event to start trade execution asynchronously
     */
    public Order createLimitOrder(Order o) {
        // Order must be emited from an existing account
        if (db.accounts.get(o.getAccount_id()) == null){
            return null;
        }

        // Enfore order in pending status
        o.setStatus(Order.Status.Pending);

        // Set nonce for duplicated order
        int i = 0;
        o.setNonce(i);
        while (db.orders.get(o.hashCode())!=null){
            o.setNonce(i++);
        }
        o.setId(o.hashCode());

        // Save order
        db.orders.put(o.getId(), o);
        log.info(o.toString());

        // Start trade execution asynchronously
        if (!orderPublisher.hasSubscribers()){
            orderPublisher.subscribe(orderExecution.getOrderExecutionSubscriberInstance());
        }        
        orderPublisher.submit(o);

        return o;
    }

    // Fetches order details and status
    public Order fetchOrderDetails(Integer order_id) {
        return db.orders.get(order_id);
    }


}
