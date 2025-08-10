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
import com.example.restaurantapp.databinding.ActivityStockBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    ActivityStockBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/stock";
    List<String> stockList;
    ArrayAdapter<String> adapter;
    JSONArray stockJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        stockList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stockList);
        binding.lvStock.setAdapter(adapter);

        fetchStock();

        binding.btnAddStock.setOnClickListener(v -> startActivity(new Intent(this, AddStockActivity.class)));
        binding.lvStock.setOnItemClickListener((parent, view, position, id) -> showOptionsDialog(position));
    }

    private void fetchStock(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    stockList.clear();
                    stockJsonArray = response;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject stockItem = response.getJSONObject(i);
                            String product = stockItem.getString("product_name");
                            int quantity = stockItem.getInt("quantity");
                            String unit = stockItem.getString("unit");
                            stockList.add(product + ": " + quantity + " " + unit);
                        }
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        Log.e("StockActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(this, "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("StockActivity", "Volley Помилка: " + error.getMessage());
                    Toast.makeText(this, "Помилка отримання даних", Toast.LENGTH_LONG).show();
                }
        );

        queue.add(jsonArrayRequest);
    }

    private void showOptionsDialog(int position){
        String selectedItem = stockList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Опції")
                .setMessage("Оберіть дію для: " + selectedItem)
                .setPositiveButton("Редагувати", (dialog, which) -> editStock(position))
                .setNegativeButton("Видалити", (dialog, which) -> deleteStock(position))
                .setNeutralButton("Скасувати", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void editStock(int position) {
        try {
            JSONObject stockItem = stockJsonArray.getJSONObject(position);

            Intent intent = new Intent(this, AddStockActivity.class);
            intent.putExtra("id", stockItem.getInt("id"));
            intent.putExtra("product_name", stockItem.getString("product_name"));
            intent.putExtra("quantity", stockItem.getInt("quantity"));
            intent.putExtra("unit", stockItem.getString("unit"));
            startActivity(intent);
        } catch (JSONException e) {
            Log.e("StockActivity", "Помилка отримання елементу: " + e.getMessage());
        }
    }

    private void deleteStock(int position){
        try{
            JSONObject stockItem = stockJsonArray.getJSONObject(position);
            int stockId = stockItem.getInt("id");

            String deleteUrl = BASE_URL + "/" + stockId;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        Toast.makeText(this, "Продукт видалено!", Toast.LENGTH_LONG).show();
                        fetchStock();
                    },
                    error -> {
                        Log.e("StockActivity", "Помилка видалення: " + error.getMessage());
                        Toast.makeText(this, "Помилка видалення!", Toast.LENGTH_LONG).show();
                    });

            queue.add(request);
        }catch (JSONException e){
            Log.e("StockActivity", "Помилка JSON: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStock();
    }
}