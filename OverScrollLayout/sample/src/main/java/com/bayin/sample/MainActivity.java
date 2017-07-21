package com.bayin.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bayin.library.view.OsBottomLayout;
import com.bayin.library.view.OsContainer;
import com.bayin.library.view.OsTopLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OsContainer container = (OsContainer) findViewById(R.id.container);
        container.setChildView(new OsTopLayout(this, R.layout.top_layout),
                new OsBottomLayout(this, R.layout.bottom_layout));
    }
}
