package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button data;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //go to homeActivity on Login
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        data = findViewById(R.id.view_data_main);
        data.setOnClickListener(this);

        startActivity(new Intent(getApplicationContext(),DataActivity.class));

    }

    @Override
    public void onClick(View view) {

        if(view == data){
            startActivity(new Intent(getApplicationContext(),DataActivity.class));
        }
    }
}