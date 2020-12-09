package com.cryptoag.orderbook.model;

import java.util.Date;

import lombok.Data;

@Data
public class Price {
    float price;
    Date timestamp;
}
