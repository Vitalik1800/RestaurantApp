package com.example.restaurantapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityAddDishBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddDishActivity extends AppCompatActivity {

    ActivityAddDishBinding binding;
    RequestQueue queue;
    String DISH_URL = "http://10.0.2.2:3000/dishes";
    String STOCK_URL = "http://10.0.2.2:3000/stock";
    List<String> stockList;
    List<Integer> stockIds;
    ArrayAdapter<String> stockAdapter;
    int dishId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDishBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        stockList = new ArrayList<>();
        stockIds = new ArrayList<>();

        stockAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stockList);
        binding.spinnerStock.setAdapter(stockAdapter);

        fetchStockItems();

        dishId = getIntent().getIntExtra("dish_id", -1);
        if(dishId != -1){
            loadDishData();
        }

        binding.btnSaveDish.setOnClickListener(v -> {
            if(dishId == -1){
                saveDish();
            } else{
                updateDish();
            }
        });
    }

    private void fetchStockItems(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, STOCK_URL, null,
                response -> {
                    stockList.clear();
                    stockIds.clear();
                    for(int i = 0; i < response.length(); i++){
                        try{
                            JSONObject stockItem = response.getJSONObject(i);
                            int id = stockItem.getInt("id");
                            String name = stockItem.getString("product_name");
                            stockList.add(name);
                            stockIds.add(id);
                        } catch (JSONException e){
                            Log.e("AddDishActivity", "JSON Error: " + e.getMessage());
                        }
                    }
                    stockAdapter.notifyDataSetChanged();
                },
                error -> Log.e("AddDishActivity", "Volley Error: " + error.getMessage())
        );
        queue.add(request);
    }

    private void saveDish(){
        JSONObject dishObject = getDishDataFromFields();
        if(dishObject == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, DISH_URL, dishObject,
                response -> {
                    Toast.makeText(this, "Страва збережена!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("AddDishActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка збереження!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void updateDish(){
        JSONObject dishObject = getDishDataFromFields();
        if(dishObject == null) return;

        String updateUrl = DISH_URL + "/" + dishId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateUrl, dishObject,
                response -> {
                    Toast.makeText(this, "Страва оновлена!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("AddDishActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка оновлення!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void loadDishData(){
        binding.etDishName.setText(getIntent().getStringExtra("name"));
        binding.etDescription.setText(getIntent().getStringExtra("description"));
        binding.etPrice.setText(String.valueOf(getIntent().getDoubleExtra("price", 0.0)));
        binding.etCategory.setText(getIntent().getStringExtra("category"));

        int stockId = getIntent().getIntExtra("stock_id", -1);
        int index = stockIds.indexOf(stockId);
        if(index != -1){
            binding.spinnerStock.setSelection(index);
        }
    }

    private JSONObject getDishDataFromFields(){
        String name = binding.etDishName.getText().toString();
        String description = binding.etDescription.getText().toString();
        String price = binding.etPrice.getText().toString().trim();
        String category = binding.etCategory.getText().toString();

        if(name.isEmpty() || description.isEmpty() || price.isEmpty() || category.isEmpty()){
            Toast.makeText(this, "Заповніть всі поля!", Toast.LENGTH_LONG).show();
            return null;
        }

        int stockId = stockIds.get(binding.spinnerStock.getSelectedItemPosition());

        JSONObject dishObject = new JSONObject();
        try{
            dishObject.put("name", name);
            dishObject.put("description", description);
            dishObject.put("price", Double.parseDouble(price));
            dishObject.put("category", category);
            dishObject.put("stock_id", stockId);
        } catch (JSONException e){
            Log.e("AddDishActivity", "JSON Error: " + e.getMessage());
            return null;
        }

        return dishObject;
    }
}