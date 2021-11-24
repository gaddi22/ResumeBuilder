package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.swoopsoft.resumebuilder.ReusableLayouts.DataRow;
import com.swoopsoft.resumebuilder.data.User;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addData;
    private LinearLayout mainLayout;
    private ArrayList<DataRow> rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setTitle("View Data");

        addData = findViewById(R.id.add_data_button);
        addData.setOnClickListener(this);

        mainLayout = findViewById(R.id.data_layout);
        User userObj = new User(FirebaseAuth.getInstance().getUid());


    }

    @Override
    public void onClick(View view) {
        if(view == addData){
            startActivity(new Intent(getApplicationContext(),CreateDataActivity.class));
        }
        for(DataRow row : rows){
            if(view == row.getButton()){
                removeItem();
            }
        }
    }

    private void removeItem() {
    }
}