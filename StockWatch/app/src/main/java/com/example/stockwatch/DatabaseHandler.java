package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper
{

    private static final String TAG = "DatabaseHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT not null)";
    private SQLiteDatabase database;

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: Created");
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d(TAG, "onCreate: New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    { }

        public ArrayList<Stock> loadStocks()
        {

            ArrayList<Stock> stocks = new ArrayList<>();

            Cursor cursor = database.query(
            TABLE_NAME,
            new String[]{SYMBOL, COMPANY},
            null,
            null,
            null,
            null,
            null);

            if (cursor != null)
            {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++)
                {
                    String symbol = cursor.getString(0);
                    String company = cursor.getString(1);
                    Stock s = new Stock( company, symbol);
                    stocks.add(s);
                    cursor.moveToNext();
                }
             }
            Log.d(TAG, "loadStocks: Finished");
            return stocks;
        }

        public void addStock(Stock stock)
        {
            Log.d(TAG, "addStock: Adding" + stock.getStockSymbol());
            ContentValues values = new ContentValues();

            values.put(SYMBOL, stock.getStockSymbol());
            values.put(COMPANY, stock.getCompanyName());
            long key = database.insert(TABLE_NAME, null, values);
            Log.d(TAG, "addStock: Add Complete" );
        }

        public void addAll(ArrayList<Stock> sList)
        {
            for(int i = 0; i < sList.size(); i++)
            {
                Stock stock = sList.get(i);
                ContentValues values = new ContentValues();

                values.put(SYMBOL, stock.getStockSymbol());
                values.put(COMPANY, stock.getCompanyName());
                long key = database.insert(TABLE_NAME, null, values);
            }

        }

        public void deleteStock(String symbol)
        {
            Log.d(TAG, "deleteStock: " + symbol);
            int cnt = database.delete(TABLE_NAME,SYMBOL + " = ?", new String[]{symbol});
            Log.d(TAG, "deleteStock: " + cnt);
        }

        public void dumpDbToLog()
        {
            Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
            if (cursor != null) {
                cursor.moveToFirst();

                Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
                for (int i = 0; i < cursor.getCount(); i++) {
                    String symbol = cursor.getString(0);
                    String company = cursor.getString(1);

                    Log.d(TAG, "dumpDbToLog: " +
                            String.format("%s %-18s", SYMBOL + ":", symbol) +
                            String.format("%s %-18s", COMPANY + ":", company));
                    cursor.moveToNext();
                }
                cursor.close();
            }

            Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }

        public void shutDown()
        {
            database.close();
        }
}
