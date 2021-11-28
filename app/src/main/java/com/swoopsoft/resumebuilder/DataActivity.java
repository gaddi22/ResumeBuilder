package com.swoopsoft.resumebuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.swoopsoft.resumebuilder.ReusableLayouts.DataRow;
import com.swoopsoft.resumebuilder.data.DataObject;
import com.swoopsoft.resumebuilder.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DataActivity extends AppCompatActivity implements View.OnClickListener{

    private Button addData;
    private LinearLayout mainLayout;
    private ArrayList<DataRow> rows;
    private FirebaseUser user;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setTitle("View Data");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

        rows = new ArrayList<>();
        addData = findViewById(R.id.add_data_button);
        addData.setOnClickListener(this);

        mainLayout = findViewById(R.id.data_layout);
        mainLayout.removeAllViews();

        Toast.makeText(getApplicationContext(),"Loading Data",Toast.LENGTH_LONG).show();

        //get references to user's database
        userRef = FirebaseDatabase.getInstance().getReference().child("users/"+user.getUid());

        loadValues();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mainLayout.removeAllViews();
                loadValues();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadValues() {
        Task<DataSnapshot> getUserObjTask = userRef.get();
        DataActivity activityRef = this;
        getUserObjTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    User userObj = task.getResult().getValue(User.class);

                    HashMap<String,DataObject> dataMap = (HashMap) userObj.getData();

                    for(Map.Entry<String, DataObject> entry : dataMap.entrySet()){
                        DataRow row = new DataRow( activityRef, entry.getValue(), entry.getKey(), getApplicationContext());
                        rows.add(row);
                        mainLayout.addView(row.getView());

                    }

                    Toast.makeText(getApplicationContext(),"Loading Done",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == addData){
            startActivity(new Intent(getApplicationContext(),CreateDataActivity.class));
        }
        for(DataRow row : rows){
            if(view == row.getButton()){
                removeItem(row);
            }
        }
    }

    private void removeItem(DataRow row) {
        //remove text from database
        Task deleteRowTask = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("data")
                .child(row.getDataName())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mainLayout.removeView(row.getView());
                            rows.remove(row);
                            if(TextUtils.equals(row.getType(),"Image")){
                                //remove image from storage
                                StorageReference fileRef = FirebaseStorage.getInstance().getReferenceFromUrl((String) row.getObj().getValue());
                                fileRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("DataActivcity","Image succefully deleted");
                                        }
                                        else{
                                            Log.d("DataActivcity","Image was not deleted: " + task.getException().getMessage());
                                        }
                                    }
                                });
                            }
                        }
                        //failure
                        else{
                            String eMessage = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),"Failed to delete data: "+eMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}