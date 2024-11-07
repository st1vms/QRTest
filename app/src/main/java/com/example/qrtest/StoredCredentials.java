package com.example.qrtest;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class StoredCredentials {
    private final SharedPreferences sharedPreferences;
    private static final String prefKey = "AUTH";
    String email;
    String password;

    public StoredCredentials (Context context){
        this.sharedPreferences = context.getSharedPreferences(prefKey, MODE_PRIVATE);
        this.email = sharedPreferences.getString("email", null);
        this.password = sharedPreferences.getString("password", null);
    }

    public boolean credentialsSet(){
        return this.email != null && this.password != null;
    }

    public void saveCredentials(String email, String password){
        this.email = email;
        this.password = password;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    public void deleteCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
