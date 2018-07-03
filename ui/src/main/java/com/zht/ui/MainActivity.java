package com.zht.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.zht.ui.filter.MyView;
import com.zht.ui.revealview.RevealDrawble;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);

        RevealDrawble rd = new RevealDrawble(getResources().getDrawable(R.drawable.avft),getResources().getDrawable(R.drawable.avft_active));
        imageView.setBackground(rd);
    }
}
