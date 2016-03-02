package com.ldceconnect.ldcecommunity.async;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ldceconnect.ldcecommunity.EditProfileActivity;
import com.ldceconnect.ldcecommunity.LoadDataActivity;
import com.ldceconnect.ldcecommunity.LoginActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.async.NetCheck;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Async Task to get and send data to My Sql database through JSON respone.
 **/
public class ProcessLogin extends AsyncTask<String, String, JSONObject> {

    private GoogleApiClient mGoogleApiClient;
    public GoogleSignInAccount mGoogleSignInAccount;

    private String email,password;
    private ProgressDialog pDialog;
    public AppCompatActivity activity;

    public LoadDataModel.LoginContext mLoginContext;

    private MaterialDialog.SingleButtonCallback forgotPasswordStatusDialogCallback;

    public ProcessLogin(AppCompatActivity activity ,LoadDataModel.LoginContext loginContext, String email,String password)
    {
        mLoginContext = loginContext;
        this.email = email;
        this.password = password;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        boolean netcheckResult= false;
        LoadDataModel.isNetworkCheckSuccessful = false;

        ConnectionDetector cd = new ConnectionDetector(activity);
        netcheckResult = cd.isConnectingToInternet();

        /*try
        {
            netcheckResult = new NetCheck(activity).execute().get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex2) {
            ex2.printStackTrace();
        } catch ( CancellationException ce)
        {
            ce.printStackTrace();
        }*/

        if(netcheckResult == true) {
            LoadDataModel.isNetworkCheckSuccessful = true;


            forgotPasswordStatusDialogCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {

                    ApplicationUtils.showResetPasswordDialog(activity);

                }
            };

            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            LoadDataModel.isCurrentDataLoadFinished = false;
        }

