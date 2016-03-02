package com.ldceconnect.ldcecommunity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.SearchView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.widget.LinearLayout;

import com.facebook.appevents.AppEventsLogger;
import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.PostFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.pushnotifications.CommonUtilities;
import com.ldceconnect.ldcecommunity.pushnotifications.InstanceIdHelper;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;

import org.json.JSONException;

import com.facebook.FacebookSdk;

public class ExploreCommunity extends DrawerActivity{

    //ActionBarDrawerToggle mDrawerToggle;
    private static DatabaseHelper sInstance;
    public ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_community);

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY);
        ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DISCUSSION);
        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_THREAD);

        /* Toolbar */
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        setDefaultUserProfileImagePaths();
        setupNavigationDrawer(toolbar);
        setupNavHeaderView();

        /* Tab layout - scrollable*/
        final ViewPager viewPager = (ViewPager) findViewById(R.id.explore_community_tabs_container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.explore_community_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                FloatingActionButton fabCreateGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);
                FloatingActionButton fabCreateThread = (FloatingActionButton) findViewById(R.id.fab_create_discussion);
                switch (tab.getPosition()) {
                    case 1:
                        fabCreateGroup.show();
                        fabCreateThread.hide();
                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_GROUP);
                        ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_GROUP);
                        showToast("Groups");
                        break;
                    case 0:
                        fabCreateGroup.hide();
                        fabCreateThread.show();
                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_POST);
                        ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DISCUSSION);
                        showToast("Posts");
                        break;
                    case 2:
                        fabCreateGroup.hide();
                        fabCreateThread.hide();
                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_DEPARTMENT);
                        ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DEPARTMENT);
                        showToast("Departments");
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        /* Search View*/
        /*SearchView searchView = (SearchView) findViewById(R.id.search);
        int magId = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView magImage = (ImageView) searchView.findViewById(magId);
        magImage.setLayoutParams(new LinearLayout.LayoutParams(50, 10));*/

        /* Floating Action Buttons */
        FloatingActionButton fabCreateGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);
        fabCreateGroup.hide();
        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreCommunity.this, CreateGroupActivity.class);
                startActivity(intent);
            }

        });

        FloatingActionButton fabCreateThread = (FloatingActionButton) findViewById(R.id.fab_create_discussion);
        //fabCreateThread.hide();
        fabCreateThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExploreCommunity.this, CreateThreadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }

        });

        ImageView searchIcon = (ImageView) findViewById(R.id.toolbar_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExploreCommunity.this, SearchActivity.class));
            }
        });

        //final ViewPager viewPager1 = (ViewPager) findViewById(R.id.explore_community_content_container);
        //setupViewPager(viewPager);
        LoadDataModel ldm = LoadDataModel.getInstance();
        ArrayList<String> groupImageFileNames = new ArrayList<>();
        for (int i = 0; i < ldm.loadedGroups.size(); i++)
        {
            Group g = ldm.loadedGroups.get(i);
            if( g != null )
            {
                groupImageFileNames.add(g.groupimageurl);
            }
        }
        new DownloadImages(this,groupImageFileNames, DownloadImages.DownloadImagesContext.DOWNLOAD_GROUP_THUMBS).execute();


        ArrayList<String> departmentImageFileNames = new ArrayList<>();
        for (int i = 0; i < ldm.loadedDepartments.size(); i++)
        {
            Department d = ldm.loadedDepartments.get(i);
            if( d != null )
            {
                departmentImageFileNames.add(d.deptimageurl);
            }
        }
        new DownloadImages(this,departmentImageFileNames, DownloadImages.DownloadImagesContext.DOWNLOAD_DEPARTMENT_IMAGES).execute();

    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshData()
    {
        finish();
        Intent upanel = new Intent(this, LoadDataActivity.class);
        upanel.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(upanel);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.explore_community_tabs);
        switch(tabLayout.getSelectedTabPosition())
        {
            case 1:
                ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_GROUP);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_GROUP);
                break;
            case 0:
                ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DISCUSSION);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_THREAD);
                break;
            case 2:
                ApplicationModel.AppEventModel.setActiveTab(ApplicationModel.Tabs.TAB_DEPARTMENT);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_DEPARTMENT);
                break;
        }

        if(UserModel.userAction == UserModel.UserAction.DELETE_GROUP) {
            if( ApplicationModel.removedGroupIndex >= 0) {
                GroupFragment f = (GroupFragment) adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_GROUPS_TAB_INDEX);
                f.adapter.notifyItemRemoved(ApplicationModel.removedGroupIndex);
                UserModel.resetUserAction();
            }
        }

        if(UserModel.userAction == UserModel.UserAction.DELETE_THREAD) {
            if( ApplicationModel.removedThreadIndex >= 0) {
                DiscussionFragment f = (DiscussionFragment) adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_POSTS_TAB_INDEX);
                f.adapter.notifyItemRemoved(ApplicationModel.removedThreadIndex);
                UserModel.resetUserAction();
            }
        }

        if(UserModel.userAction == UserModel.UserAction.UPDATE_THREAD_TITLE) {
            if( ApplicationModel.updatedThreadIndex >= 0) {
                DiscussionFragment f = (DiscussionFragment) adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_POSTS_TAB_INDEX);
                f.adapter.notifyItemChanged(ApplicationModel.updatedThreadIndex);
                UserModel.resetUserAction();
            }
        }

        if(UserModel.userAction == UserModel.UserAction.REMOVE_MEMBER_FROM_GROUP ||
                UserModel.userAction == UserModel.UserAction.ADD_MEMBER_TO_GROUP ||
                UserModel.userAction == UserModel.UserAction.UPDATE_GROUP_IMAGE ||
                UserModel.userAction == UserModel.UserAction.CREATE_NEW_GROUP)
        {
            LoadDataModel ldm = LoadDataModel.getInstance();
            if(ldm.loadedGroupForDetail.size() > 0 )
            {
                Group g = ldm.loadedGroupForDetail.get(0);
                if( g.doUpdate == true && g.listIndex >=0)
                {
                    GroupFragment f = (GroupFragment) adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_GROUPS_TAB_INDEX);

                    if(f.adapter.dataModels.size() >= g.listIndex) {
                        Group g1 = (Group) f.adapter.dataModels.get(g.listIndex);

                        if (g1 != null) {
                            g1.nummembers = g.nummembers;


                            if( g.groupImageUpdated == true) {
                                String key = "group_" + g1.id;
                                RoundedAvatarDrawable rd = ldm.loadedGroupImageThumbs.get(key);
                                ldm.loadedGroupImageThumbs.remove(key);
                                //ldm.loadedGroupImages.remove(key);

                                File dir = this.getFilesDir();
                                String dirPath = dir.getAbsolutePath();

                                Bitmap b = BitmapFactory.decodeFile(dirPath + "/" + LoadDataModel.uploadImageFileName);
                                Bitmap b2 = ImageUtils.scaleImageTo(b, 120, 120);
                                if (b2 != null) {
                                    RoundedAvatarDrawable rd1 = new RoundedAvatarDrawable(b2);
                                    ldm.loadedGroupImageThumbs.put(key, rd1);
                                }
                            }

                            if(ldm.loadedGroups.size()> 0) {
                                if (UserModel.userAction == UserModel.UserAction.CREATE_NEW_GROUP)
                                {
                                    f.adapter.notifyItemInserted(g.listIndex);
                                    f.recyclerView.scrollBy(0,-100);
                                }
                                else
                                    f.adapter.notifyItemChanged(g.listIndex);
                            }

                            g.doUpdate = false;
                            g.groupImageUpdated = false;
                            g.listIndex = -1;
                        }
                    }
                    UserModel.resetUserAction();
                }
            }
        }


        // Mechanism to refresh the data loaded to explore community activity when the activity is resumed after 5 minutes
        /*DatabaseHelper db = DatabaseHelper.getInstance(this);
        Date date2 = db.getRefreshTimeStamp();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date2);
        cal.add(Calendar.MINUTE, 5);
        date2 = cal.getTime();

        Date date = db.getCurrentTime();
        if( date.compareTo(date2) > 0)
        {
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_INITIAL_DATA,null).execute();
        }*/

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);


        // Generate Token
        InstanceIdHelper ihelper = new InstanceIdHelper(this);
        ihelper.getTokenInBackground(CommonUtilities.SENDER_ID,"GCM",new Bundle());

        // Todo : GetInstanceId from it and set it to user table for use by server
        // now using a server script send a notification
        // GcmService will receive the notification in onMessageReceived
        // execute the functionality in that


    }


    @Override
    public void onBackPressed()
    {
        if(UserModel.getInstance().isLoggedIn() == true)
        {
            new AlertDialog.Builder(this)
                    .setMessage("Exit Application?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DiscussionFragment(this,getResources().getColor(R.color.ldce_white), LoadDataModel.LoadContext.LOAD_THREADS), getString(R.string.title_tab_discussions));
        adapter.addFrag(new GroupFragment(this,getResources().getColor(R.color.ldce_white), LoadDataModel.LoadContext.LOAD_GROUPS), getString(R.string.title_tab_groups));
        //adapter.addFrag(new PostFragment(this,getResources().getColor(R.color.ldce_white), LoadDataModel.LoadContext.LOAD_THREADS), getString(R.string.title_tab_discussions));
        adapter.addFrag(new DepartmentFragment(this,getResources().getColor(R.color.ldce_white)), getString(R.string.title_tab_departments));
        viewPager.setAdapter(adapter);
    }


}

