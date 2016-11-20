package com.ldceconnect.ldcecommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.identicon.HashGeneratorInterface;
import com.ldceconnect.ldcecommunity.identicon.IconGenerator;
import com.ldceconnect.ldcecommunity.identicon.IdenticonGenerator;
import com.ldceconnect.ldcecommunity.identicon.MessageDigestHashGenerator;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.DummyDataModel;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.CircleTransform;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.PostViewHolder;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;

import org.w3c.dom.Text;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Nevil on 14-04-2015.
 */
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.CardViewHolder>{
    public ArrayList<?> dataModels;

    public CardViewHolder cardViewHolder;

    ApplicationModel.CardLayout cardType;
    public int itemPosition;
    AppCompatActivity mContext;
    OnItemClickListener clickListener;
    ProgressBar mProgressView;
    boolean showProgress = false;

    public SimpleRecyclerAdapter(AppCompatActivity context) {
        this.mContext = context;
    }

    public SimpleRecyclerAdapter(AppCompatActivity context,ArrayList<?> dataModels, ApplicationModel.CardLayout cardType) {
        this.dataModels = dataModels;
        this.cardType = cardType;
        this.mContext = context;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        ApplicationModel.CardLayout cardLayout = this.cardType;

        int recyclerId = viewGroup.getId();

        /* Note: Do not check active tabs to assign layout here because cardview holders
                 also gets created for card items which are not in active tab*/
        if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY &&
                recyclerId == R.id.dummyfrag_department_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DEPARTMENT;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_departments_list_item, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY &&
                recyclerId == R.id.dummyfrag_group_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_GROUP;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_groups_list_item, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY &&
                recyclerId == R.id.dummyfrag_discussion_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DISCUSSION;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_discussions_list_item, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_DEPARTMENT_DETAILS)
        {
            cardLayout = ApplicationModel.CardLayout.CARD_STUDENT;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_students_list_item_small, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_STUDENT_PROFILE &&
                recyclerId == R.id.dummyfrag_department_scrollableview  )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DEPARTMENT;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_departments_list_item, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_MY_CONTENT &&
                recyclerId == R.id.dummyfrag_group_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_GROUP;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_groups_list_item, viewGroup, false);
        }
        else if(recyclerId == R.id.dummyfrag_group_scrollableview_big )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_GROUP_BIG;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_groups_list_item_big, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_MY_CONTENT &&
                recyclerId == R.id.dummyfrag_discussion_scrollableview  )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DISCUSSION_SMALL;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_discussions_list_item_small, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_CREATE_GROUP)
        {
            cardLayout = ApplicationModel.CardLayout.CARD_STUDENT_NO_IMAGE;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_students_list_item_no_image, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_THREAD_DETAILS)
        {
            cardLayout = ApplicationModel.CardLayout.CARD_POST_MESSAGE;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_post_message, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_GROUP_DETAILS &&
                recyclerId == R.id.dummyfrag_department_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DEPARTMENT;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_departments_list_item, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_GROUP_DETAILS &&
                recyclerId == R.id.dummyfrag_student_scrollableview  )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_STUDENT;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_students_list_item_small, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_GROUP_DETAILS &&
                recyclerId == R.id.dummyfrag_discussion_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DISCUSSION;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_discussions_list_item, viewGroup, false);
        }
        else if(ApplicationModel.AppEventModel.getActiveScreen() == ApplicationModel.Screen.SCREEN_STARRED_THREADS &&
                recyclerId == R.id.dummyfrag_discussion_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_DISCUSSION;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_discussions_list_item, viewGroup, false);
        }
        else if(recyclerId == R.id.dummyfrag_calendar_scrollableview )
        {
            cardLayout = ApplicationModel.CardLayout.CARD_CALENDARITEM;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_calendaritem, viewGroup, false);
        }
        else
        {
            //String msg = "No View Selected for Card View Recycler Adapter.";
            //Toast.makeText(viewGroup.getContext(), msg, Toast.LENGTH_SHORT).show();
            cardLayout = ApplicationModel.CardLayout.CARD_ERROR;
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_error, viewGroup, false);
        }

        CardViewHolder viewHolder = new CardViewHolder(view,cardLayout);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {

        itemPosition = i;

        if( dataModels.size() > i && dataModels.get(i) != null ) {

                mProgressView = cardViewHolder.progressBar;

                if(cardViewHolder.progressBar != null)
                    cardViewHolder.progressBar.setVisibility(View.GONE);

                if(cardViewHolder.title != null)
                    cardViewHolder.title.setVisibility(View.VISIBLE);
                if( cardViewHolder.subTitle != null )
                    cardViewHolder.subTitle.setVisibility(View.VISIBLE);
                if( cardViewHolder.itemImage != null)
                    cardViewHolder.itemImage.setVisibility(View.VISIBLE);
                if( cardViewHolder.upVoteImage != null)
                    cardViewHolder.upVoteImage.setVisibility(View.VISIBLE);
                if( cardViewHolder.downVoteImage != null)
                    cardViewHolder.downVoteImage.setVisibility(View.VISIBLE);
                if( cardViewHolder.starCount!= null)
                    cardViewHolder.starCount.setVisibility(View.VISIBLE);
                if( cardViewHolder.starImage != null)
                    cardViewHolder.starImage.setVisibility(View.VISIBLE);
                if( cardViewHolder.commentsCount != null)
                    cardViewHolder.commentsCount.setVisibility(View.VISIBLE);
                if( cardViewHolder.commentsImage != null)
                    cardViewHolder.commentsImage.setVisibility(View.VISIBLE);
                if( cardViewHolder.numThreadsSubtitle != null)
                    cardViewHolder.numThreadsSubtitle.setVisibility(View.VISIBLE);
                if( cardViewHolder.numMembersIcon != null)
                    cardViewHolder.numMembersIcon.setVisibility(View.VISIBLE);
                if( cardViewHolder.numPostsIcon != null)
                    cardViewHolder.numPostsIcon.setVisibility(View.VISIBLE);
                if( cardViewHolder.mDraweeView!= null)
                    cardViewHolder.mDraweeView.setVisibility(View.VISIBLE);


                showProgress = false;

            switch (cardType) {
                case CARD_DEPARTMENT:
                    if ( dataModels != null && dataModels.size() > 0 ) {
                        if( dataModels.get(0) != null && dataModels.get(0).getClass() == Department.class) {
                            Department d = (Department) dataModels.get(i);
                            if( d != null) {
                                cardViewHolder.title.setText(d.name);
                                cardViewHolder.subTitle.setText(d.nummembers + " " + "Students");
                                LoadDataModel ldm = LoadDataModel.getInstance();

                                if (cardViewHolder.mDraweeView != null) {
                                    Glide.with(mContext).load(d.deptimageurl).transform(new CircleTransform(mContext)).into(cardViewHolder.mDraweeView);
                                }
                            }
                        }
                    }
                    break;
                case CARD_DISCUSSION:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if( dataModels.get(0) != null && dataModels.get(0).getClass() == Discussion.class) {
                            Discussion d = (Discussion) dataModels.get(i);
                            if( d != null) {
                                String title = d.title;
                                if (d.title.length() > 80)
                                    title = d.title.substring(0, 80) + "...";
                                cardViewHolder.title.setText(title);
                                if (d.parentgroupname != null && d.parentgroupname != "null")
                                    cardViewHolder.subTitle.setText(d.parentgroupname);
                                else
                                    cardViewHolder.subTitle.setText("");

                                Class c = mContext.getClass();

                                if (cardViewHolder.starImage != null && c == ExploreCommunity.class) {

                                    if (LoadDataModel.starredThreadsList != null) {
                                        if (ParserUtils.findStringInList(LoadDataModel.starredThreadsList, d.id) >= 0) {
                                            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ratingbar_star_on_green);
                                            ImageView im = cardViewHolder.starImage;
                                            im.setImageBitmap(b);
                                        } else {
                                            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.follow_icon);
                                            ImageView im = cardViewHolder.starImage;
                                            im.setImageBitmap(b);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case CARD_GROUP:
                case CARD_GROUP_BIG:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if( dataModels.get(0) != null && dataModels.get(0).getClass() == Group.class) {
                            Group g = (Group) dataModels.get(i);
                            if( g != null ) {
                                cardViewHolder.title.setText(g.name);
                                if (cardViewHolder.subTitle != null)
                                    cardViewHolder.subTitle.setText(g.numthreads + " Posts");
                                if (cardViewHolder.numThreadsSubtitle != null)
                                    cardViewHolder.numThreadsSubtitle.setText(g.nummembers + " Members");

                                if (cardViewHolder.mDraweeView != null) {
                                    Glide.with(mContext).load(g.groupimageurl).transform(new CircleTransform(mContext)).into(cardViewHolder.mDraweeView);
                                }
                            }
                        }
                    }
                    break;
                case CARD_POST_MESSAGE:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if( dataModels.get(0) != null && dataModels.get(0).getClass() == Post.class) {
                            Post p = (Post) dataModels.get(i);
                            cardViewHolder.title.setText(p.text);
                            cardViewHolder.subTitle.setText(p.postownername);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                            String month = dateFormat.format(p.idate);

                            DateFormat format = new SimpleDateFormat("yy");
                            String year = format.format(p.idate);

                            DateFormat format1 = new SimpleDateFormat("HH:mm");
                            String time = format1.format(p.idate);

                            DateFormat format2 = new SimpleDateFormat("dd");
                            String date = format2.format(p.idate);

                            cardViewHolder.dateTitle.setText(date + " " + month + " " + year);
                            cardViewHolder.timeTitle.setText(time);
                        }
                    }
                    break;
                case CARD_STUDENT:
                case CARD_STUDENT_NO_IMAGE:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if( dataModels.get(0) != null && dataModels.get(0).getClass() == User.class) {
                            User u = (User) dataModels.get(i);
                            if( u != null) {
                                cardViewHolder.title.setText(u.fname + " " + u.lname);

                                if (cardViewHolder.mDraweeView != null) {
                                    Glide.with(mContext).load(u.profilePictureUrl).transform(new CircleTransform(mContext)).into(cardViewHolder.mDraweeView);
                                }
                            }
                        }
                    }
                    break;
                case CARD_NONE:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if(dataModels.get(0) != null && dataModels.get(0).getClass() == String.class) {
                            String s = (String) dataModels.get(i);
                            cardViewHolder.title.setText(s);
                        }
                    }
                    break;
                case CARD_ERROR:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if(dataModels.get(0) != null && dataModels.get(0).getClass() == String.class) {
                            String s = (String) dataModels.get(i);
                            cardViewHolder.title.setText("Unexpected Error");
                            cardViewHolder.subTitle.setText("Please refresh the list");
                            cardViewHolder.itemImage.setVisibility(View.GONE);
                        }
                    }
                    break;
                case CARD_CALENDARITEM:
                    if (dataModels != null && dataModels.size() > 0 ) {
                        if (dataModels.get(0) != null && dataModels.get(0).getClass() == Discussion.class) {
                            Discussion d = (Discussion) dataModels.get(i);
                            if( d != null) {
                                String title = d.title;
                                if (d.title.length() > 80)
                                    title = d.title.substring(0, 80) + "...";
                                cardViewHolder.title.setText(title);
                            }
                        }
                    }
                    break;

            }

        }
        else if(dataModels.get(i) == null){

            // datamodel is null for showing progress card
            if(cardViewHolder.title != null)
                cardViewHolder.title.setVisibility(View.GONE);
            if( cardViewHolder.subTitle != null )
                cardViewHolder.subTitle.setVisibility(View.GONE);
            if( cardViewHolder.itemImage != null)
                cardViewHolder.itemImage.setVisibility(View.GONE);
            if( cardViewHolder.upVoteImage != null)
                cardViewHolder.upVoteImage.setVisibility(View.GONE);
            if( cardViewHolder.downVoteImage != null)
                cardViewHolder.downVoteImage.setVisibility(View.GONE);
            if( cardViewHolder.starCount!= null)
                cardViewHolder.starCount.setVisibility(View.GONE);
            if( cardViewHolder.starImage != null)
                cardViewHolder.starImage.setVisibility(View.GONE);
            if( cardViewHolder.commentsCount != null)
                cardViewHolder.commentsCount.setVisibility(View.GONE);
            if( cardViewHolder.commentsImage != null)
                cardViewHolder.commentsImage.setVisibility(View.GONE);
            if( cardViewHolder.numThreadsSubtitle != null)
                cardViewHolder.numThreadsSubtitle.setVisibility(View.GONE);
            if( cardViewHolder.numMembersIcon != null)
                cardViewHolder.numMembersIcon.setVisibility(View.GONE);
            if( cardViewHolder.numPostsIcon != null)
                cardViewHolder.numPostsIcon.setVisibility(View.GONE);
            if( cardViewHolder.mDraweeView!= null)
                cardViewHolder.mDraweeView.setVisibility(View.GONE);

            showProgress = true;

            if(cardViewHolder.progressBar != null) {
                mProgressView = cardViewHolder.progressBar;
                cardViewHolder.progressBar.setVisibility(View.VISIBLE);
                cardViewHolder.progressBar.animate().setDuration(3000).alpha(
                        showProgress  ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(showProgress  ? View.VISIBLE : View.GONE);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if( dataModels != null) {
            int itemCount = dataModels.size();
            return itemCount;
        }
        else
            return 0;
    }


    class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView title;
        TextView subTitle;
        ImageView itemImage;
        TextView dateTitle;
        TextView timeTitle;
        ProgressBar progressBar;

        ImageView upVoteImage;
        ImageView downVoteImage;
        ImageView starImage;
        TextView starCount;

        ImageView commentsImage;
        TextView commentsCount;

        TextView numThreadsSubtitle;

        View thisView;

        ImageView deleteButton;
        ImageView editButton;
        EditText editPost ;
        TextView textPost ;
        ImageView okButton ;
        ImageView cancelButton;

        ImageView numPostsIcon;
        ImageView numMembersIcon;

        ImageView mDraweeView;

        public CardViewHolder(View itemView, ApplicationModel.CardLayout cardLayout) {
            super(itemView);


            // progress bar
            progressBar = (ProgressBar) itemView.findViewById(R.id.list_progress);

            if( itemView == null)
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.error_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.error_listitem_name);
                title.setText("Invalid Card view selected");
                subTitle = (TextView) itemView.findViewById(R.id.error_listitem_subname);
                subTitle.setText("No View Selected for Card View Recycler Adapter.");
                return;
            }
            else
            {
                thisView = itemView;
            }
            // Department Card is only one type so from wherever it is included it has one card lay out only
            if( cardLayout == ApplicationModel.CardLayout.CARD_DEPARTMENT )
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.department_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.department_listeitem_name);
                subTitle = (TextView) itemView.findViewById(R.id.department_listeitem_subname);
                //itemImage = (ImageView) itemView.findViewById(R.id.department_listeitem_imagecontainer);
                mDraweeView = (ImageView) itemView.findViewById(R.id.department_listeitem_imagecontainer);

                cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = CardViewHolder.this.getAdapterPosition();
                        if (position >= 0) {

                            Department d = (Department) dataModels.get(position);

                            LoadDataModel.loadDepartmentId = d.id;
                            LoadDataModel.loadDepartmentTitle = d.name;
                            LoadDataModel.loadDepartmentNumMembers = d.nummembers;

                            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                LoadDataModel ldm = LoadDataModel.getInstance();
                                ldm.loadedDepartmentMembers.clear();
                                ldm.loadedDepartmentForDetail.clear();
                                new LoadDataAsync(mContext, LoadDataModel.LoadContext.LOAD_DEPARTMENT_DETAILS, null).execute();
                            }
                        } else {
                            //Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            // Group Card is only one type so from wherever it is included it has one card lay out only
            else if( cardLayout == ApplicationModel.CardLayout.CARD_GROUP ||  cardLayout == ApplicationModel.CardLayout.CARD_GROUP_BIG)
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.group_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.group_listitem_name);
                subTitle = (TextView) itemView.findViewById(R.id.group_listitem_subname);
                //itemImage = (ImageView) itemView.findViewById(R.id.group_listitem_imagecontainer);
                numThreadsSubtitle = (TextView) itemView.findViewById((R.id.group_listitem_subitem));
                numPostsIcon = (ImageView) itemView.findViewById(R.id.group_listitem_posts_icon);
                numMembersIcon = (ImageView) itemView.findViewById(R.id.group_listitem_persons_icon);
                mDraweeView = (ImageView) itemView.findViewById(R.id.group_listitem_imagecontainer);

                if( cardLayout != ApplicationModel.CardLayout.CARD_GROUP_BIG) {
                    cardItemLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = CardViewHolder.this.getAdapterPosition();
                            if (position >= 0) {
                                Group g = (Group) dataModels.get(position);
                                ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_GROUP);
                                ApplicationModel.AppEventModel.setGroupTabClickId(position);
                                LoadDataModel.loadGroupId = g.id;
                                LoadDataModel.loadGroupName = g.name;
                                ApplicationModel.loadedGroupIndex = position;

                                if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                    LoadDataModel ldm = LoadDataModel.getInstance();
                                    ldm.loadedGroupMembers.clear();
                                    ldm.loadedGroupThreads.clear();
                                    ldm.loadedGroupForDetail.clear();
                                    new LoadDataAsync(mContext, LoadDataModel.LoadContext.LOAD_GROUP_DETAILS, null).execute();
                                }

                            } else {
                                //Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            // There are two type of discussion card lay outs and depend on which screen , which card lay out to use
            else if( cardLayout == ApplicationModel.CardLayout.CARD_DISCUSSION /*&&
                    ApplicationModel.AppEventModel.getActiveScreen()== ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY*/ )
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.discussion_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.discussion_listitemname);
                subTitle = (TextView) itemView.findViewById(R.id.discussion_listitemparentgroup);
                starImage = (ImageView) itemView.findViewById(R.id.discussion_cardlist_item_star);

                ApplicationUtils.disableTouchTheft(starImage);

                starImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoadDataModel ldm = LoadDataModel.getInstance();

                        //Nevil Very important,getAdapterPosition() very imp function
                        Discussion d = (Discussion) dataModels.get(CardViewHolder.this.getAdapterPosition());

                        ArrayList<Object> discussions = new ArrayList<>();
                        discussions.add(d);

                        if (mContext.getClass() == ExploreCommunity.class) {
                            if (ParserUtils.findStringInList(LoadDataModel.starredThreadsList, d.id) >= 0) {
                                LoadDataModel.starredThreadsList.remove(d.id);
                                Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ratingbar_star_off_default);
                                ImageView im = (ImageView) v;
                                im.setImageBitmap(b);
                                new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_REMOVE_STAR_THREAD, discussions).execute();
                            } else {
                                LoadDataModel.starredThreadsList.add(d.id);
                                Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ratingbar_star_on_green);
                                ImageView im = (ImageView) v;
                                im.setImageBitmap(b);
                                new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_STAR_THREAD, discussions).execute();
                            }
                        } else if (mContext.getClass() == GroupViewActivity.class) {
                            if (ParserUtils.findStringInList(LoadDataModel.starredThreadsList, d.id) >= 0) {
                                LoadDataModel.starredThreadsList.remove(d.id);
                                Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ratingbar_star_off_default);
                                ImageView im = (ImageView) v;
                                im.setImageBitmap(b);
                                new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_REMOVE_STAR_THREAD, discussions).execute();
                            } else {
                                LoadDataModel.starredThreadsList.add(d.id);
                                Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ratingbar_star_on_green);
                                ImageView im = (ImageView) v;
                                im.setImageBitmap(b);
                                new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_STAR_THREAD, discussions).execute();
                            }
                        }
                    }
                });

                cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = CardViewHolder.this.getAdapterPosition();
                        if (position >= 0 ) {
                            ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DISCUSSION);
                            ApplicationModel.AppEventModel.setDiscussionTabClickId(position);

                            //recyclerView.getChildViewHolder()

                            LoadDataModel ldm = LoadDataModel.getInstance();

                            LoadDataModel.threadTouchItemId = position;
                            Discussion d = (Discussion) dataModels.get(position);

                            LoadDataModel.loadThreadId = d.id;
                            LoadDataModel.loadThreadParentGroup = d.parentgroup;
                            LoadDataModel.loadThreadTitle = d.title;
                            LoadDataModel.loadThreadDescription = d.description;
                            LoadDataModel.loadThreadParentGroupName = d.parentgroupname;
                            LoadDataModel.loadThreadVisibilityOpen = d.ispublic;

                            if (LoadDataModel.isCurrentDataLoadFinished == true) {

                                ldm.loadedThreadPosts.clear();
                                ldm.loadedThreadForDetail.clear();
                                new LoadDataAsync(mContext, LoadDataModel.LoadContext.LOAD_THREAD_DETAILS, null).execute();
                            }

                        } else {
                            //Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if( cardLayout == ApplicationModel.CardLayout.CARD_DISCUSSION_SMALL)
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.discussion_cardlist_item_small);
                title = (TextView) itemView.findViewById(R.id.discussion_listitem_name_small);
                subTitle = (TextView) itemView.findViewById(R.id.discussion_listitem_subname_small);
                //itemImage = (ImageView) itemView.findViewById(R.id.discussion_listitem_imagecontainer_small);
            }
            else if( cardLayout == ApplicationModel.CardLayout.CARD_CALENDARITEM)
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.calendar_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.calendar_listitemname);
                subTitle = (TextView) itemView.findViewById(R.id.calendar_listitemsubname);
                //itemImage = (ImageView) itemView.findViewById(R.id.discussion_listitem_imagecontainer_small);
            }
            else if( cardLayout == ApplicationModel.CardLayout.CARD_STUDENT )
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.student_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.student_listitem_name);
                //subTitle = (TextView) itemView.findViewById(R.id.student_listitem_subname);
                //itemImage = (ImageView) itemView.findViewById(R.id.student_listitem_imagecontainer);
                mDraweeView = ( ImageView) itemView.findViewById(R.id.student_listitem_imagecontainer);

                cardItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        LoadDataModel ldm = LoadDataModel.getInstance();
                        User u = (User) dataModels.get(CardViewHolder.this.getAdapterPosition());

                        LoadDataModel.loadUserId = u.userid;
                        LoadDataModel.loadUserName = u.fname + " " + u.lname;

                        boolean isUserAdminOfGroup = ApplicationUtils.isUserAdminOfGroup(ldm.loadedGroupForDetail, LoadDataModel.loadGroupId);
                        if( mContext.getClass() == GroupViewActivity.class && isUserAdminOfGroup) {
                            new MaterialDialog.Builder(mContext)
                                    .items(R.array.group_user_options)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            if(which == 0) {
                                                RelativeLayout rellayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.edit_post_dialog, null);
                                                EditText textEdit = (EditText)rellayout.findViewById(R.id.edit_post_text);
                                                textEdit.setText(title.getText());

                                                new AlertDialog.Builder(mContext)
                                                        .setMessage("Remove From Group ?")
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                UserModel um = UserModel.getInstance();
                                                                ArrayList<Object> data = new ArrayList<>();
                                                                data.add(LoadDataModel.loadUserId);
                                                                data.add(LoadDataModel.loadGroupId);
                                                                new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_REMOVE_GROUPMEMBERSHIP, data).execute();

                                                            }
                                                        })
                                                        .setNegativeButton(android.R.string.no, null).show();
                                            }
                                        }
                                    }).show();

                        }
                        return true;
                    }

                });

                cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = CardViewHolder.this.getAdapterPosition();

                        if (position >= 0) {

                                User g = (User) dataModels.get(position);
                                String userName = g.fname + " " + g.lname;

                                LoadDataModel.loadUserId = g.userid;
                                LoadDataModel.loadUserName = userName;

                                if (LoadDataModel.isCurrentDataLoadFinished == true) {
                                    LoadDataModel ldm = LoadDataModel.getInstance();

                                    ldm.loadedUserGroups.clear();
                                    ldm.loadedUserThreads.clear();
                                    ldm.loadedUserForDetail.clear();

                                    new LoadDataAsync(mContext, LoadDataModel.LoadContext.LOAD_USER_DETAILS, null).execute();
                                }

                            } else {
                                //Toast.makeText(getContext(), "Undefined Click!", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
            }
            else if( cardLayout == ApplicationModel.CardLayout.CARD_STUDENT_NO_IMAGE )
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.student_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.student_listitem_name);
                //subTitle = (TextView) itemView.findViewById(R.id.student_listitem_subname);
                itemImage = null;
            }
            else if( cardLayout == ApplicationModel.CardLayout.CARD_POST_MESSAGE )
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.post_message_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.post_message_listitem_text);
                subTitle = (TextView) itemView.findViewById(R.id.post_message_listitem_owner);
                dateTitle = (TextView) itemView.findViewById(R.id.post_message_listitem_date);
                timeTitle = (TextView) itemView.findViewById(R.id.post_message_listitem_time);

                deleteButton = (ImageView) itemView.findViewById(R.id.post_message_listitem_deletebutton);
                editButton = (ImageView) itemView.findViewById(R.id.post_message_listitem_editbutton);
                editPost = (EditText) itemView.findViewById(R.id.post_message_listitem_edit_text);
                textPost = (TextView) itemView.findViewById(R.id.post_message_listitem_text);
                okButton = (ImageView) itemView.findViewById(R.id.post_message_listitem_okbutton);
                cancelButton = (ImageView) itemView.findViewById(R.id.post_message_listitem_cancelbutton);

                cardItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        LoadDataModel ldm = LoadDataModel.getInstance();
                        Post p = (Post) dataModels.get(CardViewHolder.this.getAdapterPosition());

                        LoadDataModel.loadPostId = p.id;
                        LoadDataModel.loadPostText = p.text;

                        Boolean isOwner = ApplicationUtils.isUserOwnerOfPost(ldm.loadedThreadPosts, p.id);

                        if( isOwner) {
                            new MaterialDialog.Builder(mContext)
                                    .items(R.array.post_options)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            if( which == 0)
                                            {
                                                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("copied message", title.getText());
                                                clipboard.setPrimaryClip(clip);
                                            }
                                            else if (which == 1) {
                                                RelativeLayout rellayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.edit_post_dialog, null);
                                                EditText textEdit = (EditText)rellayout.findViewById(R.id.edit_post_text);
                                                textEdit.setText(title.getText());

                                                new MaterialDialog.Builder(mContext)
                                                        .title("Edit")
                                                        .positiveText("Ok")
                                                        .negativeText("Cancel")
                                                        .customView(rellayout, true)
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(MaterialDialog dialog, DialogAction which) {

                                                                            View v = dialog.getCustomView();
                                                                            EditText et = (EditText)v.findViewById(R.id.edit_post_text);
                                                                            String text = et.getText().toString();

                                                                            ArrayList<Object> ids = new ArrayList<>();
                                                                            ids.add(LoadDataModel.loadPostId);
                                                                            ids.add(text);
                                                                            new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_UPDATE_POST_TEXT, ids).execute();
                                                                        }
                                                                    }
                                                        ).show();


                                            } else if (which == 2) {
                                                ArrayList<Object> ids = new ArrayList<>();
                                                ids.add(LoadDataModel.loadPostId);
                                                new UploadDataAsync(mContext, LoadDataModel.UploadContext.UPLOAD_DELETE_POST, ids).execute();
                                            }
                                        }
                                    }).show();

                        }
                        return true;
                    }

                });
            }
            else if( cardLayout == ApplicationModel.CardLayout.CARD_PROGRESS )
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.progressbar_cardlist_item);
            }
            else
            {
                cardItemLayout = (CardView) itemView.findViewById(R.id.error_cardlist_item);
                title = (TextView) itemView.findViewById(R.id.error_listitem_name);
                subTitle = (TextView) itemView.findViewById(R.id.error_listitem_subname);
                itemImage = (ImageView) itemView.findViewById(R.id.error_listitem_imagecontainer);
            }
        }
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }


    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
