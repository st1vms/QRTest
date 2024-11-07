package com.example.qrtest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;


public class FireBaseQuery {
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void fetchProduct(String productId, OnSuccessListener<Product> listener) {
        DocumentReference productRef = database.collection("products").document(productId);
        productRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Product product = document.toObject(Product.class);
                    listener.onSuccess(product);
                }
            }
        });
    }
}
