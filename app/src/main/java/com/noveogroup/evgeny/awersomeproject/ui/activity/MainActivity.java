package com.noveogroup.evgeny.awersomeproject.ui.activity;

import android.app.ProgressDialog;
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
        Intent intent = new Intent(MainActivity.this, AddNewTaskActivity.class);
        startActivity(intent);
    }

    private ProgressDialog getProgressDialog(){
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Loading tasks..");
        return progressDialog;
    }

    @OnClick(R.id.button)
    public void onButtonClick(){
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }


}
