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
import com.example.restaurantapp.databinding.ActivityAddScheduleBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    ActivityAddScheduleBinding binding;
    RequestQueue queue;
    String SCHEDULE_URL = "http://10.0.2.2:3000/schedule";
    String USER_URL = "http://10.0.2.2:3000/users_staff";
    List<String> usersList;
    List<Integer> usersIds;
    ArrayAdapter<String> usersAdapter;
    int scheduleId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        usersList = new ArrayList<>();
        usersIds = new ArrayList<>();

        usersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, usersList);
        binding.spinnerUser.setAdapter(usersAdapter);

        fetchStockItems();

        scheduleId = getIntent().getIntExtra("schedule_id", -1);
        if(scheduleId != -1){
            loadScheduleData();
        }

        binding.btnSaveSchedule.setOnClickListener(v -> {
            if(scheduleId == -1){
                saveSchedule();
            } else{
                updateDish();
            }
        });
    }

    private void fetchStockItems(){
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, USER_URL, null,
                response -> {
                    usersList.clear();
                    usersIds.clear();
                    for(int i = 0; i < response.length(); i++){
                        try{
                            JSONObject usersItem = response.getJSONObject(i);
                            int id = usersItem.getInt("id");
                            String email = usersItem.getString("email");
                            usersList.add(email);
                            usersIds.add(id);
                        } catch (JSONException e){
                            Log.e("AddScheduleActivity", "JSON Error: " + e.getMessage());
                        }
                    }
                    usersAdapter.notifyDataSetChanged();
                },
                error -> Log.e("AddScheduleActivity", "Volley Error: " + error.getMessage())
        );
        queue.add(request);
    }

    private void saveSchedule(){
        JSONObject scheduleObject = getDishDataFromFields();
        if(scheduleObject == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SCHEDULE_URL, scheduleObject,
                response -> {
                    Toast.makeText(this, "Розклад збережено!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("AddScheduleActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка збереження!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void updateDish(){
        JSONObject dishObject = getDishDataFromFields();
        if(dishObject == null) return;

        String updateUrl = SCHEDULE_URL + "/" + scheduleId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, updateUrl, dishObject,
                response -> {
                    Toast.makeText(this, "Розклад оновлено!", Toast.LENGTH_LONG).show();
                    finish();
                },
                error -> {
                    Log.e("AddScheduleActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "Помилка оновлення!", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void loadScheduleData() {
        String workDate = getIntent().getStringExtra("work_date");

        if (workDate != null && !workDate.isEmpty()) {
            try {
                // Парсимо дату, якщо вона у форматі ISO (yyyy-MM-dd'T'HH:mm:ss)
                if (workDate.contains("T")) {
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date date = isoFormat.parse(workDate);

                    // Перетворюємо її у формат yyyy-MM-dd
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    workDate = dateFormat.format(date);
                }

                binding.etWorkDate.setText(workDate);
            } catch (Exception e) {
                Log.e("AddScheduleActivity", "Помилка форматування дати: " + e.getMessage());
            }
        }

        int usersId = getIntent().getIntExtra("user_id", -1);
        int index = usersIds.indexOf(usersId);
        if (index != -1) {
            binding.spinnerUser.setSelection(index);
        }
    }


    private JSONObject getDishDataFromFields(){
        String workDate = binding.etWorkDate.getText().toString();

        if(workDate.isEmpty()){
           Toast.makeText(this, "Заповніть всі поля!", Toast.LENGTH_LONG).show();
            return null;
        }

        int usersId = usersIds.get(binding.spinnerUser.getSelectedItemPosition());

        JSONObject dishObject = new JSONObject();
        try{
            dishObject.put("user_id", usersId);
            dishObject.put("work_date", workDate);
        } catch (JSONException e){
            Log.e("AddScheduleActivity", "JSON Error: " + e.getMessage());
            return null;
        }

        return dishObject;
    }
}