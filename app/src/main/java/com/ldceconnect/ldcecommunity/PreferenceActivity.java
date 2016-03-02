package com.ldceconnect.ldcecommunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.customlayouts.NothingSelectedSpinnerAdapter;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.GroupCategory;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Program;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PreferenceActivity extends AppCompatActivity {

    public MultiAutoCompleteTextView autoCompleteInterestTags;
    //public MultiAutoCompleteTextView autoCompleteGroupTypeTags;
    public ArrayAdapter<Degree> dadapter;
    public ArrayAdapter<Program> padapter;
    public ArrayAdapter<Department> departmentadapter;
    ActionBarDrawerToggle mDrawerToggle;
    public Spinner degreeSpinner;
    public Spinner programSpinner;
    public Spinner departmentSpinner;
    public Degree selectedDegree;
    public Program selectedProgram;
    public Department selectedDepartment;
    public String interests;
    public Switch showEmailid;
    public Switch showMobileno;

    ImageView doneButton ;
    ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Settings");

        degreeSpinner = (Spinner) findViewById(R.id.preference_degree_spinner);
        programSpinner = (Spinner) findViewById(R.id.preference_program_spinner);
        departmentSpinner = (Spinner) findViewById(R.id.preference_department_spinner);

        showEmailid = (Switch) findViewById(R.id.preference_show_email_switch);
        showMobileno = (Switch) findViewById (R.id.preference_show_mobile_switch);


        LoadDataModel ldm = LoadDataModel.getInstance();

        autoCompleteInterestTags=(MultiAutoCompleteTextView)findViewById(R.id.preference_interests);
        //autoCompleteGroupTypeTags=(MultiAutoCompleteTextView)findViewById(R.id.preference_grouptypes);

        // Spinners
        dadapter = new ArrayAdapter<Degree>(this,R.layout.spinner_row_normal, ldm.loadedDegrees);
        degreeSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        dadapter,
                        R.layout.spinner_row_nothing_selected,
                        //R.layout.spinner_row_normal, // Optional
                        this,"Select Degree"));
        degreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDegree = (Degree) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        padapter = new ArrayAdapter<Program>(this,R.layout.spinner_row_normal, ldm.loadedPrograms);
        programSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        padapter,
                        R.layout.spinner_row_nothing_selected,
                        //R.layout.spinner_row_normal, // Optional
                        this, "Select Program"));
        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProgram = (Program) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        departmentadapter = new ArrayAdapter<Department>(this,R.layout.spinner_row_normal, ldm.loadedDepartmentsForSpinner);
        departmentSpinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        departmentadapter,
                        R.layout.spinner_row_nothing_selected,
                        //R.layout.spinner_row_normal, // Optional
                        this, "Select Department"));
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = (Department) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*degreeSpinner.setSelection(0);
        programSpinner.setSelection(0);
        departmentSpinner.setSelection(0);*/

        // Hash tags
        autoCompleteInterestTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        autoCompleteInterestTags.setAdapter(
                new ArrayAdapter<String>(this,
                        R.layout.spinner_row_normal, ldm.loadedHashTags));
        autoCompleteInterestTags.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());

        // Hash tags
        /*autoCompleteGroupTypeTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        autoCompleteGroupTypeTags.setAdapter(
                new ArrayAdapter<GroupCategory>(this,
                        R.layout.spinner_row_normal, ldm.loadedGroupCategories));
        autoCompleteGroupTypeTags.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());*/


        // Set data
        UserModel um = UserModel.getInstance();
        User user = um.user;
        if( user != null)
        {
            this.autoCompleteInterestTags.setText(user.interests);
            //this.autoCompleteGroupTypeTags.setText(user.groupType);

            if (this.getClass() == PreferenceActivity.class) {
                this.padapter.notifyDataSetChanged();
                this.dadapter.notifyDataSetChanged();
                this.departmentadapter.notifyDataSetChanged();
            }

            if(user.department != null)
            {
                int items = this.departmentSpinner.getCount();

                for(int i = 0 ; i < items; i ++)
                {
                    Department d = (Department)this.departmentSpinner.getItemAtPosition(i);
                    if( d!= null && d.id.equals(user.department.id))
                    {
                        this.departmentSpinner.setSelection(i);
                        break;
                    }
                }
            }

            if(user.degree != null)
            {
                int items = this.degreeSpinner.getCount();

                for(int i = 0 ; i < items; i ++)
                {
                    Degree d = (Degree)this.degreeSpinner.getItemAtPosition(i);
                    if( d!= null && d.id.equals(user.degree.id))
                    {

                        this.degreeSpinner.setSelection(i);
                        break;
                    }
                }
            }

            if( user.program != null)
            {
                int items = this.programSpinner.getCount();

                for(int i = 0 ; i < items; i ++)
                {
                    Program p = (Program)this.programSpinner.getItemAtPosition(i);
                    if( p != null && p.id.equals( user.program.id))
                    {

                        this.programSpinner.setSelection(i);
                        break;
                    }
                }
            }

            if(um.user.showEmailId.equals("1"))
            {
                showEmailid.setChecked(true);
            }
            else
            {
                showEmailid.setChecked(false);
            }

            if(um.user.showMobileNo.equals("1"))
            {
                showMobileno.setChecked(true);
            }
            else
            {
                showMobileno.setChecked(false);
            }

        }

        doneButton = (ImageView) findViewById(R.id.preference_submit_button);
        closeButton = (ImageView) findViewById(R.id.preference_close_button);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( selectedDegree!= null && selectedProgram!= null && selectedDepartment!= null && autoCompleteInterestTags!= null ) {
                    ArrayList<Object> data = new ArrayList<Object>();
                    data.add(selectedDegree.id);
                    data.add(selectedProgram.id);
                    data.add(selectedDepartment.id);
                    data.add(autoCompleteInterestTags.getText().toString());
                    String showEmail = "0";
                    if( showEmailid.isChecked())
                        showEmail = "1";
                    String showMobile = "0";
                    if( showMobileno.isChecked())
                        showMobile = "1";
                    data.add(showEmail);
                    data.add(showMobile);
                    interests = autoCompleteInterestTags.getText().toString();
                    new UploadDataAsync(PreferenceActivity.this, LoadDataModel.UploadContext.UPLOAD_USER_PREFERENCES, data).execute();
                }
                else
                {
                    showToastLong("Please fill all data ...");
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceActivity.this.onBackPressed();
            }
        });

    }

    void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actions_preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_password) {
            ApplicationUtils.showResetPasswordDialog(this);
            return true;
        }else if (id == R.id.action_forgot_password) {

            new AlertDialog.Builder(this)
                    .setMessage("Reset the password ?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            UserModel um = UserModel.getInstance();
                            ArrayList<Object> data = new ArrayList<>();

                            data.add(um.user.userid);
                            data.add(um.user.fname);
                            data.add(um.user.email);
                            new UploadDataAsync(PreferenceActivity.this, LoadDataModel.UploadContext.UPLOAD_FORGOT_PASSWORD,data).execute();

                        }})
                    .setNegativeButton(android.R.string.no, null).show();

            return true;

        }else if(id == R.id.action_refresh_data)
        {
            refreshData();
            return true;
        }else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshData()
    {
        if (LoadDataModel.isCurrentDataLoadFinished == true) {

            LoadDataModel ldm = LoadDataModel.getInstance();
            ldm.loadedPrograms.clear();
            ldm.loadedDegrees.clear();
            ldm.loadedGroupCategories.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA,null).execute();
        }
    }
}
