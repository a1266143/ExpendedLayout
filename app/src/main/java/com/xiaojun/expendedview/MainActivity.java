package com.xiaojun.expendedview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExpendedLayout layout = findViewById(R.id.expendedLayout);
        ExpendedLayout layout2 = findViewById(R.id.expendedLayout2);
        ExpendedLayout layout3 = findViewById(R.id.expendedLayout3);
        layout.setContentView(getLayoutInflater().inflate(R.layout.test2,null));
        layout.setNextViews(layout2,layout3);
    }
}
