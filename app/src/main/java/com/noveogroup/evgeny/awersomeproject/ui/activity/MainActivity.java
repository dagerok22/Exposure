package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.noveogroup.evgeny.awersomeproject.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        System.out.println("asd");
    }


    @OnClick(R.id.on_screen3_button)
    public void onScreen3() {
        Intent intent = new Intent(MainActivity.this, Activity3.class);
        startActivity(intent);
    }


}
