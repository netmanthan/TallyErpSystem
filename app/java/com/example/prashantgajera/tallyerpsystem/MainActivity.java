package com.example.prashantgajera.tallyerpsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.prashantgajera.tallyerpsystem.Adapter.AdapterCardLayout;
import com.example.prashantgajera.tallyerpsystem.Adapter.MenuNavigationAdapter;
import com.example.prashantgajera.tallyerpsystem.Adapter.SpacingItemDecoration;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    SlidingPaneLayout mSlidingPanel;
    View bg;
    TextView tv;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    GoogleSignInClient mGoogleSignInClient;
    Uri uri;
    List<CardCategory> items;
    private View parent_view;
    private RecyclerView recyclerView;
    private AdapterCardLayout mAdapter;
    AsyncHttpClient httpClient;
    SharedPreferences sharedPreferences;
    ACProgressFlower  acdialog;
    SliderLayout sliderLayout;
    View photo;

    
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
            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Call the function callInstamojo to start payment here

        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        mSlidingPanel.setPanelSlideListener(panelListener);
        mSlidingPanel.setParallaxDistance(100);
        mSlidingPanel.setSliderFadeColor(ContextCompat.getColor(this, android.R.color.transparent));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        View mCustomView;
        ImageView imageView;

        bg = findViewById(R.id.imageView);
        tv= findViewById(R.id.name);
        sliderLayout= findViewById(R.id.slider);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email="";

        if (user != null) {
            email  = user.getEmail();
        }

        if (user != null) {
            uri=user.getPhotoUrl();
        }
        tv.setText(email);
        Picasso.get().load(uri.toString()).into((ImageView) bg);

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Home");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.hamburger);


            LayoutInflater mInflater= LayoutInflater.from(this);
            mCustomView = mInflater.inflate(R.layout.iconview_main, null);
            imageView = (ImageView) mCustomView.findViewById(R.id.icon_view);
            Picasso.get().load(uri.toString()).into(imageView);
            toolbar.addView(mCustomView, new Toolbar.LayoutParams(Gravity.RIGHT));


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Click on Menu", Toast.LENGTH_SHORT).show();
                }
            });
        }

        loadSlider();

        final String[] mGroups = {
                "Help",
                "Share App",
                "FAQs",
                "About"
        };

        final String[][] mChilds = {
                {},
                {},
                {},
                {}
        };

        ExpandableListView listMenu = (ExpandableListView) findViewById(R.id.menu_list);
        MenuNavigationAdapter adapter = new MenuNavigationAdapter(this, mGroups, mChilds);
        listMenu.setAdapter(adapter);
        listMenu.expandGroup(0);
        listMenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(MainActivity.this, "Menu "+mChilds[groupPosition][childPosition]+" clicked!", Toast.LENGTH_SHORT).show();
                mSlidingPanel.closePane();
                return false;
            }
        });

        listMenu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if(groupPosition > 0) {
                    Toast.makeText(MainActivity.this, "Menu " + mGroups[groupPosition] + " clicked!", Toast.LENGTH_SHORT).show();
                    mSlidingPanel.closePane();
                }
                return false;
            }
        });

        initComponent();
    }

    SlidingPaneLayout.PanelSlideListener panelListener = new SlidingPaneLayout.PanelSlideListener(){
        @Override
        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub
            getWindow().setStatusBarColor(getResources().getColor(R.color.Statusbar));
        }

        @Override
        public void onPanelOpened(View arg0) {
            // TODO Auto-generated method stub
            getWindow().setStatusBarColor(getResources().getColor(R.color.red));

        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {
            // TODO Auto-generated method stub


        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mSlidingPanel.isOpen()){
                    mSlidingPanel.closePane();
                }else{
                    mSlidingPanel.openPane();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                //Toast.makeText(this, "Button logout clicked!", Toast.LENGTH_SHORT).show();
                doLogout();
                break;
            case R.id.btnSetting:
                Toast.makeText(this, "Button setting clicked!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void doLogout()
    {
        firebaseAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        finish();
        Intent intent = new Intent(this,Login_screen.class);
        startActivity(intent);

    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, dpToPx(this, 20), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        items = new ArrayList<>();
        items.add(new CardCategory(R.drawable.ledger,"Ledger"));
        items.add(new CardCategory(R.drawable.stock,"Stock"));
        items.add(new CardCategory(R.drawable.voucher,"Voucher"));
        items.add(new CardCategory(R.drawable.outstanding,"Outstanding"));
        items.add(new CardCategory(R.drawable.balancesheet,"Balance Sheet"));
        items.add(new CardCategory(R.drawable.profitloss,"Profit/Loss"));
        items.add(new CardCategory(R.drawable.tallystore,"Tally Store"));


        //set data and list adapter
        mAdapter = new AdapterCardLayout(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterCardLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CardCategory obj, int position) {
                Toast.makeText(MainActivity.this, obj.getCardcatagory()+"-"+position, Toast.LENGTH_SHORT).show();
                //Snackbar.make(parent_view, "Item " + obj.getCardcatagory() + " clicked", Snackbar.LENGTH_SHORT).show();
                acdialog = new ACProgressFlower.Builder(MainActivity.this)
                        .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                        .themeColor(Color.BLUE)
                        .text("Please Wait..")
                        .bgAlpha(1)
                        .speed(15)
                        .textAlpha(1)
                        .textColor(Color.BLACK)
                        .bgColor(Color.WHITE)
                        .fadeColor(Color.WHITE).build();
                if(position==0)
                {
//                    finish();
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 3000);
                    Intent intent = new Intent(MainActivity.this,Ledger_Details.class);
                    startActivity(intent);

                }
                else if(position==1)
                {
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 3000);
                    Intent intent = new Intent(MainActivity.this,Stock_Details.class);
                    startActivity(intent);
                }
                else if(position==2)
                {
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 3000);
                    Intent intent = new Intent(MainActivity.this,Voucher_Details.class);
                    startActivity(intent);
                }
                else if(position==4)
                {
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 2000);
                    Intent intent = new Intent(MainActivity.this,Balancesheet_Details.class);
                    startActivity(intent);
                }
                else if(position==5)
                {
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 2000);
                    Intent intent = new Intent(MainActivity.this,Profit_Loss_Details.class);
                    startActivity(intent);
                }
                else if(position==6)
                {
                    acdialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acdialog.dismiss();
                        }

                    }, 2000);
                    Intent intent = new Intent(MainActivity.this,Store.class);
                    startActivity(intent);
                }


            }
        });

    }

    private void Jsondata(List<CardCategory> items) {

    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void loadSlider() {
        ArrayList<Integer> image = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        image.add(R.drawable.image_bg);
        image.add(R.drawable.accounting1);
        image.add(R.drawable.accounting2);
        image.add(R.drawable.accounting3);

        RequestOptions requestOptions = new RequestOptions();

        for (int i = 0; i < image.size(); i++) {
            TextSliderView sliderView = new TextSliderView(MainActivity.this);

            // initialize SliderLayout
            sliderView
                    .image(image.get(i))
                    .setRequestOption(requestOptions);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderLayout.addSlider(sliderView);
            sliderLayout.setDuration(1);
        }
    }

}
