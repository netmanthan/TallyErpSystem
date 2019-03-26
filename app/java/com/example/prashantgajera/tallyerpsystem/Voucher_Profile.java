package com.example.prashantgajera.tallyerpsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.Adapter.AdapterVoucherCatagory;
import com.example.prashantgajera.tallyerpsystem.Adapter.SpacingItemDecoration;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.FileSaveInStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Voucher_Profile extends AppCompatActivity {

    Button btn1, btn2,clear;
    private EditText searchbar;
    ImageView closeimage;
    SharedPreferences sharedPreferences;
    String startdate,enddate;
    List<VoucherProfileCatagory> myitems = new ArrayList<>();
    RecyclerView recyclerView;
    private AdapterVoucherCatagory adapterVoucherCatagory;
    Map<String,Integer> myhash = new HashMap<>();
    String Vouchertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voucher_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        View mCustomView;
        ImageView imageView;
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Voucher List");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }

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
                adapterVoucherCatagory.filter(s.toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InitComponent();

        btn1= findViewById(R.id.pickstartdate);
        btn2=findViewById(R.id.pickenddate);
        clear =findViewById(R.id.clear);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker(btn1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker(btn2);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterVoucherCatagory.clearSearch();
                btn1.setText("Start Date");
                btn2.setText("End Date");
            }
        });

    }

    private void InitComponent() {

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, MainActivity.dpToPx(this, 15), true));

        try {

            JSONObject jsonObject = new JSONObject();
            byte[] a = null;

            FileSaveInStorage fileSaveInStorage=new FileSaveInStorage("Voucher.json",a);
            sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
            String mypath =sharedPreferences.getString("VoucherPath",null);
            jsonObject = fileSaveInStorage.RetriveFile(mypath);

            FetchVoucherJson(jsonObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void FetchVoucherJson(JSONObject jsonObject)
    {
        final JSONArray TallyMessage;
        try {

            Vouchertype = getIntent().getExtras().getString("VoucherName");

            JSONObject body = jsonObject.getJSONObject("BODY");
            Log.d("---body---", body.toString());
            JSONObject ImportData = body.getJSONObject("IMPORTDATA");

            JSONObject RequestData = ImportData.getJSONObject("REQUESTDATA");
            TallyMessage = RequestData.getJSONArray("TALLYMESSAGE");

            for(int i=0;i<TallyMessage.length();i++)
            {
                JSONObject  VoucherObject =  TallyMessage.getJSONObject(i);
                JSONObject MyVoucher,attributes;
                try {
                    MyVoucher = VoucherObject.getJSONObject("VOUCHER");
                    attributes = MyVoucher.getJSONObject("@attributes");
                }catch (Exception e)
                {
                    continue;
                }

                if(attributes.getString("VCHTYPE").trim().equals(Vouchertype))
                {
                    String Partyledgername=" ";

                    try{ Partyledgername = MyVoucher.getString("PARTYLEDGERNAME");}catch (Exception e){}

                    if(Partyledgername.equals("{}"))       // use of more try/catch and forms because of every inventoryentriesin.list have different format array/object
                    {
                        try {
                                    try{Partyledgername =  MyVoucher.getJSONObject("INVENTORYENTRIESIN.LIST").getString("STOCKITEMNAME");}catch (Exception e){}

                            if(Partyledgername.equals("{}"))
                            {
                                Partyledgername= MyVoucher.getJSONArray("INVENTORYENTRIESIN.LIST").getJSONObject(0).getString("STOCKITEMNAME");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    String VoucherNO = MyVoucher.getString("VOUCHERNUMBER");
                    String date = MyVoucher.getString("DATE");
                    String newdate = date.substring(6) + "/" + date.substring(4, 6) + "/" + date.substring(0, 4);

                    if (MyVoucher.length() != 0) {
                        myitems.add(new VoucherProfileCatagory(Partyledgername, newdate, VoucherNO));
                        myhash.put(VoucherNO,i);
                    }

                }
            }
            adapterVoucherCatagory = new AdapterVoucherCatagory(Voucher_Profile.this,myitems);
            recyclerView.setAdapter(adapterVoucherCatagory);
            adapterVoucherCatagory.setOnItemClickListener(new AdapterVoucherCatagory.OnItemClickListener() {

                @Override
                public void onItemClick(View view, VoucherProfileCatagory obj, int position) {
                    int i = myhash.get(obj.getVoucherNo());
                    String VoucherNo = obj.getVoucherNo();
                    String date = obj.getDate();
                    try{
                        JSONObject MyVoucher;
                        JSONObject object = TallyMessage.getJSONObject(i);
                        MyVoucher = object.getJSONObject("VOUCHER");
                        String voucher = MyVoucher.toString();

                        Log.d("voucherno",VoucherNo+"// "+date);

                        Intent intent = new Intent(Voucher_Profile.this,Voucher_Accounting.class);
                        intent.putExtra("VoucherAccount",voucher);
                        intent.putExtra("VoucherNo",VoucherNo);
                        intent.putExtra("date",date);
                        intent.putExtra("vouchertype",Vouchertype);
                        startActivity(intent);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }

    public void datepicker(final Button bt)
    {
        Calendar c;
        int mYear,mMonth,mDay;

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int flag=1;

                        if(bt==btn1)
                        {
                                bt.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                                startdate = dayOfMonth + "" + (monthOfYear + 1) + "" + year;
                        }
                        if(bt==btn2)
                        {
                            String sdate= btn1.getText().toString();
                            Log.d("sdate",""+sdate);
                            if(sdate.equals("Start date"))
                            {
                                Toast.makeText(Voucher_Profile.this, "Enter Start date", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String ed = dayOfMonth + " / " + (monthOfYear + 1) + " / " + year;
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                sdf.setLenient(false);
                                Date d1=new Date(),d2=new Date();
                                try{
                                    d1=sdf.parse(sdate.replaceAll("\\s+",""));
                                    d2=sdf.parse(ed.replaceAll("\\s+",""));
                                }catch (Exception e){}


                                if(d1.compareTo(d2)>0)
                                {
                                    Toast.makeText(Voucher_Profile.this, "End Date should be bigger", Toast.LENGTH_SHORT).show();
                                    flag=0;
                                }
                                if(flag==0)
                                {
                                    bt.setText("End Date");
                                }
                                else if(flag==1)
                                {
                                    String enddate=dayOfMonth + " / " + (monthOfYear + 1) + " / " + year;
                                    String startdate = btn1.getText().toString();
                                    bt.setText(enddate);
                                    adapterVoucherCatagory.filterDate(startdate,enddate);
                                }
                            }
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();

    }

}
