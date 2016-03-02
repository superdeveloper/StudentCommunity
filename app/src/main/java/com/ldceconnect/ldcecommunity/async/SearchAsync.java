package com.ldceconnect.ldcecommunity.async;

/**
 * Created by Nevil on 1/6/2016.
 */

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Async Task to get and send data to My Sql database through JSON respone.
 **/
public class SearchAsync extends AsyncTask<String, Integer, Boolean> {


    private ProgressDialog pDialog;
    private AppCompatActivity activity;
    String grouptitle,groupinfo,groupadmin;

    private Map<String,JSONObject> mJSONObjArray = new HashMap<>();
    private MatrixCursor mCursor;
    private JSONObject json_departments;
    private JSONObject json_groups;
    private JSONObject json_threads;
    private JSONObject json_members;
    private JSONObject json_degrees;
    private JSONObject json_programs;
    private JSONObject json_hashtags;
    private ApplicationModel.SearchContext mSearchContext;

    public SearchAsync(AppCompatActivity activity,ApplicationModel.SearchContext searchContext, MatrixCursor cursor)
    {
        this.activity = activity;
        this.mSearchContext = searchContext;
        this.mCursor = cursor;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LoadDataModel.isCurrentSearchFinished = false;
        pDialog = new ProgressDialog(this.activity);
        pDialog.setMessage("Searching ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        LoadDataModel ldm = LoadDataModel.getInstance();

        if( mSearchContext == ApplicationModel.SearchContext.SEARCH_USER)
        {
            ldm.loadSearchUsers.clear();
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_DEPARTMENT)
        {
            ldm.loadSearchDepartments.clear();
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_GROUP)
        {
            ldm.loadSearchGroups.clear();
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_THREAD)
        {
            ldm.loadSearchThreads.clear();
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_POST)
        {
            ldm.loadSearchPosts.clear();
        }
    }

    @Override
    protected Boolean doInBackground(String... args) {
        UserFunctions userFunction = new UserFunctions();
        UserModel user = UserModel.getInstance();
        LoadDataModel ldm = LoadDataModel.getInstance();


        if(mSearchContext == ApplicationModel.SearchContext.SEARCH_USER)
        {
            userFunction.searchUser(LoadDataModel.searchQuery, mCursor);
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_DEPARTMENT)
        {
            userFunction.searchDepartment(LoadDataModel.searchQuery);
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_GROUP)
        {
            userFunction.searchGroup(LoadDataModel.searchQuery);
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_THREAD)
        {
            userFunction.searchThread(LoadDataModel.searchQuery);
        }else if( mSearchContext == ApplicationModel.SearchContext.SEARCH_POST)
        {
            userFunction.searchGroup(LoadDataModel.searchQuery);
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean bool) {

            LoadDataModel.isCurrentSearchFinished = true;
            pDialog.dismiss();

    }

    void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    void showToastLong(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }
}
