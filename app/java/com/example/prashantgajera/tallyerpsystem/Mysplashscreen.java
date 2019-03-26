package com.example.prashantgajera.tallyerpsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Mysplashscreen extends AppCompatActivity {


    ImageView img;
    int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysplashscreen);

        //hide the ActionBar and notification bar
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //find image and run the thread
        img = findViewById(R.id.img);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeOutAndHideImage(img);
            }
        }, SPLASH_TIME_OUT);

    }

    public void fadeOutAndHideImage(final ImageView img)
    {
        //img.setVisibility(View.GONE);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
                finish();
                Intent intent = new Intent(Mysplashscreen.this,Login_screen.class);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        img.startAnimation(fadeOut);
    }

    public void myanimation()
    {
        img.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.translate_topto_center );
        anim.setDuration(1200);
        img.startAnimation(anim);
    }
}
