package com.cwc.testannotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyRetrofit myRetrofit = new MyRetrofit();
        MyApi myApi = myRetrofit.create(MyApi.class);
        String s = myApi.sendRequest(2, 1);
        toast(s);
    }

    private void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
