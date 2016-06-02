package io.skygear.skygear_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.skygear.skygear.Container;

public class MainActivity extends AppCompatActivity {
    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        skygear = new Container();
    }
}
