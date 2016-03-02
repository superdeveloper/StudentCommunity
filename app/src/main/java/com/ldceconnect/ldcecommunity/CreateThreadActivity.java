package com.ldceconnect.ldcecommunity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.customlayouts.NothingSelectedSpinnerAdapter;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.GroupCategory;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateThreadActivity extends AppCompatActivity {

    ActionBarDrawerToggle mDrawerToggle;
    EditText editTextTitle;
    EditText editTextdescription;
    public ArrayAdapter<Group> spinneradapter;
    public Group spinnerSelectedGroup = new Group();
    private MultiAutoCompleteTextView autoCompleteTags;
    private Switch mPublicThreadSwitch;
    private Spinner threadGroupSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);

        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_CREATE_THREAD);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_create_thread);

        editTextTitle = (EditText)findViewById(R.id.createthread_threadTitleEditText);

        editTextdescription = (EditText)findViewById(R.id.createthread_edit_description);

        ImageView cancelButton = (ImageView)findViewById(R.id.createthread_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView createThreadButton = (ImageView)findViewById(R.id.createthread_submit_button);
        createThreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDataModel ldm = LoadDataModel.getInstance();
                if (!editTextTitle.getText().toString().isEmpty() && !editTextdescription.getText().toString().isEmpty()
                        /*&& ldm.loadSearchSelectedUsers.size() != 0*/) {

                    UserModel um = UserModel.getInstance();

                    ArrayList<Object> discussions = new ArrayList<>();

                    Discussion d = new Discussion();

                    d.id = "null";
                    d.title = editTextTitle.getText().toString();
                    d.description = editTextdescription.getText().toString();

                    if( um.user.userid != null && !um.user.userid.isEmpty())
                        d.owner = um.user.userid;
                    else {
                        ApplicationUtils.showWarningDialog(CreateThreadActivity.this,"Application data is corrupted. Please sign out and sign in again",null);
                    }

                    if (spinnerSelectedGroup != null)
                        d.parentgroup = spinnerSelectedGroup.id;
                    else
                        d.parentgroup = "null";

                    if(mPublicThreadSwitch.isChecked())
                    {
                        d.ispublic = "1";
                    }
                    else
                    {
                        d.ispublic = "0";
                    }

                    discussions.add(d);

                    ArrayList<String> tags = ParserUtils.parseHashTagsFromString(autoCompleteTags.getText().toString());

                    discussions.add(tags);

                    new UploadDataAsync(CreateThreadActivity.this, LoadDataModel.UploadContext.UPLOAD_CREATE_THREAD, discussions).execute();
                } else {
                    ApplicationUtils.showWarningDialog(CreateThreadActivity.this,"Please fill the required details",null);
                }
            }
        });

        LoadDataModel ldm = LoadDataModel.getInstance();

        new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_CONTENT_HASH_TAGS, null).execute();

        threadGroupSpinner = (Spinner) findViewById(R.id.createthread_group_spinner);
        // Spinners
        spinneradapter = new ArrayAdapter<Group>(this,R.layout.spinner_row_normal, ldm.loadedAppUserGroups);
        //spinneradapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        threadGroupSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        spinneradapter,
                        R.layout.spinner_row_nothing_selected,
                        //R.layout.spinner_row_normal, // Optional
                        this,"Select Group"));
        threadGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelectedGroup = (Group) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Hash tags
        autoCompleteTags=(MultiAutoCompleteTextView)findViewById(R.id.createthread_hash_tags);
        autoCompleteTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        autoCompleteTags.setAdapter(
                new ArrayAdapter<String>(this,
                        R.layout.spinner_row_normal, ldm.loadedHashTags));
        autoCompleteTags.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());

        mPublicThreadSwitch = (Switch) findViewById(R.id.createthread_open_thread_switch);

        mPublicThreadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    threadGroupSpinner.setEnabled(false);
                    spinnerSelectedGroup = null;
                    threadGroupSpinner.setSelection(0,true);
                }else{
                    threadGroupSpinner.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationModel.AppEventModel.setActiveScreen(ApplicationModel.Screen.SCREEN_CREATE_THREAD);

    }

    @Override
    public void onBackPressed() {
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

        }

        return super.onOptionsItemSelected(item);
    }
}
