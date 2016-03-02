package com.ldceconnect.ldcecommunity.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.model.Discussion;

/**
 * Created by TDuffy on 4/16/2015.
 */
public class PostViewHolder {

    public int position;

    CardView cardItemLayout;
    TextView title;
    TextView subTitle;
    ImageView itemImage;
    ProgressBar progressBar;

    ImageView upVoteImage;
    ImageView downVoteImage;
    ImageView starImage;
    Context mContext;
    View thisView;


    public PostViewHolder(Context context, int position, View itemView) {
        mContext = context;
        this.position = position;

        //Typeface font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");

        cardItemLayout = (CardView) itemView.findViewById(R.id.discussion_cardlist_item);
        title = (TextView) itemView.findViewById(R.id.discussion_listitemname);
        subTitle = (TextView) itemView.findViewById(R.id.discussion_listitemparentgroup);
        starImage = (ImageView) itemView.findViewById(R.id.discussion_cardlist_item_star);

    }
    
    public void build(final Context context, final Discussion post) {

        //ApplicationUtils.disableTouchTheft(starImage);
        title.setText(post.title);
        subTitle.setText(post.parentgroupname);

        starImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //v.getParent();
                Toast.makeText(mContext, "Clicked Star", Toast.LENGTH_SHORT).show();
                //LoadDataModel.detachCardItemOnTouchListener = true;
                //thisView.getParent().requestDisallowInterceptTouchEvent(true);
            }
        });

    }
}