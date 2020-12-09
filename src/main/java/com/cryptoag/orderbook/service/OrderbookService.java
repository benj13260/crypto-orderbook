package com.cryptoag.orderbook.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.cryptoag.Exceptions.BadRequestException;
import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.model.Order;
import com.cryptoag.orderbook.model.Price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderbookService {

    @Autowired
    PriceFeedService priceFeedService;

    ConcurrentHashMap<Integer, Account> Accounts;
    ConcurrentHashMap<Integer, Order> Orders;
    TreeMap<Float, ArrayList<Integer>> OrderPrice;

    public OrderbookService() {
        Accounts = new ConcurrentHashMap<>();
        Orders = new ConcurrentHashMap<>();
        OrderPrice = new TreeMap<>();
    }

    public PriceFeedService getPriceFeedService() {
        return priceFeedService;
    }

    public void validAccount(Account a) throws BadRequestException{
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
        Accounts.put(a.getId(), a);
        return a;
    }

    // Fetches account detail
    public Account fetchAccountDetails(Integer account_id) {
        return Accounts.get(account_id);
    }

    public List<Account> ListAccounts() {
        return new ArrayList<Account>(Accounts.values());
    }

    // Creates a limit order, waiting to be executed when the price limit is
    // reached.
    public Order createLimitOrder(Order o) {
        if (Accounts.get(o.getAccount_id()) == null){
            return null;
        }
        o.setId(o.hashCode());
        o.setStatus(Order.Status.Pending);
        log.info(o.toString());
        Orders.put(o.getId(), o);

        ArrayList<Integer> listOrdersForPrice = OrderPrice.get(o.getPrice_limit());
        if (listOrdersForPrice == null) {
            listOrdersForPrice = new ArrayList<Integer>();
            OrderPrice.put(o.getPrice_limit(), listOrdersForPrice);
        }
        listOrdersForPrice.add(o.getId());

        return o;
    }

    // Fetches order details and status
    public Order fetchOrderDetails(Integer order_id) {
        return Orders.get(order_id);
    }

    public void executeOrders(Price p) {

        OrderPrice.subMap(p.getPrice(),Float.MAX_VALUE).values()
        .forEach(orderList -> {
            orderList.forEach(order -> 
            {
                Order o = Orders.get(order);
                if(o.getStatus() != Order.Status.Executed ){
                    Account a = Accounts.get(o.getAccount_id());
                    o.setStatus(Order.Status.Executed);
                    a.setBtc_balance(a.getBtc_balance()+o.getAmount());
                    a.setUsd_balance(a.getUsd_balance()-(o.getAmount() * p.getPrice()) );
                    log.info(Orders.get(order).toString());
                }
            });
        });
  
    }
}
