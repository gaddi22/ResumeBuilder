package com.swoopsoft.resumebuilder.ReusableLayouts;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.text.Layout;
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
    private String dataName;
    private DataObject obj;

    public DataRow(DataActivity parentActivity, DataObject data, String dataName, Context context){


        this.dataName = dataName;
        this.obj = data;
        type = data.getType();
        this.context = context;
        //build card view
        container = new CardView(context);
        int padding = (int) parentActivity.getResources().getDimension(R.dimen.data_card_content_padding);
        float radius = (float) parentActivity.getResources().getDimension(R.dimen.data_card_corner_radius);

        container.setContentPadding(padding,padding,padding,padding);
        container.setRadius(radius);
        container.setUseCompatPadding(true);
        container.setMinimumHeight(WRAP_CONTENT);
        container.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        //build row linear layout
        row = new LinearLayout(context);
        row.setLayoutMode(LinearLayout.HORIZONTAL);
        row.setMinimumHeight(WRAP_CONTENT);
        row.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        //put row into card
        container.addView(row);

        //build column params
        LinearLayout.LayoutParams leftColumnParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT,
                //Float.valueOf(parentActivity.getResources().getString(R.string.data_key_weight))
                1
        );

        LinearLayout.LayoutParams centerColumnParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT,
                //Float.valueOf(parentActivity.getResources().getString(R.string.data_values_weight))
                1
        );
        LinearLayout.LayoutParams rightColumnParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                WRAP_CONTENT,
                //Float.valueOf(parentActivity.getResources().getString(R.string.data_button_weight))
                1
        );

        //build data key name
        key = new TextView(context);
        row.addView(key);
        key.setLayoutParams(leftColumnParam);
        key.setText(dataName);
        key.setTextAppearance(android.R.style.TextAppearance_Medium);

        //build values layout
        valLayout = new LinearLayout(context);
        row.addView(valLayout);
        valLayout.setBackgroundColor(0xFFEEEEEE);
        valLayout.setLayoutParams(centerColumnParam);
        valLayout.setLayoutMode(LinearLayout.VERTICAL);
        values = new ArrayList<>();
        buildValues(valLayout, context, data, centerColumnParam);

        //build button
        remove = new Button(context);
        row.addView(remove);
        remove.setLayoutParams(rightColumnParam);
        remove.setOnClickListener(parentActivity);
        remove.setText(parentActivity.getResources().getString(R.string.remove_item));
        remove.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        remove.setTextColor(Color.WHITE);
        remove.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);


    }

    private void buildValues(LinearLayout linearLayout, Context context, DataObject data, LinearLayout.LayoutParams outerParams){
        if(TextUtils.equals(data.getType(), "Text")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    WRAP_CONTENT
            );

            TextView textData = new TextView(context);
            linearLayout.addView(textData);
            textData.setLayoutParams(params);
            textData.setTextAppearance(android.R.style.TextAppearance_Medium);
            values.add(textData);
            try {
                textData.setText((String) data.getValue());
            }
            catch(Exception e){
                Log.d("DataRow","Invalid data from Text Object, datatype: " + data.getValue().getClass().getName());
            }

        }
        if(TextUtils.equals(data.getType(), "Image")){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    WRAP_CONTENT
            );

            ImageView image = new ImageView(context);
            image.setLayoutParams(params);
            linearLayout.addView(image);
            image.setMaxHeight(linearLayout.getHeight());
            Picasso.get().load((String)data.value).into(image);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            values.add(image);

            outerParams.height = 300;
            linearLayout.setLayoutParams(outerParams);
        }
    }

    public CardView getView(){
        //Returns the row so it can be added to a linear layout
        return container;
    }

    public Button getButton(){
        //returns button, used for parent's click detection
        return remove;
    }

    public String getType(){
        return type;
    }

    public String getDataName(){ return dataName; }

    public DataObject getObj(){ return obj; }
}
