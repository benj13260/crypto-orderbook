package com.cryptoag.orderbook.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

    public enum Status {
        Pending, Executed 
    }

    private Integer id;
    private Integer account_id;
    private float price_limit;
    private float amount;
    private Status status;
}
