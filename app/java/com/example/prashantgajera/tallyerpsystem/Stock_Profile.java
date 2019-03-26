package com.example.prashantgajera.tallyerpsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Stock_Profile extends AppCompatActivity {
    TextView textView,title;
    Map<String,String> mymap = new HashMap<>();
    StringBuilder StockProfile= new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }

        StringBuilder ledgerprofile=new StringBuilder();
        Intent intent = getIntent();
        String legderdata = intent.getExtras().getString("StockItems");
        String legdername = intent.getExtras().getString("StockName");
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(legderdata);
            FetchLedgerDetails(jsonObject);
            Set keys = mymap.keySet();
            StockProfile.append("\n\n");

            for(Iterator i = keys.iterator(); i.hasNext();)
            {
                String key = (String)i.next();
                String value = (String)mymap.get(key);
                StockProfile.append(key+" : "+value+"\n\n");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.stocktext);
        title=findViewById(R.id.titlestock);
        title.setText(legdername);
        textView.setText(StockProfile);

    }


    public void FetchLedgerDetails(JSONObject jsonObject)
    {
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            Object value=" ";
            try {
                value = jsonObject.get(key);

                if(value instanceof JSONObject) {
                    FetchLedgerDetails((JSONObject)value);
                }
                else if (value instanceof JSONArray) {
                    int i=0;

                    while(((JSONArray) value).length()!=0) {
                        FetchLedgerDetails(((JSONArray) value).getJSONObject(i));
                        i++;
                    }
                }
                else
                {
                    if(!value.toString().equals("")) {
                        mymap.put(key,value.toString());
                    }
                }

            } catch (JSONException e) {
                // Something went wrong!
            }

        }

    }


    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }


}
