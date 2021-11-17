package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class ChooseTemplateActivity extends AppCompatActivity {

    Button btnBuildTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);

        btnBuildTemplate = findViewById(R.id.btnChooseTemplateBuildTemplate);

        btnBuildTemplate.setOnClickListener(view -> {
            openBuildTemplateActivity();
        });
    }

    private void openBuildTemplateActivity(){
        Toast.makeText(getApplicationContext(), "Open the Activity for building a Template", Toast.LENGTH_SHORT).show();
    }
}