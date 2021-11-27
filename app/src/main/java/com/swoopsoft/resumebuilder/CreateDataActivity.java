package com.swoopsoft.resumebuilder;

import static android.view.View.GONE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.swoopsoft.resumebuilder.data.DataObject;
import com.swoopsoft.resumebuilder.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateDataActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText dataName, textVal;
    private Button submit;
    private ImageView image;
    private Spinner dataType;
    private FirebaseUser user;
    private Uri imageurl;
    private Uri cloudImg;

    //Activity laucher for image selector
    ActivityResultLauncher<String>  selectImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    image.setImageURI(result);
                    imageurl = result;

                    uploadImage();
                }
            });

    private void uploadImage(){
        //upload image to firebase and get download link
        StorageReference imageLocation = FirebaseStorage.getInstance().getReference()
                .child(user.getUid()).child("images/"+imageurl.getLastPathSegment());
        UploadTask uploadTask = imageLocation.putFile(imageurl);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageLocation.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    //add image data object to user
                    cloudImg = task.getResult();
                } else {
                    // Handle failures
                    Toast.makeText(getApplicationContext(),"Failed to upload image: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

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

        image.setVisibility(GONE);
        submit.setVisibility(GONE);
        textVal.setVisibility(GONE);
        dataName.setVisibility(GONE);

        image.setOnClickListener(this);
        submit.setOnClickListener(this);
        dataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adjustUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void addText(){
        //add text data object to user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users/"+user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    DataObject dataEntry = new DataObject(dataType.getSelectedItem().toString(),textVal.getText().toString());

                    HashMap dataMap = (HashMap) snapshot.child("data").getValue();
                    if(dataMap == null){
                        dataMap = new HashMap();
                    }

                    dataMap.put(dataName.getText().toString(),dataEntry);
                    userRef.child("data").setValue(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed to submit data: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Failed to submit data: " + error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addImage(){
        //add image object to user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users/"+user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    DataObject dataEntry = new DataObject(dataType.getSelectedItem().toString(),cloudImg.toString());

                    HashMap dataMap = (HashMap) snapshot.child("data").getValue();
                    if(dataMap == null){
                        dataMap = new HashMap();
                    }

                    dataMap.put(dataName.getText().toString(),dataEntry);
                    userRef.child("data").setValue(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed to submit data: " + e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Failed to submit data: " + error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == submit){
            if(TextUtils.isEmpty(dataName.getText().toString())){
                Toast.makeText(getApplicationContext(),"No name given",Toast.LENGTH_LONG).show();
                return;
            }
            if(isEmpty()){
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
        if(v == image){
            selectImage(image);
        }
    }

    private boolean isEmpty() {
        if(dataType.getSelectedItem().toString().equals("Text")){
            if(TextUtils.isEmpty(textVal.getText().toString())){
                Toast.makeText(getApplicationContext(),"No text value entered",Toast.LENGTH_LONG).show();
                return true;
            }
            else return false;

        }
        else if(dataType.getSelectedItem().toString().equals("Image")) {
            if (TextUtils.isEmpty(cloudImg.toString())) {
                Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_LONG).show();
                return true;
            }
            else return false;
        }
        else if(dataType.getSelectedItem().toString().equals("None")){
            Toast.makeText(getApplicationContext(), "No data type selected", Toast.LENGTH_LONG).show();
            return true;
        }
        else{
            Toast.makeText(getApplicationContext(), "Unimplimented type: '" + dataType.getSelectedItem().toString() + "'", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    public Uri selectImage(ImageView view) {
        /*
        Selects an image from local storage and adds it to the provided image view.
        Method then uploads the image to firebase storage and returns the download link.
         */

        view = image;
        selectImageLauncher.launch("image/*");

        //wait for task completion
        //while(cloudImg == null);
        return cloudImg;
    }// end selectImage();

    private void adjustUI() {
        if(dataType.getSelectedItem().toString().equals("Text")){
            image.setVisibility(GONE);
            textVal.setVisibility(View.VISIBLE);
            dataName.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        }
        else if(dataType.getSelectedItem().toString().equals("Image")){
            textVal.setVisibility(GONE);
            image.setVisibility(View.VISIBLE);
            dataName.setVisibility(View.VISIBLE);
            submit.setVisibility(View.VISIBLE);
        }
        else if(dataType.getSelectedItem().toString().equals("None")){
            image.setVisibility(GONE);
            submit.setVisibility(GONE);
            textVal.setVisibility(GONE);
            dataName.setVisibility(GONE);
        }
    }

    private boolean unique() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("data");
        Task<DataSnapshot> getUserData = dataRef.get();
        Map[] dataMap = new Map[1];
        dataMap[0] = new HashMap();
        getUserData.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    dataMap[0] = (HashMap) task.getResult().getValue();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Could not submit data: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    Log.d("CreateDataActivity","Could not submit data: "+task.getException().getMessage());
                }
            }
        });

        while(!getUserData.isComplete());
        if(dataMap[0].containsKey(dataName.getText().toString())){
            return false;
        }
        else return true;
    }
}