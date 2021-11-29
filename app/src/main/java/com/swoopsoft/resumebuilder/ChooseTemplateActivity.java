package com.swoopsoft.resumebuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChooseTemplateActivity extends AppCompatActivity {

    Button btnBuildTemplate;
    LinearLayout llDocumentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);

        btnBuildTemplate = findViewById(R.id.btnChooseTemplateBuildTemplate);
        llDocumentContainer = findViewById(R.id.linearLayoutDocumentContainer);

        btnBuildTemplate.setOnClickListener(view -> openBuildTemplateActivity());

        getDocuments();
    }

    private void openBuildTemplateActivity(){
        startActivity(new Intent(getApplicationContext(), TemplateBuilderActivity.class));
    }

    private void getDocuments(){
        // connect to Firebase
        DocumentReference templateRef = FirebaseFirestore.getInstance().collection("templates").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // iterate through documents of user
        templateRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                // configure the layout parameters for the cards
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

                layoutParams.setMargins(5, 10, 5, 10);

                documentSnapshot.getData().keySet().forEach(key -> {
                    // create the cardView with the key
                    CardView card = getCard(key, "");
                    // put the cardView in the layout
                    llDocumentContainer.addView(card, layoutParams);
                });
            })
            .addOnFailureListener(e -> {

            });
        // for each document create a simple card for display


//        LinearLayout ll_1 = new LinearLayout(getApplicationContext());
//        ll_1.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout ll_2 = new LinearLayout(getApplicationContext());
//        ll_2.setOrientation(LinearLayout.HORIZONTAL);
//        LinearLayout ll_3 = new LinearLayout(getApplicationContext());
//        ll_3.setOrientation(LinearLayout.HORIZONTAL);
//        ll_1.addView(getCard("Name of Document","10/10/10"), layoutParams);
//        ll_1.addView(getCard("Second Document", "11/11/11"), layoutParams);
//        ll_1.addView(getCard("Third Document", "12/12/12"), layoutParams);
//        ll_2.addView(getCard("Fourth Document", "4/4/14"), layoutParams);
//        ll_2.addView(getCard("Fifth Document", "5/5/15"), layoutParams);
//        ll_3.addView(getCard("Sixth Document", "6/6/16"), layoutParams);
//
//        llDocumentContainer.addView(ll_1);
//        llDocumentContainer.addView(ll_2);
//        llDocumentContainer.addView(ll_3);
    }

    private CardView getCard(String nameString, String dateString){
        CardView card = new CardView(getApplicationContext());
        card.setPadding(3,3,3,3);
        card.setRadius(15f);
        card.setCardElevation(25f);
        card.setCardBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(10, 3, 10, 3);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(2,2,2,2);

        TextView name = new TextView(getApplicationContext());
        name.setText(nameString);
        name.setTextColor(Color.BLACK);
        name.setPadding(5,5,5,5);
        layout.addView(name, layoutParams);
        if(! dateString.isEmpty()) {
            TextView date = new TextView(getApplicationContext());
            date.setText(dateString);
            date.setTextColor(Color.BLACK);
            date.setPadding(5, 5, 5, 5);
            layout.addView(date);
        }

        card.addView(layout);
        return card;
    }
}