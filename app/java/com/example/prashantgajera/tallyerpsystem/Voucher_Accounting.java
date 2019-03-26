package com.example.prashantgajera.tallyerpsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class Voucher_Accounting extends AppCompatActivity {

    TextView  voucherType, voucherNo, date, partyname, title1 , title2 , myitemlist , narration,narrationamount,startitem,middleitem,enditem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voucher_accounting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }

        voucherType = findViewById(R.id.vouchertype);
        voucherNo = findViewById(R.id.voucherno);
        date = findViewById(R.id.date);
        partyname = findViewById(R.id.partyname);
        title1 = findViewById(R.id.title1);
        title2 = findViewById(R.id.title2);
        myitemlist= findViewById(R.id.itemlist);
        startitem= findViewById(R.id.startitem);
        middleitem=findViewById(R.id.middleitem);
        enditem= findViewById(R.id.enditem);
        narration = findViewById(R.id.narration);
        narrationamount = findViewById(R.id.narrationamount);

        Intent intent = getIntent();
        String VoucherType= intent.getExtras().getString("vouchertype");
        String VoucherNo = intent.getExtras().getString("VoucherNo");
        String Date = intent.getExtras().getString("date");
        String VoucherAccount = intent.getExtras().getString("VoucherAccount");

        date.setText(Date);
        voucherNo.setText(VoucherNo);
        voucherType.setText(VoucherType);

        if(voucherType.getText().toString().equals("Journal"))
        {
            title2.setVisibility(View.GONE);
        }
        else
        {
            title1.setVisibility(View.GONE);
        }

        JSONObject jsonObject = null;
        try
        {
            jsonObject= new JSONObject(VoucherAccount);

            if(voucherType.getText().toString().equals("Journal"))
            {
                FetchVoucherDetails1(jsonObject);
            }
            else
            {
                FetchVoucherDetails2(jsonObject);

            }

        }
        catch (Exception e)
        { e.printStackTrace();}
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }

    public void FetchVoucherDetails1(JSONObject jsonObject)
    {
        String Narration ="";
        String PartyLedgername="";
        String itemlist="";
        String myStartitem="";
        String myenditem= "";
        try {
            Narration = jsonObject.getString("NARRATION");
            narration.setText(Narration);

            PartyLedgername = jsonObject.getString("PARTYLEDGERNAME");

            JSONArray LedgerEnteries = jsonObject.getJSONArray("ALLLEDGERENTRIES.LIST");
            for(int i=0;i<LedgerEnteries.length();i++)
            {
                JSONObject voucherobject = LedgerEnteries.getJSONObject(i);
                JSONObject oldauditlist = voucherobject.getJSONObject("OLDAUDITENTRYIDS.LIST");

                String ledgername = voucherobject.getString("LEDGERNAME");
                String amount = voucherobject.getString("AMOUNT");

                double intamount=0.0;
                try{
                intamount = Double.parseDouble(amount);} catch(Exception e){}
                if(intamount<0)
                {
                    itemlist = itemlist+ledgername+"\n\n";
                    myenditem = myenditem+amount+"\n\n\n\n";
                    myStartitem=myStartitem+"\n\n\n\n";
                }
                else
                {
                    itemlist = itemlist+ledgername+"\n\n";
                    myStartitem = myStartitem+amount+"\n\n\n\n";
                    myenditem = myenditem+"\n\n\n\n";
                }

                if(ledgername.equals(PartyLedgername))
                {
                        amount=amount+"   "+amount;
                        narrationamount.setText(amount);
                        partyname.setText(ledgername);
                }
            }

            myitemlist.setText(itemlist);
            startitem.setText(myStartitem);
            enditem.setText(myenditem);
        }
        catch(Exception e ){  e.printStackTrace();  }
    }

    public void FetchVoucherDetails2(JSONObject jsonObject)
    {
        String Narration ="";
        String itemlist="";
        try {
            Narration = jsonObject.getString("NARRATION");
        }
          catch(Exception e)
            {
                e.printStackTrace();
            }
            narration.setText(Narration);

                DifferentJsonobjectEnteries(jsonObject,"ALLINVENTORYENTRIES.LIST");
                if(jsonObject.optJSONObject("INVENTORYENTRIESIN.LIST")==null)
                {
                    Log.i("true",true+"");
                    DifferentjsonArrayEnteries(jsonObject, "INVENTORYENTRIESIN.LIST");
                }
                else
                {
                    Log.i("false",""+false);
                    DifferentJsonobjectEnteries(jsonObject,"INVENTORYENTRIESIN.LIST");
                }

           // if(jsonObject.optJSONArray("ALLLEDGERENTRIES.LIST")!=null  || jsonObject.optJSONArray("LEDGERENTRIES.LIST") !=null || jsonObject.optJSONArray("NVENTORYENTRIESIN.LIST")!=null) {
                DifferentjsonArrayEnteries(jsonObject, "ALLLEDGERENTRIES.LIST");
                DifferentjsonArrayEnteries(jsonObject, "LEDGERENTRIES.LIST");
          //  }

    }

    public String  NulljsonCheck(String myjson)
    {
        String a=myjson;
        if(a.equals(""))
        {
            a="--";
        }
        return a;
    }

    public void  DifferentJsonobjectEnteries(JSONObject jsonObject , String key)
    {
        String itemlist ="";
        String mystartitem="";
        String mymiddleitem="";
        String myenditem="";
        JSONObject Allinventorylist = null;

        if(! jsonObject.isNull(key) )
        {
            try {
                Allinventorylist = jsonObject.getJSONObject(key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String ledgername="";
            try{ ledgername =  Allinventorylist.optString("LEDGERNAME");} catch (NullPointerException e){e.printStackTrace();}
            String Quantity = Allinventorylist.optString("ACTUALQTY");
            String rate = Allinventorylist.optString("RATE");
            String amount = Allinventorylist.optString("AMOUNT");
            String Stockitemname = Allinventorylist.optString("STOCKITEMNAME ");
            Log.i("stock",""+Stockitemname);

            Quantity = NulljsonCheck(Quantity);
            rate = NulljsonCheck(rate);
            amount = NulljsonCheck(amount);
            Stockitemname=Stockitemname+"";
            ledgername=ledgername+"";

            itemlist = itemlist + Stockitemname +ledgername+ "\n\n";
            mystartitem=mystartitem+Quantity+"\n\n\n\n";
            mymiddleitem= mymiddleitem+rate+"\n\n\n\n";
            myenditem = myenditem+amount+"\n\n\n\n";

            Log.i("stock",""+Stockitemname);
            if(voucherType.getText().toString().equals("Stock Journal"))
            {
                partyname.setText(Allinventorylist.optString("STOCKITEMNAME"));
                amount="  "+amount;
                narrationamount.setText(amount);
            }
        }

        myitemlist.setText(itemlist);
        startitem.setText(mystartitem);
        middleitem.setText(mymiddleitem);
        enditem.setText(myenditem);

    }

    public void DifferentjsonArrayEnteries(JSONObject jsonObject, String key)
    {
        String itemlist ="";
        String mystartitem="";
        String mymiddleitem="";
        String myenditem="";
        if(!jsonObject.isNull(key) )
        {
            JSONArray myjsonArray = null;
            try
            {
                String PartyLedgername="";
                try{ PartyLedgername = jsonObject.optString("PARTYLEDGERNAME");}catch (Exception e){e.printStackTrace();}

                myjsonArray= jsonObject.getJSONArray(key);

                for(int i=0;i<myjsonArray.length();i++)
                {
                    JSONObject jsonObject1 = myjsonArray.getJSONObject(i);

                    String ledgername="";
                    try{ledgername =  jsonObject1.optString("LEDGERNAME");}catch (Exception e){e.printStackTrace();}

                    String Stockitemname = jsonObject1.optString("STOCKITEMNAME ");
                    String  Quantity = jsonObject1.optString("ACTUALQTY");
                    String rate = jsonObject1.optString("RATE");
                    String amount = jsonObject1.optString("AMOUNT");

                    Quantity= NulljsonCheck(Quantity);
                    rate = NulljsonCheck(rate);
                    amount = NulljsonCheck(amount);
                    Stockitemname=Stockitemname+"";
                    ledgername=ledgername+"";

                    itemlist = itemlist + Stockitemname +ledgername+ "\n\n";
                    mystartitem=mystartitem+Quantity+"\n\n\n\n";
                    mymiddleitem= mymiddleitem+rate+"\n\n\n\n";
                    myenditem = myenditem+amount+"\n\n\n\n";


                    if(ledgername.equals(PartyLedgername) || Stockitemname.equals(PartyLedgername))
                    {
                        amount="  "+amount;
                        narrationamount.setText(amount);
                        partyname.setText(ledgername);
                    }
                    else if(voucherType.getText().toString().equals("Stock Journal"))
                    {
                        partyname.setText(myjsonArray.getJSONObject(0).getString("STOCKITEMNAME"));
                    }

                }
                myitemlist.setText(itemlist);
                startitem.setText(mystartitem);
                middleitem.setText(mymiddleitem);
                enditem.setText(myenditem);

            }catch(Exception e){
                e.printStackTrace();}

        }


    }
}
