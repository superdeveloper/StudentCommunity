package com.ldceconnect.ldcecommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.fragments.DepartmentDetailFragment;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DepartmentView extends DrawerActivity {

    ActionBarDrawerToggle mDrawerToggle;
    public ViewPagerAdapter adapter;
    public ImageView mImageView;
    public TextView mTitle ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_view);

        String title = getIntent().getStringExtra("title");
        String subTitle = getIntent().getStringExtra("subtitle");


        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_DEPARTMENT_DETAILS);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_department_details);

        if(title != null && !title.isEmpty())
        {
            mTitle = (TextView) findViewById(R.id.departmentview_listitem_name);
            mTitle.setText(title);
        }
        if(subTitle != null && !subTitle.isEmpty()) {
            TextView subTitleView = (TextView) findViewById(R.id.departmentview_listitem_subname);
            subTitleView.setText(subTitle);
        }

        /*  Drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        setDefaultUserProfileImagePaths();
        setupNavHeaderView();

        /* Tab layout - scrollable*/
        final ViewPager viewPager = (ViewPager) findViewById(R.id.department_content_container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.departmentdetails_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        //ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_NONE);
                        //ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_ABOUT);
                        showToast("ABOUT");
                        break;
                    case 1:
                        //ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_USER);
                        //ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_MEMBERS);
                        showToast("STUDENTS");
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

        mImageView = (ImageView) findViewById(R.id.departmentview_imagecontainer);

        LoadDataModel ldm = LoadDataModel.getInstance();
        if( ldm.loadedDepartmentForDetail.size() > 0) {
            Department d = ldm.loadedDepartmentForDetail.get(0);
            ArrayList<String> filePaths = new ArrayList<>();
            if(d.deptimageurl != null) {
                filePaths.add(d.deptimageurl);
            }

            new DownloadImages(this, filePaths, DownloadImages.DownloadImagesContext.DOWNLOAD_DEPARTMENT_PROFILE_IMAGE).execute();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_DEPARTMENT_DETAILS);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DepartmentDetailFragment(getResources().getColor(R.color.app_white)), getString(R.string.title_group_detail_tab_about));
        adapter.addFrag(new StudentFragment(this,getResources().getColor(R.color.app_white), LoadDataModel.LoadContext.LOAD_DEPARTMENT_MEMBERS), getString(R.string.title_group_detail_tab_members));
        viewPager.setAdapter(adapter);
    }

}
