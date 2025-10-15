package com.explore.ratelimiting.Ratelimiting.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderData {

    private String userId;
    private String restaurantId;
    private String items;
    private String amount;
    private String estimatedDelivery;
    private String orderStatus;
    private String placedAt;


    public OrderData(String userId, String restaurantId, String items, String amount) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.amount = amount;
        this.estimatedDelivery = "30-45 minutes";
        this.orderStatus = "CONFIRMED";
        this.placedAt = Instant.now().toString();
    }


    public static OrderData create(String userId, String restaurantId, String items, String amount) {
        return new OrderData(userId, restaurantId, items, amount);
    }
}
