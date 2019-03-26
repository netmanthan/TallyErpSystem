package com.example.prashantgajera.tallyerpsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.Adapter.AdapterBalancesheet;
import com.example.prashantgajera.tallyerpsystem.Adapter.SpacingItemDecoration;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.FileSaveInStorage;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.MycommonUtilities;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cz.msebera.android.httpclient.Header;

public class Profit_Loss_Details extends AppCompatActivity {

    private RecyclerView recyclerView;
    Button mybutton;
    List<BalancesheetCatagory> balancesheetlist=new ArrayList<>();
    ACProgressFlower acdialog;
    SharedPreferences sharedPreferences;
    private AdapterBalancesheet adapterBalancesheet;
    AsyncHttpClient httpClient;
    Map<String,Integer> hashmap = new HashMap<String,Integer>();
    ImageView syncview;
    LinearLayout linearLayout;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profitloss_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        syncview = findViewById(R.id.syncbutton);
        linearLayout = findViewById(R.id.titlebarlayout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        View mCustomView;
        ImageView imageView;
            if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Profit/Loss");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            LayoutInflater mInflater = LayoutInflater.from(this);
            mCustomView = mInflater.inflate(R.layout.syncbuttonview, null);
            imageView = mCustomView.findViewById(R.id.syncbutton);
            toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.RIGHT));

            mybutton = findViewById(R.id.alldetails);
            mybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 2000);
                    Intent intent = new Intent(Profit_Loss_Details.this,Profit_Loss_AllDetails.class);
                    startActivity(intent);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    balancesheetlist.clear();
                    JsonData(balancesheetlist);
                }
            });


        }

        initComponent();

}

    @Override
    public boolean onSupportNavigateUp()
    {
        startActivity(new Intent(Profit_Loss_Details.this,MainActivity.class));
        return true;
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, MainActivity.dpToPx(this, 13), true));

        sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
        Boolean value =sharedPreferences.getBoolean("login",false);

        if(value)
        {
            JsonData(balancesheetlist);
        }
        else if(!value)
        {

            try {
                JSONObject jsonObject = new JSONObject();
                byte[] a = null;
                FileSaveInStorage fileSaveInStorage=new FileSaveInStorage("ProfitLoss.json",a);
                sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
                String mypath =sharedPreferences.getString("ProfitlLosspath",null);
                jsonObject = fileSaveInStorage.RetriveFile(mypath);
                BalancesheetJson(jsonObject);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    private void JsonData(final List<BalancesheetCatagory> balancesheetCatagories) {

        acdialog = new ACProgressFlower.Builder(Profit_Loss_Details.this)
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

        httpClient.post(MycommonUtilities.profitlossjsonurl, new JsonHttpResponseHandler(){

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
                String FileName = "ProfitLoss.json";
                FileSaveInStorage fileSaveInStorage = null;
                byte[] myResponse=  response.toString().getBytes();
                if(haveStoragePermission()) {
                    fileSaveInStorage = new FileSaveInStorage(FileName, myResponse);
                    fileSaveInStorage.SaveFile();
                    String mypath=fileSaveInStorage.dataFile.toString();
                    sharedPreferences = getApplication().getSharedPreferences("mypref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ProfitlLosspath",mypath);
                    editor.apply();
                }
                BalancesheetJson(response);

            }



            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    public void BalancesheetJson(JSONObject jsonObject)
    {
        try {
            String mybalancevalue="";
            String mybalancename="";
            JSONArray balancesheetname = jsonObject.getJSONArray("DSPACCNAME");
            JSONArray balancesheetvalue = jsonObject.getJSONArray("PLAMT");

            for(int i=0;i<balancesheetname.length();i++)
            {
                JSONObject balancename = balancesheetname.getJSONObject(i);
                JSONObject balancevalue = balancesheetvalue.getJSONObject(i);

                mybalancename = balancename.getString("DSPDISPNAME");

                if(!balancevalue.getString("PLSUBAMT").equals("{}"))
                {
                    mybalancevalue = balancevalue.optString("PLSUBAMT");

                }
                else if(!balancevalue.getString("BSMAINAMT").equals("{}"))
                {
                    mybalancevalue = balancevalue.optString("BSMAINAMT");
                }
                Log.i("mybalance",mybalancename+"||"+mybalancevalue);

                balancesheetlist.add(new BalancesheetCatagory(mybalancename,mybalancevalue));

                double balance = Double.parseDouble(mybalancevalue);
            }
            Toast.makeText(Profit_Loss_Details.this, "", Toast.LENGTH_LONG).show();

            adapterBalancesheet = new AdapterBalancesheet(Profit_Loss_Details.this, balancesheetlist);
            recyclerView.setAdapter(adapterBalancesheet);

            acdialog = new ACProgressFlower.Builder(Profit_Loss_Details.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.BLUE)
                    .text("Please Wait..")
                    .bgAlpha(1)
                    .speed(15)
                    .textAlpha(1)
                    .textColor(Color.BLACK)
                    .bgColor(Color.WHITE)
                    .fadeColor(Color.WHITE).build();


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
