package com.swoopsoft.resumebuilder.ReusableLayouts;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;
import com.swoopsoft.resumebuilder.DataActivity;
import com.swoopsoft.resumebuilder.R;
import com.swoopsoft.resumebuilder.data.DataObject;

import java.util.ArrayList;

public class DataRow {

    private LinearLayout row, valLayout;
    private CardView container;
    private TextView key;
    private ArrayList<View> values;
    private Button remove;
    private Context context;
    private String type;

    public DataRow(DataActivity parentActivity, DataObject data, String dataName, Context context){

        type = data.getType();
        this.context = context;
        //build card view
        container = new CardView(context);
        int padding = (int) parentActivity.getResources().getDimension(R.dimen.data_card_content_padding);
        float radius = (float) parentActivity.getResources().getDimension(R.dimen.data_card_corner_radius);

        container.setContentPadding(padding,padding,padding,padding);
        container.setRadius(radius);
        container.setUseCompatPadding(true);
        container.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        container.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        //build row linear layout
        row = new LinearLayout(context);
        row.setLayoutMode(LinearLayout.HORIZONTAL);
        row.setMinimumHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        //build column params
        LinearLayout.LayoutParams leftColumnParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                parentActivity.getResources().getDimension(R.dimen.data_key_weight)
        );
        LinearLayout.LayoutParams centerColumnParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                parentActivity.getResources().getDimension(R.dimen.data_values_weight)
        );
        LinearLayout.LayoutParams rightColumnParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                parentActivity.getResources().getDimension(R.dimen.data_button_weight)
        );

        //build data key name
        key = new TextView(context);
        key.setLayoutParams(leftColumnParam);
        key.setText(dataName);
        key.setTextAppearance(android.R.style.TextAppearance_Medium);

        //build values layout
        valLayout = new LinearLayout(context);
        valLayout.setLayoutParams(centerColumnParam);
        valLayout.setLayoutMode(LinearLayout.VERTICAL);
        buildValues(valLayout, context, data);

        //build button
        remove = new Button(context);
        remove.setLayoutParams(rightColumnParam);
        remove.setOnClickListener(parentActivity);

        //add views to row
        row.addView(key);
        row.addView(valLayout);
        row.addView(remove);
    }

    private void buildValues(LinearLayout linearLayout, Context context, DataObject data){
        if(TextUtils.equals(data.getType(), "Text")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            TextView textData = new TextView(context);
            textData.setLayoutParams(params);
            values.add(textData);
            try {
                textData.setText((String) data.getValue());
            }
            catch(Exception e){
                Log.d("DataRow","Invalid data from Text Object, datatype: " + data.getValue().getClass().getName());
            }
            linearLayout.addView(textData);
        }
        if(TextUtils.equals(data.getType(), "Image")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            ImageView image = new ImageView(context);
            image.setLayoutParams(params);
            Picasso.get().load((String)data.value).into(image);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            values.add(image);
            linearLayout.addView(image);

        }
    }

    public CardView getView(){
        //Returns the row so it can be added to a linear layout
        return container;
    }

    public Button getButton(){
        //returns button, used for pareant's click detection
        return remove;
    }

    public String getType(){
        return type;
    }
}
