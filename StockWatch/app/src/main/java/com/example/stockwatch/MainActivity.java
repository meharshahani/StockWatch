package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.text.InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private String m_text;

    private SwipeRefreshLayout swiper;
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private List<Stock> stockList = new ArrayList<>();
    private StockAdapter stockAdapter;
    private DatabaseHandler databaseHandler;
    private MainActivity mainActivity = this;
    private Stock stock;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        stockAdapter = new StockAdapter(stockList, this);
        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHandler = new DatabaseHandler(this);
        swiper = findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                if(connected())
                {
                    ArrayList<Stock> list = databaseHandler.loadStocks();
                    stockList.clear();
                    for(int i = 0; i < list.size(); i++)
                    {
                        String symbol = list.get(i).getStockSymbol();
                        String name = list.get(i).getCompanyName();
                        new AsyncFinancialDataLoader(mainActivity).execute(symbol);
                    }
                    Collections.sort(stockList);
                    stockAdapter.notifyDataSetChanged();
                    swiper.setRefreshing(false);
                }
                else
                {
                    noNetDialogRefresh();
                    swiper.setRefreshing(false);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        if(connected())
        {
            databaseHandler.dumpDbToLog();
            ArrayList<Stock> list = databaseHandler.loadStocks();

            stockList.clear();

            for (int i = 0; i < list.size(); i++)
            {
                String symbol = list.get(i).getStockSymbol();
                String name = list.get(i).getCompanyName();
                new AsyncFinancialDataLoader(mainActivity).execute(symbol);
            }

            // after for loop
            Collections.sort(stockList);
            stockAdapter.notifyDataSetChanged();
        }
        else
        {
            noNetDialogRefresh();
        }
        super.onResume();

    }

    protected void addNewStock(Stock stock)
    {
        // called by async fin data load

        Log.d(TAG, "addNewStock: " + stock.getStockSymbol());
        // get name from dummy list in onResume maybe?
        //stock.setName();
        ArrayList<Stock> list = databaseHandler.loadStocks();

        for(int i = 0; i < list.size(); i ++){
            if(list.get(i).getStockSymbol().equals( stock.getStockSymbol())){
                stock.setCompanyName( list.get(i).getCompanyName());
            }
        }

        stockList.add(stock);
        Collections.sort(stockList);
        stockAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy()
    {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case(R.id.add_item):
                if(connected())
                {
                    addStockDialog();
                }
                else
                {
                    noNetDialogAdd();
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v)
    {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        String symbol = s.getStockSymbol();
        String url = "http://www.marketwatch.com/investing/stock/" + symbol;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v)
    {
        int pos = recyclerView.getChildLayoutPosition(v);
        Stock s = stockList.get(pos);

        //Dialog to check if want to delete
        deleteDialog(pos, s);

        return false;
    }

    private boolean connected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void noNetDialogAdd()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void noNetDialogRefresh()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addStockDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Log.d("addStockDialog", "addStockDialog called:");
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_CAP_CHARACTERS );

        et.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(et);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("addStockDialog", "positive clicked");
                String s = et.getText().toString();

                boolean in = false;
                for(int j = 0; j < stockList.size() ;j++){
                    String symb = stockList.get(j).getStockSymbol();
                    if(symb.equals(s)){in = true;}
                }
                if(in)
                {
                    // Duplicate
                    mainActivity.duplicateDialog(s);
                }
                else
                    {
                    // Not duplicate
                    String[] sArr = {s};
                        new AsyncStockLoader(mainActivity).execute(sArr);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
                Log.d("addStockDialog", "negative clicked");
                //return;
            }
        });
        builder.setMessage("Please enter a Stock Symbol");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void duplicateDialog(String symb)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stock Symbol " + symb + " is already displayed");
        builder.setTitle("Duplicate Stock");
        // NEED: exclamation icon
        //builder.setIcon();

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void notFoundDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Data for stock symbol");
        builder.setTitle("Stock Symbol Not Found");


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void stockSelect(ArrayList<Stock> s){
        // list select dialog, user chooses stock and it's sent to updateData

        final ArrayList<Stock> sList = s;

        final CharSequence[] sArray = new CharSequence[sList.size()];
        for(int i = 0; i < sList.size(); i++){
            sArray[i] = sList.get(i).getStockSymbol() + " - " + sList.get(i).getCompanyName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a selection");

        builder.setItems(sArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                ArrayList<Stock> selected = new ArrayList<>();
                selected.add(sList.get(which));
                //mainActivity.getFinancialData(selected);
                updateData(selected);
            }
        });
        builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing, return
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void deleteDialog(int pos, Stock s)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int pos2 = pos;
        //builder.setIcon(R.drawable.deleteIcon) or something

        builder.setIcon(R.drawable.delete_icon);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // delete from DB, delete from list, notify Adapter
                databaseHandler.deleteStock(stockList.get(pos2).getStockSymbol());
                stockList.remove(pos2);
                stockAdapter.notifyDataSetChanged();
                //return;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
                //return;
            }
        });
        builder.setMessage("Delete Stock Symbol " + s.getStockSymbol() + "?"); // NEED ACTUAL SYMBOL HERE
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateData(ArrayList<Stock> sList)
    {
        if(sList != null)
        {
            databaseHandler.addAll(sList);

            ArrayList<Stock> list = databaseHandler.loadStocks();
            stockList.clear();

            if(list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {

                    String symbol = list.get(i).getStockSymbol();
                    new AsyncFinancialDataLoader(mainActivity).execute(symbol);
                }
            }
            else{
                String symbol = list.get(0).getStockSymbol();


                new AsyncFinancialDataLoader(mainActivity).execute(symbol);
            }
            Collections.sort(stockList);
            stockAdapter.notifyDataSetChanged();

        }
    }
}
