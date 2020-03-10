package com.example.stockwatch;

import androidx.annotation.NonNull;

class Stock implements Comparable<Stock>
{
    private String stockSymbol;
    private String companyName;
    private double stockPrice;
    private double priceChange;
    private double percentageChange;

    public Stock(String companyName, String stockSymbol, double stockPrice, double priceChange, double percentageChange)
    {
        setCompanyName(companyName);
        setStockSymbol(stockSymbol);
        setPercentageChange(percentageChange);
        setPriceChange(priceChange);
        setStockPrice(stockPrice);

    }

    public Stock(String companyName, String stockSymbol)
    {
        setCompanyName(companyName);
        setStockSymbol(stockSymbol);
    }

    public String getStockSymbol()
    {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol)
    {
        this.stockSymbol = stockSymbol;
    }

    public String getCompanyName()
    {
        return companyName;
    }

    public void setCompanyName(String companyName)
    {
        this.companyName = companyName;
    }

    public double getStockPrice()
    {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice)
    {
        this.stockPrice = stockPrice;
    }

    public double getPriceChange()
    {
        return priceChange;
    }

    public void setPriceChange(Double priceChange)
    {
        this.priceChange = priceChange;
    }

    public void setPercentageChange(double percentageChange)
    {
        this.percentageChange = percentageChange;
    }

    public double getPercentageChange()
    {
        return percentageChange;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "Stock{"+
                "name='" + companyName + '\'' +
                ", symbol='" + stockSymbol + '\'' +
                ", price='" + stockPrice + '\'' +
                ", price change='" + priceChange + '\'' +
                ", percentage change='" + percentageChange + '\'' +
                '}';
    }


    @Override
    public int compareTo(Stock o)
    {
        return this.getStockSymbol().compareTo(o.getStockSymbol());
    }
}
