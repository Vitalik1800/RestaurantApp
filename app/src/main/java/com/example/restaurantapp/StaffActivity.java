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
import com.example.restaurantapp.databinding.ActivityStaffBinding;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class StaffActivity extends AppCompatActivity {

    int userId;
    ActivityStaffBinding binding;
    ScheduleAdapter scheduleAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getUserId();
        Log.d("StaffActivity", "UserId: " + userId);

        binding.scheduleHeaderTextView.setText("Розклад для працівника №: " + userId);
        binding.scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSchedule(userId);

        binding.logout.setOnClickListener(v -> logout());
    }

    private int getUserId() {
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    private void getSchedule(int userId) {
        String url = "http://10.0.2.2:3000/schedule/" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Schedule> scheduleList = parseScheduleResponse(response);

                    displaySchedule(scheduleList);
                },
                error -> {
                    Toast.makeText(StaffActivity.this, "Помилка при отриманні розкладу", Toast.LENGTH_SHORT).show();
                    Log.e("StaffActivity", "Error: " + error.getMessage());
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private List<Schedule> parseScheduleResponse(JSONArray response) {
        List<Schedule> scheduleList = new ArrayList<>();

        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject scheduleObject = response.getJSONObject(i);

                Schedule schedule = new Schedule();
                schedule.setSchedule_id(scheduleObject.getInt("schedule_id"));
                schedule.setUser_id(scheduleObject.getInt("user_id"));
                schedule.setWork_date(scheduleObject.getString("work_date"));
                schedule.setShift(scheduleObject.getString("shift"));

                scheduleList.add(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scheduleList;
    }

    private void displaySchedule(List<Schedule> scheduleList) {
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        binding.scheduleRecyclerView.setAdapter(scheduleAdapter);
    }

    private void logout() {
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(StaffActivity.this, "Ви вийшли з облікового запису", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(StaffActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
