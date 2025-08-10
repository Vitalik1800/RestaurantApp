package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityMyOrdersBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    OrderAdapter orderAdapter;
    List<Order> orderList = new ArrayList<>();
    int userId;
    ActivityMyOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getIntExtra("userId", -1);

        Log.d("UserID", "User ID: " + userId);

        if (userId != -1) {
            loadOrders(userId);
        } else {
            Toast.makeText(this, "Помилка: ID користувача відсутній", Toast.LENGTH_LONG).show();
        }
    }

    private void loadOrders(int userId) {
        String url = "http://10.0.2.2:3000/orders/" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API_RESPONSE", "Response: " + response.toString());

                        orderList.clear();

                        if (response.length() == 0) {
                            Toast.makeText(MyOrdersActivity.this, "Немає замовлень", Toast.LENGTH_SHORT).show();
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject orderJson = response.getJSONObject(i);

                            int orderId = orderJson.getInt("order_id");
                            String orderStatus = orderJson.getString("order_status");
                            double totalPrice = orderJson.getDouble("total_price");
                            String createdAt = orderJson.getString("created_at");

                            JSONArray dishesJson = orderJson.getJSONArray("dishes");
                            List<Dish> dishes = new ArrayList<>();
                            for (int j = 0; j < dishesJson.length(); j++) {
                                JSONObject dishJson = dishesJson.getJSONObject(j);
                                int dishId = dishJson.getInt("dish_id");
                                String dishName = dishJson.getString("dish_name");
                                int quantity = dishJson.getInt("quantity");
                                double price = dishJson.getDouble("price");

                                dishes.add(new Dish(dishId, dishName, quantity, price));
                            }

                            orderList.add(new Order(orderId, orderStatus, totalPrice, createdAt, dishes));
                        }

                        Log.d("ORDER_LIST_SIZE", "Order List Size: " + orderList.size());

                        if (orderList.isEmpty()) {
                            Toast.makeText(this, "Немає замовлень для відображення", Toast.LENGTH_SHORT).show();
                        } else {
                            orderAdapter = new OrderAdapter(orderList);
                            binding.recyclerViewOrders.setAdapter(orderAdapter);
                            binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

                            orderAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        Log.e("API_ERROR", "Помилка: " + e.getMessage());
                    }
                },
                error -> Log.e("API_ERROR", "Помилка при завантаженні замовлень", error)
        );

        queue.add(request);
    }
}
