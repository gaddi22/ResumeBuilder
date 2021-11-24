package com.swoopsoft.resumebuilder.data;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String uid;
    public String email;
    public Map data;    //attribute name, object
    public DatabaseReference userRef;

    public User(String uid){
        this.uid = uid;
        data = new HashMap();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
    }

    public void addData(String name,DataObject value){
        data.put(name, value);
        userRef.child("data").child(name).setValue(value);
    }

}


