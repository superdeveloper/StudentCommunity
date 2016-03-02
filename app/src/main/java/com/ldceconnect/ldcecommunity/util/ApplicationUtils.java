package com.ldceconnect.ldcecommunity.util;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ldceconnect.ldcecommunity.LoadDataActivity;
import com.ldceconnect.ldcecommunity.LoginActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by a8qymn on 1/4/2016.
 */
public class ApplicationUtils {

    public static RelativeLayout messageLayout;
    public static TextView messageText;
    public static ImageView messageImage;
    public static EditText oldPassword;
    public static EditText newPassword;
    public static EditText confrimPassword;
    public static TextView errorMsg;
    public static int EXPLORE_COMMUNITY_POSTS_TAB_INDEX = 0;
    public static int EXPLORE_COMMUNITY_GROUPS_TAB_INDEX = 1;
    public static int EXPLORE_COMMUNITY_DEPARTMENTS_TAB_INDEX = 2;
    public static int GROUPVIEW_POSTS_TAB_INDEX = 0;
    public static int GROUPVIEW_MEMBERS_TAB_INDEX = 1;
    public static int GROUPVIEW_ABOUT_TAB_INDEX = 2;
    public static int MY_CONTENT_GROUPS_TAB_INDEX = 0;
    public static int MY_CONTENT_THREADS_TAB_INDEX = 0;

    public static void showDoneDialog( AppCompatActivity activity, String dontText)
    {
        if(activity.isDestroyed() || activity.isFinishing())
            return;

        messageLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.message_dialog, null);
        messageText = (TextView)messageLayout.findViewById(R.id.message_text);
        messageImage = (ImageView)messageLayout.findViewById(R.id.message_image);

