package com.example.restaurantapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityLoginBinding;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(v -> loginUser());
        binding.registerTextView.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {
        String userEmail = binding.email.getText().toString().trim();
        String userPassword = binding.password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Будь ласка, введіть email та пароль!", Toast.LENGTH_LONG).show();
            return;
        }

        url = "http://10.0.2.2:3000/login";

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", userEmail);
            postData.put("password", userPassword);
        } catch (JSONException e) {
            Log.e("LoginActivity", "JSON Exception: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    Log.d("LoginResponse", "Response: " + response.toString());
                    handleLoginSuccess(response);
                },
                this::handleLoginError
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void handleLoginSuccess(JSONObject response) {
        String token = response.optString("token", null);
        String role = response.optString("role", "user");
        int userId = response.optInt("userId", -1);

        Log.d("LoginSuccess", "Token: " + token);
        Log.d("LoginSuccess", "Role: " + role);
        Log.d("LoginSuccess", "UserID: " + userId);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.putString("role", role);
        editor.putInt("userId", userId);
        editor.apply();

        Log.d("LoginActivity", "User ID saved: " + userId);

        Toast.makeText(this, "Авторизація успішна!", Toast.LENGTH_LONG).show();

        Intent intent;
        if("admin".equals(role)){
            intent = new Intent(this, AdminActivity.class);
        }else if("staff".equals(role)){
            intent = new Intent(this, StaffActivity.class);
        }
        else{
            intent = new Intent(this, MainActivity.class);
        }
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    private void handleLoginError(VolleyError error) {
        if (error.networkResponse != null) {
            Log.e("LoginError", "Статус код: " + error.networkResponse.statusCode);
            Log.e("LoginError", "Відповідь: " + new String(error.networkResponse.data));
        }
        Toast.makeText(this, "Авторизація неуспішна! Перевірте email та пароль.", Toast.LENGTH_LONG).show();
    }
}
