package com.ldceconnect.ldcecommunity;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.PostMessageFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;

public class StarredThreadsActivity extends DrawerActivity {

    public Toolbar toolbar;
    public ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starred_threads);

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_STARRED_THREADS);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Starred Posts");

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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.starredthreads_container);
        setupViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DiscussionFragment(this, getResources().getColor(R.color.app_white), LoadDataModel.LoadContext.LOAD_STARRED_THREADS_FOR_RECYCLER), getString(R.string.title_tab_discussions));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_STARRED_THREADS);
    }

}
