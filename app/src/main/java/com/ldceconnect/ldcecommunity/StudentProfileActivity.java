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
import com.ldceconnect.ldcecommunity.util.ImageUtils;
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

public class StudentProfileActivity extends DrawerActivity {

    private static final int CORNER_RADIUS = 55; // dips
    private static final int MARGIN = 12; // dips

    public int mCornerRadius = 0;
    public int mMargin = 0;

    public ViewPagerAdapter adapter;

    ActionBarDrawerToggle mDrawerToggle;
    public ImageView mImageView;
    public RelativeLayout mProfilePicContainer;
    private TextView uploadButton;
    private TextView nameText;

    public TabLayout tabLayout;

    private ApplicationModel.SearchContext previousSearchContext ;

    public ImageView searchIcon ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        String title = getIntent().getStringExtra("title");
        String caller = getIntent().getStringExtra("caller");
        //this.setCallerActivity(getIntent().getStringExtra("caller"));

        mProfilePicContainer = (RelativeLayout)findViewById(R.id.student_dp_container);

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_STUDENT_PROFILE);
        ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_ABOUT);

        mImageView  = (ImageView)findViewById(R.id.header_imageview);

        /* Make the image round */
        /*final float density = getApplicationContext().getResources().getDisplayMetrics().density;
        mCornerRadius = (int) (CORNER_RADIUS * density + 0.5f);
        mMargin = (int) (MARGIN * density + 0.5f);
        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.avatar_small);
        StreamDrawable d = new StreamDrawable(image, mCornerRadius , mMargin);*/
        //img.setBackground(d);

        uploadButton = (TextView) findViewById(R.id.header_uploadbutton);
        nameText = (TextView) findViewById(R.id.header_name);
        searchIcon = (ImageView) findViewById(R.id.toolbar_search_icon);

        UserModel um = UserModel.getInstance();
        User user = null;
        if( LoadDataModel.loadUserId.equals(um.user.userid)) {
            user = um.user;
            uploadButton.setVisibility(View.VISIBLE);
            nameText.setText(user.fname + " " + user.lname);
            searchIcon.setVisibility(View.VISIBLE);
        }
        else {

            try {
                LoadDataModel ldm = LoadDataModel.getInstance();
                //ldm.loadedUserForDetail.clear();
                //Map<String,JSONObject> jMap = new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_USER_DATA, null).get();
                if( ldm.loadedUserForDetail.size() > 0 )
                {
                    user = ldm.loadedUserForDetail.get(0);

                    if( user != null && user.profilePictureUrl != null && !user.profilePictureUrl.isEmpty())
                    {
                        user.userProfilePicBitmap = new DownloadImage(this,user,user.profilePictureUrl).execute().get();
                        nameText.setText(user.fname + " " + user.lname);
                    }
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex2) {
                ex2.printStackTrace();
            } catch ( CancellationException ce)
            {
                ce.printStackTrace();
            }

            uploadButton.setVisibility(View.GONE);
            searchIcon.setVisibility(View.GONE);
        }

        if( user != null)
        {
            if ( user.userProfilePicBitmap != null) {
                Bitmap b = ImageUtils.scaleImageTo(user.userProfilePicBitmap, 300, 300);
                RoundedAvatarDrawable d = new RoundedAvatarDrawable(b);
                mImageView.setBackground(d);
            } else if (user.profilePictureLocalPath != null && !user.profilePictureLocalPath.isEmpty()) {
                Bitmap b = BitmapFactory.decodeFile(user.profilePictureLocalPath);
                if (b != null) {
                    Bitmap b1 = ImageUtils.scaleImageTo(b, 300, 300);
                    RoundedAvatarDrawable d = new RoundedAvatarDrawable(b1);
                    mImageView.setBackground(d);
                }
            }else {
                Bitmap imageBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.avatar_small);
                if (imageBitmap != null) {
                    Bitmap b = ImageUtils.scaleImageTo(imageBitmap, 300, 300);
                    RoundedAvatarDrawable d = new RoundedAvatarDrawable(b);
                    mImageView.setBackground(d);
                }
            }
        }
        else {
            Bitmap imageBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.avatar_small);
            if (imageBitmap != null) {
                Bitmap b = ImageUtils.scaleImageTo(imageBitmap, 300, 300);
                RoundedAvatarDrawable d = new RoundedAvatarDrawable(b);
                mImageView.setBackground(d);
            }
        }


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
        final ViewPager viewPager = (ViewPager) findViewById(R.id.profile_content_container);
        tabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        setupViewPager(viewPager);
        mProfilePicContainer.setVisibility(View.VISIBLE);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(StudentProfileActivity.this);
            }
        });

    }

    private void beginCrop(Uri source) {
        UserModel um = UserModel.getInstance();
        String imageFile = ImageFilePath.getPath(this, source);
        String ext = ParserUtils.getFileExtension(imageFile);

        if(um.user.email == null) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            User user = db.getUser();
            if(user!= null)
            {
                Uri destination = Uri.fromFile(new File(getFilesDir(), user.email + ext));
                Crop.of(source, destination).asSquare().start(this);
            }
        }else
        {
            Uri destination = Uri.fromFile(new File(getFilesDir(), um.user.email + ext));
            Crop.of(source, destination).asSquare().start(this);
        }


    }

    private boolean handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);

            String abPath = ImageFilePath.getPath(this, uri);
            Bitmap b = BitmapFactory.decodeFile(abPath);
            if(b.getHeight() < 300 || b.getWidth() < 300)
                b = Bitmap.createScaledBitmap(b,300,300,false);

            UserModel um = UserModel.getInstance();
            String fileName;
            fileName = ParserUtils.getFileNameFromPath(abPath);
            if( um.user.email == null || um.user.email.isEmpty())
                fileName = "tempFile";

            LoadDataModel.uploadImageLocalSourcePath = abPath;
            LoadDataModel.uploadImageFileName = fileName;

            RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
            mImageView.setImageDrawable(rd);

            if( LoadDataModel.uploadImageLocalSourcePath != null && !LoadDataModel.uploadImageLocalSourcePath.isEmpty() &&
                    LoadDataModel.uploadImageFileName != null && !LoadDataModel.uploadImageFileName.isEmpty()) {
                //Upload image to server
                new UploadImage(StudentProfileActivity.this, LoadDataModel.uploadImageLocalSourcePath, LoadDataModel.uploadImageFileName, true).execute();
            }

            return true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
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
            Intent intent = new Intent(StudentProfileActivity.this, CreateGroupActivity.class);
            startActivity(intent);

        }else if(id == R.id.action_student_create_thread)
        {
            Intent intent = new Intent(StudentProfileActivity.this, CreateThreadActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_STUDENT_PROFILE);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.profile_tabs);
        switch(tabLayout.getSelectedTabPosition())
        {
            case 0:
                ApplicationModel.AppEventModel.setProfileActiveTab(ApplicationModel.ProfileTabs.TAB_ABOUT);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_NONE);
                break;
        }

        /*if(UserModel.userAction == UserModel.UserAction.LEAVE_GROUP ||
                UserModel.userAction == UserModel.UserAction.DELETE_GROUP) {
            GroupFragment f = (GroupFragment) adapter.getItem(0);
            f.adapter.notifyItemRemoved(ApplicationModel.removedGroupIndex);
            UserModel.resetUserAction();
        }*/
    }

    @Override
    public void onBackPressed() {
        //set the parent screen as active on back press
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_EXPLORE_COMMUNITY);
        ApplicationModel.SearchModel.setSearchContext(previousSearchContext);
        LoadDataModel ldm = LoadDataModel.getInstance();
        ldm.loadedUserGroups.clear();
        ldm.loadedUserThreads.clear();
        finish();
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setupViewPager(ViewPager viewPager) {
        //about
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StudentDetailFragment(this,getResources().getColor(R.color.ldce_white)), "ABOUT");
        tabLayout.setVisibility(View.GONE);
        viewPager.setAdapter(adapter);
    }
}