        Bitmap b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.tick_green);

        if( b!= null)
            messageImage.setImageBitmap(b);

        messageText.setText(dontText);

        new MaterialDialog.Builder(activity)
                .customView(messageLayout,true)
                .autoDismiss(true)
                .show();
    }


    public static void showMessageDialog( AppCompatActivity activity, String message, MaterialDialog.SingleButtonCallback sb, int iconDrawableid)
    {
        if(activity.isDestroyed() || activity.isFinishing())
            return;

        messageLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.message_dialog, null);
        messageText = (TextView)messageLayout.findViewById(R.id.message_text);
        messageImage = (ImageView)messageLayout.findViewById(R.id.message_image);

        if( iconDrawableid != 0) {
            Bitmap b = BitmapFactory.decodeResource(activity.getResources(), iconDrawableid);

            if (b != null)
                messageImage.setImageBitmap(b);
        }

        messageText.setText(message);

        if( sb != null)
        {
            MaterialDialog m = new MaterialDialog.Builder(activity)
                    .customView(messageLayout, true)
                    .positiveText("Ok")
                    .onPositive(sb)
                    .show();
        }
        else
        {
            MaterialDialog m = new MaterialDialog.Builder(activity)
                    .customView(messageLayout, true)
                    .show();
        }
    }

    public static void showWarningDialog( AppCompatActivity activity, String message, MaterialDialog.SingleButtonCallback sb)
    {
        if(activity.isDestroyed() || activity.isFinishing())
            return;

        messageLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.message_dialog, null);
        messageText = (TextView)messageLayout.findViewById(R.id.message_text);
        messageImage = (ImageView)messageLayout.findViewById(R.id.message_image);

        Bitmap b = BitmapFactory.decodeResource(activity.getResources(), R.drawable.warning_icon_red);

        if( b!= null)
            messageImage.setImageBitmap(b);

        messageText.setText(message);

        MaterialDialog m = new MaterialDialog.Builder(activity)
                .customView(messageLayout, true)
                .show();

        if( sb != null)
        {
            m.getBuilder().positiveText("Ok");
            m.getBuilder().onPositive(sb);
        }
    }


    public static void showResetPasswordDialog( final AppCompatActivity activity)
    {
        if(activity.isDestroyed() || activity.isFinishing())
            return;

        messageLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.reset_password_dialog, null);
        oldPassword = (EditText) messageLayout.findViewById(R.id.reset_password_existing);
        newPassword = (EditText) messageLayout.findViewById(R.id.reset_password_new);
        confrimPassword = (EditText) messageLayout.findViewById(R.id.reset_password_new_confirm);
        errorMsg = (TextView)messageLayout.findViewById(R.id.error_msg);
        Button okButton = (Button) messageLayout.findViewById(R.id.reset_password_ok_button);
        Button cancelButton = (Button) messageLayout.findViewById(R.id.reset_password_cancel_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passwordSuccess = false;
                try {

                    ArrayList<Object> data = new ArrayList<>();
                    String oldPass = oldPassword.getText().toString();
                    String newPass = newPassword.getText().toString();
                    String confirmPass = confrimPassword.getText().toString();

                    if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                        errorMsg.setText("Required fields are empty");
                    } else {
                        // temporary success
                        passwordSuccess = true;
                    }

                    UserModel um = UserModel.getInstance();
                    if (passwordSuccess == true) {

                        // Setting again to false
                        passwordSuccess = false;

                        if (newPass.equals(confirmPass)) {

                            data.add(um.user.email);
                            data.add(oldPass);
                            data.add(newPass);

                            Map<String, JSONObject> jsonObjectArray = new UploadDataAsync(activity, LoadDataModel.UploadContext.UPLOAD_RESET_PASSWORD, data).execute().get();

                            if (jsonObjectArray != null) {
                                JSONObject jsonObj = jsonObjectArray.get("resetpassword");

                                if (jsonObj != null && jsonObj.getString(DataModel.KEY_SUCCESS) != null &&
                                        Integer.parseInt(jsonObj.getString(DataModel.KEY_SUCCESS)) == 1) {

                                    passwordSuccess = true;
                                } else {

                                    errorMsg.setText("Incorrect password");
                                    //showWarningDialog(activity,"" , null);
                                }

                            }

                        }
                        // if passwords are not equal
                        else {
                            errorMsg.setText("Passwords do not match");
                            //showWarningDialog(activity, "Passwords do not match", null);
                        }
                    }

                } catch (InterruptedException ie) {

                } catch (ExecutionException ee) {

                } catch (JSONException je) {

                } catch (CancellationException ce) {
                    ce.printStackTrace();
                }

                if (passwordSuccess == true) {
                    try {
                        ArrayList<Object> data = new ArrayList<>();
                        UserModel um = UserModel.getInstance();
                        String email = um.user.email;
                        String status = "0";
                        data.add(email);
                        data.add(status);

                        Map<String, JSONObject> jsonObjectArray = new UploadDataAsync(activity, LoadDataModel.UploadContext.UPLOAD_FORGOT_PASSWORD_STATUS, data).execute().get();

                        if (jsonObjectArray != null) {

                            JSONObject jsonObj = jsonObjectArray.get("updateforgotpasswordstatus");

                            if (jsonObj != null && jsonObj.getString(DataModel.KEY_SUCCESS) != null &&
                                    Integer.parseInt(jsonObj.getString(DataModel.KEY_SUCCESS)) == 1) {

                            } else {
                                passwordSuccess = false;
                                ApplicationUtils.showWarningDialog(activity, "Password reset failed...", null);

                            }
                        } else {
                            passwordSuccess = false;
                            ApplicationUtils.showWarningDialog(activity, "Password reset failed...", null);
                        }

                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    } catch (ExecutionException ee) {
                        ee.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (passwordSuccess == true) {
                        // Load Data now
                        Intent upanel = new Intent(activity, LoadDataActivity.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(upanel);
                    }
                }
            }
        });

        final MaterialDialog m = new MaterialDialog.Builder(activity)
                .title("Change Password")
                .customView(messageLayout, true)
                .cancelable(false)
                .show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.dismiss();
                if(activity.getClass() == LoginActivity.class)
                    activity.finish();
            }
        });

    }

    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);

                        break;
                }
                return false;
            }
        });
    }

    public static int removeGroupFromArray(ArrayList<Group> groups,String groupid )
    {
        for( int i = 0 ; i < groups.size(); i++)
        {
            Group g = groups.get(i);
            if( g != null && g.id.equals(groupid) ) {
                groups.remove(i);
                ApplicationModel.removedGroupIndex = i;
                return i;
            }
        }

        return -1;
    }

    public static int removeThreadFromArray(ArrayList<Discussion> threads,String threadid )
    {
        for( int i = 0 ; i < threads.size(); i++)
        {
            Discussion d = threads.get(i);
            if( d != null && d.id.equals(threadid) ) {
                threads.remove(i);
                return i;
            }
        }

        return -1;
    }

    public static int removePostFromArray(ArrayList<Post> posts,String postid)
    {
        for( int i = 0 ; i < posts.size(); i++)
        {
            Post u = posts.get(i);
            if( u != null && u.id.equals(postid) ) {
                posts.remove(i);
                ApplicationModel.removedPostIndex = i;
                return i;
            }
        }

        return -1;
    }

    public static int removeMemberFromArray(ArrayList<User> members,String userid )
    {
        for( int i = 0 ; i < members.size(); i++)
        {
            User u = members.get(i);
            if( u != null && u.userid.equals(userid) ) {
                members.remove(i);
                ApplicationModel.removedMemberIndex = i;
                return i;
            }
        }

        return -1;
    }

    public static boolean isUserMemberOfGroup(ArrayList<Group> groups,String groupid)
    {
        for( int i = 0 ; i < groups.size(); i++)
        {
            Group g = groups.get(i);
            if( g != null && g.id.equals(groupid) ) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserAdminOfGroup(ArrayList<Group> groups,String groupid)
    {
        UserModel um = UserModel.getInstance();
        for( int i = 0 ; i < groups.size(); i++)
        {
            Group g = groups.get(i);
            if( g != null &&  g.id.equals(groupid) && g.admin.equals(um.user.userid) ) {
                    return true;
            }
        }

        return false;
    }


    // need to modify this function later
    public static boolean isUserMemberOfThread(ArrayList<Discussion> threads,String threadid )
    {
        for( int i = 0 ; i < threads.size(); i++)
        {
            Discussion d = threads.get(i);
            if( d != null && d.id.equals(threadid) ) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserAdminOfThread(ArrayList<Discussion> threads,String threadid )
    {
        UserModel um = UserModel.getInstance();
        for( int i = 0 ; i < threads.size(); i++)
        {
            Discussion d = threads.get(i);
            if( d != null && d.id.equals(threadid ) && d.owner.equals(um.user.userid)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUserOwnerOfPost(ArrayList<Post> posts,String postid )
    {
        UserModel um = UserModel.getInstance();
        for( int i = 0 ; i < posts.size(); i++)
        {
            Post d = posts.get(i);
            if( d != null && d.id.equals(postid ) && d.postowner.equals(um.user.userid)) {
                return true;
            }
        }

        return false;
    }

    public static MatrixCursor getCursorFromUserArray(ArrayList<User> models)
    {
        //String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(ApplicationModel.SearchModel.user_columns);

        for ( int i = 0 ; i < models.size(); i++)
        {
            User u = models.get(i);
            if( u != null)
            {
                mx.addRow(new Object[] { u.userid, u.userid,u.fname + " " + u.lname,u.email});
            }
        }

        return mx;
    }

    public static boolean isUserAlreadySelected(String userid)
    {
        LoadDataModel ldm = LoadDataModel.getInstance();

        for( int i = 0; i < ldm.loadSearchSelectedUsers.size(); i++)
        {
            User u = ldm.loadSearchSelectedUsers.get(i);
            if( u != null)
            {
                if(u.userid.equals(userid))
                    return true;
            }
        }
        return false;
    }

    public static void removeAlreadySelectedUsersFromSearchResult()
    {
        LoadDataModel ldm = LoadDataModel.getInstance();
        int size = ldm.loadSearchUsers.size();
        for( int i = 0; i < size; i++)
        {
            if( i < 0)
                break;

            User u = ldm.loadSearchUsers.get(i);
            if( u != null)
            {
                if(isUserAlreadySelected(u.userid))
                {
                    ldm.loadSearchUsers.remove(i);
                    size = ldm.loadSearchUsers.size();
                    i--;
                }
            }
        }
    }

    public static MatrixCursor getCursorFromThreadArray(ArrayList<Discussion> models)
    {
        //String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(ApplicationModel.SearchModel.thread_columns);

        for ( int i = 0 ; i < models.size(); i++)
        {
            Discussion d = models.get(i);
            if( d != null)
            {
                mx.addRow(new Object[] { d.id,d.id, d.title,d.description});
            }
        }

        return mx;
    }

    public static MatrixCursor getCursorFromGroupArray(ArrayList<Group> models)
    {
        //String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(ApplicationModel.SearchModel.group_columns);

        for ( int i = 0 ; i < models.size(); i++)
        {
            Group d = models.get(i);
            if( d != null)
            {
                mx.addRow(new Object[] { d.id,d.id, d.name,d.descritpion});
            }
        }

        return mx;
    }

    public static MatrixCursor getCursorFromPostArray(ArrayList<Post> models)
    {
        //String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(ApplicationModel.SearchModel.post_columns);

        for ( int i = 0 ; i < models.size(); i++)
        {
            Post d = models.get(i);
            if( d != null)
            {
                mx.addRow(new Object[] { d.id,d.id, d.text,d.postownername});
            }
        }

        return mx;
    }

    public static MatrixCursor getCursorFromDepartmentArray(ArrayList<Department> models)
    {
        //String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(ApplicationModel.SearchModel.department_columns);

        for ( int i = 0 ; i < models.size(); i++)
        {
            Department d = models.get(i);
            if( d != null)
            {
                mx.addRow(new Object[] { d.id,d.id, d.name,d.descritpion});
            }
        }

        return mx;
    }

    public static int findPostInArray(ArrayList<Post> posts,String postid)
    {
        for( int i = 0 ; i < posts.size(); i++)
        {
            Post u = posts.get(i);
            if( u != null && u.id.equals(postid) ) {
                return i;
            }
        }
        return -1;
    }

    public static int findThreadInArray(ArrayList<Discussion> posts,String threadid)
    {
        for( int i = 0 ; i < posts.size(); i++)
        {
            Discussion u = posts.get(i);
            if( u != null && u.id.equals(threadid) ) {
                return i;
            }
        }
        return -1;
    }

    public static int findUserInArray(ArrayList<User> users,String userid)
    {
        for( int i = 0 ; i < users.size(); i++)
        {
            User u = users.get(i);
            if( u != null && u.userid.equals(userid) ) {
                return i;
            }
        }
        return -1;
    }

    public static void addGroupToTop(ArrayList<Group> array , Group g)
    {
        ArrayList<Group> newArray = new ArrayList<>();

        for(int i =  0 ; i < array.size() ; i++)
        {
            newArray.add(array.get(i));
        }

        array.clear();
        array.add(g);

        for(int i =  0 ; i < newArray.size() ; i++)
        {
            array.add(newArray.get(i));
        }
    }

}
