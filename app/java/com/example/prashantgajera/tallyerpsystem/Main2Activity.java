package com.example.prashantgajera.tallyerpsystem;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.CommenUtilities.MycommonUtilities;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class Main2Activity extends AppCompatActivity {

    TextView tv;
    Button btn,btn2,btn3;
    String fileName = "test.xml";
    DownloadManager downloadManager;
    BufferedInputStream bi=null;
    AsyncHttpClient myhttpclient;
    RequestParams requestParams;
    ProgressDialog dialog;
    String parent = " ";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tv = findViewById(R.id.mytext);
        btn = findViewById(R.id.mybutton);
        btn2=findViewById(R.id.mybutton2);
        btn3=findViewById(R.id.mybutton3);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myDownload();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepicker();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                parseJson();
                //MycommonUtilities mycommonUtilities= new MycommonUtilities();
                //mycommonUtilities.ParseJson();
            }
        });

    }

    public void myDownload()  {             //code for download file from the server
        Uri Download_Uri = Uri.parse("http://prashanthost.tk/test.xml");
        Log.d("DownloadManager", "download url:" + Download_Uri);

        //crete download manager
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        //set requests
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Downloading");
        request.setDescription("Downloading File");
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();

        //check if app have permission to store file in mobile
        if(haveStoragePermission())
        {
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "myxml.xml");
            Long reference = downloadManager.enqueue(request);
        }

    }

    public void datepicker()
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

                        tv.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public  boolean haveStoragePermission() {                       //function to check permission
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

    public boolean getpermission()
    {
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            return true;
        }
        else
            return false;
    }

    public void parseJson()
    {
        myhttpclient = new AsyncHttpClient();
        dialog = new ProgressDialog(Main2Activity.this);
        dialog.setMessage("wait");
        dialog.setCancelable(false);
        myhttpclient.post(MycommonUtilities.ledgerjsonurl, new JsonHttpResponseHandler(){

            @Override
            public void onStart() {
                dialog.show();
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                //tv.setText(response.toString());
                try {
                    JSONObject body = response.getJSONObject("BODY");
                    Log.d("---body---",body.toString());
                    JSONObject ImportData = body.getJSONObject("IMPORTDATA");

                    //get company name
                    JSONObject RequestDisc = ImportData.getJSONObject("REQUESTDESC");
                    JSONObject StaticVariable = RequestDisc.getJSONObject("STATICVARIABLES");
                    String CompanyName = StaticVariable.getString("SVCURRENTCOMPANY");

                    sharedPreferences = getApplication().getSharedPreferences("mypref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Company Name",CompanyName);
                    editor.commit();

                    JSONObject RequestData = ImportData.getJSONObject("REQUESTDATA");
                    JSONArray TallyMessage = RequestData.getJSONArray("TALLYMESSAGE");

                    //JSONObject object = TallyMessage.getJSONObject(2);
                    for (int i=1;i<TallyMessage.length();i++) {

                        JSONObject object = TallyMessage.getJSONObject(i);
                        JSONObject MyLedger;
                        try {
                            MyLedger = object.getJSONObject("LEDGER");

                        }
                        catch (Exception e)
                        {
                            continue;
                        }
                        JSONObject attributes = MyLedger.getJSONObject("@attributes");
                        String name = attributes.getString("NAME");
                        parent=parent+name;
                   }
                    Toast.makeText(Main2Activity.this, ""+parent, Toast.LENGTH_SHORT).show();
                    tv.setText(parent);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });


    }
}

