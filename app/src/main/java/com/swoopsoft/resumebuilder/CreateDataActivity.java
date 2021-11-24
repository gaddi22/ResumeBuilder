package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swoopsoft.resumebuilder.data.DataObject;
import com.swoopsoft.resumebuilder.data.User;

import java.util.HashMap;
import java.util.Map;

public class CreateDataActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText dataName, textVal;
    private Button submit;
    private ImageView image;
    private Spinner dataType;
    private FirebaseUser user;
    private String imgurl;
    private boolean isEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_data);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        dataName = findViewById(R.id.obj_name);
        textVal = findViewById(R.id.obj_text);
        image = findViewById(R.id.obj_image);
        dataType = findViewById(R.id.data_type);
        submit = findViewById(R.id.submit_obj);

        image.setOnClickListener(this);
        submit.setOnClickListener(this);
        dataType.setOnClickListener(this);

    }

    private void addText(){
        new User(user.getUid()).addData(dataName.getText().toString(), new DataObject("String",textVal.getText().toString()));
    }

    private void addImage(){
        new User(user.getUid()).addData(dataName.getText().toString(), new DataObject("Image",imgurl));
    }

    @Override
    public void onClick(View v) {
        if(v == submit){
            if(TextUtils.isEmpty(dataName.getText().toString())){
                Toast.makeText(getApplicationContext(),"No name given",Toast.LENGTH_LONG).show();
                return;
            }
            if(isEmpty){
                Toast.makeText(getApplicationContext(),"No data value or image",Toast.LENGTH_LONG).show();
                return;
            }
            if(unique()){
                if(dataType.getSelectedItem().toString().equals("Text")){
                    addText();
                }
                else if(dataType.getSelectedItem().toString().equals("Image")){
                    addImage();
                }
                else if(dataType.getSelectedItem().toString().equals("Recommendation Letter")){
                    Toast.makeText(getApplicationContext(),"unimplemented",Toast.LENGTH_LONG).show();
                    //addRecLetter();
                }
                else{
                    Toast.makeText(getApplicationContext(),"No item selected",Toast.LENGTH_LONG).show();
                }
            }
        }
        if(v == dataType){
            adjustUI();
        }
    }

    private void adjustUI() {
        if(dataType.getSelectedItem().toString().equals("Text")){

        }
    }

    private boolean unique() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("data");
        Map dataMap = (Map) dataRef.get();
        if(dataMap.containsKey(dataName.getText().toString())){
            return false;
        }
        else return true;
    }
}