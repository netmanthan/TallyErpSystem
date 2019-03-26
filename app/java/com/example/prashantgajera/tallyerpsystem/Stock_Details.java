package com.example.prashantgajera.tallyerpsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.Adapter.AdapterLedgerdetails;
import com.example.prashantgajera.tallyerpsystem.Adapter.SpacingItemDecoration;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.FileSaveInStorage;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.MycommonUtilities;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cz.msebera.android.httpclient.Header;

public class Stock_Details extends AppCompatActivity {

    private EditText searchbar;
    ImageView closeimage;
    private RecyclerView recyclerView;
    List<LedgerCatagory> ledgerCatagory=new ArrayList<>();
    ACProgressFlower acdialog;
    SharedPreferences sharedPreferences;
    private AdapterLedgerdetails adapterLedgerdetails;
    AsyncHttpClient httpClient;
    Map<String,Integer> hashmap = new HashMap<String,Integer>();
    ImageView syncview;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ledger_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        syncview = findViewById(R.id.syncbutton);
        linearLayout = findViewById(R.id.titlebarlayout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        View mCustomView;
        ImageView imageView;
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Stock Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            LayoutInflater mInflater= LayoutInflater.from(this);
            mCustomView = mInflater.inflate(R.layout.syncbuttonview, null);
            imageView = (ImageView) mCustomView.findViewById(R.id.syncbutton);
            toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.RIGHT));


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ledgerCatagory.clear();
                    JsonData(ledgerCatagory);
                }
            });

        }

        initComponent();

        searchbar = findViewById(R.id.searchbar);
        closeimage  = findViewById(R.id.closebutton);
        closeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.setText("");
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterLedgerdetails.filter(s.toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }


    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, MainActivity.dpToPx(this, 15), true));

        sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
        Boolean value =sharedPreferences.getBoolean("login",false);

        if(value)
        {
            JsonData(ledgerCatagory);
        }
        else if(!value)
        {

            try {
                JSONObject jsonObject = new JSONObject();
                byte[] a = null;
                FileSaveInStorage fileSaveInStorage=new FileSaveInStorage("Stock.json",a);
                sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
                String mypath =sharedPreferences.getString("StockPath",null);
                jsonObject = fileSaveInStorage.RetriveFile(mypath);
                StockJson(jsonObject);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    private void JsonData(final List<LedgerCatagory> ledgerCatagory) {

        acdialog = new ACProgressFlower.Builder(Stock_Details.this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE)
                .text("Please Wait..")
                .bgAlpha(1)
                .speed(15)
                .textAlpha(1)
                .textColor(Color.BLACK)
                .bgColor(Color.WHITE)
                .fadeColor(Color.WHITE).build();

        httpClient = new AsyncHttpClient();

        MycommonUtilities mycommonUtilities = new MycommonUtilities();

        httpClient.post(MycommonUtilities.stockjsonurl, new JsonHttpResponseHandler(){

            @Override
            public void onStart() {
                acdialog.show();
            }

            @Override
            public void onFinish() {
                acdialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                String FileName = "Stock.json";
                FileSaveInStorage fileSaveInStorage = null;
                byte[] myResponse=  response.toString().getBytes();
                if(haveStoragePermission()) {
                    fileSaveInStorage = new FileSaveInStorage(FileName, myResponse);
                    fileSaveInStorage.SaveFile();
                    String mypath=fileSaveInStorage.dataFile.toString();
                    sharedPreferences = getApplication().getSharedPreferences("mypref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("StockPath",mypath);
                    editor.apply();
                }
                StockJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    public void StockJson(JSONObject jsonObject)
    {
        final JSONArray TallyMessage;
        try {
            JSONObject body = jsonObject.getJSONObject("BODY");
            Log.d("---body---",body.toString());
            JSONObject ImportData = body.getJSONObject("IMPORTDATA");

            //get company name
            JSONObject RequestDisc = ImportData.getJSONObject("REQUESTDESC");
            JSONObject StaticVariable = RequestDisc.getJSONObject("STATICVARIABLES");
            String CompanyName = StaticVariable.getString("SVCURRENTCOMPANY");

            sharedPreferences = getApplication().getSharedPreferences("mypref",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Company Name",CompanyName);
            editor.apply();

            JSONObject RequestData = ImportData.getJSONObject("REQUESTDATA");
            TallyMessage = RequestData.getJSONArray("TALLYMESSAGE");

            //JSONObject object = TallyMessage.getJSONObject(2);
            for (int i=1;i<TallyMessage.length();i++) {

                JSONObject object = TallyMessage.getJSONObject(i);
                JSONObject StockItems;
                try {
                   StockItems = object.getJSONObject("STOCKITEM");
                }
                catch (Exception e)
                {
                    continue;
                }
                String parent = StockItems.getString("PARENT").trim();
                JSONObject attributes = StockItems.getJSONObject("@attributes");
                String name = attributes.getString("NAME").trim();
                CompanyName=name;
                String OpeningBalance = StockItems.getString("OPENINGBALANCE").trim();

                if(StockItems.length()!=0)
                {
                    hashmap.put(name,i);
                    ledgerCatagory.add(new LedgerCatagory(name,OpeningBalance,"",parent,R.drawable.ic_contacts));

                }

            }
            adapterLedgerdetails = new AdapterLedgerdetails(Stock_Details.this, ledgerCatagory);
            recyclerView.setAdapter(adapterLedgerdetails);
            Toast.makeText(Stock_Details.this, "", Toast.LENGTH_SHORT).show();

            adapterLedgerdetails.setOnItemClickListener(new AdapterLedgerdetails.OnItemClickListener() {

                @Override
                public void onItemClick(View view, LedgerCatagory obj, int position) {

                    int i = hashmap.get(obj.getName());
                    try {
                        JSONObject StockItems;
                        JSONObject object = TallyMessage.getJSONObject(i);
                        StockItems = object.getJSONObject("STOCKITEM");
                        JSONObject attributes = StockItems.getJSONObject("@attributes");
                        String name = attributes.getString("NAME").trim();
                        String Stock =StockItems.toString();
                        Intent intent = new Intent(Stock_Details.this,Stock_Profile.class);
                        intent.putExtra("StockItems",Stock);
                        intent.putExtra("StockName",name);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  boolean haveStoragePermission() {                     //function to check permission of storage
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }
}
