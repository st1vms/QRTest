package com.example.qrtest;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private StoredCredentials credentials;
    private EditText resultsPageEditText;
    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(),this::OnScanCompleted);


    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            // Show explanation if needed (optional, here we just proceed)
            Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_LONG).show();
        }
        // Request the camera permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with your app
                OnPermissionGranted();
            } else {
                // Permission denied, close the app
                Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void OnLoginSuccess(){
        Toast.makeText(this, "Logged in as: " + this.credentials.email, Toast.LENGTH_SHORT).show();
    }

    public void OnLoginFailure(){
        Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
        credentials.deleteCredentials();
        startLoginActivity();
    }

    private void tryFireBaseLogin(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(this.credentials.email, this.credentials.password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    OnLoginSuccess();
                } else {
                    OnLoginFailure();
                }
            }
        );
    }

    private void startLoginActivity(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void scanButtonOnClickListener(View v){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }


    private void OnScanCompleted(ScanIntentResult result){
        if(result.getContents() == null){
            Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO Make FireBase product query based off product ID in the code.
        FireBaseQuery fireBaseQuery = new FireBaseQuery();
        fireBaseQuery.fetchProduct(result.getContents(), new OnSuccessListener<Product>() {
            @Override
            public void onSuccess(Product product) {
                resultsPageEditText.setText(product.toString());
            }
        });
    }

    private void OnPermissionGranted(){
        setContentView(R.layout.activity_main);
        this.resultsPageEditText = findViewById(R.id.result_text_page);
        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this::scanButtonOnClickListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        this.credentials = new StoredCredentials(getApplicationContext());
        if (!this.credentials.credentialsSet()){
            // Go to Login activity
            startLoginActivity();
            return;
        }

        Log.d("Auth", this.credentials.email);
        Log.d("Auth", this.credentials.password);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Credentials saved, but user is not signed in
            tryFireBaseLogin();
            return;
        }
        // User is already signed in, proceed to the main activity
        Log.d("Firebase", "Logged as user: " + currentUser.getUid());

        // Check for camera permission
        if (isCameraPermissionGranted()) {
            // Continue with the normal flow of your app
            OnPermissionGranted();
        } else {
            // Request camera permission
            requestCameraPermission();
        }
    }
}