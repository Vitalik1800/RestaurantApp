package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityAddOrderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddOrderActivity extends AppCompatActivity {

    ActivityAddOrderBinding binding;
    RequestQueue queue;
    String ORDER_URL = "http://10.0.2.2:3000/order";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String dishName = getIntent().getStringExtra("dishName");
        binding.dishNameTextView.setText("Замовлення: " + dishName);
        queue = Volley.newRequestQueue(this);

        // Get user ID and dish list from intent
        int userId = getIntent().getIntExtra("userId", -1);
        Log.d("AddOrderActivity", "User ID: " + userId);

        List<DishItem> dishItemList = (List<DishItem>) getIntent().getSerializableExtra("dishList");

        // Calculate total price and update the TextView
        double totalPrice = calculateTotalPrice(dishItemList);
        binding.totalPrice.setText("Загальна вартість: " + totalPrice + " грн");

        binding.btnSaveOrder.setOnClickListener(v -> saveOrder());
    }

    private double calculateTotalPrice(List<DishItem> dishItemList) {
        double totalPrice = 0;
        if (dishItemList != null && !dishItemList.isEmpty()) {
            for (DishItem dishItem : dishItemList) {
                totalPrice += dishItem.getStockQuantity() * dishItem.getPrice();
            }
        }
        return totalPrice;
    }

    private void saveOrder() {
        JSONObject orderObject = getOrderDataFromFields();
        if (orderObject == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ORDER_URL, orderObject,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Log.e("AddOrderActivity", "Error parsing response: " + e.getMessage());
                    }
                    finish();
                },
                error -> {
                    if (error.networkResponse != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("AddOrderActivity", "Error: " + errorMessage);
                        Toast.makeText(this, "Помилка: " + errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("AddOrderActivity", "Error: " + error.getMessage());
                        Toast.makeText(this, "Не вдалося відправити замовлення", Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(request);
    }

    private JSONObject getOrderDataFromFields() {
        int userId = getIntent().getIntExtra("userId", -1);
        List<DishItem> dishItemList = (List<DishItem>) getIntent().getSerializableExtra("dishList");

        if (userId == -1 || dishItemList == null || dishItemList.isEmpty()) {
            Log.e("AddOrderActivity", "Некоректні дані для замовлення!");
            return null;
        }

        double totalPrice = 0;
        JSONArray dishesArray = new JSONArray();

        try {
            for (DishItem dishItem : dishItemList) {
                JSONObject dishObject = new JSONObject();
                dishObject.put("dish_id", dishItem.getDishId());
                dishObject.put("quantity", dishItem.getStockQuantity());
                dishesArray.put(dishObject);

                totalPrice += dishItem.getStockQuantity() * dishItem.getPrice();
            }

            JSONObject orderObject = new JSONObject();
            orderObject.put("user_id", userId);
            orderObject.put("total_price", totalPrice);
            orderObject.put("items", dishesArray);

            return orderObject;

        } catch (JSONException e) {
            Log.e("AddOrderActivity", "Помилка формування JSON: " + e.getMessage());
            return null;
        }
    }
}
