package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MyViewHolder extends RecyclerView.ViewHolder
{
    public TextView companyName;
    public TextView stockSymbol;
    public TextView stockPrice;
    public TextView priceChange;


    public MyViewHolder(@NonNull View itemView)
    {
        super(itemView);

        companyName = itemView.findViewById(R.id.companyName);
        stockSymbol = itemView.findViewById(R.id.stockSymbol);
        stockPrice = itemView.findViewById(R.id.lastTradePrice);
        priceChange = itemView.findViewById(R.id.priceChangeAmount);

    }
}
