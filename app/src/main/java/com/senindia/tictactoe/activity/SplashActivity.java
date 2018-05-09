package com.senindia.tictactoe.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.senindia.tictactoe.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 4000;
    LinearLayout iv_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        iv_logo =(LinearLayout) findViewById(R.id.iv_logo);


        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_anim_logo);
        iv_logo.startAnimation(pulse);

        CountDown();

    }

    private void CountDown() {



        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
