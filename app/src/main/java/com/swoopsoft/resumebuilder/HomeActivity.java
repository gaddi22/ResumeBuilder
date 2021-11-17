package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    Button btnAddNewDocument;
    Button btnAddData;
    LinearLayout llDocumentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAddData = findViewById(R.id.btnHomeAddData);
        btnAddNewDocument = findViewById(R.id.btnHomeAddNewDocument);
        llDocumentContainer = findViewById(R.id.linearLayoutDocumentContainer);

        btnAddData.setOnClickListener(view -> addData());
        btnAddNewDocument.setOnClickListener(view -> addDocument());

        getDocuments();

    }

    private void addData() {
        Toast.makeText(getApplicationContext(), "Navigate to Add Data Activity", Toast.LENGTH_SHORT).show();
    }

    private void addDocument() {
        Toast.makeText(getApplicationContext(), "Navigate to Add Document Activity", Toast.LENGTH_SHORT).show();
    }

    private void getDocuments(){
        // connect to Firebase
        // iterate through documents of user
        // for each document create a simple card for display
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        layoutParams.setMargins(5, 10, 5, 10);

        LinearLayout ll_1 = new LinearLayout(getApplicationContext());
        ll_1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ll_2 = new LinearLayout(getApplicationContext());
        ll_2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ll_3 = new LinearLayout(getApplicationContext());
        ll_3.setOrientation(LinearLayout.HORIZONTAL);
        ll_1.addView(getCard("Name of Document","10/10/10"), layoutParams);
        ll_1.addView(getCard("Second Document", "11/11/11"), layoutParams);
        ll_1.addView(getCard("Third Document", "12/12/12"), layoutParams);
        ll_2.addView(getCard("Fourth Document", "4/4/14"), layoutParams);
        ll_2.addView(getCard("Fifth Document", "5/5/15"), layoutParams);
        ll_3.addView(getCard("Sixth Document", "6/6/16"), layoutParams);

        llDocumentContainer.addView(ll_1);
        llDocumentContainer.addView(ll_2);
        llDocumentContainer.addView(ll_3);
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

        TextView date = new TextView(getApplicationContext());
        date.setText(dateString);
        date.setTextColor(Color.BLACK);
        date.setPadding(5,5,5,5);

        layout.addView(name, layoutParams);
        layout.addView(date);
        card.addView(layout);
        return card;
    }
}