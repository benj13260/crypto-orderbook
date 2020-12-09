package com.cryptoag.orderbook.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private Integer id;
    private String name;
    private float usd_balance;
    private float btc_balance;
    
}
