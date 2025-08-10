package com.example.restaurantapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.databinding.DishCardBinding;

import java.util.ArrayList;
import java.util.List;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder> {

    private final List<Dish> dishList;
    private final Context context;

    public DishAdapter(Context context, List<Dish> dishList) {
        this.context = context;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DishCardBinding binding = DishCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DishViewHolder(binding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull DishAdapter.DishViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.binding.tvDishName.setText(dish.getName());
        holder.binding.tvDishDescription.setText(dish.getDescription());
        holder.binding.tvDishCategory.setText(dish.getCategory());
        holder.binding.tvDishPrice.setText(String.format("%.2f грн", dish.getPrice()));

        int userId = getUserId();
        Log.d("DishAdapter", "User ID: " + userId);

        // Ініціалізуємо кількість на 1
        int[] quantity = {1}; // Використовуємо масив, щоб мати доступ до змінної з обробників кнопок

        holder.binding.tvQuantity.setText(String.valueOf(quantity[0])); // Встановлюємо початкову кількість

        holder.binding.btnIncrease.setOnClickListener(v -> {
            quantity[0]++;
            holder.binding.tvQuantity.setText(String.valueOf(quantity[0])); // Оновлюємо кількість
        });

        holder.binding.btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                holder.binding.tvQuantity.setText(String.valueOf(quantity[0])); // Оновлюємо кількість
            }
        });

        holder.binding.btnOrder.setOnClickListener(v -> {
            if (userId != -1 && dish.getId() > 0) {
                Intent intent = new Intent(context, AddOrderActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("dishName", dish.getName());
                intent.putExtra("dishPrice", dish.getPrice());

                ArrayList<DishItem> dishItemList = new ArrayList<>();
                // Отримуємо кількість
                DishItem dishItem = new DishItem(dish.getId(), dish.getPrice(), quantity[0]);
                dishItemList.add(dishItem);

                Log.d("DishAdapter", "Dish ID: " + dish.getId() + ", Quantity: " + quantity[0]);
                intent.putExtra("dishList", dishItemList);

                context.startActivity(intent);
            } else {
                Log.e("DishAdapter", "Invalid userId or dishId!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    private int getUserId() {
        SharedPreferences preferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {
        final DishCardBinding binding;

        public DishViewHolder(DishCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
