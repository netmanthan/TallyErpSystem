package com.example.prashantgajera.tallyerpsystem;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.prashantgajera.tallyerpsystem.CommenUtilities.DialogHelper;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.MycommonUtilities;
import com.example.prashantgajera.tallyerpsystem.CommenUtilities.SquareImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Store extends AppCompatActivity {

    RecyclerView list;
    AsyncHttpClient client;
    ArrayList<Productpojo> Products;
    SharedPreferences cache;
    Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        cache=getSharedPreferences("store",MODE_PRIVATE);
        list=findViewById(R.id.list);
        client=new AsyncHttpClient();
        Products=new ArrayList<>();

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

        list.setLayoutManager(new GridLayoutManager(getApplicationContext(),2,LinearLayoutManager.VERTICAL,false));
        list.setAdapter(new CustomAdapter());
        getdata();
        d= DialogHelper.showdialog(Store.this);
        d.show();
        if(cache.contains("data")){
            String data=cache.getString("data","");

            try {
                JSONObject response=new JSONObject(data);
                if(response.getString("response").equals("success")){

                    JSONArray ja=response.getJSONArray("data");

                    for(int i=0;i<ja.length();i++){
                        JSONObject jo=ja.getJSONObject(i);
                        Productpojo p=new Productpojo();
                        p.setTitle(jo.getString("productname"));
                        p.setBadge(jo.getString("highlight"));
                        p.setPrice(jo.getString("price"));
                      //  p.setDescription(jo.getString("details"));
                        p.setImage(MycommonUtilities.instamojourl+"images/"+"tallylogo.jpg");
                        p.setDescription("this is tally add on. you can buy this from g here");
                       // p.setImage(Integer.toString(R.drawable.tallylogo));

                        Products.add(p);
                    }
                    list.getAdapter().notifyDataSetChanged();
                }
                if(d.isShowing()){
                    d.dismiss();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getdata() {
        client.post(MycommonUtilities.BASE_URL+"getstoredata.php",new RequestParams(),new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if(d.isShowing()){
                    d.dismiss();
                }
                cache.edit().putString("data",response.toString()).apply();
                Products=new ArrayList<>();
                try {
                    if(response.getString("response").equals("success")){

                        JSONArray ja=response.getJSONArray("data");

                        for(int i=0;i<ja.length();i++){
                            JSONObject jo=ja.getJSONObject(i);
                            Productpojo p=new Productpojo();
                            p.setTitle(jo.getString("productname"));
                            p.setBadge(jo.getString("highlight"));
                            p.setPrice(jo.getString("price"));
                            p.setDescription("this is tally add on. you can buy this from g here");
                            p.setImage(MycommonUtilities.instamojourl+"images/"+"tallylogo.jpg");
                            p.setId(jo.getString("productid"));
                            Products.add(p);
                        }
                        list.getAdapter().notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class CustomAdapter extends RecyclerView.Adapter<Viewholder> {
        @NonNull
        @Override
        public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Viewholder(getLayoutInflater().inflate(R.layout.productrow,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull Viewholder holder, int position) {

            final Productpojo p=Products.get(position);
            holder.details.setText(p.getDescription());
            holder.price.setText(p.getPrice());
            if(!p.getBadge().equals("no")){
                holder.highlight.setVisibility(View.VISIBLE);
            }else{
                holder.highlight.setVisibility(View.GONE);
            }
            holder.name.setText(p.getTitle());
            Picasso.get().load(p.getImage()).into(holder.image);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),StoreDetailView.class).putExtra("data",p));
                }
            });
        }

        @Override
        public int getItemCount() {
            return Products.size();
        }
    }

    private class Viewholder extends RecyclerView.ViewHolder {
        TextView name,price,details;
        ImageView highlight;
        SquareImageView image;
        RelativeLayout item;
        public Viewholder(View itemView) {
            super(itemView);
            highlight=itemView.findViewById(R.id.highlight);
            name=itemView.findViewById(R.id.name);
            price=itemView.findViewById(R.id.price);
            details=itemView.findViewById(R.id.details);
            image=(SquareImageView)itemView.findViewById(R.id.image);
            item=itemView.findViewById(R.id.item);
        }
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
