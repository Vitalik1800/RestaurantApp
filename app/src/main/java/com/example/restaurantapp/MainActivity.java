package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityMainBinding;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    DishAdapter adapter;
    List<Dish> dishList;
    RequestQueue queue;
    private static final String DISH_URL = "http://10.0.2.2:3000/dishes";
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getUserId();
        Log.d("MainActivity", "UserId: " + userId);

        binding.btnMyOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        binding.recyclerViewDishes.setLayoutManager(new LinearLayoutManager(this));

        dishList = new ArrayList<>();
        adapter = new DishAdapter(this, dishList);
        binding.recyclerViewDishes.setAdapter(adapter);

        queue = Volley.newRequestQueue(this);
        fetchDishes();

        binding.logout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this, "Ви вийшли з облікового запису", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private int getUserId() {
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    private void fetchDishes(){
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                DISH_URL,
                null,
                response -> {
                    try{
                        dishList.clear();
                        for(int i = 0; i < response.length(); i++){
                            JSONObject dishObject = response.getJSONObject(i);
                            int id = dishObject.getInt("id");
                            String name = dishObject.getString("name");
                            String description = dishObject.getString("description");
                            String category = dishObject.getString("category");
                            double price = dishObject.getDouble("price");
                            dishList.add(new Dish(id, name, description, category, price));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e){
                        Log.e("MainActivity", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("MainActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка отримання даних!", Toast.LENGTH_LONG).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}