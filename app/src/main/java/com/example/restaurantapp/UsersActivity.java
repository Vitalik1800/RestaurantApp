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
import com.example.restaurantapp.databinding.ActivityUsersBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    ActivityUsersBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/users";
    List<String> usersList;
    ArrayAdapter<String> adapter;
    JSONArray usersJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        usersList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList);
        binding.lvUsers.setAdapter(adapter);

        fetchOrder();
    }

    private void fetchOrder(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    usersList.clear();
                    usersJsonArray = response;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject users = response.getJSONObject(i);
                            String name = users.getString("name");
                            String email = users.getString("email");
                            String phone = users.getString("phone");
                            usersList.add(name + "\n" + email + "\n" + phone);
                        }
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        Log.e("UsersActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(this, "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("UsersActivity", "Volley Помилка: " + error.getMessage());
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