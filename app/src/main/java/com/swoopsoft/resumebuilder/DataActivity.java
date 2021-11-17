package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DataActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setTitle("View Data");

        addData = findViewById(R.id.add_data_button);
        addData.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == addData){
            startActivity(new Intent(getApplicationContext(),CreateDataActivity.class));
        }
    }
}