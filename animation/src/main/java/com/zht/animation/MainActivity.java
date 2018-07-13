package com.zht.animation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


public class MainActivity extends AppCompatActivity {

    private View iv;
    private FrameLayout mMainView;
    private SplashView splashView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainView = new FrameLayout(this);
        View view = new View(this);
        mMainView.addView(view);
        splashView = new SplashView(this);
        mMainView.addView(splashView);

        setContentView(mMainView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashView.splashDisappear();
            }
        },5000);

    }
}
