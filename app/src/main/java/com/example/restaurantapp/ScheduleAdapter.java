package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    List<Schedule> scheduleList;

    // Constructor
    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);

        // Parse and format the date
        String formattedDate = formatDate(schedule.getWork_date());
        holder.dateTextView.setText("Дата: " + formattedDate);

        holder.shiftTextView.setText("Зміна: " + schedule.getShift());
    }

    // Return the size of the data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    // Method to format the date
    private String formatDate(String dateStr) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // adjust if needed
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy"); // adjust the desired format

        try {
            Date date = originalFormat.parse(dateStr);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // return the original string if parsing fails
        }
    }

    // Provide a reference to the type of views you're using (custom ViewHolder)
    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView shiftTextView;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.schedule_date);
            shiftTextView = itemView.findViewById(R.id.schedule_shift);
        }
    }
}
