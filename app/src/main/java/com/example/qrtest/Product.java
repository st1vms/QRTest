package com.example.qrtest;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Product {
    public String productId;
    public String name;
    public double price;
    public String description;

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "ID -> %s\n\nNAME -> %s\n\nPRICE -> %f\n\nDESCRIPTION -> %s", productId, name, price, description
        );
    }
}
