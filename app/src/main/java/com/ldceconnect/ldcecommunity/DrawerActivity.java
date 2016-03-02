package com.ldceconnect.ldcecommunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.ldceconnect.ldcecommunity.async.DownloadImage;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.customlayouts.RoundedLetterView;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle mDrawerToggle;

    private String callerActivity;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_drawer);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDefaultUserProfileImagePaths();
        setupNavHeaderView();
    }*/

    public String getCallerActivity()
    {
        return this.callerActivity;
    }

    public void setCallerActivity(String activityClass)
    {
        this.callerActivity = activityClass;
    }

    @Override
    public void setContentView(int layoutResID) {

        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        FrameLayout frameLayout = (FrameLayout) fullLayout.findViewById(R.id.content_frame);
        LayoutInflater inflater = getLayoutInflater();
        if(inflater != null)
            inflater.inflate(layoutResID, frameLayout, true);
        super.setContentView(fullLayout);


        /* Side Bar Navigation */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void refreshData()
    {
        this.recreate();
    }

    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh_data) {
            refreshData();
            return true;
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

                            DatabaseHelper db = DatabaseHelper.getInstance(DrawerActivity.this);
                            db.logoutUser();

                            Intent intent = new Intent(DrawerActivity.this,WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }

        return super.onOptionsItemSelected(item);
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        UserModel um = UserModel.getInstance();
        LoadDataModel.loadUserId = um.user.userid;

        int tab = -1;
        final Intent intent = new Intent(this, StudentProfileActivity.class);
        intent.putExtra("caller","navigator");

        if (id == R.id.nav_profile) {

            LoadDataModel.loadUserId = um.user.userid;
            LoadDataModel.loadUserName = um.user.fname + " " + um.user.lname;


                if (LoadDataModel.isCurrentDataLoadFinished == true) {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ldm.loadedUserGroups.clear();
                    ldm.loadedUserThreads.clear();
                    ldm.loadedUserForDetail.clear();
                    new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_USER_DETAILS, null).execute();
                }

        } else if (id == R.id.nav_threads) {
            LoadDataModel.loadUserId = um.user.userid;
            LoadDataModel.loadUserName = um.user.fname + " " + um.user.lname;

            if( LoadDataModel.activeStudentProfileTab < 0) {
                if (LoadDataModel.isCurrentDataLoadFinished == true) {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ldm.loadedUserGroups.clear();
                    ldm.loadedUserThreads.clear();
                    LoadDataModel.activeStudentProfileTab = 1;
                    new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_USER_GROUPS_THREADS, null).execute();
                }
            }else if(LoadDataModel.activeStudentProfileTab == 0 && this.getClass() == MyContentActivity.class) {
                ((MyContentActivity)this).viewPager.setCurrentItem(LoadDataModel.activeStudentProfileTab);
                //((MyContentActivity)this).viewPager.invalidate();
            }
        } else if (id == R.id.nav_groups) {
            LoadDataModel.loadUserId = um.user.userid;
            LoadDataModel.loadUserName = um.user.fname + " " + um.user.lname;

            if(LoadDataModel.activeStudentProfileTab < 0) {
                if (LoadDataModel.isCurrentDataLoadFinished == true) {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ldm.loadedUserGroups.clear();
                    ldm.loadedUserThreads.clear();
                    LoadDataModel.activeStudentProfileTab = 0;
                    new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_USER_GROUPS_THREADS, null).execute();
                }
            }else if(LoadDataModel.activeStudentProfileTab == 1 && this.getClass() == MyContentActivity.class) {
                ((MyContentActivity)this).viewPager.setCurrentItem(LoadDataModel.activeStudentProfileTab);

                //((MyContentActivity)this).viewPager.invalidate();
            }
        }  else if (id == R.id.nav_preferences) {

            LoadDataModel ldm = LoadDataModel.getInstance();

            ldm.loadedDepartments.clear();
            ldm.loadedPrograms.clear();
            ldm.loadedDegrees.clear();
            ldm.loadedHashTags.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA,null).execute();

        }
        else if (id == R.id.nav_share) {

            new MaterialDialog.Builder(this)
                    .title("Share")
                    .items(R.array.social_media)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if( which == 0) {
                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse("www.therandomquestion.com/LogInAPI/downloadldce_redirect.php"))
                                        .setImageUrl(Uri.parse("https://lh3.googleusercontent.com/frLOsoFtK9opVU4GDUyle1Jda8AXgH9-Y27em6wDbktFglEt1UNWQITj4l-46Bx0Qsk=w300"))
                                        .setContentTitle("LDCE Community")
                                        .setContentDescription("Learn, Share and Collaborate")
                                        .build();

                                ShareDialog.show(DrawerActivity.this, content);
                            }
                            else if (which == 1)
                            {
                                PlusShare.Builder builder = new PlusShare.Builder(DrawerActivity.this);

                                // Set call-to-action metadata.
                                builder.addCallToAction(
                                        "DOWNLOAD", /** call-to-action button label */
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.ldceconnect.ldcecommunity"), /** call-to-action url (for desktop use) */
                                        "https://play.google.com/store/apps/details?id=com.ldceconnect.ldcecommunity" /** call to action deep-link ID (for mobile use), 512 characters or fewer */);

                                // Set the content url (for desktop use).
                                builder.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.ldceconnect.ldcecommunity"));

                                // Set the target deep-link ID (for mobile use).
                                builder.setContentDeepLinkId("https://play.google.com/store/apps/details?id=com.ldceconnect.ldcecommunity",
                                        null, null, null);

                                // Set the share text.
                                builder.setText("Hey LDites, Checkout LDCE Community Application");

                                startActivityForResult(builder.getIntent(), 0);
                            }
                        }
                    })
                    .show();

        } else if (id == R.id.nav_send) {

            RelativeLayout rellayout = (RelativeLayout) LayoutInflater.from(DrawerActivity.this).inflate(R.layout.send_invite_dialog, null);
            RadioButton rb = (RadioButton)rellayout.findViewById(R.id.send_invite_from_email);
            rb.setText(um.user.email);

            new MaterialDialog.Builder(this)
                    .title("Invite a Friend")
                    .customView(rellayout, true)
                    .positiveText("Ok")
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {

                            View v = dialog.getCustomView();
                            EditText et = (EditText) v.findViewById(R.id.send_invite_email_input);
                            String toEmail = et.getText().toString().trim();

                            if (toEmail.isEmpty()) {
                                ApplicationUtils.showWarningDialog(DrawerActivity.this, "Please enter email", null);
                            } else {
                                ArrayList<Object> emails = new ArrayList<>();
                                UserModel um = UserModel.getInstance();
                                if (um.user.email != null)
                                    emails.add(um.user.email);
                                else
                                    emails.add("ldcecommunity@therandomquestion.com");

                                if (um.user.fname != null)
                                    emails.add(um.user.fname + " " + um.user.lname);
                                else
                                    emails.add("us");

                                emails.add(toEmail);
                                emails.add("");

                                //new UploadDataAsync(DrawerActivity.this, LoadDataModel.UploadContext.UPLOAD_SEND_INVITE, emails).execute();

                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setData(Uri.parse("mailto:"));
                                emailIntent.setType("text/html");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "Message Body");

                                //get a list of apps that meet your criteria above
                                List<ResolveInfo> pkgAppsList = DrawerActivity.this.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);

                                //select the first one in the list
                                ResolveInfo info;
                                String gmailpackageName = "",emailpackageName ="";
                                String gmailclassName="", emailclassName="" ;

                                if( pkgAppsList != null) {
                                    for (int i = 0; i < pkgAppsList.size(); i++) {
                                        info = pkgAppsList.get(i);
                                        if (info.activityInfo.name.equals( "com.google.android.gm.ComposeActivityGmail")) {
                                            gmailpackageName = info.activityInfo.packageName;
                                            gmailclassName = info.activityInfo.name;
                                            break;
                                        } else if (info.activityInfo.name.equals("com.android.email.activity.MessageCompose")) {
                                            emailpackageName = info.activityInfo.packageName;
                                            emailclassName = info.activityInfo.name;
                                        }
                                    }
                                }

                                if(  !gmailpackageName.isEmpty() && !gmailclassName.isEmpty())
                                {
                                    //set the intent to luanch that specific app
                                    emailIntent.setClassName(gmailpackageName, gmailclassName);
                                }else if (!emailpackageName.isEmpty() && !emailclassName.isEmpty())
                                {
                                    emailIntent.setClassName(emailpackageName, emailclassName);
                                }
                                else
                                {
                                    ApplicationUtils.showWarningDialog(DrawerActivity.this, "No Email application is configured on your device.", null);
                                }

                                try {
                                    startActivity(emailIntent);
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(DrawerActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    })
                    .show();

        }else if (id == R.id.nav_logout) {

            new AlertDialog.Builder(this)
                    .setMessage("Sign out?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            UserModel um = UserModel.getInstance();
                            um.logout();

                            DatabaseHelper db = DatabaseHelper.getInstance(DrawerActivity.this);
                            db.logoutUser();

                            Intent intent = new Intent(DrawerActivity.this,WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        } else if(id == R.id.nav_feedback)
        {
            RelativeLayout rellayout = (RelativeLayout) LayoutInflater.from(DrawerActivity.this).inflate(R.layout.edit_post_dialog, null);
            new MaterialDialog.Builder(this)
                    .title("Send Feedback")
                    .positiveText("Ok")
                    .negativeText("Cancel")
                    .customView(rellayout, true)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {

                                        View v = dialog.getCustomView();
                                        EditText et = (EditText)v.findViewById(R.id.edit_post_text);
                                        String text = et.getText().toString();

                                        ArrayList<Object> data = new ArrayList<>();

                                        UserModel um = UserModel.getInstance();
                                        if( um.user != null && um.user.email != null && !um.user.email.isEmpty())
                                            data.add(um.user.email);
                                        else
                                            data.add("ldcecommunity@therandomquestion.com");

                                        data.add(text);
                                        new UploadDataAsync(DrawerActivity.this, LoadDataModel.UploadContext.UPLOAD_SEND_FEEDBACK, data).execute();
                                    }
                                }
                    ).show();
        }
        else if(id == R.id.nav_starred_threads)
        {
            LoadDataModel ldm = LoadDataModel.getInstance();
            ldm.loadedStarredThreads.clear();
            LoadDataModel.starredThreadsList.clear();
            new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_STARRED_THREADS, null).execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setDefaultUserProfileImagePaths()
    {
        UserModel um = UserModel.getInstance();
        um.user.profilePictureUrl = DataModel.uploadImageURL + um.user.email + ".jpg";
        um.user.profilePictureLocalPath = getApplicationContext().getFilesDir() + "/" + ParserUtils.getFileNameFromPath(um.user.profilePictureUrl);
        um.user.profilePictureThumbUrl = DataModel.uploadImageURL + um.user.email + "_thumb.jpg";
        um.user.profilePictureLocalThumbPath = getApplicationContext().getFilesDir() + "/" + ParserUtils.getFileThumbName(ParserUtils.getFileNameFromPath(um.user.profilePictureUrl));

    }

    public void setupNavigationDrawer(Toolbar toolbar)
    {
        /*  Drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void hideNavDrawer()
    {
        /* Side Bar Navigation */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setVisibility(View.GONE);
    }

    public void setupNavHeaderView()
    {
        try
        {
            UserModel um = UserModel.getInstance();

            boolean fileAlreadyExist = false;
            File file = new File(um.user.profilePictureLocalPath);
            if (file.exists())
            {
                fileAlreadyExist = true;
                if(file.isDirectory())
                {
                    fileAlreadyExist = false;
                }
                else if(file.isFile())
                {
                    fileAlreadyExist = true;
                }
            }

            if(!fileAlreadyExist) {
                um.user.userProfilePicBitmap = new DownloadImage(getApplicationContext(), um.user,um.user.profilePictureUrl).execute().get();
            }else
            {
                um.user.userProfilePicBitmap = BitmapFactory.decodeFile(um.user.profilePictureLocalPath);
            }

            LinearLayout linearlayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_header_side_drawer,null);

            //Drawable d = new BitmapDrawable(getResources(), um.userProfilePicBitmap);
            //linearlayout.setBackground(d);

            ImageView img = (ImageView)linearlayout.findViewById(R.id.nav_header_imageview);

            if( um.user.userProfilePicBitmap != null)
                img.setImageDrawable(new RoundedAvatarDrawable(um.user.userProfilePicBitmap));

            RoundedLetterView textLogo = (RoundedLetterView)linearlayout.findViewById(R.id.nav_header_rlv_name_view);
            TextView name = (TextView) linearlayout.findViewById(R.id.nav_header_name_text);
            TextView email = (TextView) linearlayout.findViewById(R.id.nav_header_email_text);
            if( um.user.fname != null && !um.user.fname.isEmpty())
            {
                String rlv = um.user.fname.substring(0,1);
                String dispayName = um.user.fname;

                if( um.user.lname != null && !um.user.lname.isEmpty())
                {
                    rlv = rlv + um.user.lname.substring(0,1);
                    dispayName = dispayName + " " + um.user.lname;
                }

                textLogo.setTitleText(rlv);

                name.setText(dispayName);
                if(um.user.email != null)
                    email.setText(um.user.email);
                else
                    email.setText(" ");
            }

            NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
            navView.addHeaderView(linearlayout);

        }
        catch(InterruptedException ex)
        {

        }catch(ExecutionException ex2)
        {

        }
    }
}
