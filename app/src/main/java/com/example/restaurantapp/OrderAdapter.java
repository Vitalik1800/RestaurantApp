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
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText("Замовлення №" + order.getOrderId());
        holder.orderStatus.setText("Статус: " + order.getOrderStatus());
        holder.totalPrice.setText("Сума: " + order.getTotalPrice() + " грн");

        // Форматування дати перед відображенням
        String formattedDate = formatDate(order.getCreatedAt());
        holder.createdAt.setText("Дата: " + formattedDate);

        StringBuilder dishesText = new StringBuilder();
        for (Dish dish : order.getDishes()) {
            dishesText.append(dish.getName()).append(" x").append(dish.getQuantity()).append("\n");
        }
        holder.dishes.setText(dishesText.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, totalPrice, createdAt, dishes;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.textOrderId);
            orderStatus = itemView.findViewById(R.id.textOrderStatus);
            totalPrice = itemView.findViewById(R.id.textTotalPrice);
            createdAt = itemView.findViewById(R.id.textCreatedAt);
            dishes = itemView.findViewById(R.id.textDishes);
        }
    }

    // Метод для форматування дати
    private String formatDate(String rawDate) {
        try {
            // Вхідний формат (припустимо, що сервер повертає "yyyy-MM-dd'T'HH:mm:ss")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            Date date = inputFormat.parse(rawDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate; // Якщо сталася помилка, повертаємо оригінальний рядок
        }
    }
}
