package com.example.prashantgajera.tallyerpsystem;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.prashantgajera.tallyerpsystem.CommenUtilities.DialogHelper;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.MycommonUtilities;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AddPurchaseRequest extends AppCompatActivity {

    TextInputEditText name,number,email,productname;
    Button request;
    AsyncHttpClient client;
    String productid;
    Dialog d;
    Productpojo p;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase_request);
        client=new AsyncHttpClient();
        d= DialogHelper.showdialog(AddPurchaseRequest.this);
        p=getIntent().getParcelableExtra("data");
        Log.d("productid",p.getId());
        name=findViewById(R.id.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        number=findViewById(R.id.number);
        email=findViewById(R.id.email);
        productname=findViewById(R.id.productname);
        request=findViewById(R.id.request);

        sp=getSharedPreferences("pref",MODE_PRIVATE);
        name.setText(sp.getString("name",""));
        number.setText(sp.getString("number",""));
        email.setText(sp.getString("email",""));
        productname.setText(p.getTitle());
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b=true;
                if(name.getText().toString().equals("")){
                    b=false;
                    name.setError("Required");
                }
                if(email.getText().toString().equals("")){
                    b=false;
                    email.setError("Required");
                }
                if(number.getText().toString().equals("")){
                    b=false;
                    number.setError("Required");
                }
                if(productname.getText().toString().equals("")){
                    b=false;
                    productname.setError("Required");
                }
                if(b){
                    d.show();
                    RequestParams rp=new RequestParams();
                    rp.put("name",name.getText().toString());
                    rp.put("email",email.getText().toString());
                    rp.put("number",number.getText().toString());
                    rp.put("productname",productname.getText().toString());
                    rp.put("userid",sp.getString("id",""));
                    rp.put("productid",p.getId());
                    Log.d("productid",p.getId());
                    client.post(MycommonUtilities.BASE_URL+"Addrequest.php",rp,new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            d.dismiss();
                            try {
                                if(response.getString("response").equals("success"))
                                    DialogHelper.showAlert(AddPurchaseRequest.this,"Thanks for showing your interest , we will contact you soon").show();
                                else
                                    DialogHelper.showAlert(AddPurchaseRequest.this,"Some Error ").show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            sp.edit().putString("number",number.getText().toString()).apply();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            d.dismiss();
                        }
                    });
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
