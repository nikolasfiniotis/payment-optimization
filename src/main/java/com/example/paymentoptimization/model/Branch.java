package com.example.paymentoptimization.model;

import lombok.Data;

@Data
public class Branch {
    private final String name;
    private final int cost;

    public Branch(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }
}
