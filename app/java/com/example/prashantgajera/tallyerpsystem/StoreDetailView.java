package com.example.prashantgajera.tallyerpsystem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prashantgajera.tallyerpsystem.CommenUtilities.DialogHelper;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.SquareImageView;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.dbHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

public class StoreDetailView extends AppCompatActivity {

    Productpojo p;
    SquareImageView image;
    TextView details,price;
    SharedPreferences sp;
    dbHelper d;
    AsyncHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail_view);
        d=new dbHelper(StoreDetailView.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Tally Shop");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        }

        sp=getSharedPreferences("payment",MODE_PRIVATE);
        p=getIntent().getParcelableExtra("data");
        image=(SquareImageView )findViewById(R.id.image);
        details=findViewById(R.id.details);
        price=findViewById(R.id.price);
        Picasso.get().load(p.getImage()).into(image);
       // image.setImageDrawable((getDrawable(R.drawable.tallylogo)));
        details.setText(p.getDescription());
        price.setText(p.getPrice());
        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });
    }
    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);
        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
            pay.put("currency","INR");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener();
        instamojoPay.start(activity, pay, listener);
    }

    InstapayListener listener;


    private void initListener() {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
                        .show();
                Orderpojo o=new Orderpojo();
                o.setOrderdata(response);
                o.setTitle("Purchase: "+p.getTitle());
                o.setStatus("success");
                d.insertorderhistory(o);
                updatestatus(response,"INSTAMOJO");
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
                Orderpojo o=new Orderpojo();
                o.setOrderdata("Failed"+reason+" "+code);
                o.setTitle("Purchase: "+p.getTitle());

                o.setStatus("fail");
                d.insertorderhistory(o);
            }
        };
    }

    private void updatestatus(String response, String instamojo) {
        final Dialog data= DialogHelper.showdialog(StoreDetailView.this);

        AsyncHttpClient client=new AsyncHttpClient();
        client.setTimeout(100000);

        RequestParams rp=new RequestParams();
        rp.put("email",getSharedPreferences("pref",MODE_PRIVATE).getString("email",""));
        rp.put("mobile",sp.getString("number",""));
        rp.put("userid",getSharedPreferences("pref",MODE_PRIVATE).getString("id",""));
        rp.put("productname",p.getTitle());
        rp.put("productid",p.getId());
        rp.put("response",instamojo+":"+response);
        client.post(instamojo+"addpayment.php",rp,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if(response.getString("response").equals("success")){

                        Toast.makeText(StoreDetailView.this, "Successfull", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                data.dismiss();
            }
        });

    }

    public void show(){
        final Dialog dialog=new Dialog(StoreDetailView.this);
        dialog.setContentView(R.layout.checkout);
        dialog.setCancelable(true);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final Button placeorder= (Button) dialog.findViewById(R.id.place);
        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.rg);
                RadioButton rb= (RadioButton) dialog.findViewById(R.id.radioButton);

                if(rb.isChecked()){

                    startActivity(new Intent(getApplicationContext(),AddPurchaseRequest.class).putExtra("data",p));

                }else{
                    dialog.dismiss();
                    showpaymentdetailslayout();
                }
                dialog.dismiss();
            }
        });

        ImageView iv= (ImageView) dialog.findViewById(R.id.cancel);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void showpaymentdetailslayout(){
        final Dialog dialog=new Dialog(StoreDetailView.this);
        dialog.setContentView(R.layout.paymentdetailslayout);
        dialog.setCancelable(true);
        final TextInputEditText name=dialog.findViewById(R.id.name);
        final TextInputEditText email=dialog.findViewById(R.id.email);
        final TextInputEditText number=dialog.findViewById(R.id.number);
        name.setText(sp.getString("name",""));
        number.setText(sp.getString("number",""));
        email.setText(getSharedPreferences("pref",MODE_PRIVATE).getString("email",""));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bo=true;
                if(name.getText().toString().equals("")){
                    name.setError("Required");
                    bo=false;
                }
                if(number.getText().toString().length()<10){
                    number.setError("Enter Valid number");
                    bo=false;
                }
                if(bo){

                    getSharedPreferences("pref",MODE_PRIVATE).edit().putString("number",number.getText().toString()).apply();
                    sp.edit().putString("name",name.getText().toString()).putString("number",number.getText().toString()).apply();
                    callInstamojoPay(email.getText().toString(), number.getText().toString(), p.getPrice(), "TDL Purchase:"+p.getTitle(), name.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
}
