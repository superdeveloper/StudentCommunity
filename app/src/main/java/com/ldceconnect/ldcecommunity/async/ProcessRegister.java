package com.ldceconnect.ldcecommunity.async;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.ldceconnect.ldcecommunity.EditProfileActivity;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nevil on 2/3/2016.
 */
public class ProcessRegister extends AsyncTask<String, String, JSONObject> {

    /**
     * Defining Process dialog
     **/
    private ProgressDialog pDialog;

    public String email,password,fname,lname,uname;

    public AppCompatActivity activity;


    public ProcessRegister(AppCompatActivity activity, String email,String password,String fname,String lname, String uname)
    {
        this.email = email;
        this.password = password;
        this.activity = activity;
        this.fname = fname;
        this.lname = lname;
        this.uname = uname;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        boolean netcheckResult= false;
        LoadDataModel.isNetworkCheckSuccessful = false;

        try
        {
            netcheckResult = new com.ldceconnect.ldcecommunity.async.NetCheck(activity).execute().get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex2) {
            ex2.printStackTrace();
        } catch ( CancellationException ce)
        {
            ce.printStackTrace();
        }

        if(netcheckResult == true) {
            LoadDataModel.isNetworkCheckSuccessful = true;


            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            LoadDataModel.isCurrentDataLoadFinished = false;

        }
    }

    @Override
    protected JSONObject doInBackground(String... args) {


        UserFunctions userFunction = new UserFunctions();
        JSONObject json = userFunction.registerUser(fname, lname, email, uname, password);

        return json;


    }
    @Override
    protected void onPostExecute(JSONObject json) {
        /**
         * Checks for success message.
         **/
        try {
            if (json.getString(DataModel.KEY_SUCCESS) != null) {

                String res = json.getString(DataModel.KEY_SUCCESS);

                String red = json.getString(DataModel.KEY_ERROR);

                if(Integer.parseInt(res) == 1){
                    //pDialog.setTitle("Getting Data");
                    //pDialog.setMessage("Loading Info");

                    DatabaseHelper db = DatabaseHelper.getInstance(activity.getApplicationContext());
                    JSONObject json_user = json.getJSONObject("user");

                    /**
                     * Removes all the previous data in the SQlite database
                     **/

                    UserFunctions logout = new UserFunctions();
                    logout.logoutUser(activity.getApplicationContext());
                    db.addUser(json_user.getString(DataModel.LOGIN_KEY_FIRSTNAME), json_user.getString(DataModel.LOGIN_KEY_LASTNAME), json_user.getString(DataModel.LOGIN_KEY_EMAIL), json_user.getString(DataModel.LOGIN_KEY_USERNAME), json_user.getString(DataModel.LOGIN_KEY_UID), json_user.getString(DataModel.LOGIN_KEY_USERID), json_user.getString(DataModel.LOGIN_KEY_IDATE));

                    UserModel um = UserModel.getInstance();
                    um.user.email = json_user.getString("email");
                    um.user.fname = json_user.getString("fname");
                    um.user.lname = json_user.getString("lname");
                    um.user.userid = json_user.getString("email");

                    /**
                     * Stores registered data in SQlite Database
                     * Launch Registered screen
                     **/
                    Intent registered = new Intent(activity.getApplicationContext(), EditProfileActivity.class);
                    registered.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(registered);
                    activity.finish();
                }

                else if (Integer.parseInt(red) ==2){
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Email already exists", null);
                }
                else if (Integer.parseInt(red) ==3){
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Invalid Email id", null);
                }

            }


            else{
                //pDialog.dismiss();
                ApplicationUtils.showWarningDialog(activity,"Error occured while registering",null);
            }

        } catch (JSONException e) {
            e.printStackTrace();


        }

        pDialog.dismiss();
    }}