        //showProgress(true);
    }

    @Override
    protected JSONObject doInBackground(String... args) {

        JSONObject json = null;
        UserFunctions userFunction = new UserFunctions();
        if(mLoginContext == LoadDataModel.LoginContext.LOGIN_LOGIN) {
            json = userFunction.getUser(email);
            try {
                if (json != null && json.getString(DataModel.KEY_SUCCESS) != null /*&& mLoginContext == LoadDataModel.LoginContext.LOGIN_GETUSER*/) {
                    JSONArray ja = json.getJSONArray("members");

                    if( ja.length() > 0 )
                    {
                        JSONObject jo = ja.getJSONObject(0);

                        String forgotpassrequest = jo.getString("passwordrecoveryrequest");

                        if( forgotpassrequest != null && ( forgotpassrequest.equals("0") || forgotpassrequest.equals("null")))
                        {
                            json = userFunction.loginUser(email, password);
                        }
                        else if( forgotpassrequest != null && forgotpassrequest.equals("1"))
                        {
                            ApplicationUtils.showMessageDialog(activity,"You password has been reset. Please change your password.",forgotPasswordStatusDialogCallback, R.drawable.warning_icon1);
                            json = null;
                        }
                    }
                }
            }
            catch(JSONException je)
            {
                je.printStackTrace();
            }

        }
        else if( mLoginContext == LoadDataModel.LoginContext.LOGIN_GETUSER)
            json = userFunction.getUser(email);

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject json) {

        try {
            LoadDataModel.isCurrentDataLoadFinished = true;
            if (json != null && json.getString(DataModel.KEY_SUCCESS) != null && mLoginContext == LoadDataModel.LoginContext.LOGIN_LOGIN) {

                String res = json.getString(DataModel.KEY_SUCCESS);

                if (Integer.parseInt(res) == 1) {
                        /*pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");*/

                    //mLoginErrorView.setText("Logged in Successfully ...");
                    DatabaseHelper db = DatabaseHelper.getInstance(activity);
                    //JSONObject json_user = json.getJSONObject("user");

                    ArrayList<User> userArray = new ArrayList<>();
                    ParserUtils.ParseMembersData(json, userArray);
                    User u = null;
                    if (userArray.size() > 0)
                        u = userArray.get(0);

                    UserFunctions logout = new UserFunctions();
                    logout.logoutUser(activity);
                    db.addUser(u.fname, u.lname, u.email, u.username, u.userid, u.userid, u.idate.toString());
                    LoadDataModel.loadUserId = u.userid;

                    /**
                     * Clear all previous data in SQlite database.
                     **/
                    if (u != null && u.forgotpassrequest != null && u.forgotpassrequest.equals("0")) {

                        /**
                         *If JSON array details are stored in SQlite it launches the User Panel.
                         **/
                        Intent upanel = new Intent(activity, LoadDataActivity.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(upanel);

                        /**
                         * Close Login Screen
                         **/
                        if( pDialog != null)
                            pDialog.dismiss();
                        activity.finish();

                    } else if (u.forgotpassrequest != null && u.forgotpassrequest.equals("1")) {
                        ApplicationUtils.showMessageDialog(activity, "You password has been reset. Please change your password.", forgotPasswordStatusDialogCallback,R.drawable.warning_icon1);
                        if( pDialog != null)
                            pDialog.dismiss();
                    }
                }
                else
                {
                    if( pDialog != null)
                        pDialog.dismiss();
                    String msg = "Incorrect username/password";
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }

            } else if (json != null && json.getString(DataModel.KEY_SUCCESS) != null && mLoginContext == LoadDataModel.LoginContext.LOGIN_GETUSER) {

                String res_success = json.getString(DataModel.KEY_SUCCESS);
                String res_error = json.getString(DataModel.KEY_ERROR);

                if (Integer.parseInt(res_success) == 1) {
                    if (res_error != null && Integer.parseInt(res_error) == 0) {

                        //mLoginErrorView.setText("Logged in Successfully ...");
                        DatabaseHelper db = DatabaseHelper.getInstance(activity);
                        //JSONArray json_user_array = json.getJSONArray("user");

                        ArrayList<User> userArray = new ArrayList<>();

                        ParserUtils.ParseMembersData(json, userArray);

                        User u = null;
                        if (userArray.size() > 0)
                            u = userArray.get(0);

                        //UserFunctions logout = new UserFunctions();
                        //logout.logoutUser(activity);

                        /**
                         * Clear all previous data in SQlite database.
                         **/
                        if (u != null && u.forgotpassrequest != null && u.forgotpassrequest.equals("0")) {

                                db.addUser(u.fname, u.lname, u.email, u.username, u.userid, u.userid, u.idate.toString());
                                LoadDataModel.loadUserId = u.userid;

                                /**
                                 *If JSON array details are stored in SQlite it launches the User Panel.
                                 **/
                                Intent upanel = new Intent(activity, LoadDataActivity.class);
                                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(upanel);

                                /**
                                 * Close Login Screen
                                 **/
                                if( pDialog != null)
                                    pDialog.dismiss();
                                activity.finish();

                        } else if ( u != null && u.forgotpassrequest != null && u.forgotpassrequest.equals("1")) {
                            db.addUser(u.fname, u.lname, u.email, u.username, u.userid, u.userid, u.idate.toString());
                            LoadDataModel.loadUserId = u.userid;
                            ApplicationUtils.showMessageDialog(activity, "You have requested password reset. Please change your password.", forgotPasswordStatusDialogCallback,R.drawable.warning_icon1);
                            if( pDialog != null)
                                pDialog.dismiss();
                        }

                    } else if (res_error != null && Integer.parseInt(res_error) == 1) {
                        String msg = "You are not registered. Please register.";
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(activity, EditProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if( activity.getClass() == LoginActivity.class) {
                            UserModel um = UserModel.getInstance();
                            if( um.user != null) {
                                intent.putExtra("GoogleAccount", um.user.email);
                                intent.putExtra("GoogleAccountName", um.user.fname);
                            }
                            activity.startActivity(intent);
                        }

                        /**
                         * Close Login Screen
                         **/
                        if( pDialog != null)
                            pDialog.dismiss();
                        //activity.finish();
                    }
                }
                else
                {
                    if( pDialog != null)
                        pDialog.dismiss();
                    String msg = "Incorrect username/password";
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                }

            } else {
                if( pDialog != null)
                    pDialog.dismiss();
            }

        }catch(JSONException je)
        {
            je.printStackTrace();
        }

        if( pDialog != null)
            pDialog.dismiss();
    }
}