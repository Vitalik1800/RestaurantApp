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
import com.example.restaurantapp.databinding.ActivityScheduleBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    ActivityScheduleBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/schedule";
    List<String> scheduleList;
    ArrayAdapter<String> adapter;
    JSONArray scheduleJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        scheduleList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
        binding.lvSchedule.setAdapter(adapter);

        fetchSchedule();

        binding.btnAddSchedule.setOnClickListener(v -> startActivity(new Intent(this, AddScheduleActivity.class)));
        binding.lvSchedule.setOnItemClickListener((parent, view, position, id) -> showOptionsDialog(position));
    }

    private void fetchSchedule(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    scheduleList.clear();
                    scheduleJsonArray = response;
                    try {
                        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject Schedule = response.getJSONObject(i);
                            String email = Schedule.getString("email");
                            String workDate = Schedule.getString("work_date");

                            String formattedDate;
                            try {
                                Date date = serverFormat.parse(workDate);
                                formattedDate = displayFormat.format(date);
                            } catch (ParseException e) {
                                formattedDate = workDate;
                            }

                            scheduleList.add(email + "\n" + formattedDate);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("ScheduleActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(this, "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("ScheduleActivity", "Volley Помилка: " + error.getMessage());
            Toast.makeText(this, "Помилка отримання даних", Toast.LENGTH_LONG).show();
        });

        queue.add(jsonArrayRequest);
    }

    private void showOptionsDialog(int position){
        String selectedItem = scheduleList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Опції")
                .setMessage("Оберіть дію для: " + selectedItem)
                .setPositiveButton("Редагувати", (dialog, which) -> editSchedule(position))
                .setNegativeButton("Видалити", (dialog, which) -> deleteSchedule(position))
                .setNeutralButton("Скасувати", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void editSchedule(int position){
        try{
            JSONObject scheduleItem = scheduleJsonArray.getJSONObject(position);

            Intent intent = new Intent(this, AddScheduleActivity.class);
            intent.putExtra("schedule_id", scheduleItem.getInt("id"));
            intent.putExtra("user_id", scheduleItem.getInt("user_id"));
            intent.putExtra("work_date", scheduleItem.getString("work_date"));
            startActivity(intent);
        }catch (JSONException e){
            Log.e("ScheduleActivity", "Помилка отримання даних для редагування: " + e.getMessage());
        }
    }

    private void deleteSchedule(int position){
        try{
            JSONObject scheduleItem = scheduleJsonArray.getJSONObject(position);
            int scheduleId = scheduleItem.getInt("id");

            String deleteUrl = BASE_URL + "/" + scheduleId;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        Toast.makeText(this, "Розклад видалено!", Toast.LENGTH_LONG).show();
                        fetchSchedule();
                    },
                    error -> {
                        Log.e("ScheduleActivity", "Помилка видалення: " + error.getMessage());
                        Toast.makeText(this, "Помилка видалення!", Toast.LENGTH_LONG).show();
                    });
            queue.add(request);
        } catch (JSONException e){
            Log.e("ScheduleActivity", "Помилка JSON: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSchedule();
    }
}