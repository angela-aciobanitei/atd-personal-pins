package com.ang.acb.personalpins.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ang.acb.personalpins.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    //.replace(R.id.container, CameraFragment.newInstance())
                    .replace(R.id.container, VideoFragment.newInstance())
                    .commit();
        }
    }
}
