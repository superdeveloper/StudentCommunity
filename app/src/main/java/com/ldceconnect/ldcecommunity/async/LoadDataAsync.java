package com.ldceconnect.ldcecommunity.async;

/**
 * Created by Nevil on 12/18/2015.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.CreateGroupActivity;
import com.ldceconnect.ldcecommunity.DepartmentView;
import com.ldceconnect.ldcecommunity.DiscussionViewActivity;
import com.ldceconnect.ldcecommunity.EditProfileActivity;
import com.ldceconnect.ldcecommunity.ExploreCommunity;
import com.ldceconnect.ldcecommunity.GroupViewActivity;
import com.ldceconnect.ldcecommunity.LoadDataActivity;
import com.ldceconnect.ldcecommunity.MyContentActivity;
import com.ldceconnect.ldcecommunity.PreferenceActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.SearchActivity;
import com.ldceconnect.ldcecommunity.StarredThreadsActivity;
import com.ldceconnect.ldcecommunity.StudentProfileActivity;
import com.ldceconnect.ldcecommunity.fragments.DiscussionFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupDetailFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.PostFragment;
import com.ldceconnect.ldcecommunity.fragments.PostMessageFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.fragments.ViewPagerAdapter;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.GroupCategory;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.Program;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Async Task to get and send data to My Sql database through JSON respone.
 **/
public class LoadDataAsync extends AsyncTask<String, Integer, Map<String,JSONObject>>  {

    private Map<String,JSONObject> mJSONObjArray = new HashMap<>();
    private JSONObject json_departments;
    private JSONObject json_groups;
    private JSONObject json_threads;
    private JSONObject json_members;
    private JSONObject json_degrees;
    private JSONObject json_programs;
    private JSONObject json_hashtags;
    private JSONObject json_group_members;
    private JSONObject json_group_details;
    private JSONObject json_group_threads;
    private JSONObject json_app_user_groups;
    private JSONObject json_user_groups;
    private JSONObject json_thread_details;
    private JSONObject json_thread_posts;
    private JSONObject json_user_threads;
    private JSONObject json_group_categories;
    private JSONObject json_department_members;
    private JSONObject json_department_details;
    private JSONObject json_departments_spinner;
    private JSONObject json_user_details;
    private JSONObject json_starred_threads;
    private JSONObject json_get_servertime;
    private JSONObject json_get_file_synctime;
    private LoadDataModel.LoadContext loadContext;
    private AppCompatActivity activity;
    ProgressBar progressBar;
    ProgressDialog pDialog;
    Handler handler;
    Runnable myRunnable;

    public LoadDataAsync(AppCompatActivity activity,LoadDataModel.LoadContext context,ProgressBar progress)
    {
        super();
        this.activity = activity;
        this.loadContext = context;
        this.progressBar = progress;
        this.handler = new Handler();
    }

