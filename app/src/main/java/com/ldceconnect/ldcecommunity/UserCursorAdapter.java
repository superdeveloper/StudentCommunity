package com.ldceconnect.ldcecommunity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.ldceconnect.ldcecommunity.model.LoadDataModel;

/**
 * Created by Nevil on 1/6/2016.
 */
public class UserCursorAdapter extends SimpleCursorAdapter {

    private LayoutInflater mInflater;
    private int viewResourceId;
    private int mSelectedPosition;
    private Context context;
    private int layout;
    private CheckBox resultCheckBox;
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return super.newView(context,cursor,parent);
    }


    public UserCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.context = context;
        this.layout = layout;
    }


    @Override
    public void bindView(View v, Context context, Cursor c) {
        super.bindView(v,context,c);
        resultCheckBox = (CheckBox) v.findViewById(R.id.search_result_add_check);

        if( c != null ) {
            int nameCol = c.getColumnIndex("name");
            String name = c.getString(nameCol);

            TextView name_text = (TextView) v.findViewById(R.id.search_result_with_add_button);


            if (name_text != null) {
                name_text.setText(name);
            }

            if( resultCheckBox != null)
            {
                LoadDataModel ldm = LoadDataModel.getInstance();
                int selected = ldm.loadSearchSelectedUsers.size();
                int loaded = ldm.loadSearchUsers.size();

                if(c.getPosition() < selected)
                {
                    resultCheckBox.setVisibility(View.VISIBLE);
                    resultCheckBox.setChecked(true);
                }
                else
                {
                    resultCheckBox.setVisibility(View.INVISIBLE);
                    resultCheckBox.setChecked(false);
                }
            }
        }

    }


    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
        resultCheckBox.setChecked(true);
        notifyDataSetChanged();
    }

}
