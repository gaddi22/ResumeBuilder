package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    Button btnAddNewDocument;
    Button btnAddData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAddData = findViewById(R.id.btnHomeAddData);
        btnAddNewDocument = findViewById(R.id.btnHomeAddNewDocument);

        btnAddData.setOnClickListener(view -> addData());
        btnAddNewDocument.setOnClickListener(view -> addDocument());

    }

    private void addData() {
        Toast.makeText(getApplicationContext(), "Navigate to Add Data Activity", Toast.LENGTH_SHORT).show();
    }

    private void addDocument() {
        Toast.makeText(getApplicationContext(), "Navigate to Add Document Activity", Toast.LENGTH_SHORT).show();
    }
}