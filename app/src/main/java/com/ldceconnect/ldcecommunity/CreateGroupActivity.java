package com.ldceconnect.ldcecommunity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadImage;
import com.ldceconnect.ldcecommunity.customlayouts.NothingSelectedSpinnerAdapter;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.GroupCategory;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//todo: just a test
public class CreateGroupActivity extends AppCompatActivity {

    ActionBarDrawerToggle mDrawerToggle;
    private ViewPagerAdapter adapter;
    private EditText editTextTitle;
    private EditText editTextdescription;
    public ArrayAdapter<GroupCategory> spinneradapter;
    private ImageView mImageView;
    private Spinner groupCategorySpinner;
    public Handler handler;

    public void clearAllFields()
    {
        editTextTitle.setText("");
        editTextdescription.setText("");
        groupCategorySpinner.setSelection(0);
        LoadDataModel ldm = LoadDataModel.getInstance();
        ldm.loadSearchSelectedUsers.clear();
        ((StudentFragment) adapter.getItem(0)).adapter.notifyDataSetChanged();
        mImageView.setImageDrawable(CreateGroupActivity.this.getDrawable(R.drawable.group_icon));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_CREATE_GROUP);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_create_group);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.student_members_container);
        setupViewPager(viewPager);

        editTextTitle = (EditText)findViewById(R.id.creategroup_groupTitleEditText);
        editTextdescription = (EditText)findViewById(R.id.creategroup_edit_description);

        ImageView addMember = (ImageView)findViewById(R.id.creategroup_addmember_button);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                ApplicationModel.SearchModel.setSearchContext(ApplicationModel.SearchContext.SEARCH_USER);
                ApplicationModel.SearchModel.setAddUserMode(true);
                startActivity(intent);
            }
        });

        ImageView createGroup = (ImageView)findViewById(R.id.creategroup_submit_button);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                if (!editTextTitle.getText().toString().isEmpty() && !editTextdescription.getText().toString().isEmpty() &&
                        ldm.loadSearchSelectedUsers.size() != 0) {
                    new ProcessGroupAsync(CreateGroupActivity.this).execute();
                } else {
                    showToastLong("Please fill all data ...");
                }
            }
        });


        resetUploadImagePaths();

        LoadDataModel ldm = LoadDataModel.getInstance();

        new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_GROUPCATEGORIES,null).execute();

        groupCategorySpinner = (Spinner) findViewById(R.id.creategroup_category_spinner);
        // Spinners
        spinneradapter = new ArrayAdapter<GroupCategory>(this,R.layout.spinner_row_normal, ldm.loadedGroupCategories);
        //spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupCategorySpinner.setAdapter( new NothingSelectedSpinnerAdapter(
                spinneradapter,
                R.layout.spinner_row_nothing_selected,
                //R.layout.spinner_row_normal, // Optional
                this,"Select Group Category"));

        groupCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mImageView = (ImageView)findViewById(R.id.creategroup_imagecontainer);

        TextView changePic = (TextView) findViewById(R.id.creategroup_uploadbutton);
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(CreateGroupActivity.this);
            }
        });

        ldm.loadSearchSelectedUsers.clear();
        ldm.loadSearchUsers.clear();

        handler = new Handler();
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void resetUploadImagePaths()
    {
        LoadDataModel.uploadImageLocalSourcePath = "";
        LoadDataModel.uploadImageFileName = "";
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

            boolean fileAlreadyExist = false;
            if(abPath != null && !abPath.isEmpty()) {
                File file = new File(abPath);
                if (file.exists())
                {
                    fileAlreadyExist = true;
                }
            }

            Bitmap b = BitmapFactory.decodeFile(abPath);

            if(b != null && ( b.getHeight() < 300 || b.getWidth() < 300))
                b = Bitmap.createScaledBitmap(b,300,300,false);

            String fileName;
            fileName = ParserUtils.getFileNameFromPath(abPath);

            //source path
            LoadDataModel.uploadImageLocalSourcePath = abPath;
            LoadDataModel.uploadImageFileName = fileName;

            if( b != null ) {
                RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                mImageView.setImageDrawable(rd);
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
    public void onResume() {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_CREATE_GROUP);
        StudentFragment sf = (StudentFragment) adapter.getItem(0);
        LoadDataModel ldm = LoadDataModel.getInstance();

        if(sf != null)
        {
            if( ldm.loadSearchSelectedUsers.size() == 0) {
                User u = new User();
                UserModel um = UserModel.getInstance();

                u.userid = um.user.userid;
                u.fname = um.user.fname;
                u.lname = um.user.lname;
                u.mobile = um.user.mobile;
                u.email = um.user.email;

                ldm.loadSearchSelectedUsers.add(u);
                if( sf.adapter != null)
                    sf.adapter.notifyItemInserted(0);
            }

            if( sf.adapter != null)
                sf.adapter.notifyDataSetChanged();

            ldm.loadSearchUsers.clear();
        }

        if( ldm.loadSearchSelectedUsers.size() > 1 && sf != null)
        {
            for(int i = 1 ; i < ldm.loadSearchSelectedUsers.size(); i++)
            {
                User u1 = ldm.loadSearchSelectedUsers.get(i);
                if(u1.removedflag )
                {
                    ldm.loadSearchSelectedUsers.remove(u1);
                    int index = sf.adapter.getItemCount();
                    sf.adapter.notifyItemRemoved(index);
                }
            }
        }
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
            clearAllFields();
            resetUploadImagePaths();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StudentFragment(this,getResources().getColor(R.color.app_white), LoadDataModel.LoadContext.LOAD_SEARCH_MEMBERS), getString(R.string.title_tab_groups));
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessGroupAsync extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;
        private AppCompatActivity activity;
        String grouptitle,groupinfo,groupadmin;

        public ProcessGroupAsync(AppCompatActivity activity)
        {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            EditText editTextTitle = (EditText)findViewById(R.id.creategroup_groupTitleEditText);
            EditText editTextdescription = (EditText)findViewById(R.id.creategroup_edit_description);
            grouptitle = editTextTitle.getText().toString();
            groupinfo = editTextdescription.getText().toString();

            pDialog = new ProgressDialog(CreateGroupActivity.this);
            pDialog.setMessage("Creating Group ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();
            UserModel user = UserModel.getInstance();
            LoadDataModel ldm = LoadDataModel.getInstance();
            JSONObject json = userFunction.createGroup(grouptitle, groupinfo, user.user.userid,ldm.loadSearchSelectedUsers );
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if ( json != null && json.getString(DataModel.KEY_SUCCESS) != null) {

                    String res = json.getString(DataModel.KEY_SUCCESS);

                    if(Integer.parseInt(res) == 1){
                        ApplicationUtils.showDoneDialog(CreateGroupActivity.this,"Group created successfully");
                        ((CreateGroupActivity)activity).clearAllFields();

                        ArrayList<Group> createdGroup = new ArrayList<>();

                        ParserUtils.ParseGroupData(json, createdGroup);
                        Group g = null;

                        if( createdGroup.size() > 0) {
                            g = createdGroup.get(0);
                            LoadDataModel ldm = LoadDataModel.getInstance();
                            if( g != null) {
                                g.numthreads = "0";
                                // TODO : get actual num of members
                                g.nummembers = "1";
                                g.groupImageUpdated = true;
                                g.doUpdate = true;
                                g.listIndex = 0;
                                ApplicationUtils.addGroupToTop(ldm.loadedGroups, g);
                                ApplicationUtils.addGroupToTop(ldm.loadedAppUserGroups, g);
                                //ldm.loadedAppUserGroups.add(g);

                                ldm.loadedGroupForDetail.clear();
                                ldm.loadedGroupForDetail.add(g);

                                UserModel.userAction = UserModel.UserAction.CREATE_NEW_GROUP;

                            }
                        }

                        if (LoadDataModel.uploadImageLocalSourcePath != null && !LoadDataModel.uploadImageLocalSourcePath.isEmpty() &&
                                LoadDataModel.uploadImageFileName != null && !LoadDataModel.uploadImageFileName.isEmpty()) {
                            //Upload image to server
                            if( createdGroup.size() > 0)
                            {
                                if( g != null) {
                                    String ext = ParserUtils.getFileExtension(LoadDataModel.uploadImageLocalSourcePath);
                                    LoadDataModel.uploadImageFileName = "group_" + g.id + ext;
                                }
                            }
                            new UploadImage(CreateGroupActivity.this, LoadDataModel.uploadImageLocalSourcePath, LoadDataModel.uploadImageFileName, true).execute();
                        }

                        // finish the activity after created
                        ((CreateGroupActivity) activity).handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.finish();
                            }
                        }, 2000);


                    }else{
                        //pDialog.setMessage("Group Creation Failed..");
                        String msg = "Group Creation Failed ...";
                        showToastLong(msg);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }
}
