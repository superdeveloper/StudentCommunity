package com.ldceconnect.ldcecommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadImage;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentDetailFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class MyContentActivity extends DrawerActivity {

    public ViewPagerAdapter adapter;

    ActionBarDrawerToggle mDrawerToggle;
    public TabLayout tabLayout;

    private ApplicationModel.SearchContext previousSearchContext ;

    public ImageView searchIcon ;

    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_content);
        String title = getIntent().getStringExtra("title");
        String caller = getIntent().getStringExtra("caller");

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_MY_CONTENT);
        ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_GROUPS);

        UserModel um = UserModel.getInstance();

        /* Toolbar */
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_profile_details);

        /*  Drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_ab_up_ltr);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDrawerToggle.syncState();
        setupNavHeaderView();

        /* Tab layout - scrollable*/
        viewPager = (ViewPager) findViewById(R.id.my_content_viewpager);

        tabLayout = (TabLayout) findViewById(R.id.my_content_tabs);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                FloatingActionButton fabCreateGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);
                FloatingActionButton fabCreateThread = (FloatingActionButton) findViewById(R.id.fab_create_discussion);

                switch (tab.getPosition()) {
                    case 0:
                        fabCreateGroup.show();
                        fabCreateThread.hide();
                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_GROUP);
                        ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_GROUPS);
                        showToast("MY GROUPS");
                        break;
                    case 1:
                        fabCreateGroup.hide();
                        fabCreateThread.show();
                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_POST);
                        ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_POSTS);
                        showToast("MY POSTS");
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

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        if( LoadDataModel.activeStudentProfileTab >= 0 && LoadDataModel.activeStudentProfileTab < 2)
            viewPager.setCurrentItem(LoadDataModel.activeStudentProfileTab);
        else
            viewPager.setCurrentItem(0);

        searchIcon = (ImageView) findViewById(R.id.toolbar_search_icon);
        searchIcon.setVisibility(View.VISIBLE);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });

        /* Floating Action Buttons */
        FloatingActionButton fabCreateGroup = (FloatingActionButton) findViewById(R.id.fab_create_group);
        fabCreateGroup.hide();
        fabCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyContentActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }

        });

        FloatingActionButton fabCreateThread = (FloatingActionButton) findViewById(R.id.fab_create_discussion);
        fabCreateThread.hide();
        fabCreateThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyContentActivity.this, CreateThreadActivity.class);
                startActivity(intent);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_student_view, menu);

        LoadDataModel ldm = LoadDataModel.getInstance();

        MenuItem itemCreateGroup = menu.getItem(0);
        MenuItem itemCreateThread = menu.getItem(1);
        MenuItem itemSettings = menu.getItem(2);

        UserModel um = UserModel.getInstance();
        if( LoadDataModel.loadUserId == um.user.userid) {
            itemCreateGroup.setVisible(true);
            itemCreateThread.setVisible(true);
            itemSettings.setVisible(true);
        }
        else
        {
            itemCreateGroup.setVisible(false);
            itemCreateThread.setVisible(false);
            itemSettings.setVisible(false);
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
        if (id == R.id.action_settings) {

            LoadDataModel ldm = LoadDataModel.getInstance();
            ldm.loadedPrograms.clear();
            ldm.loadedDegrees.clear();
            ldm.loadedGroupCategories.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA,null).execute();

        }else if(id == R.id.action_student_create_group)
        {
            Intent intent = new Intent(MyContentActivity.this, CreateGroupActivity.class);
            startActivity(intent);

        }else if(id == R.id.action_student_create_thread)
        {
            Intent intent = new Intent(MyContentActivity.this, CreateThreadActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_MY_CONTENT);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.my_content_tabs);
        switch(tabLayout.getSelectedTabPosition())
        {
            case 0:
                ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_GROUPS);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_GROUP);
                break;
            case 1:
                ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_POSTS);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_POST);
                break;
        }

        if(UserModel.userAction == UserModel.UserAction.LEAVE_GROUP ||
                UserModel.userAction == UserModel.UserAction.DELETE_GROUP) {
            GroupFragment f = (GroupFragment) adapter.getItem(1);
            f.adapter.notifyItemRemoved(ApplicationModel.removedGroupIndex);
            UserModel.resetUserAction();
        }
    }

    @Override
    public void onBackPressed() {
        //set the parent screen as active on back press
        LoadDataModel.activeStudentProfileTab = -1;
        finish();
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager(ViewPager viewPager) {
        //LoadDataModel.studentProfileViewContext = context;

        // Groups and Threads
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new GroupFragment(this, getResources().getColor(R.color.ldce_white), LoadDataModel.LoadContext.LOAD_USER_GROUPS), getString(R.string.title_profile_tab_groups));
        adapter.addFrag(new DiscussionFragment(this, getResources().getColor(R.color.ldce_white), LoadDataModel.LoadContext.LOAD_USER_THREADS), getString(R.string.title_profile_tab_posts));
        tabLayout.setVisibility(View.VISIBLE);

        viewPager.setAdapter(adapter);
    }
}

