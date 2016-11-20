package com.ldceconnect.ldcecommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.PostMessageFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DiscussionViewActivity extends DrawerActivity {

    ActionBarDrawerToggle mDrawerToggle;
    public TextView threadTitle;
    public TextView threadDescription;
    EditText threadTitleEdit;
    EditText threadDescriptionEdit;
    ImageView titleOkButton;
    ImageView titleCancelButton;

    ImageView descriptionOkButton;
    ImageView descriptionCancelButton;

    public ViewPagerAdapter adapter;
    TextView titleTags;
    public EditText discussion_post_reply;
    public ImageView discussion_post_reply_button;
    public TextView discussion_join_group;
    public ImageView discussion_expand_description;
    public ImageView discussion_collapse_description;
    int collapsedheight;

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_view);
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        this.setCallerActivity(getIntent().getStringExtra("caller"));

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_THREAD_DETAILS);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_thread_details);

        /*  Drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDrawerToggle.syncState();

        setupNavHeaderView();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.discussion_container);
        setupViewPager(viewPager);

        discussion_post_reply = (EditText)findViewById(R.id.discussion_post_reply);
        discussion_post_reply_button = (ImageButton) findViewById(R.id.submit_reply_button);
        discussion_join_group = (TextView) findViewById(R.id.discussion_join_group);

        discussion_post_reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!discussion_post_reply.getText().toString().trim().isEmpty()) {

                    ArrayList<Object> posts = new ArrayList<>();

                    Post p = new Post();

                    UserModel um = UserModel.getInstance();

                    p.text = discussion_post_reply.getText().toString();
                    p.threadid = LoadDataModel.loadThreadId;
                    p.postowner = um.user.userid;

                    posts.add(p);

                    new UploadDataAsync(DiscussionViewActivity.this, LoadDataModel.UploadContext.UPLOAD_CREATE_POST, posts).execute();
                }
            }

        });

        LoadDataModel ldm = LoadDataModel.getInstance();
        if(!ApplicationUtils.isUserMemberOfGroup(ldm.loadedAppUserGroups,LoadDataModel.loadThreadParentGroup) &&
                LoadDataModel.loadThreadVisibilityOpen != null &&
                LoadDataModel.loadThreadVisibilityOpen.equals("0"))
        {
            discussion_post_reply.setVisibility(View.GONE);
            discussion_join_group.setVisibility(View.VISIBLE);
            discussion_join_group.setText("Join " + LoadDataModel.loadThreadParentGroupName + " to post");
            discussion_join_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(DiscussionViewActivity.this)
                            .setMessage("Join the Group ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    UserModel um = UserModel.getInstance();
                                    ArrayList<Object> data = new ArrayList<>();
                                    ArrayList<User> members = new ArrayList<User>();
                                    members.add(um.user);
                                    data.add(LoadDataModel.loadThreadParentGroup);
                                    data.add(members);
                                    new UploadDataAsync(DiscussionViewActivity.this, LoadDataModel.UploadContext.UPLOAD_JOIN_GROUP, data).execute();

                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
            discussion_post_reply_button.setVisibility(View.GONE);
        }

        threadTitle = (TextView)findViewById(R.id.discussionview_title);

        if( title != null && !title.isEmpty())
            threadTitle.setText(title);

        threadDescription = (TextView) findViewById(R.id.discussionview_description);

        if(description != null && !description.isEmpty())
            threadDescription.setText(description);
        else
        {
            threadDescription.setVisibility(View.GONE);
        }

        discussion_expand_description = (ImageView)findViewById(R.id.discussionview_expand_button);
        discussion_collapse_description= (ImageView)findViewById(R.id.discussionview_collapse_button);

        discussion_expand_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = threadDescription.getLayoutParams();
                collapsedheight = params.height;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                threadDescription.setLayoutParams(params);
                discussion_collapse_description.setVisibility(View.VISIBLE);
                discussion_expand_description.setVisibility(View.GONE);
            }
        });


        discussion_collapse_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams params = threadDescription.getLayoutParams();
                params.height = collapsedheight ;
                threadDescription.setLayoutParams(params);
                discussion_collapse_description.setVisibility(View.GONE);
                discussion_expand_description.setVisibility(View.VISIBLE);
            }
        });

        threadTitleEdit = (EditText)findViewById(R.id.discussionview_title_edit);
        threadDescriptionEdit = (EditText)findViewById(R.id.discussionview_description_edit);
        titleOkButton = (ImageView)findViewById(R.id.discussionview_title_okbutton);
        titleCancelButton = (ImageView)findViewById(R.id.discussionview_title_cancelbutton);

        descriptionOkButton = (ImageView)findViewById(R.id.discussionview_description_okbutton);
        descriptionCancelButton = (ImageView)findViewById(R.id.discussionview_description_cancelbutton);

        titleTags = (TextView)findViewById(R.id.discussionview_title_tags);
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_THREAD_DETAILS);
    }

    @Override
    public void refreshData()
    {
        LoadDataModel ldm = LoadDataModel.getInstance();
        if (LoadDataModel.isCurrentDataLoadFinished == true) {

            ldm.loadedThreadPosts.clear();
            ldm.loadedThreadForDetail.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_REFRESH_THREAD_DETAILS, null).execute();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void doExitAnim() {
        final View searchPanel = this.findViewById(R.id.discussion_appbar);
        final View parent = this.findViewById(R.id.parent_view);

        searchPanel.animate().translationY(-searchPanel.getHeight()).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable(){
            public void run(){
                searchPanel.setVisibility(View.GONE);
                // do something
            }
        });


        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        int revealRadius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
                +   Math.pow(searchPanel.getHeight(), 2));
        // Animating the radius to 0 produces the contracting effect
        /*Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
                searchPanel.getRight(), searchPanel.getTop(), revealRadius, 0f);
        shrink.setDuration(2000L);
        shrink.setInterpolator(AnimationUtils.loadInterpolator(DiscussionViewActivity.this,
                android.R.interpolator.linear_out_slow_in));
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                searchPanel.setVisibility(View.GONE);
            }
        });
        shrink.start();*/
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void doEnterAnim() {
        final View searchPanel = this.findViewById(R.id.discussion_appbar);
        final View parent = this.findViewById(R.id.parent_view);

        searchPanel.animate().translationY(searchPanel.getHeight()).setInterpolator(new AccelerateInterpolator()).withStartAction(new Runnable(){
            public void run(){
                searchPanel.setVisibility(View.VISIBLE);
                // do something
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_thread_view, menu);

        LoadDataModel ldm = LoadDataModel.getInstance();

        MenuItem itemEditTitle = menu.getItem(0);
        MenuItem itemEditDescription = menu.getItem(1);
        MenuItem itemDelete = menu.getItem(2);

        if(ApplicationUtils.isUserAdminOfThread(ldm.loadedThreadForDetail, LoadDataModel.loadThreadId) )
        {
            itemEditTitle.setVisible(true);
            itemEditDescription.setVisible(true);
            itemDelete.setVisible(true);
        }else {
            itemEditTitle.setVisible(false);
            itemEditDescription.setVisible(false);
            itemDelete.setVisible(false);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh_data) {
            refreshData();
            return true;
        }else if (id == R.id.action_thread_edit_title) {
            threadTitle.setVisibility(View.GONE);
            titleTags.setVisibility(View.GONE);
            threadTitleEdit.setVisibility(View.VISIBLE);
            titleOkButton.setVisibility(View.VISIBLE);
            titleCancelButton.setVisibility(View.VISIBLE);
            threadTitleEdit.setText(threadTitle.getText());
            threadTitleEdit.requestFocus();


            RelativeLayout rl = (RelativeLayout)findViewById(R.id.dummySection_buttons);


            titleOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    threadTitleEdit.setVisibility(View.GONE);
                    titleOkButton.setVisibility(View.GONE);
                    titleCancelButton.setVisibility(View.GONE);
                    threadTitle.setVisibility(View.VISIBLE);
                    titleTags.setVisibility(View.VISIBLE);

                    ArrayList<Object> threadData = new ArrayList<>();
                    threadData.add(LoadDataModel.loadThreadId);
                    threadData.add(threadTitleEdit.getText().toString());

                    new UploadDataAsync(DiscussionViewActivity.this, LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_TITLE,threadData).execute();
                }
            });

            titleCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    threadTitle.setVisibility(View.VISIBLE);
                    threadTitleEdit.setVisibility(View.GONE);
                    titleOkButton.setVisibility(View.GONE);
                    titleCancelButton.setVisibility(View.GONE);
                }
            });

            return true;
        }else if (id == R.id.action_thread_edit_description) {

            threadDescription.setVisibility(View.GONE);
            threadDescriptionEdit.setVisibility(View.VISIBLE);

            descriptionOkButton.setVisibility(View.VISIBLE);
            descriptionCancelButton.setVisibility(View.VISIBLE);
            threadDescriptionEdit.setText(threadDescription.getText());
            threadDescriptionEdit.requestFocus();

            descriptionOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    threadDescriptionEdit.setVisibility(View.GONE);
                    descriptionOkButton.setVisibility(View.GONE);
                    descriptionCancelButton.setVisibility(View.GONE);
                    threadDescription.setVisibility(View.VISIBLE);

                    ArrayList<Object> threadData = new ArrayList<>();
                    threadData.add(LoadDataModel.loadThreadId);
                    threadData.add(threadDescriptionEdit.getText().toString());

                    new UploadDataAsync(DiscussionViewActivity.this, LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_DESCRIPTION, threadData).execute();
                }
            });

            descriptionCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    threadDescription.setVisibility(View.VISIBLE);
                    threadDescriptionEdit.setVisibility(View.GONE);
                    descriptionOkButton.setVisibility(View.GONE);
                    descriptionCancelButton.setVisibility(View.GONE);
                }
            });


        } else if (id == R.id.action_thread_delete) {

            new AlertDialog.Builder(this)
                    .setMessage("Do you really want delete this post ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            ArrayList<Object> threadIds = new ArrayList<>();
                            threadIds.add(LoadDataModel.loadThreadId);
                            new UploadDataAsync(DiscussionViewActivity.this, LoadDataModel.UploadContext.UPLOAD_DELETE_THREAD,threadIds).execute();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFrag(new PostMessageFragment(this,getResources().getColor(R.color.app_white), LoadDataModel.LoadContext.LOAD_THREAD_POSTS), getString(R.string.title_tab_discussions));
        viewPager.setAdapter(adapter);
    }

}
