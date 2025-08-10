package com.example.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityDishesBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DishesActivity extends AppCompatActivity {

    ActivityDishesBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/dishes";
    List<String> dishList;
    ArrayAdapter<String> adapter;
    JSONArray dishJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDishesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        dishList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishList);
        binding.lvDish.setAdapter(adapter);

        fetchDish();

        binding.btnAddDish.setOnClickListener(v -> startActivity(new Intent(this, AddDishActivity.class)));
        binding.lvDish.setOnItemClickListener((parent, view, position, id) -> showOptionsDialog(position));
    }

    private void fetchDish(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    dishList.clear();
                    dishJsonArray = response;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject dishItem = response.getJSONObject(i);
                            String name = dishItem.getString("name");
                            String description = dishItem.getString("description");
                            double price = dishItem.getDouble("price");
                            String category = dishItem.getString("category");
                            String stockId = dishItem.getString("product_name");
                            dishList.add(name + ": " + description + "\n" + price + "\n" + category + "\n" + stockId);
                        }
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        Log.e("DishesActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(this, "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("DishesActivity", "Volley Помилка: " + error.getMessage());
            Toast.makeText(this, "Помилка отримання даних", Toast.LENGTH_LONG).show();
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void showOptionsDialog(int position){
        String selectedItem = dishList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Опції")
                .setMessage("Оберіть дію для: " + selectedItem)
                .setPositiveButton("Редагувати", (dialog, which) -> editDish(position))
                .setNegativeButton("Видалити", (dialog, which) -> deleteDish(position))
                .setNeutralButton("Скасувати", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void editDish(int position){
        try{
            JSONObject dishItem = dishJsonArray.getJSONObject(position);

            Intent intent = new Intent(this, AddDishActivity.class);
            intent.putExtra("dish_id", dishItem.getInt("id"));
            intent.putExtra("name", dishItem.getString("name"));
            intent.putExtra("description", dishItem.getString("description"));
            intent.putExtra("price", dishItem.getDouble("price"));
            intent.putExtra("category", dishItem.getString("category"));
            intent.putExtra("stock_id", dishItem.getInt("stock_id"));
            startActivity(intent);
        }catch (JSONException e){
            Log.e("DishesActivity", "Помилка отримання даних для редагування: " + e.getMessage());
        }
    }

    private void deleteDish(int position){
        try{
            JSONObject dishItem = dishJsonArray.getJSONObject(position);
            int dishId = dishItem.getInt("id");

            String deleteUrl = BASE_URL + "/" + dishId;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        Toast.makeText(this, "Страву видалено!", Toast.LENGTH_LONG).show();
                        fetchDish();
                    },
                    error -> {
                        Log.e("DishesActivity", "Помилка видалення: " + error.getMessage());
                        Toast.makeText(this, "Помилка видалення!", Toast.LENGTH_LONG).show();
                    });
            queue.add(request);
        } catch (JSONException e){
            Log.e("DishActivity", "Помилка JSON: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDish();
    }
}