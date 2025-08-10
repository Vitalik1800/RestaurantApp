package com.example.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityRegisterBinding;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser(){
        String userName = binding.username.getText().toString();
        String userEmail = binding.email.getText().toString();
        String userPhone = binding.phone.getText().toString();
        String userPassword = binding.password.getText().toString();

        if(userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(this, "Будь ласка заповніть всі поля!", Toast.LENGTH_LONG).show();
            return;
        }

        String url = "http://10.0.2.2:3000/register";
        JSONObject postData = new JSONObject();
        try{
            postData.put("name", userName);
            postData.put("email", userEmail);
            postData.put("phone", userPhone);
            postData.put("password", userPassword);
        } catch (JSONException e){
            Log.e("RegisterActivity", Objects.requireNonNull(e.getMessage()));
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    Log.d("RegisterActivity", "Відповідь сервера: " + response.toString());
                    Toast.makeText(this, "Реєстрація успішна!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, LoginActivity.class));
                },
                error -> {
                    Log.e("RegisterError", "Помилка: " + error.getMessage());
                    Toast.makeText(this, "Реєстрація неуспішна!", Toast.LENGTH_LONG).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}