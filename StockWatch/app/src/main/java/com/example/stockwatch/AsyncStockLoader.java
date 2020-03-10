package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.makeText;

class AsyncStockLoader extends AsyncTask<String, Integer, String>
{
    private MainActivity mainActivity;
    private int count;

    private final String dataURLStem = "http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=";
    private static final String TAG = "AsyncStockLoader";

    public AsyncStockLoader(MainActivity ma)
    {
        mainActivity = ma;
    }

    @Override
    protected void onPreExecute()
    {
        Toast.makeText(mainActivity, "Loading Stock Data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String s)
    {
        ArrayList<Stock> stockList = parseJSON(s);
        if(stockList.size() == 0)
        {
            mainActivity.notFoundDialog();
        }
        else
        {
            mainActivity.stockSelect(stockList);
        }
    }

    @Override
    protected String doInBackground(String... params)
    {
        String dataURL = dataURLStem + params[0];
        Log.d(TAG, "doInBackground: URL is " + dataURL);
        Uri dataUri = Uri.parse(dataURL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        }
        catch (Exception e)
        {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        Log.d(TAG, "doInBackground: " + sb.toString());
        Log.d(TAG, "doInBackground: returning");
        return sb.toString();

    }

    private ArrayList<Stock> parseJSON(String s)
    {
        Log.d(TAG, "parseJSON: started JSON");

        ArrayList<Stock> stockList = new ArrayList<>();
        try
        {
            JSONObject jObjMain3 = new JSONObject(s);
            JSONObject jObjMain2 = jObjMain3.getJSONObject("ResultSet");
            JSONArray jObjMain = jObjMain2.getJSONArray("Result");
            count = jObjMain.length();

            for (int i = 0; i < jObjMain.length(); i++)
            {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String type = jStock.getString("type");
                if( type.equals("S")  )
                {
                    // so only get Stocks
                    String symbol = jStock.getString("symbol");
                    String name = jStock.getString("name");

                    // 3/21 changes
                    int idx = symbol.indexOf('.'); // to check for the character
                    if(idx >= 0){ // '.' is in symbol, ignore stock
                        Log.d(TAG, "parseJSON: ignored " + name + ", " + symbol);
                    }
                    else{ // '.' not in symbol, accept stock
                        Log.d(TAG, "parseJSON: loaded " + name + ", " + symbol);
                        stockList.add(new Stock(name, symbol));
                    }

                }
            }
            return stockList;
        }
        catch (Exception e)
        {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
