package com.cryptoag.orderbook.service.subscribers;

import java.util.ArrayList;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import com.cryptoag.orderbook.data.CryptoDB;
import com.cryptoag.orderbook.model.Account;
import com.cryptoag.orderbook.model.Order;
import com.cryptoag.orderbook.model.Price;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderExecutionSubscriber implements Subscriber<Object> {

    public CryptoDB db;

    private Subscription subscription;

    public OrderExecutionSubscriber(CryptoDB db) {
        this.db = db;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();

    }

    @Override
    public void onComplete() {
        log.info("Done");

    }

    /**
     * Orderbook trade execution is performed for each new event: Price 0R Order
     * and avoid any multi-threading issue
     */
    @Override
    public void onNext(Object item) {
        log.info("Run orderbook execution based on new " + item);

        if (item instanceof Price)
            // Set the price
            db.currentPrice = (Price) item;

        else if (item instanceof Order) {
            // Attach the order to the orderbook
            
            Order o = (Order) item;
            ArrayList<Integer> listOrdersForPrice = db.orderPrice.get(o.getPrice_limit());
            if (listOrdersForPrice == null) {
                listOrdersForPrice = new ArrayList<Integer>();
                db.orderPrice.put(o.getPrice_limit(), listOrdersForPrice);
            }
            listOrdersForPrice.add(o.getId());

            if (db.currentPrice == null) // Prevent error when an order is created before price feed is started
                return;
        }

        // Execute trades
        float p = db.currentPrice.getPrice();

        db.orderPrice.subMap(p, Float.MAX_VALUE).values().forEach(orderList -> { // Get all orders below the limit price
            orderList.forEach(order -> { // For all orders with the same price
                Order o = db.orders.get(order);
                if (o.getStatus() != Order.Status.Executed) { // Update balances
                    Account a = db.accounts.get(o.getAccount_id());
                    o.setStatus(Order.Status.Executed);
                    a.setBtc_balance(a.getBtc_balance() + o.getAmount());
                    a.setUsd_balance(a.getUsd_balance() - (o.getAmount() * p));
                    log.info("Order Executed: " + db.orders.get(order).toString());
                }
            });
        });
        subscription.request(1);
    }

}
