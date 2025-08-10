package com.example.restaurantapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantapp.databinding.ActivityOrdersBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    ActivityOrdersBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/orders_admin";
    List<String> ordersList;
    ArrayAdapter<String> adapter;
    JSONArray ordersJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        ordersList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ordersList);
        binding.lvOrders.setAdapter(adapter);

        fetchOrder();
    }

    private void fetchOrder(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    ordersList.clear();
                    ordersJsonArray = response;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject orders = response.getJSONObject(i);
                            String email = orders.getString("email");
                            String order_status = orders.getString("order_status");
                            double price = orders.getDouble("total_price");
                            ordersList.add(email + "\n" + order_status + "\n" + price);
                        }
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        Log.e("OrdersActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(this, "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("OrdersActivity", "Volley Помилка: " + error.getMessage());
            Toast.makeText(this, "Помилка отримання даних", Toast.LENGTH_LONG).show();
        }
        );

        queue.add(jsonArrayRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOrder();
    }
}