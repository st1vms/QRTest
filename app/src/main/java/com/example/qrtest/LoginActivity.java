package com.example.qrtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this::loginButtonOnClickListener);
    }

    public void OnLoginSuccess(String email, String password){
        StoredCredentials credentials = new StoredCredentials(getApplicationContext());
        credentials.saveCredentials(email, password);
        Toast.makeText(this, "Login success!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void OnLoginFailure(){
        Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
    }

    private void tryFireBaseLogin(String email, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    OnLoginSuccess(email, password);
                } else {
                    OnLoginFailure();
                }
            }
        );
    }

    private void loginButtonOnClickListener(View v){
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            // Wrong credentials
            Toast.makeText(this, "Please provide email and password", Toast.LENGTH_SHORT).show();
            return;
        }
        tryFireBaseLogin(email, password);
    }
}