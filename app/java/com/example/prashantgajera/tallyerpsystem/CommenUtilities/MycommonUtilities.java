package com.example.prashantgajera.tallyerpsystem.CommenUtilities;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Prashant Gajera on 08-Jan-19.
 **/


public class MycommonUtilities {
    AsyncHttpClient asyncHttpClient;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    public static String email;
    public MycommonUtilities()
    {
        email=firebaseUser.getEmail();
    }

    public String url ="http://prashanthost.tk/getfilename.php";
    public static String ledgerjsonurl = "http://prashanthost.tk/tallyfiles/gajeraprashant1998@gmail.com/Ledger.json";
    public static String stockjsonurl = "http://prashanthost.tk/tallyfiles/gajeraprashant1998@gmail.com/Stock.json";
    public static String voucherjsonurl = "http://prashanthost.tk/tallyfiles/gajeraprashant1998@gmail.com/Voucher.json";
    public static String balancesheetjsonurl = "http://prashanthost.tk/tallyfiles/gajeraprashant1998@gmail.com/Balancesheet.json";
    public static String profitlossjsonurl = "http://prashanthost.tk/tallyfiles/gajeraprashant1998@gmail.com/Profitandloss.json";
    public static String instamojourl ="http://prashanthost.tk/tallyfiles/";
    public static String BASE_URL="http://android1.gujinfotech.com/mobiletally/";

    public String getfile ="tallyfiles/";

    public void ParseJson(String myfile)
    {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams pram = new RequestParams();
        pram.put("path",myfile);
        asyncHttpClient.post(url,pram, new JsonHttpResponseHandler()
        {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                Log.i("file","this is ");
                try {

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    String file=jsonArray.toString();
                    Log.i("file",file);
                    setJsonArray(jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse,Throwable throwable) {
                super.onFailure(statusCode, headers, errorResponse, throwable);
                Log.e("this is error", "onFailure: " + errorResponse);
            }
        });
    }
    JSONArray jsonArray = new JSONArray();
    private void setJsonArray(JSONArray jsonArray) {
       this.jsonArray=jsonArray;
    }

}
