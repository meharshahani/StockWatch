package com.example.stockwatch;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class StockAdapter extends RecyclerView.Adapter<MyViewHolder>
{
    public static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> stockList, MainActivity mainActivity)
    {
        this.stockList = stockList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder: THE VIEW");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout
        .stock_entry, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Stock stock = stockList.get(position);
        holder.companyName.setText(stock.getCompanyName());
        holder.stockSymbol.setText(stock.getStockSymbol());
        holder.stockPrice.setText(String.format("$%s", stock.getStockPrice()));
       // holder.priceChange.setText(String.format("%.2f (%.2f%%)", stock.getPriceChange(), stock.getPercentageChange()));

        Log.d(TAG, "price change:" + stock.getStockPrice());
        if(stock.getPercentageChange() >= 0)
        {
            holder.priceChange.setText(String.format("▲ %.2f (%.2f%%)", stock.getPriceChange(), stock.getPercentageChange()));
            holder.companyName.setTextColor(Color.GREEN);
            holder.stockSymbol.setTextColor(Color.GREEN);
            holder.stockPrice.setTextColor(Color.GREEN);
            holder.priceChange.setTextColor(Color.GREEN);
        }
        else
        {
            holder.priceChange.setText(String.format("▼ %.2f (%.2f%%)", stock.getPriceChange(), stock.getPercentageChange()));
            holder.companyName.setTextColor(Color.RED);
            holder.stockSymbol.setTextColor(Color.RED);
            holder.stockPrice.setTextColor(Color.RED);
            holder.priceChange.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount()
    {
        return stockList.size();
    }
}
























