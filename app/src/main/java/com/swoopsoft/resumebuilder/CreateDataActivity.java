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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private Uri imgurl;

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
        dataType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                adjustUI();
                return true;
            }
        });

    }

    private void addText(){
        //add text data object to user
        new User(user.getUid()).addData(dataName.getText().toString(), new DataObject("String",textVal.getText().toString()));
    }

    private void addImage(){
        new User(user.getUid()).addData(dataName.getText().toString(), new DataObject("Image",imgurl.toString()));
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
            imgurl = selectImage(image);
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
            if (TextUtils.isEmpty(imgurl.toString())) {
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

    private Uri selectImage(ImageView view) {
        /*
        Selects an image from local storage and adds it to the provided image view.
        Method then uploads the image to firebase stroage and returns the download link.
         */

        final Uri[] result = new Uri[1];

        //select image from local storage
        ActivityResultLauncher<String>  selectImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                view.setImageURI(result);
            }
        });
        selectImageLauncher.launch("image/*");

        //upload image to firebase and get download link
        StorageReference imageLocation = FirebaseStorage.getInstance().getReference()
                .child(user.getUid()).child("images/"+imgurl.getLastPathSegment());
        UploadTask uploadTask = imageLocation.putFile(imgurl);
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
                    Uri downloadUri = task.getResult();
                    result[0] = task.getResult();
                } else {
                    // Handle failures
                    Toast.makeText(getApplicationContext(),"Failed to upload image: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        return result[0];
    }// end selectImage();

    private void adjustUI() {
        if(dataType.getSelectedItem().toString().equals("Text")){
            image.setVisibility(GONE);
        }
        else if(dataType.getSelectedItem().toString().equals("Image")){
            textVal.setVisibility(GONE);
        }
    }

    private boolean unique() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("data");
        try {
            Map dataMap = (Map) dataRef.get();
            if (dataMap.containsKey(dataName.getText().toString())) {
                return false;
            }
        } catch(Exception e){

        }
        return true;
    }
}