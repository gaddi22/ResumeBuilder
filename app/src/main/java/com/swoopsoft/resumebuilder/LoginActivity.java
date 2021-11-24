package com.swoopsoft.resumebuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password;
    private TextView register;
    private Button login;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Login");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        register = findViewById(R.id.register_link);
        login = findViewById(R.id.login_button);

        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == register){
            startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            finish();
        }
        if(view == login){
            login();
        }
    }

    private void login() {

        //check for empty fields
        if(TextUtils.isEmpty(email.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please enter an email", Toast.LENGTH_LONG);
            return;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG);
            return;
        }

        auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
            })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_LONG).show();
            }
        });
    }
}