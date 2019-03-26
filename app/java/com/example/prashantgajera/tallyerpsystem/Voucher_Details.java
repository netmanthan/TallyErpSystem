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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.Adapter.AdapterVoucher;
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
import java.util.Set;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cz.msebera.android.httpclient.Header;

public class Voucher_Details extends AppCompatActivity {
    private EditText searchbar;
    ImageView closeimage;
    private RecyclerView recyclerView;
    List<VoucherCatagory> VoucherTypes=new ArrayList<>();
    ACProgressFlower acdialog;
    SharedPreferences sharedPreferences;
    private AdapterVoucher adapterVoucher;
    AsyncHttpClient httpClient;
    Map<String,Integer> hashmap = new HashMap<String,Integer>();
    ImageView syncview;
    LinearLayout linearLayout;
    String Ledgername;
    Map<Integer,String>  myhash = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voucher_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        syncview = findViewById(R.id.syncbutton);
        linearLayout = findViewById(R.id.titlebarlayout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        View mCustomView;
        ImageView imageView;
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Voucher");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
            LayoutInflater mInflater = LayoutInflater.from(this);
            mCustomView = mInflater.inflate(R.layout.syncbuttonview, null);
            imageView = (ImageView) mCustomView.findViewById(R.id.syncbutton);
            toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.RIGHT));


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VoucherTypes.clear();
                    JsonData(VoucherTypes);
                }
            });
        }

        initComponent();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        startActivity(new Intent(Voucher_Details.this,MainActivity.class));
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
            JsonData(VoucherTypes);
        }
        else if(!value)
        {

            try {
                JSONObject jsonObject = new JSONObject();
                byte[] a = null;
                FileSaveInStorage fileSaveInStorage=new FileSaveInStorage("Voucher.json",a);
                sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
                String mypath =sharedPreferences.getString("VoucherPath",null);
                jsonObject = fileSaveInStorage.RetriveFile(mypath);
                VoucherJson(jsonObject);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

    private void JsonData(final List<VoucherCatagory> voucherCatagories) {

        acdialog = new ACProgressFlower.Builder(Voucher_Details.this)
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

        httpClient.post(MycommonUtilities.voucherjsonurl, new JsonHttpResponseHandler(){

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
                String FileName = "Voucher.json";
                FileSaveInStorage fileSaveInStorage = null;
                byte[] myResponse=  response.toString().getBytes();
                if(haveStoragePermission()) {

                    fileSaveInStorage = new FileSaveInStorage(FileName, myResponse);
                    fileSaveInStorage.SaveFile();
                    String mypath=fileSaveInStorage.dataFile.toString();
                    sharedPreferences = getApplication().getSharedPreferences("mypref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("VoucherPath",mypath);
                    editor.apply();

                }

                VoucherJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }

    public void VoucherJson(JSONObject jsonObject)
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


            for (int i=1;i<TallyMessage.length();i++) {

                JSONObject object = TallyMessage.getJSONObject(i);
                JSONObject MyVoucher;
                try {
                    MyVoucher = object.getJSONObject("VOUCHER");
                }
                catch (Exception e)
                {
                    continue;
                }
                JSONObject attributes = MyVoucher.getJSONObject("@attributes");
                String Vouchername = attributes.getString("VCHTYPE").trim();

                if(MyVoucher.length()!=0)
                {
                    hashmap.put(Vouchername,i);
                    myhash.put(i,Vouchername);
                }

            }

            Set<String> Voucherkey= hashmap.keySet();
            for(String keys: Voucherkey)
            {
                VoucherTypes.add(new VoucherCatagory(keys));
            }

            adapterVoucher = new AdapterVoucher(Voucher_Details.this, VoucherTypes);
            recyclerView.setAdapter(adapterVoucher);
            Toast.makeText(Voucher_Details.this, "", Toast.LENGTH_SHORT).show();

            final Set<Integer> myset = myhash.keySet() ;

            acdialog = new ACProgressFlower.Builder(Voucher_Details.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.BLUE)
                    .text("Please Wait..")
                    .bgAlpha(1)
                    .speed(15)
                    .textAlpha(1)
                    .textColor(Color.BLACK)
                    .bgColor(Color.WHITE)
                    .fadeColor(Color.WHITE).build();

            adapterVoucher.setOnItemClickListener(new AdapterVoucher.OnItemClickListener() {

                @Override
                public void onItemClick(View view, VoucherCatagory obj, int position) {
                    acdialog.show();

                    try {
                        ArrayList<JSONObject> myjsonobject =new ArrayList<>();

                        for(int keys : myset)
                        {
                            if(myhash.get(keys).equals(obj.getVouchername()))
                            {
                                JSONObject voucherobject = TallyMessage.getJSONObject(keys);
                                myjsonobject.add(voucherobject);
                            }
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                acdialog.dismiss();
                            }

                        }, 2000);

                        Intent intent = new Intent(Voucher_Details.this,Voucher_Profile.class);
                        intent.putExtra("VoucherName",obj.getVouchername());
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(Voucher_Details.this, "Clicked", Toast.LENGTH_SHORT).show();

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