    void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    void showToastLong(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if( progressBar != null) {
            // updating progress bar value
            progressBar.setProgress(progress[0]);
        }
        pDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        boolean netcheckResult= false;
        LoadDataModel.isNetworkCheckSuccessful = false;


        ConnectionDetector cd = new ConnectionDetector(activity);
        netcheckResult = cd.isConnectingToInternet();//new NetCheck(activity).execute().get();


        if(netcheckResult == true)
        {
            LoadDataModel.isNetworkCheckSuccessful = true;


            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);

            if(activity.isDestroyed() || activity.isFinishing())
            {
                cancel(true);
                return;
            }

            pDialog.show();
            LoadDataModel.isCurrentDataLoadFinished = false;
            LoadDataModel ldm = LoadDataModel.getInstance();

            if(loadContext == LoadDataModel.LoadContext.LOAD_INITIAL_DATA)
            {
                ldm.loadedPrograms.clear();
                ldm.loadedThreads.clear();
                ldm.loadedGroups.clear();
                ldm.loadedDegrees.clear();
                ldm.loadedDepartments.clear();
                ldm.loadedMembers.clear();
                ldm.loadedAppUserGroups.clear();
                ldm.loadedUserThreads.clear();

                // Call garbage collector when you refresh or load initial data
                System.gc();

            }else if(loadContext == LoadDataModel.LoadContext.LOAD_DEGREES_PROGRAMS) {
                ldm.loadedPrograms.clear();
                ldm.loadedDegrees.clear();
                ldm.loadedHashTags.clear();
            }else if (loadContext == LoadDataModel.LoadContext.LOAD_CONTENT_HASH_TAGS)
            {
                ldm.loadedHashTags.clear();
            }
            else if( loadContext == LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA)
            {
                ldm.loadedPrograms.clear();
                ldm.loadedDegrees.clear();
                ldm.loadedGroupCategories.clear();
            }

            if( progressBar != null) {
                // Making progress bar visible
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    protected Map<String,JSONObject> doInBackground(String... args) {
        UserFunctions userFunction = new UserFunctions();
        UserModel user = UserModel.getInstance();
        //LoadDataModel.loadUserId = user.user.userid;

        if( LoadDataModel.isCurrentDataLoadFinished == true)
        {
            return mJSONObjArray;
        }


        if(LoadDataModel.isNetworkCheckSuccessful) {

                myRunnable = new Runnable() {
                    public void run() {
                        if (pDialog != null) {
                            //pDialog.dismiss();
                            //ApplicationUtils.showWarningDialog(activity, "Network timeout", null);
                            //LoadDataModel.isCurrentDataLoadFinished = true;
                            //LoadDataModel.isNetworkCheckSuccessful = true;
                            //mJSONObjArray = null;
                            //cancel(true);
                        }
                    }
                };
                handler.postDelayed(myRunnable, 40000);

                LoadDataModel ldm = LoadDataModel.getInstance();

                if (loadContext == LoadDataModel.LoadContext.LOAD_INITIAL_DATA) {
                    mJSONObjArray.clear();
                    JSONObject json_departments = userFunction.loadDepartments();
                    mJSONObjArray.put("departments", json_departments);
                    JSONObject json_groups = userFunction.loadGroups();
                    mJSONObjArray.put("groups", json_groups);
                    JSONObject json_threads = userFunction.loadThreads();
                    mJSONObjArray.put("threads", json_threads);
                    JSONObject json_degrees = userFunction.loadDegrees();
                    mJSONObjArray.put("degrees", json_degrees);
                    JSONObject json_programs = userFunction.loadPrograms();
                    mJSONObjArray.put("programs", json_programs);

                    JSONObject json_user_details = userFunction.getUserById(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userdetails", json_user_details);

                    JSONObject json_app_user_groups = userFunction.loadAppUserGroups(LoadDataModel.loadUserId);
                    mJSONObjArray.put("appusergroups", json_app_user_groups);
                    JSONObject json_user_threads = userFunction.loadUserThreads(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userthreads", json_user_threads);

                    UserModel um = UserModel.getInstance();
                    ldm.loadedStarredThreads.clear();
                    LoadDataModel.starredThreadsList.clear();
                    JSONObject json_starred_threads = userFunction.loadStarredThreads(um.user.userid);
                    mJSONObjArray.put("starredthreads", json_starred_threads);

                    Bitmap b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.group_icon);
                    b = ImageUtils.scaleImageTo(b, 100, 100);
                    ldm.loadedDefaultGroupThumb = new RoundedAvatarDrawable(b);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENTS) {
                    mJSONObjArray.clear();
                    JSONObject json_departments = userFunction.loadDepartments();
                    mJSONObjArray.put("departments", json_departments);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENT_MEMBERS) {
                    mJSONObjArray.clear();
                    JSONObject json_department_members = userFunction.loadDepartmentMembers(LoadDataModel.loadDepartmentId);
                    mJSONObjArray.put("departmentmembers", json_department_members);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENT_DETAILS) {
                    mJSONObjArray.clear();
                    JSONObject json_department_details = userFunction.loadDepartmentDetails(LoadDataModel.loadDepartmentId);
                    mJSONObjArray.put("departmentdetails", json_department_details);
                    JSONObject json_department_members = userFunction.loadDepartmentMembers(LoadDataModel.loadDepartmentId);
                    mJSONObjArray.put("departmentmembers", json_department_members);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUPS) {
                    mJSONObjArray.clear();
                    JSONObject json_groups = userFunction.loadGroups();
                    mJSONObjArray.put("groups", json_groups);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_THREADS) {
                    mJSONObjArray.clear();
                    JSONObject json_threads = userFunction.loadThreads();
                    mJSONObjArray.put("threads", json_threads);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_DETAILS ||
                        loadContext == LoadDataModel.LoadContext.LOAD_REFRESH_GROUP_DETAILS) {

                    mJSONObjArray.clear();
                    if (LoadDataModel.loadGroupId != null) {
                        JSONObject json_group_members = userFunction.loadGroupMembers(LoadDataModel.loadGroupId);
                        mJSONObjArray.put("groupmembers", json_group_members);
                        JSONObject json_group_details = userFunction.loadGroupDetails(LoadDataModel.loadGroupId);
                        mJSONObjArray.put("groupdetails", json_group_details);
                        JSONObject json_group_threads = userFunction.loadGroupThreads(LoadDataModel.loadGroupId);
                        mJSONObjArray.put("groupthreads", json_group_threads);

                        UserModel um = UserModel.getInstance();
                        ldm.loadedStarredThreads.clear();
                        LoadDataModel.starredThreadsList.clear();
                        JSONObject json_starred_threads = userFunction.loadStarredThreads(um.user.userid);
                        mJSONObjArray.put("starredthreads", json_starred_threads);

                        if(ldm.loadedAppUserGroups.size() == 0) {
                            JSONObject json_app_user_groups = userFunction.loadAppUserGroups(LoadDataModel.loadUserId);
                            mJSONObjArray.put("appusergroups", json_app_user_groups);
                        }
                        if(ldm.loadedUserThreads.size() == 0) {
                            JSONObject json_user_threads = userFunction.loadUserThreads(LoadDataModel.loadUserId);
                            mJSONObjArray.put("userthreads", json_user_threads);
                        }
                    }
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_MEMBERS) {
                    mJSONObjArray.clear();
                    JSONObject json_group_members = userFunction.loadGroupMembers(LoadDataModel.loadGroupId);
                    mJSONObjArray.put("groupmembers", json_group_members);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_THREADS) {
                    mJSONObjArray.clear();
                    JSONObject json_group_members = userFunction.loadGroupThreads(LoadDataModel.loadGroupId);
                    mJSONObjArray.put("groupthreads", json_group_members);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUPCATEGORIES) {
                    mJSONObjArray.clear();
                    JSONObject json_group_categories = userFunction.loadGroupCategories();
                    mJSONObjArray.put("groupcategories", json_group_categories);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEGREES_PROGRAMS) {
                    mJSONObjArray.clear();
                    JSONObject json_degrees = userFunction.loadDegrees();
                    mJSONObjArray.put("degrees", json_degrees);
                    JSONObject json_programs = userFunction.loadPrograms();
                    mJSONObjArray.put("programs", json_programs);
                    JSONObject json_interests = userFunction.loadInterests();
                    mJSONObjArray.put("interests", json_interests);
                    JSONObject json_departments = userFunction.loadDepartments();
                    mJSONObjArray.put("departmentsforspinner", json_departments);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_DETAILS) {
                    mJSONObjArray.clear();
                    JSONObject json_user_details = userFunction.getUserById(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userdetails", json_user_details);
                    JSONObject json_user_groups = userFunction.loadUserGroups(LoadDataModel.loadUserId);
                    mJSONObjArray.put("usergroups", json_user_groups);
                    JSONObject json_user_threads = userFunction.loadUserThreads(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userthreads", json_user_threads);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS) {
                    mJSONObjArray.clear();
                    JSONObject json_user_groups = userFunction.loadUserGroups(LoadDataModel.loadUserId);
                    mJSONObjArray.put("usergroups", json_user_groups);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_THREADS) {
                    mJSONObjArray.clear();
                    JSONObject json_user_threads = userFunction.loadUserThreads(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userthreads", json_user_threads);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_DATA) {
                    mJSONObjArray.clear();
                    JSONObject json_user_details = userFunction.getUserById(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userdetails", json_user_details);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS_THREADS) {
                    mJSONObjArray.clear();
                    JSONObject json_user_groups = userFunction.loadUserGroups(LoadDataModel.loadUserId);
                    mJSONObjArray.put("usergroups", json_user_groups);
                    JSONObject json_user_threads = userFunction.loadUserThreads(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userthreads", json_user_threads);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_THREAD_DETAILS ||
                        loadContext == LoadDataModel.LoadContext.LOAD_REFRESH_THREAD_DETAILS) {
                    mJSONObjArray.clear();
                    JSONObject json_thread_details = userFunction.loadThreadDetails(LoadDataModel.loadThreadId);
                    mJSONObjArray.put("threaddetail", json_thread_details);
                    JSONObject json_thread_posts = userFunction.loadThreadPosts(LoadDataModel.loadThreadId);
                    mJSONObjArray.put("threadposts", json_thread_posts);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_THREAD_POSTS) {
                    mJSONObjArray.clear();
                    JSONObject json_thread_posts = userFunction.loadThreadPosts(LoadDataModel.loadThreadId);
                    mJSONObjArray.put("threadposts", json_thread_posts);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_CONTENT_HASH_TAGS) {
                    mJSONObjArray.clear();
                    JSONObject json_interests = userFunction.loadInterests();
                    mJSONObjArray.put("interests", json_interests);
                } else if (loadContext == LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA) {
                    mJSONObjArray.clear();
                    JSONObject json_interests = userFunction.loadInterests();
                    mJSONObjArray.put("interests", json_interests);

                    /*JSONObject json_group_categories = userFunction.loadGroupCategories();
                    mJSONObjArray.put("groupcategories", json_group_categories);*/

                    JSONObject json_degrees = userFunction.loadDegrees();
                    mJSONObjArray.put("degrees", json_degrees);
                    JSONObject json_programs = userFunction.loadPrograms();
                    mJSONObjArray.put("programs", json_programs);

                    ldm.loadedDepartmentsForSpinner.clear();
                    ldm.loadedDepartments.clear();
                    JSONObject json_departments = userFunction.loadDepartments();
                    mJSONObjArray.put("departmentsforspinner", json_departments);

                    UserModel um = UserModel.getInstance();
                    LoadDataModel.loadUserId = um.user.userid;
                    JSONObject json_user_details = userFunction.getUserById(LoadDataModel.loadUserId);
                    mJSONObjArray.put("userdetails", json_user_details);

                } else if (loadContext == LoadDataModel.LoadContext.LOAD_STARRED_THREADS || loadContext == LoadDataModel.LoadContext.LOAD_STARRED_THREADS_FOR_RECYCLER) {
                    mJSONObjArray.clear();
                    UserModel um = UserModel.getInstance();
                    JSONObject json_starred_threads = userFunction.loadStarredThreads(um.user.userid);
                    mJSONObjArray.put("starredthreads", json_starred_threads);
                } else if( loadContext == LoadDataModel.LoadContext.LOAD_SERVERTIME)
                {
                    mJSONObjArray.clear();
                    JSONObject json_get_servertime = userFunction.getServerTime();
                    mJSONObjArray.put("servertime",json_get_servertime);
                }
                else if( loadContext == LoadDataModel.LoadContext.LOAD_GET_FILE_SYNCTIME)
                {
                    mJSONObjArray.clear();
                    JSONObject json_get_file_synctime = userFunction.getUploadedFileTimestamp(LoadDataModel.loadSynTimeForFile);
                    mJSONObjArray.put("filesynctime", json_get_file_synctime);

                }

                json_departments = mJSONObjArray.get("departments");
                json_groups = mJSONObjArray.get("groups");
                json_threads = mJSONObjArray.get("threads");
                json_degrees = mJSONObjArray.get("degrees");
                json_programs = mJSONObjArray.get("programs");
                json_hashtags = mJSONObjArray.get("interests");
                json_group_members = mJSONObjArray.get("groupmembers");
                json_group_details = mJSONObjArray.get("groupdetails");
                json_group_threads = mJSONObjArray.get("groupthreads");
                json_app_user_groups = mJSONObjArray.get("appusergroups");
                json_user_groups = mJSONObjArray.get("usergroups");
                json_user_threads = mJSONObjArray.get("userthreads");
                json_thread_details = mJSONObjArray.get("threaddetail");
                json_thread_posts = mJSONObjArray.get("threadposts");
                json_group_categories = mJSONObjArray.get("groupcategories");
                json_department_members = mJSONObjArray.get("departmentmembers");
                json_department_details = mJSONObjArray.get("departmentdetails");
                json_departments_spinner = mJSONObjArray.get("departmentsforspinner");
                json_user_details = mJSONObjArray.get("userdetails");
                json_starred_threads = mJSONObjArray.get("starredthreads");
                json_get_servertime = mJSONObjArray.get("servertime");
                json_get_file_synctime = mJSONObjArray.get("filesynctime");

                if (json_departments_spinner != null) {

                   /* Department dg = new Department();
                    dg.id = "0";
                    dg.name = "Department";
                    ldm.loadedDepartmentsForSpinner.add(dg);*/

                    ParserUtils.ParseDepartmentData(json_departments_spinner, ldm.loadedDepartments);

                    for (int i = 0; i < ldm.loadedDepartments.size(); i++) {
                        ldm.loadedDepartmentsForSpinner.add(ldm.loadedDepartments.get(i));
                    }
                }
                if (json_departments != null)
                    ParserUtils.ParseDepartmentData(json_departments, ldm.loadedDepartments);
                if (json_groups != null)
                    ParserUtils.ParseGroupData(json_groups, ldm.loadedGroups);
                if (json_threads != null)
                    ParserUtils.ParseDiscussionData(json_threads, ldm.loadedThreads);
                if (json_degrees != null)
                    ParserUtils.ParseDegreesData(json_degrees, ldm.loadedDegrees);
                if (json_programs != null)
                    ParserUtils.ParseProgramsData(json_programs, ldm.loadedPrograms);
                if (json_hashtags != null)
                    ParserUtils.ParseHashtagsData(json_hashtags, ldm.loadedHashTags);
                if (json_group_members != null)
                    ParserUtils.ParseMembersData(json_group_members, ldm.loadedGroupMembers);
                if (json_group_details != null)
                    ParserUtils.ParseGroupData(json_group_details, ldm.loadedGroupForDetail);
                if (json_group_threads != null)
                    ParserUtils.ParseDiscussionData(json_group_threads, ldm.loadedGroupThreads);

                if( json_app_user_groups != null)
                {
                    ParserUtils.ParseGroupData(json_app_user_groups, ldm.loadedAppUserGroups);
                }
                if (json_user_groups != null)
                    ParserUtils.ParseGroupData(json_user_groups, ldm.loadedUserGroups);
                if (json_user_threads != null)
                    ParserUtils.ParseDiscussionData(json_user_threads, ldm.loadedUserThreads);
                if (json_thread_details != null)
                    ParserUtils.ParseDiscussionData(json_thread_details, ldm.loadedThreadForDetail);
                if (json_thread_posts != null)
                    ParserUtils.ParsePostsData(json_thread_posts, ldm.loadedThreadPosts);
                if (json_group_categories != null)
                    ParserUtils.ParseGroupCategoryData(json_group_categories, ldm.loadedGroupCategories);
                if (json_department_members != null)
                    ParserUtils.ParseMembersData(json_department_members, ldm.loadedDepartmentMembers);
                if (json_department_details != null)
                    ParserUtils.ParseDepartmentData(json_department_details, ldm.loadedDepartmentForDetail);
                if (json_user_details != null )
                {
                    ParserUtils.ParseMembersData(json_user_details, ldm.loadedUserForDetail);
                    if( ldm.loadedUserForDetail.size() > 0) {
                        UserModel um = UserModel.getInstance();
                        User u = ldm.loadedUserForDetail.get(0);
                        if( u.userid.equals(um.user.userid)) {
                            um.user.degree = u.degree;
                            um.user.department = u.department;
                            um.user.program = u.program;
                            um.user.interests = u.interests;
                            um.user.showEmailId = u.showEmailId;
                            um.user.showMobileNo = u.showMobileNo;
                        }
                    }
                }
                if (json_starred_threads != null) {

                    if (LoadDataModel.starredThreadsList != null) {
                        ParserUtils.ParseStarData(json_starred_threads, LoadDataModel.starredThreadsList);
                        ParserUtils.ParseDiscussionData(json_starred_threads,ldm.loadedStarredThreads);
                    }
                }
            }
        return mJSONObjArray;
    }


    @Override
    protected void onPostExecute(Map<String,JSONObject> jsonObjectArray) {

        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (LoadDataModel.isNetworkCheckSuccessful) {

            try {

                    if (LoadDataModel.isCurrentDataLoadFinished == true) {
                        return;
                    }

                    if(activity.isDestroyed() || activity.isFinishing())
                    {
                        return;
                    }

                    if(jsonObjectArray == null)
                    {
                        return;
                    }

                    if (loadContext == LoadDataModel.LoadContext.LOAD_INITIAL_DATA &&
                            json_departments != null &&
                            json_groups != null &&
                            json_threads != null) {

                        if (json_departments.getString(DataModel.KEY_SUCCESS) != null &&
                                json_groups.getString(DataModel.KEY_SUCCESS) != null &&
                                json_threads.getString(DataModel.KEY_SUCCESS) != null) {

                            String result_departments = json_departments.getString(DataModel.KEY_SUCCESS);
                            String result_groups = json_groups.getString(DataModel.KEY_SUCCESS);
                            String result_threads = json_threads.getString(DataModel.KEY_SUCCESS);

                            if (Integer.parseInt(result_departments) == 1 &&
                                    Integer.parseInt(result_groups) == 1 &&
                                    Integer.parseInt(result_threads) == 1) {
                                //pDialog.setMessage("Group Created Successfully ...");
                                //pDialog.setTitle("Getting Data");

                                LoadDataModel ldm = LoadDataModel.getInstance();

                                Bitmap b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.group_icon_medium);
                                if( b != null) {
                                    b = ImageUtils.scaleImageTo(b, 100, 100);
                                    RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                                    ldm.loadedDefaultGroupThumb = rd;
                                }

                                b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.department_logo);
                                if( b != null) {
                                    b = ImageUtils.scaleImageTo(b, 100, 100);
                                    RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                                    ldm.loadedDefaultDepartmentThumb = rd;
                                }

                                DatabaseHelper db = DatabaseHelper.getInstance(activity);
                                db.updateRefreshTimeStamp();

                                //pDialog.dismiss();
                                showToast("Welcome to LDCE Community");
                                Intent intent = new Intent(activity, ExploreCommunity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);

                            } else {
                                //pDialog.dismiss();
                                ApplicationUtils.showWarningDialog(activity,"Data Load Failed",null);
                            }
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEGREES_PROGRAMS &&
                            json_degrees != null &&
                            json_programs != null &&
                            json_departments != null) {
                        if (json_degrees.getString(DataModel.KEY_SUCCESS) != null &&
                                json_programs.getString(DataModel.KEY_SUCCESS) != null &&
                                json_departments.getString(DataModel.KEY_SUCCESS) != null) {

                            String result_degrees = json_degrees.getString(DataModel.KEY_SUCCESS);
                            String result_programs = json_programs.getString(DataModel.KEY_SUCCESS);
                            String result_departments = json_departments.getString(DataModel.KEY_SUCCESS);

                            if (Integer.parseInt(result_degrees) == 1 &&
                                    Integer.parseInt(result_programs) == 1 &&
                                    Integer.parseInt(result_departments) == 1) {
                                //pDialog.setMessage("Group Created Successfully ...");
                                //pDialog.setTitle("Getting Data");

                                //pDialog.dismiss();
                                showToast("Welcome to LDCE Community");
/*                        Intent intent = new Intent(activity, ExploreCommunity.class);
                        activity.startActivity(intent);*/

                            } else {
                                //pDialog.dismiss();
                                ApplicationUtils.showWarningDialog(activity, "Data Load Failed", null);
                            }
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEGREES_PROGRAMS &&
                            json_degrees == null &&
                            json_programs == null) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_INITIAL_DATA &&
                            json_departments == null &&
                            json_groups == null &&
                            json_threads == null) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_DETAILS &&
                            json_group_members != null &&
                            json_group_threads != null &&
                            json_group_details != null) {
                        if (Integer.parseInt(json_group_members.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_group_threads.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_group_details.getString(DataModel.KEY_SUCCESS)) == 1) {
                            loadContext = LoadDataModel.LoadContext.LOAD_NONE;

                            if(activity.getClass() == SearchActivity.class)
                            {
                                activity.finish();
                            }

                            LoadDataModel ldm = LoadDataModel.getInstance();
                            // this will download all thumbs for students
                            if( ldm.loadedGroupMembers.size() > 0) {
                                ArrayList<String> userNames = ParserUtils.getUsernameArrayFromUserArray(ldm.loadedGroupMembers);
                                ArrayList<String> fileNames = ParserUtils.getFilenamesFromUserNames(userNames, "_thumb");
                                ArrayList<String> filePaths = ParserUtils.getUploadFilePathsFromFilenames(fileNames);

                                new DownloadImages(activity, filePaths, DownloadImages.DownloadImagesContext.DOWNLOAD_STUDENT_THUMBS).execute();
                            }

                            Intent intent = new Intent(activity, GroupViewActivity.class);
                            intent.putExtra("title", LoadDataModel.loadGroupName);
                            intent.putExtra("caller", activity.getClass().toString());
                            activity.startActivity(intent);

                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_DETAILS &&
                            json_group_members == null &&
                            json_group_threads == null &&
                            json_group_details == null) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }else if (loadContext == LoadDataModel.LoadContext.LOAD_REFRESH_GROUP_DETAILS &&
                            json_group_members != null &&
                            json_group_threads != null &&
                            json_group_details != null) {
                        if (Integer.parseInt(json_group_members.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_group_threads.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_group_details.getString(DataModel.KEY_SUCCESS)) == 1) {


                            LoadDataModel ldm = LoadDataModel.getInstance();

                            if( activity.getClass() == GroupViewActivity.class)
                            {
                                Group g = null;
                                if( ldm.loadedGroupForDetail.size() > 0)
                                {
                                    g = ldm.loadedGroupForDetail.get(0);
                                }

                                GroupDetailFragment gf = (GroupDetailFragment) ((GroupViewActivity) activity).adapter.getItem(2);
                                if(gf != null && g != null && gf.admin != null && gf.category != null && gf.about != null)
                                {
                                    gf.admin.setText(g.adminname);
                                    gf.about.setText(g.descritpion);
                                    gf.category.setText(g.category);

                                }

                                StudentFragment sf = (StudentFragment) ((GroupViewActivity) activity).adapter.getItem(ApplicationUtils.GROUPVIEW_MEMBERS_TAB_INDEX);
                                if(sf != null && sf.adapter != null)
                                {
                                    sf.adapter.notifyDataSetChanged();
                                }

                                DiscussionFragment df = (DiscussionFragment) ((GroupViewActivity) activity).adapter.getItem(ApplicationUtils.GROUPVIEW_POSTS_TAB_INDEX);
                                if(df != null && df.adapter!= null)
                                {
                                    df.adapter.notifyDataSetChanged();
                                }

                                if( g != null) {
                                    ((GroupViewActivity) activity).mGroupTitle.setText(g.name);
                                    ((GroupViewActivity) activity).mMembersCount.setText(g.nummembers + " Members");
                                    ((GroupViewActivity) activity).mPostsCount.setText(g.numthreads+ " Posts");
                                }


                                // this will download all thumbs for students
                                if( ldm.loadedGroupMembers.size() > 0) {
                                    ArrayList<String> userNames = ParserUtils.getUsernameArrayFromUserArray(ldm.loadedGroupMembers);
                                    ArrayList<String> fileNames = ParserUtils.getFilenamesFromUserNames(userNames, "_thumb");
                                    ArrayList<String> filePaths = ParserUtils.getUploadFilePathsFromFilenames(fileNames);

                                    new DownloadImages(activity, filePaths, DownloadImages.DownloadImagesContext.DOWNLOAD_STUDENT_THUMBS).execute();
                                }
                            }

                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }
                    else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_DETAILS &&
                            json_user_threads != null &&
                            json_user_groups != null) {
                        if (Integer.parseInt(json_user_threads.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_user_groups.getString(DataModel.KEY_SUCCESS)) == 1) {

                            if(activity.getClass() == SearchActivity.class)
                            {
                                activity.finish();
                            }

                            loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                            Intent intent = new Intent(activity, StudentProfileActivity.class);
                            intent.putExtra("title", LoadDataModel.loadUserName);
                            /*if( LoadDataModel.studentProfileCaller == "navigator")
                            {
                                intent.putExtra("caller", "navigator");
                            }
                            else
                            {
                                intent.putExtra("caller", activity.getClass().toString());
                            }

                            if( LoadDataModel.activeStudentProfileTab >= 0 )
                            {
                                intent.putExtra("tab", String.valueOf(LoadDataModel.activeStudentProfileTab));
                            }
                            else
                            {
                                LoadDataModel.activeStudentProfileTab = 0;
                                intent.putExtra("tab", String.valueOf(LoadDataModel.activeStudentProfileTab));
                            }*/

                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            activity.startActivity(intent);

                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_DETAILS &&
                            ( json_user_threads == null ||
                            json_user_groups == null )) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS_THREADS &&
                            json_user_threads != null &&
                            json_user_groups != null) {
                        if (Integer.parseInt(json_user_threads.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_user_groups.getString(DataModel.KEY_SUCCESS)) == 1) {

                            if(activity.getClass() == SearchActivity.class)
                            {
                                activity.finish();
                            }

                            loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                            Intent intent = new Intent(activity, MyContentActivity.class);
                            intent.putExtra("title", LoadDataModel.loadUserName);

                            if( LoadDataModel.activeStudentProfileTab >= 0 )
                            {
                                intent.putExtra("tab", LoadDataModel.activeStudentProfileTab);
                            }
                            else
                            {
                                LoadDataModel.activeStudentProfileTab = 0;
                                intent.putExtra("tab", LoadDataModel.activeStudentProfileTab);
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            activity.startActivity(intent);
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS_THREADS &&
                            ( json_user_threads == null || json_user_groups == null )) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }
                    else if (loadContext == LoadDataModel.LoadContext.LOAD_THREAD_DETAILS &&
                            json_thread_details != null &&
                            json_thread_posts != null) {
                        if (Integer.parseInt(json_thread_details.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_thread_posts.getString(DataModel.KEY_SUCCESS)) == 1) {

                            if(activity.getClass() == SearchActivity.class)
                            {
                                activity.finish();
                            }

                            loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                            Intent intent = new Intent(activity, DiscussionViewActivity.class);
                            intent.putExtra("title", LoadDataModel.loadThreadTitle);
                            intent.putExtra("description", LoadDataModel.loadThreadDescription);
                            intent.putExtra("caller", activity.getClass().toString());

                            activity.startActivity(intent);
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }else if (loadContext == LoadDataModel.LoadContext.LOAD_REFRESH_THREAD_DETAILS &&
                            json_thread_details != null &&
                            json_thread_posts != null) {
                        if (Integer.parseInt(json_thread_details.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_thread_posts.getString(DataModel.KEY_SUCCESS)) == 1) {

                            LoadDataModel ldm = LoadDataModel.getInstance();
                            ldm.loadedThreadForDetail.clear();
                            ParserUtils.ParseDiscussionData(json_thread_details, ldm.loadedThreadForDetail);

                            Discussion d = null;

                            if( ldm.loadedThreadForDetail.size() > 0 )
                                d = ldm.loadedThreadForDetail.get(0);

                            ldm.loadedThreadPosts.clear();
                            ParserUtils.ParsePostsData(json_thread_posts,ldm.loadedThreadPosts);

                            if(activity.getClass() == DiscussionViewActivity.class)
                            {
                                ((DiscussionViewActivity)activity).threadDescription.setText(d.description);
                                ((DiscussionViewActivity)activity).threadTitle.setText(d.title);
                                PostMessageFragment pf = (PostMessageFragment)((DiscussionViewActivity) activity).adapter.getItem(0);
                                pf.adapter.notifyDataSetChanged();
                            }
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }
                    else if (loadContext == LoadDataModel.LoadContext.LOAD_THREAD_DETAILS &&
                            json_thread_details == null &&
                            json_thread_posts == null) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUPCATEGORIES &&
                            json_group_categories != null) {
                        if (Integer.parseInt(json_group_categories.getString(DataModel.KEY_SUCCESS)) == 1) {

                            if(activity.getClass() == CreateGroupActivity.class)
                                ((CreateGroupActivity)activity).spinneradapter.notifyDataSetChanged();
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if ((loadContext == LoadDataModel.LoadContext.LOAD_THREADS && json_threads != null) ||
                            (loadContext == LoadDataModel.LoadContext.LOAD_USER_THREADS && json_user_threads != null) ||
                            (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_THREADS && json_group_threads != null)) {

                        if (( json_threads != null && Integer.parseInt(json_threads.getString(DataModel.KEY_SUCCESS)) == 1 ) ||
                                ( json_user_threads != null && Integer.parseInt(json_user_threads.getString(DataModel.KEY_SUCCESS)) == 1 ) ||
                                (json_group_threads != null && Integer.parseInt(json_group_threads.getString(DataModel.KEY_SUCCESS)) == 1 )) {

                            DiscussionFragment gf = null;

                            if (loadContext == LoadDataModel.LoadContext.LOAD_THREADS && activity.getClass() == ExploreCommunity.class)
                                gf = (DiscussionFragment) ((ExploreCommunity) activity).adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_POSTS_TAB_INDEX);
                            else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_THREADS && activity.getClass() == MyContentActivity.class) {
                                gf = (DiscussionFragment) ((MyContentActivity) activity).adapter.getItem(1);
                            }
                            else if (loadContext == LoadDataModel.LoadContext.LOAD_GROUP_THREADS && activity.getClass() == GroupViewActivity.class)
                                gf = (DiscussionFragment) ((GroupViewActivity) activity).adapter.getItem(ApplicationUtils.GROUPVIEW_POSTS_TAB_INDEX);

                            if(gf != null)
                            {
                                gf.adapter.notifyDataSetChanged();
                                if (gf.mSwipeRefreshLayout != null)
                                    gf.mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if ((loadContext == LoadDataModel.LoadContext.LOAD_GROUPS && json_groups != null) ||
                            (loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS && json_user_groups != null)) {
                        if ((json_groups != null && Integer.parseInt(json_groups.getString(DataModel.KEY_SUCCESS)) == 1 )||
                                ( json_user_groups != null && Integer.parseInt(json_user_groups.getString(DataModel.KEY_SUCCESS)) == 1 )) {

                            GroupFragment gf = null;

                            if (loadContext == LoadDataModel.LoadContext.LOAD_GROUPS && activity.getClass() == ExploreCommunity.class)
                                gf = (GroupFragment) ((ExploreCommunity) activity).adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_GROUPS_TAB_INDEX);
                            else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_GROUPS && activity.getClass() == MyContentActivity.class) {
                                gf = (GroupFragment) ((MyContentActivity) activity).adapter.getItem(0);
                            }
                            else
                                gf = null;

                            if(gf != null)
                            {
                                gf.adapter.notifyDataSetChanged();

                                if (gf.mSwipeRefreshLayout != null)
                                    gf.mSwipeRefreshLayout.setRefreshing(false);
                            }

                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if ((loadContext == LoadDataModel.LoadContext.LOAD_THREAD_POSTS && json_thread_posts != null)) {
                        if (Integer.parseInt(json_thread_posts.getString(DataModel.KEY_SUCCESS)) == 1) {

                            PostMessageFragment pf;

                            if (loadContext == LoadDataModel.LoadContext.LOAD_THREAD_POSTS && activity.getClass() == DiscussionViewActivity.class)
                                pf = (PostMessageFragment) ((DiscussionViewActivity) activity).adapter.getItem(0);
                            else
                                pf = null;

                            if( pf != null)
                            {
                                pf.adapter.notifyDataSetChanged();

                                if (pf.mSwipeRefreshLayout != null)
                                    pf.mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENT_DETAILS &&
                            json_department_details != null &&
                            json_department_members != null) {
                        if (Integer.parseInt(json_department_details.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_department_members.getString(DataModel.KEY_SUCCESS)) == 1) {
                            LoadDataModel ldm = new LoadDataModel();

                            if(activity.getClass() == SearchActivity.class)
                            {
                                activity.finish();
                            }

                            if (ldm.loadedDepartmentForDetail.size() > 0) {
                                Department d = ldm.loadedDepartmentForDetail.get(0);
                                if (d != null)
                                    LoadDataModel.loadDepartmentNumMembers = d.nummembers;
                            }

                            Intent intent = new Intent(activity, DepartmentView.class);
                            intent.putExtra("title", LoadDataModel.loadDepartmentTitle);
                            intent.putExtra("caller", activity.getClass().toString());
                            intent.putExtra("subtitle", LoadDataModel.loadDepartmentNumMembers + " Students");
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            activity.startActivity(intent);
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_DEPARTMENT_MEMBERS &&
                            json_department_members != null) {

                        if (Integer.parseInt(json_department_members.getString(DataModel.KEY_SUCCESS)) == 1) {
                            StudentFragment sf = (StudentFragment) ((DepartmentView) activity).adapter.getItem(1);
                            sf.adapter.notifyDataSetChanged();
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }else if ( loadContext == LoadDataModel.LoadContext.LOAD_GROUP_MEMBERS)
                    {
                        if (Integer.parseInt(json_group_members.getString(DataModel.KEY_SUCCESS)) == 1) {
                            LoadDataModel ldm = LoadDataModel.getInstance();
                            if( ldm.loadedGroupMembers.size() > 0) {
                                ArrayList<String> userNames = ParserUtils.getUsernameArrayFromUserArray(ldm.loadedGroupMembers);
                                ArrayList<String> fileNames = ParserUtils.getFilenamesFromUserNames(userNames, "_thumb");
                                ArrayList<String> filePaths = ParserUtils.getUploadFilePathsFromFilenames(fileNames);

                                new DownloadImages(activity, filePaths, DownloadImages.DownloadImagesContext.DOWNLOAD_STUDENT_THUMBS).execute();

                                if( activity.getClass() == GroupViewActivity.class)
                                {
                                    StudentFragment sf = (StudentFragment) ((GroupViewActivity)activity).adapter.getItem(ApplicationUtils.GROUPVIEW_MEMBERS_TAB_INDEX);
                                    if( sf != null)
                                        sf.adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                    else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_DATA &&
                            json_user_details != null) {
                        if (Integer.parseInt(json_user_details.getString(DataModel.KEY_SUCCESS)) == 1 ) {

                            if(activity.getClass() == SearchActivity.class)
                            {
                                activity.finish();
                            }
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_USER_DATA &&
                            json_user_details == null ) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    }else if (loadContext == LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA &&
                            json_programs != null && json_degrees != null && json_departments_spinner != null && json_hashtags != null
                            ) {
                        if (Integer.parseInt(json_programs.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_degrees.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_departments_spinner.getString(DataModel.KEY_SUCCESS)) == 1 &&
                                Integer.parseInt(json_hashtags.getString(DataModel.KEY_SUCCESS)) == 1 ) {

                            LoadDataModel ldm = LoadDataModel.getInstance();

                            Intent intent = new Intent(activity,PreferenceActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            activity.startActivity(intent);
                        }
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if (loadContext == LoadDataModel.LoadContext.LOAD_PREFERENCES_DATA &&
                            (json_programs == null || json_degrees == null || json_hashtags == null
                                     || json_departments_spinner == null )) {
                        ApplicationUtils.showWarningDialog(activity, "Unable to load data", null);
                        loadContext = LoadDataModel.LoadContext.LOAD_NONE;
                    } else if( loadContext == LoadDataModel.LoadContext.LOAD_STARRED_THREADS)
                    {
                        if (json_starred_threads != null && Integer.parseInt(json_starred_threads.getString(DataModel.KEY_SUCCESS)) == 1 ) {
                            Intent newintent = new Intent(activity, StarredThreadsActivity.class);
                            newintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            activity.startActivity(newintent);
                        }
                    }
                    else if ( loadContext == LoadDataModel.LoadContext.LOAD_SERVERTIME)
                    {
                        if( json_get_servertime != null && json_get_servertime.has(DataModel.KEY_SUCCESS) && Integer.parseInt(json_get_servertime.getString(DataModel.KEY_SUCCESS)) == 1) {
                            try{
                                if (json_get_servertime.has("servertime")) {
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    LoadDataModel.serverTime = format.parse(json_get_servertime.getString("servertime"));
                                }
                            }
                            catch(ParseException p)
                            {
                                p.printStackTrace();
                            }
                        }
                    }
                    else if ( loadContext == LoadDataModel.LoadContext.LOAD_GET_FILE_SYNCTIME)
                    {
                        if( json_get_file_synctime != null && json_get_file_synctime.has(DataModel.KEY_SUCCESS) && Integer.parseInt(json_get_file_synctime.getString(DataModel.KEY_SUCCESS)) == 1) {
                            try{
                                if (json_get_file_synctime.has("servertime")) {
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date synctime = format.parse(json_get_file_synctime.getString("servertime"));
                                    String filename = json_get_file_synctime.getString("filename");
                                    DatabaseHelper db = DatabaseHelper.getInstance(activity);
                                    db.setDownloadFileSyncTimeStamp(filename,synctime);
                                }
                            }
                            catch(ParseException p)
                            {
                                p.printStackTrace();
                            }
                        }
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }

                LoadDataModel.isCurrentDataLoadFinished = true;

                if (activity.getClass() == EditProfileActivity.class) {
                    ((EditProfileActivity) activity).padapter.notifyDataSetChanged();
                    ((EditProfileActivity) activity).dadapter.notifyDataSetChanged();
                    ((EditProfileActivity) activity).departmentadapter.notifyDataSetChanged();
                }

            }

        if( pDialog != null)
            pDialog.dismiss();

        if (progressBar != null) {
            // Making progress bar visible
            progressBar.setVisibility(View.INVISIBLE);
        }

        handler.removeCallbacks(myRunnable);
    }

}