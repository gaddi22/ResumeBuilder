package com.swoopsoft.resumebuilder.data;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String email;
    public Map<String,DataObject> data;    //attribute name, object

    public User(String email, Map data){
        this.email = email;
        this.data = data;
    }

    public User(){
        email = "";
        data = new HashMap<>();
    }

    public Map getData(){
        return data;
    }

    public String getEmail(){
        return email;
    }

}


