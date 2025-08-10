package com.example.restaurantapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityAddFinanceBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddFinanceActivity extends AppCompatActivity {

    ActivityAddFinanceBinding binding;
    RequestQueue queue;
    String FINANCE_URL = "http://10.0.2.2:3000/finance";
    String ORDER_URL = "http://10.0.2.2:3000/orders_admin";
    ArrayList<String> orderIds = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFinanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, orderIds);
        binding.spinnerOrderId.setAdapter(adapter);

        loadOrderIds();

        binding.spinnerOrderId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchOrderAmount(orderIds.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.tvAmount.setText("Сума: 0.00");
            }
        });

        // Check if we are editing an existing finance record
        if (getIntent().hasExtra("finance_id")) {
            int financeId = getIntent().getIntExtra("finance_id", -1);
            double amount = getIntent().getDoubleExtra("amount", 0);
            int orderId = getIntent().getIntExtra("order_id", -1);
            String description = getIntent().getStringExtra("description");

            binding.spinnerOrderId.setSelection(orderIds.indexOf(String.valueOf(orderId)));
            binding.tvAmount.setText("Сума: " + amount);
            binding.etDescription.setText(description);

            binding.btnSaveSchedule.setText("Зберегти зміни");
            binding.btnSaveSchedule.setOnClickListener(v -> updateFinance(financeId));
        } else {
            binding.btnSaveSchedule.setOnClickListener(v -> addFinanceRecord());
        }
    }

    private void loadOrderIds() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, ORDER_URL, null,
                response -> {
                    orderIds.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject order = response.getJSONObject(i);
                            orderIds.add(order.getString("id"));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("AddFinanceActivity", "Error: " + e.getMessage());
                    }
                },
                error -> Log.e("AddFinanceActivity", "Volley Error: " + error.getMessage()));

        queue.add(request);
    }

    private void fetchOrderAmount(String orderId) {
        String url = ORDER_URL + "/" + orderId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        double amount = response.getDouble("amount");
                        binding.tvAmount.setText("Сума: " + amount);
                    } catch (JSONException e) {
                        Log.e("AddFinanceActivity", "Error: " + e.getMessage());
                    }
                },
                error -> Log.e("AddFinanceActivity", "Volley Error: " + error.getMessage()));

        queue.add(request);
    }

    private void addFinanceRecord() {
        String orderId = (String) binding.spinnerOrderId.getSelectedItem();
        String description = binding.etDescription.getText().toString();

        if (orderId == null || description.isEmpty()) {
            Toast.makeText(this, "Заповніть всі поля!", Toast.LENGTH_LONG).show();
            return;
        }

        double amount = Double.parseDouble(binding.tvAmount.getText().toString().replace("Сума: ", ""));

        JSONObject financeObject = new JSONObject();

        try {
            financeObject.put("amount", amount);
            financeObject.put("order_id", Integer.parseInt(orderId));
            financeObject.put("description", description);
        } catch (JSONException e) {
            Log.e("AddFinanceActivity", "Volley Error: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FINANCE_URL, financeObject,
                response -> {
                    Toast.makeText(this, "Фінансовий запис додано!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("FinanceActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка збереження!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void updateFinance(int financeId) {
        String orderId = (String) binding.spinnerOrderId.getSelectedItem();
        String description = binding.etDescription.getText().toString();

        if (orderId == null || description.isEmpty()) {
            Toast.makeText(this, "Заповніть всі поля!", Toast.LENGTH_LONG).show();
            return;
        }

        double amount = Double.parseDouble(binding.tvAmount.getText().toString().replace("Сума: ", ""));

        JSONObject financeObject = new JSONObject();

        try {
            financeObject.put("amount", amount);
            financeObject.put("order_id", Integer.parseInt(orderId));
            financeObject.put("description", description);
        } catch (JSONException e) {
            Log.e("AddFinanceActivity", "Volley Error: " + e.getMessage());
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, FINANCE_URL + "/" + financeId, financeObject,
                response -> {
                    Toast.makeText(this, "Фінансовий запис оновлено!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("FinanceActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка оновлення!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }
}
