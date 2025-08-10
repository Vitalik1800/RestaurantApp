package com.example.restaurantapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityAddStockBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class AddStockActivity extends AppCompatActivity {

    ActivityAddStockBinding binding;
    RequestQueue queue;
    static final String BASE_URL = "http://10.0.2.2:3000/stock";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);

        int stockId = getIntent().getIntExtra("id", -1);
        Log.d("AddStockActivity", "Отриманий stockId: " + stockId);
        if(stockId != -1){
            binding.btnAddProduct.setText("Оновити залишок");
            String productName = getIntent().getStringExtra("product_name");
            int quantity = getIntent().getIntExtra("quantity", 0);
            String unit = getIntent().getStringExtra("unit");
            if(productName != null) binding.etProductName.setText(productName);
            binding.etQuantity.setText(String.valueOf(quantity));
            if(unit != null) binding.etUnit.setText(unit);
        } else{
            binding.btnAddProduct.setText("Додати залишок");
        }

        binding.btnAddProduct.setOnClickListener(v -> {
            if(stockId == -1){
                addProduct();
            } else{
                updateProduct(stockId);
            }
        });
    }

    private void addProduct(){
        JSONObject productData = getProductData();
        if(productData == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL, productData,
                response -> {
                    Toast.makeText(this, "Продукт додано!", Toast.LENGTH_LONG).show();
                    finish();
                }, error -> {
                    Log.e("AddStockActivity", "Помилка додавання: " + error.getMessage());
                    Toast.makeText(this, "Помилка під час додавання!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void updateProduct(int id){
        JSONObject productData = getProductData();
        if(productData == null) return;

        String updateURL = BASE_URL + "/" + id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateURL, productData,
                response -> {
                    Toast.makeText(this, "Продукт оновлено!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("AddStockActivity", "Помилка оновлення: " + error.getMessage());
                    Toast.makeText(this, "Помилка під час оновлення!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private JSONObject getProductData(){
        String productName = binding.etProductName.getText().toString();
        String quantityStr = binding.etQuantity.getText().toString().trim();
        String unit = binding.etUnit.getText().toString().trim();

        if(productName.isEmpty() || quantityStr.isEmpty() || unit.isEmpty()){
            Toast.makeText(this, "Всі поля обов'язкові!", Toast.LENGTH_LONG).show();
            return null;
        }

        JSONObject productData = new JSONObject();
        try{
            productData.put("product_name", productName);
            productData.put("quantity", Integer.parseInt(quantityStr));
            productData.put("unit", unit);
        } catch (JSONException e){
            Log.e("AddStockActivity", "JSON Помилка: " + e.getMessage());
            return null;
        }
        return productData;
    }
}