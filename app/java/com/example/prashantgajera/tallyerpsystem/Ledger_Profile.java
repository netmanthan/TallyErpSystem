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

public class Ledger_Profile extends AppCompatActivity {
    TextView textView,title;
    Map<String,String> mymap = new HashMap<>();
    StringBuilder LedgerProfile= new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ledger_profile);
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
        String legderdata = intent.getExtras().getString("LedgerProfile");
        String legdername = intent.getExtras().getString("LedgerName");
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(legderdata);
            FetchLedgerDetails(jsonObject);
            Set keys = mymap.keySet();
            LedgerProfile.append("\n\n");
            for(Iterator i =keys.iterator();i.hasNext();)
            {
                String key = (String)i.next();
                String value = (String)mymap.get(key);
                LedgerProfile.append(key+" : "+value+"\n\n");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        textView = findViewById(R.id.ledgertext);
        title=findViewById(R.id.titleledger);
        title.setText(legdername);
        textView.setText(LedgerProfile);

    }


    public void FetchLedgerDetails(JSONObject jsonObject)
    {
        Iterator<String> iter = jsonObject.keys();

        while (iter.hasNext()) {

            String key = (String)iter.next();
            Object value=" ";
            try {

                value = jsonObject.get(key);

                if(value instanceof JSONObject)
                {
                    FetchLedgerDetails((JSONObject)value);
                }

                else if (value instanceof JSONArray) {

                    int i=0;
                    while(((JSONArray) value).length()!=0)
                    {
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

    /*public String FetchLedgerDetails(JSONObject jsonObject)
    {
        String ledgerString=" ";
        try {

            JSONObject attributes= jsonObject.getJSONObject("@attributes");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  ledgerString;
    }*/
    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
