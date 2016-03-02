package com.ldceconnect.ldcecommunity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;

import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.customlayouts.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class LoadDataActivity extends AppCompatActivity {

    ActionBarDrawerToggle mDrawerToggle;
    public ProgressBar mProgressBar;
    public AppCompatActivity activity;
    public ImageView mRefreshButton;
    public ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);
        this.activity = this;

        // Call garbage collector when you refresh or load initial data
        //System.gc();

        /* Toolbar */
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);


        mProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
        mRefreshButton = (ImageView) findViewById(R.id.load_data_refresh);

        if( mRefreshButton != null)
        {
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshData();
                }
            });
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.getUser();

        new LoadDataAsync(this,LoadDataModel.LoadContext.LOAD_INITIAL_DATA,null).execute();

    }

    @Override
    public void onBackPressed() {
        UserModel.getInstance().logout();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        else if(id == R.id.action_refresh_data)
        {
            refreshData();
        }
        else if( id == R.id.action_sign_out)
        {
            new AlertDialog.Builder(this)
                    .setMessage("Sign out?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            UserModel um = UserModel.getInstance();
                            um.logout();

                            DatabaseHelper db = DatabaseHelper.getInstance(LoadDataActivity.this);
                            db.logoutUser();

                            Intent intent = new Intent(LoadDataActivity.this,WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshData()
    {
        LoadDataModel ldm = LoadDataModel.getInstance();
        if (LoadDataModel.isCurrentDataLoadFinished == true) {
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_INITIAL_DATA, null).execute();
        }
    }

}