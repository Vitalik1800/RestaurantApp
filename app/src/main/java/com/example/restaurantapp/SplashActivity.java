package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurantapp.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    SharedPreferences sharedPreferences;
    String token;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        role = sharedPreferences.getString("role", "user");

        new Handler().postDelayed(() -> {
            if (token != null) {
                if ("admin".equalsIgnoreCase(role)){
                    startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                }else if("staff".equalsIgnoreCase(role)){
                    startActivity(new Intent(SplashActivity.this, StaffActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } 
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}