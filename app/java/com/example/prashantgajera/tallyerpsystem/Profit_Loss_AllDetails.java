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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.Adapter.AdapterBalancesheet;
import com.example.prashantgajera.tallyerpsystem.Adapter.SpacingItemDecoration;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.FileSaveInStorage;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class Profit_Loss_AllDetails extends AppCompatActivity {

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
        setContentView(R.layout.profit_loss_alldetails);
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

    }

    initComponent();

}

    @Override
    public boolean onSupportNavigateUp()
    {
        startActivity(new Intent(Profit_Loss_AllDetails.this,Profit_Loss_Details.class));
        return true;
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, MainActivity.dpToPx(this, 13), true));

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


    public void BalancesheetJson(JSONObject jsonObject)
    {
        try {
            String mybalancevalue="";
            String mybalancename="";
            JSONArray balancesheetname = jsonObject.getJSONArray("BSNAME");
            JSONArray balancesheetvalue = jsonObject.getJSONArray("BSAMT");

            for(int i=0;i<balancesheetname.length();i++)
            {
                JSONObject balancename = balancesheetname.getJSONObject(i);
                JSONObject balancevalue = balancesheetvalue.getJSONObject(i);

                JSONObject myjsonname = balancename.getJSONObject("DSPACCNAME");
                mybalancename = myjsonname.getString("DSPDISPNAME");

                if(!balancevalue.getString("BSSUBAMT").equals("{}"))
                {
                    mybalancevalue = balancevalue.optString("BSSUBAMT");

                }
                else if(!balancevalue.getString("BSMAINAMT").equals("{}"))
                {
                    mybalancevalue = balancevalue.optString("BSMAINAMT");
                }
                Log.i("mybalance",mybalancename+"||"+mybalancevalue);

                balancesheetlist.add(new BalancesheetCatagory(mybalancename,mybalancevalue));

                double balance = Double.parseDouble(mybalancevalue);
            }
            Toast.makeText(Profit_Loss_AllDetails.this, "", Toast.LENGTH_LONG).show();

            adapterBalancesheet = new AdapterBalancesheet(Profit_Loss_AllDetails.this, balancesheetlist);
            recyclerView.setAdapter(adapterBalancesheet);

            acdialog = new ACProgressFlower.Builder(Profit_Loss_AllDetails.this)
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
