package com.swoopsoft.resumebuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password, confPass;
    private TextView login;
    private Button register;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setTitle("Register");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user != null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confPass = findViewById(R.id.register_confirm_pass);
        register = findViewById(R.id.register_button);
        login = findViewById(R.id.login_link);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == login){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
        if(view == register){
            registerUser();
            System.out.println("reached here");
        }
    }

    private void registerUser() {
        //check for empty fields
        if(TextUtils.isEmpty(email.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please enter an email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).show();
            return;
        }
        if(!TextUtils.equals(password.getText().toString(),confPass.getText().toString())){
            Toast.makeText(getApplicationContext(), "Confirmation password does not match", Toast.LENGTH_LONG).show();
            return;
        }


        Toast.makeText(getApplicationContext(),"Registering ... ", Toast.LENGTH_LONG).show();
        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("RegisterActivity","Creating User");
                        createUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to create user: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void createUser() {
        user = auth.getCurrentUser();
        //Create user object
        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).setValue(user.getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                userRef.child("email").setValue(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Could not create user: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Could not create user: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

}