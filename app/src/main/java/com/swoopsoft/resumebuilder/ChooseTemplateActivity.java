package com.swoopsoft.resumebuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.Objects;

public class ChooseTemplateActivity extends AppCompatActivity {

    Button btnBuildTemplate;
    LinearLayout llDocumentContainer;
    LinearLayout llDocumentViewer;
    Map<String, Object> templateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_template);

        btnBuildTemplate = findViewById(R.id.btnChooseTemplateBuildTemplate);
        llDocumentContainer = findViewById(R.id.linearLayoutDocumentContainer);
        llDocumentViewer = findViewById(R.id.linearLayoutDocumentViewer);

        btnBuildTemplate.setOnClickListener(view -> openBuildTemplateActivity());

        getDocuments();
    }

    private void openBuildTemplateActivity(){
        startActivity(new Intent(getApplicationContext(), TemplateBuilderActivity.class));
    }

    private void getDocuments(){
        // connect to Firebase
        DocumentReference templateRef = FirebaseFirestore.getInstance().collection("templates").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        // iterate through documents of user
        templateRef.get()
            .addOnSuccessListener(documentSnapshot -> {
                // configure the layout parameters for the cards
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

                layoutParams.setMargins(5, 10, 5, 10);
                templateData = documentSnapshot.getData();
                Objects.requireNonNull(documentSnapshot.getData()).keySet().forEach(key -> {
                    // create the cardView with the key
                    CardView card = getCard(key, "");
                    card.setOnClickListener(onClick);
                    // put the cardView in the layout
                    llDocumentContainer.addView(card, layoutParams);
                });
            })
            .addOnFailureListener(e -> {

            });
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

    private final View.OnClickListener onClick = (View view) -> {
        if(view instanceof CardView) {
            clearLLDocumentViewer();
            TextView tView = (TextView) ((LinearLayout)((CardView) view).getChildAt(0)).getChildAt(0);
            displayDocument(tView.getText().toString());
        }
    };

    private void clearLLDocumentViewer() {
        llDocumentViewer.removeAllViews();
    }

    private void displayDocument(String templateKey) {
        Map<String, Object> rowData = ((Map<String, Object>) templateData.get(templateKey));
        if (rowData != null) {
            for (Object object:rowData.values()) {
                // create row
                Map<String, Object> cellData = (Map<String, Object>) object;
                for(Object cell: cellData.values()){
                    // create cell
                    String value = cell.toString();
                    TextView tView = new TextView(getApplicationContext());
                    tView.setText(value);
                    llDocumentViewer.addView(tView);
                }
            }
        }
    }

    private void displayToastWithMessage(String message, int length){
        Toast.makeText(getApplicationContext(),message,length).show();
    }
}