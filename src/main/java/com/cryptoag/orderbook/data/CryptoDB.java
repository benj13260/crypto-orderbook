package com.cryptoag.orderbook.data;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.model.Order;
import com.cryptoag.orderbook.model.Price;

import org.springframework.stereotype.Service;


@Service
public class CryptoDB {

    public Price currentPrice;

    public ConcurrentHashMap<Integer, Account> accounts;    // Acccounts repository
    public ConcurrentHashMap<Integer, Order> orders;        // Order repository
    public TreeMap<Float, ArrayList<Integer>> orderPrice;   // Orders organized by price, treemap for fast crawling
    
    public CryptoDB() {
        accounts = new ConcurrentHashMap<>();
        orders = new ConcurrentHashMap<>();
        orderPrice = new TreeMap<>();
    }

}
