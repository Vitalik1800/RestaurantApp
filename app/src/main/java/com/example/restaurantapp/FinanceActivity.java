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
import com.example.restaurantapp.databinding.ActivityFinanceBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FinanceActivity extends AppCompatActivity {

    ActivityFinanceBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/finance";
    List<String> financeList;
    ArrayAdapter<String> adapter;
    JSONArray financeJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        financeList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, financeList);
        binding.lvFinance.setAdapter(adapter);

        fetchFinance();

        binding.btnAddFinance.setOnClickListener(v -> startActivity(new Intent(this, AddFinanceActivity.class)));
        binding.lvFinance.setOnItemClickListener((parent, view, position, id) -> showOptionsDialog(position));
    }

    private void fetchFinance() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    financeList.clear();
                    financeJsonArray = response;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject finances = response.getJSONObject(i);
                            String transaction_type = finances.getString("transaction_type");
                            double amount = finances.getDouble("amount");
                            int order_id = finances.getInt("order_id");
                            String description = finances.getString("description");
                            financeList.add(transaction_type + "\n" + amount + "\n" + order_id + "\n" + description);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("FinanceActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(this, "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("FinanceActivity", "Volley Помилка: " + error.getMessage());
            Toast.makeText(this, "Помилка отримання даних", Toast.LENGTH_LONG).show();
        });

        queue.add(jsonArrayRequest);
    }

    private void showOptionsDialog(int position) {
        String selectedItem = financeList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Опції")
                .setMessage("Оберіть дію для: " + selectedItem)
                .setPositiveButton("Редагувати", (dialog, which) -> editFinance(position))
                .setNegativeButton("Видалити", (dialog, which) -> deleteFinance(position))
                .setNeutralButton("Скасувати", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void editFinance(int position) {
        try {
            JSONObject financeItem = financeJsonArray.getJSONObject(position);

            Intent intent = new Intent(this, AddFinanceActivity.class);
            intent.putExtra("finance_id", financeItem.getInt("id"));
            intent.putExtra("amount", financeItem.getDouble("amount"));
            intent.putExtra("order_id", financeItem.getInt("order_id"));
            intent.putExtra("description", financeItem.getString("description"));
            startActivity(intent);
        } catch (JSONException e) {
            Log.e("FinanceActivity", "Помилка отримання даних для редагування: " + e.getMessage());
        }
    }

    private void deleteFinance(int position) {
        try {
            JSONObject financeItem = financeJsonArray.getJSONObject(position);
            int financeId = financeItem.getInt("id");

            String deleteUrl = BASE_URL + "/" + financeId;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        Toast.makeText(this, "Фінансову інформацію видалено!", Toast.LENGTH_LONG).show();
                        fetchFinance();
                    },
                    error -> {
                        Log.e("FinanceActivity", "Помилка видалення: " + error.getMessage());
                        Toast.makeText(this, "Помилка видалення!", Toast.LENGTH_LONG).show();
                    });
            queue.add(request);
        } catch (JSONException e) {
            Log.e("FinanceActivity", "Помилка JSON: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchFinance();
    }
}
