package com.ldceconnect.ldcecommunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadImage;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupDetailFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class GroupViewActivity extends DrawerActivity {

    private static final int CORNER_RADIUS = 55; // dips
    private static final int MARGIN = 12; // dips

    public int mCornerRadius = 0;
    public int mMargin = 0;

    ActionBarDrawerToggle mDrawerToggle;

    private ApplicationModel.SearchContext previousSearchContext ;
    private String groupId;

    public ViewPagerAdapter adapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public FloatingActionButton fabAddMember;
    public FloatingActionButton fabCreateThread;
    public FloatingActionButton fabJoinGroup;

    public ImageView mImageView;

    public TextView mGroupTitle;
    public TextView mMembersCount;
    public TextView mPostsCount;

    public boolean isUserMemberOfGroup;
    public boolean isUserAdminOfGroup;

    public Menu menu;

    public ArrayList<Group> loadedUserGroups = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        String title = getIntent().getStringExtra("title");
        this.setCallerActivity(getIntent().getStringExtra("caller"));

        groupId = LoadDataModel.loadGroupId;

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_GROUP_DETAILS);
        ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_ABOUT);

        /* Toolbar */
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_group_details);

        LoadDataModel ldm = LoadDataModel.getInstance();

        if(ldm.loadedGroupForDetail.size() == 0)
        {
            showToastLong("Unable to load the group");
            finish();
        }

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

        setDefaultUserProfileImagePaths();
        setupNavHeaderView();


        /* Tab layout - scrollable*/
        mViewPager = (ViewPager) findViewById(R.id.groupdetails_content_container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.groupdetails_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        isUserMemberOfGroup = ApplicationUtils.isUserMemberOfGroup(ldm.loadedAppUserGroups, LoadDataModel.loadGroupId);
        isUserAdminOfGroup = ApplicationUtils.isUserAdminOfGroup(ldm.loadedGroupForDetail, LoadDataModel.loadGroupId);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mViewPager.setCurrentItem(tab.getPosition());

                fabAddMember = (FloatingActionButton) findViewById(R.id.fab_add_member);
                fabCreateThread = (FloatingActionButton) findViewById(R.id.fab_create_discussion);
                fabJoinGroup = (FloatingActionButton) findViewById(R.id.fab_join_group);
                LoadDataModel ldm = LoadDataModel.getInstance();


                switch (tab.getPosition()) {
                    case 2:
                        fabAddMember.hide();
                        fabCreateThread.hide();

                        if (!isUserMemberOfGroup)
                            fabJoinGroup.show();
                        else
                            fabJoinGroup.hide();

                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_NONE);
                        ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_ABOUT);
                        showToast("ABOUT");
                        break;
                    case 1:

                        if (isUserAdminOfGroup)
                            fabAddMember.show();
                        else
                            fabAddMember.hide();

                        if (!isUserMemberOfGroup) {
                            fabJoinGroup.show();
                        } else
                            fabJoinGroup.hide();

                        fabCreateThread.hide();
                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_USER);
                        ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_MEMBERS);
                        showToast("MEMBERS");
                        break;
                    case 0:
                        fabAddMember.hide();
                        if (!isUserMemberOfGroup) {
                            fabJoinGroup.show();
                            fabCreateThread.hide();
                        } else {
                            fabJoinGroup.hide();
                            fabCreateThread.show();
                        }

                        ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_THREAD);
                        ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_POSTS);
                        showToast("POSTS");
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

        /* Floating Action Buttons */
        fabAddMember = (FloatingActionButton) findViewById(R.id.fab_add_member);
        fabAddMember.hide();
        fabAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                ldm.loadSearchSelectedUsers.clear();

                    for( int i = 0 ; i < ldm.loadedGroupMembers.size(); i++) {
                        //User u = new User();
                        User user = ldm.loadedGroupMembers.get(i);

                        /*u.userid = user.userid;
                        u.fname = user.fname;
                        u.lname = user.lname;
                        u.mobile = user.mobile;
                        u.email = user.email;*/

                        ldm.loadSearchSelectedUsers.add(user);
                    }
                LoadDataModel.groupViewUsersAdded = true;

                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_USER);
                ApplicationModel.SearchModel.setAddUserMode(true);
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("caller", GroupViewActivity.class.getName());
                startActivity(intent);
            }

        });

        fabCreateThread = (FloatingActionButton) findViewById(R.id.fab_create_discussion);
        fabCreateThread.hide();
        fabCreateThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupViewActivity.this, CreateThreadActivity.class);
                startActivity(intent);
            }

        });

        fabJoinGroup = (FloatingActionButton) findViewById(R.id.fab_join_group);
        fabJoinGroup.hide();
        fabJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(GroupViewActivity.this)
                        .setMessage("Join the Group ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                UserModel um = UserModel.getInstance();
                                ArrayList<Object> data = new ArrayList<>();
                                ArrayList<User> members = new ArrayList<User>();
                                members.add(um.user);
                                data.add(LoadDataModel.loadGroupId);
                                data.add(members);
                                new UploadDataAsync(GroupViewActivity.this, LoadDataModel.UploadContext.UPLOAD_JOIN_GROUP,data).execute();

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }

        });

        if (!isUserMemberOfGroup)
        {
            fabJoinGroup.show();
            fabCreateThread.hide();
        }
        else {
            fabJoinGroup.hide();
            fabCreateThread.show();
        }

        if( isUserAdminOfGroup)
        {
            TextView changePic = (TextView) findViewById(R.id.groupdetails_uploadbutton);

            changePic.setVisibility(View.VISIBLE);
            changePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Crop.pickImage(GroupViewActivity.this);
                }
            });
        }

        ImageView searchIcon = (ImageView)findViewById(R.id.toolbar_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });

        mImageView = (ImageView)findViewById(R.id.groupdetails_header_imageview);
        Bitmap imageDefault = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.group_icon_medium);

        if(imageDefault != null) {
            RoundedAvatarDrawable rd = new RoundedAvatarDrawable(imageDefault);
            mImageView.setBackground(rd);
        }
        //Download Group Image
        ArrayList<String> groupImage = new ArrayList<>();

        if(ldm.loadedGroupForDetail.size() > 0) {
            Group g = ldm.loadedGroupForDetail.get(0);
            String fileName = g.groupimageurl;
            if( fileName != null && !fileName.isEmpty()) {
                groupImage.add(fileName);
                //ArrayList<String> groupImagePath = ParserUtils.getUploadFilePathsFromFilenames(groupImage);
                new DownloadImages(this, groupImage, DownloadImages.DownloadImagesContext.DOWNLOAD_GROUP_PROFILE_IMAGE).execute();
            }

            mGroupTitle = (TextView)findViewById(R.id.header_name);
            mGroupTitle.setText(g.name);
            mMembersCount = (TextView)findViewById(R.id.header_members_count);
            mMembersCount.setText(g.nummembers + " Members");
            mPostsCount = (TextView)findViewById(R.id.header_posts_count);
            mPostsCount.setText(g.numthreads + " Posts");
        }


    }

    private void beginCrop(Uri source) {
        UserModel um = UserModel.getInstance();
        String imageFile = ImageFilePath.getPath(this, source);
        String fileName = ParserUtils.getFileNameFromPath(imageFile);

        Uri destination = Uri.fromFile(new File(getFilesDir(), fileName));
        Crop.of(source, destination).asSquare().start(this);
    }

    private boolean handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);

            String abPath = ImageFilePath.getPath(this, uri);

            Bitmap b = BitmapFactory.decodeFile(abPath);

            if(b != null )//&& ( b.getHeight() < 300 || b.getWidth() < 300))
                b = Bitmap.createScaledBitmap(b,300,300,false);

            String fileName;
            fileName = ParserUtils.getFileNameFromPath(abPath);

            //source path
            LoadDataModel.uploadImageLocalSourcePath = abPath;
            LoadDataModel.uploadImageFileName = fileName;

            if( b != null ) {
                RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                mImageView.setBackground(rd);
            }
            return true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            if(true == handleCrop(resultCode, data))
            {
                if (LoadDataModel.uploadImageLocalSourcePath != null && !LoadDataModel.uploadImageLocalSourcePath.isEmpty() &&
                        LoadDataModel.uploadImageFileName != null && !LoadDataModel.uploadImageFileName.isEmpty()) {
                    //Upload image to server
                    String ext = ParserUtils.getFileExtension(LoadDataModel.uploadImageLocalSourcePath);
                    LoadDataModel.uploadImageFileName = "group_" + LoadDataModel.loadGroupId + ext;

                    new UploadImage(GroupViewActivity.this, LoadDataModel.uploadImageLocalSourcePath, LoadDataModel.uploadImageFileName, true).execute();

                    ArrayList<Object> uploaddata = new ArrayList<>();
                    uploaddata.add(LoadDataModel.loadGroupId);
                    uploaddata.add(LoadDataModel.defaultImageServerPath + LoadDataModel.uploadImageFileName);
                    new UploadDataAsync(this,LoadDataModel.UploadContext.UPLOAD_UPDATE_GROUPIMAGE,uploaddata).execute();

                    LoadDataModel ldm = LoadDataModel.getInstance();
                    if( ldm.loadedGroupForDetail.size() > 0) {
                        Group g = ldm.loadedGroupForDetail.get(0);
                        if (g != null) {
                            g.listIndex = ApplicationModel.loadedGroupIndex;
                            g.groupImageUpdated = true;
                            g.doUpdate = true;
                            UserModel.userAction = UserModel.UserAction.UPDATE_GROUP_IMAGE;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void refreshData()
    {
        LoadDataModel ldm = LoadDataModel.getInstance();
        if (LoadDataModel.isCurrentDataLoadFinished == true) {
            ldm.loadedGroupMembers.clear();
            ldm.loadedGroupThreads.clear();
            ldm.loadedGroupForDetail.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_REFRESH_GROUP_DETAILS, null).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_group_view, menu);

        LoadDataModel ldm = LoadDataModel.getInstance();

        MenuItem itemJoinGroup = menu.getItem(0);
        MenuItem itemAddMember = menu.getItem(1);
        MenuItem itemLeaveGroup = menu.getItem(2);
        MenuItem itemDeleteGroup = menu.getItem(3);

        this.menu = menu;

        boolean isAdmin = ApplicationUtils.isUserAdminOfGroup(ldm.loadedAppUserGroups, LoadDataModel.loadGroupId);
        boolean isMember = ApplicationUtils.isUserMemberOfGroup(ldm.loadedAppUserGroups, LoadDataModel.loadGroupId);

        if(isAdmin)
        {
            itemAddMember.setVisible(true);
            itemDeleteGroup.setVisible(true);
        }else {
            itemAddMember.setVisible(false);
            itemDeleteGroup.setVisible(false);
        }

        if(isMember)
        {
            if(!isAdmin)
                itemLeaveGroup.setVisible(true);

            itemJoinGroup.setVisible(false);
        }
        else {
            itemLeaveGroup.setVisible(false);
            itemJoinGroup.setVisible(true);
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
        }else if (id == R.id.action_group_join) {
            new AlertDialog.Builder(this)
                    .setMessage("Join the Group ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            UserModel um = UserModel.getInstance();
                            ArrayList<Object> data = new ArrayList<>();
                            ArrayList<User> members = new ArrayList<User>();
                            members.add(um.user);
                            data.add(LoadDataModel.loadGroupId);
                            new UploadDataAsync(GroupViewActivity.this, LoadDataModel.UploadContext.UPLOAD_JOIN_GROUP,data).execute();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        else if (id == R.id.action_group_add_member) {
            ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_USER);
            ApplicationModel.SearchModel.setAddUserMode(true);
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            return true;
        }else if (id == R.id.action_group_leave) {

            new AlertDialog.Builder(this)
                    .setMessage("Do you really want leave the Group ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                                ArrayList<Object> groupIds = new ArrayList<>();
                                groupIds.add(LoadDataModel.loadGroupId);
                                new UploadDataAsync(GroupViewActivity.this, LoadDataModel.UploadContext.UPLOAD_LEAVE_GROUP,groupIds).execute();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }else if (id == R.id.action_group_delete) {
            new AlertDialog.Builder(this)
                    .setMessage("Deleting the group will delete all its associated content. Do you really want to delete the Group ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            ArrayList<Object> groupIds = new ArrayList<>();
                            groupIds.add(LoadDataModel.loadGroupId);
                            new UploadDataAsync(GroupViewActivity.this, LoadDataModel.UploadContext.UPLOAD_DELETE_GROUP,groupIds).execute();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_GROUP_DETAILS);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.groupdetails_tabs);
        switch(tabLayout.getSelectedTabPosition())
        {
            case 0:
                ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_ABOUT);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_NONE);
                break;
            case 1:
                ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_MEMBERS);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_USER);
                break;
            case 2:
                ApplicationModel.AppEventModel.setGroupDetailActiveTab(ApplicationModel.GroupDetailTabs.TAB_POSTS);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_THREAD);
                break;
        }
        LoadDataModel ldm = LoadDataModel.getInstance();

        if( LoadDataModel.groupViewUsersAdded == true)
        {

            StudentFragment sf = (StudentFragment) this.adapter.getItem(ApplicationUtils.GROUPVIEW_MEMBERS_TAB_INDEX);

            if (sf != null && ldm.loadSearchSelectedUsers.size() > 0) {
                // This update the view with selected users
                /*if (sf.adapter != null)
                    sf.adapter.notifyDataSetChanged();*/

                ldm.loadSearchUsers.clear();

                for (int i = 0; i < ldm.loadSearchSelectedUsers.size(); i++) {
                    User u1 = ldm.loadSearchSelectedUsers.get(i);
                    int id = ApplicationUtils.findUserInArray(ldm.loadedGroupMembers, u1.userid);
                    User u2 = null;
                    if (id < 0) {
                        ldm.loadedGroupMembers.add(u1);
                        sf.adapter.notifyItemInserted(sf.adapter.getItemCount());
                    }
                }


                if( ldm.loadedGroupForDetail.size() > 0) {
                    Group g = ldm.loadedGroupForDetail.get(0);
                    if (g != null) {
                        int nums = Integer.valueOf(g.nummembers) + 1;
                        g.nummembers = String.valueOf(nums);
                        g.listIndex = ApplicationModel.loadedGroupIndex;
                        g.doUpdate = true;
                        UserModel.userAction = UserModel.UserAction.ADD_MEMBER_TO_GROUP;
                        this.mMembersCount.setText(g.nummembers + " Members");
                    }
                }
            }
            LoadDataModel.groupViewUsersAdded = false;
        }

        if(ldm.loadedAppUserGroups.size() == 0 || ldm.loadedGroupForDetail.size() == 0 ||
                ldm.loadedGroupMembers.size() == 0 ) {
            refreshData();
        }
    }

    /*@Override
    public void onPause()
    {
        super.onPause();

        LoadDataModel ldm = LoadDataModel.getInstance();
        if(ldm.loadedUserGroups.size() == 0) {
            // user groups required to know if user is a member of group or admin of a group
            if (LoadDataModel.isCurrentDataLoadFinished == true) {
                new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_USER_GROUPS, null).execute();
            }
        }

        // this will download all thumbs for students
        if( ldm.loadedGroupMembers.size() > 0) {
            ArrayList<String> userNames = ParserUtils.getUsernameArrayFromUserArray(ldm.loadedGroupMembers);
            ArrayList<String> fileNames = ParserUtils.getFilenamesFromUserNames(userNames, "_thumb");
            ArrayList<String> filePaths = ParserUtils.getUploadFilePathsFromFilenames(fileNames);

            new DownloadImages(this, filePaths, DownloadImages.DownloadImagesContext.DOWNLOAD_STUDENT_THUMBS).execute();
        }
        else {
            // if not loaded then load
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_GROUP_MEMBERS, null).execute();
        }
    }*/

    @Override
    public void onBackPressed() {
        LoadDataModel ldm = LoadDataModel.getInstance();
        ldm.loadedGroupMembers.clear();
        ldm.loadedGroupThreads.clear();
        LoadDataModel.loadGroupId = "";
        ldm.loadSearchSelectedUsers.clear();
        ldm.loadSearchUsers.clear();
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DiscussionFragment(this,getResources().getColor(R.color.app_white), LoadDataModel.LoadContext.LOAD_GROUP_THREADS), getString(R.string.title_group_detail_tab_posts));
        adapter.addFrag(new StudentFragment(this, getResources().getColor(R.color.app_white), LoadDataModel.LoadContext.LOAD_GROUP_MEMBERS), getString(R.string.title_group_detail_tab_members));
        adapter.addFrag(new GroupDetailFragment(getResources().getColor(R.color.ldce_white)), getString(R.string.title_group_detail_tab_about));
        viewPager.setAdapter(adapter);
    }
}
