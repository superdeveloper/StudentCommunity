package com.ldceconnect.ldcecommunity.async;

/**
 * Created by Nevil on 12/18/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ldceconnect.ldcecommunity.DiscussionViewActivity;
import com.ldceconnect.ldcecommunity.DrawerActivity;
import com.ldceconnect.ldcecommunity.ExploreCommunity;
import com.ldceconnect.ldcecommunity.GroupViewActivity;
import com.ldceconnect.ldcecommunity.LoadDataActivity;
import com.ldceconnect.ldcecommunity.SearchActivity;
import com.ldceconnect.ldcecommunity.StudentProfileActivity;
import com.ldceconnect.ldcecommunity.WelcomeActivity;
import com.ldceconnect.ldcecommunity.fragments.PostMessageFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.Program;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Async Task to get and send data to My Sql database through JSON respone.
 **/
public class UploadDataAsync extends AsyncTask<String, String, Map<String,JSONObject>>  {

    private ProgressDialog pDialog;
    private Map<String,JSONObject> mJSONObjArray = new HashMap<>();
    private JSONObject json_users;
    private JSONObject json_leavegroup;
    private JSONObject json_deletegroup;
    private JSONObject json_deletethread;
    private JSONObject json_start_thread;
    private JSONObject json_unstar_thread;
    private JSONObject json_updatethreadtitle;
    private JSONObject json_update_posttext;
    private JSONObject json_delete_post;
    private JSONObject json_create_groupmembership;
    private JSONObject json_forgot_password;
    private JSONObject json_update_forgot_password_status;
    private JSONObject json_update_reset_password;
    private JSONObject json_create_thread;
    private JSONObject json_create_post;
    private JSONObject json_send_feedback;
    private JSONObject json_send_invite;
    private JSONObject json_user_preferences;
    private JSONObject json_group_imageurl;
    private JSONObject json_set_file_synctime;
    private LoadDataModel.UploadContext uploadContext;
    private AppCompatActivity activity;
    private ArrayList<Object> data;

    private Handler handler;

    public UploadDataAsync(AppCompatActivity activity,LoadDataModel.UploadContext uploadContext, ArrayList<Object> inputData)
    {
        super();
        this.activity = activity;
        this.uploadContext = uploadContext;
        this.data = inputData;
        handler = new Handler();
    }

    void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    void showToastLong(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Processing...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        if(activity.isDestroyed() || activity.isFinishing())
        {
            cancel(true);
            return;
        }

        pDialog.show();

        LoadDataModel.isCurrentDataLoadFinished = false;

    }

    @Override
    protected Map<String,JSONObject> doInBackground(String... args) {
        UserFunctions userFunction = new UserFunctions();
        if(uploadContext == LoadDataModel.UploadContext.UPLOAD_USER_DETAILS)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            json_users = userFunction.addUserData(um.user.email, um.user.mobile, um.user.degree.id, um.user.program.id,um.user.department.id, um.user.interests,um.user.profilePictureUrl,um.user.showEmailId,um.user.showMobileNo );
            mJSONObjArray.put("user", json_users);
        }

        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_LEAVE_GROUP )
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            json_leavegroup = userFunction.leaveGroup(um.user.userid,(String)data.get(0) );
            mJSONObjArray.put("leavegroup", json_leavegroup);
        }else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_REMOVE_GROUPMEMBERSHIP)
        {
            mJSONObjArray.clear();
            json_leavegroup = userFunction.leaveGroup((String)data.get(0),(String)data.get(1) );
            mJSONObjArray.put("leavegroup", json_leavegroup);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_GROUP)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            json_deletegroup = userFunction.deleteGroup(um.user.userid, (String) data.get(0));
            mJSONObjArray.put("deletegroup", json_deletegroup);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_THREAD)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            json_deletethread = userFunction.deleteThread(um.user.userid, (String) data.get(0));
            mJSONObjArray.put("deletethread", json_deletethread);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_STAR_THREAD)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            Discussion d = (Discussion)data.get(0);
            json_start_thread = userFunction.starThread(um.user.userid, d.id);
            mJSONObjArray.put("starthread", json_start_thread);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_REMOVE_STAR_THREAD)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            Discussion d = (Discussion)data.get(0);
            json_unstar_thread = userFunction.removeStarThread(um.user.userid, d.id);
            mJSONObjArray.put("removestarthread", json_unstar_thread);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_TITLE)
        {
            mJSONObjArray.clear();
            json_updatethreadtitle = userFunction.updateThreadTitle((String) data.get(0), (String) data.get(1));
            mJSONObjArray.put("updatethreadtitle", json_updatethreadtitle);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_DESCRIPTION)
        {
            mJSONObjArray.clear();
            json_updatethreadtitle = userFunction.updateThreadDescription((String) data.get(0), (String) data.get(1));
            mJSONObjArray.put("updatethreaddescription", json_updatethreadtitle);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_POST_TEXT)
        {
            mJSONObjArray.clear();
            json_update_posttext = userFunction.updatePostText((String) data.get(0), (String) data.get(1));
            mJSONObjArray.put("updateposttext", json_update_posttext);
        }

        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_POST)
        {
            mJSONObjArray.clear();
            json_delete_post = userFunction.deletePost((String) data.get(0));
            mJSONObjArray.put("deletepost", json_delete_post);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_CREATE_GROUPMEMBERSHIP ||
                uploadContext == LoadDataModel.UploadContext.UPLOAD_JOIN_GROUP)
        {
            mJSONObjArray.clear();
            ArrayList<User> members = (ArrayList<User>) data.get(1);
            json_create_groupmembership = userFunction.createGroupMembership((String) data.get(0), members);
            mJSONObjArray.put("creategroupmembership", json_create_groupmembership);
        }
        else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_SEND_INVITE)
        {
            String fromEmail = (String) data.get(0);
            String fromName = (String) data.get(1);
            String toEmail = (String) data.get(2);
            String toName = (String) data.get(3);
            json_send_invite = userFunction.sendInvite(fromEmail,fromName,toEmail,toName);
        }else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_FORGOT_PASSWORD)
        {
            if( data != null && data.size() == 3) {
                json_forgot_password = userFunction.forPass((String)data.get(0),(String)data.get(1),(String)data.get(2));
                mJSONObjArray.put("forgotpassword", json_forgot_password);
            }
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_FORGOT_PASSWORD_STATUS)
        {
            mJSONObjArray.clear();
            json_update_forgot_password_status = userFunction.updateForgotPasswordStatus((String) data.get(0), (String) data.get(1));
            mJSONObjArray.put("updateforgotpasswordstatus", json_update_forgot_password_status);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_RESET_PASSWORD)
        {
            mJSONObjArray.clear();
            json_update_reset_password = userFunction.resetPassword((String) data.get(0), (String) data.get(1), (String) data.get(2));
            mJSONObjArray.put("resetpassword", json_update_reset_password);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_CREATE_THREAD)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            Discussion d = (Discussion) data.get(0);

            ArrayList<String> hashtags = (ArrayList<String>) data.get(1);

            json_create_thread = userFunction.createThread(d.title, d.description, d.owner, d.ispublic, d.parentgroup, hashtags);
            mJSONObjArray.put("createthread", json_create_thread);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_CREATE_POST)
        {
            mJSONObjArray.clear();
            Post p = (Post) data.get(0);

            json_create_post = userFunction.createPost(p.text, p.threadid, p.postowner);
            mJSONObjArray.put("createpost", json_create_post);
        }else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_SEND_FEEDBACK)
        {
            mJSONObjArray.clear();
            json_send_feedback = userFunction.sendFeedback((String) data.get(0), (String) data.get(1));
            mJSONObjArray.put("sendfeedback",json_send_feedback);
        } else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_USER_PREFERENCES)
        {
            mJSONObjArray.clear();
            UserModel um = UserModel.getInstance();
            json_user_preferences = userFunction.addUserData(um.user.email, um.user.mobile, (String) data.get(0), (String) data.get(1), (String) data.get(2),
                                                            (String) data.get(3), um.user.profilePictureUrl,(String) data.get(4), (String) data.get(5) );
            mJSONObjArray.put("user", json_user_preferences);
        }
        else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_GROUPIMAGE)
        {
            mJSONObjArray.clear();
            json_group_imageurl = userFunction.updateGroupImageUrl((String) data.get(0), (String) data.get(1));
            mJSONObjArray.put("groupimageurl", json_group_imageurl);
        }
        else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_SET_FILE_SYNTIME)
        {
            mJSONObjArray.clear();
            json_set_file_synctime = userFunction.setUploadedFileTimestamp((String)data.get(0));
            mJSONObjArray.put("setuploadedfiletimestamp",json_set_file_synctime);
        }

        return mJSONObjArray;
    }

    @Override
    protected void onPostExecute(Map<String,JSONObject> jsonObjectArray) {
        try {

            if(activity.isDestroyed() || activity.isFinishing())
            {
                cancel(true);
                return;
            }

            if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_USER_DETAILS) {

                String result_user = json_users.getString(DataModel.KEY_SUCCESS);

                if(Integer.parseInt(result_user) == 1  ){

                    Intent intent = new Intent(activity, LoadDataActivity.class);
                    activity.startActivity(intent);

                }else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Upload Failed", null);
                }
            }
            else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_LEAVE_GROUP && json_leavegroup != null) {

                if ( json_leavegroup != null && json_leavegroup.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_leavegroup.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ArrayList<Group> loadedGroups;

                    loadedGroups = ldm.loadedAppUserGroups;

                    ApplicationUtils.showDoneDialog(activity, "You have left the group");

                    UserModel um = UserModel.getInstance();
                    ApplicationUtils.removeGroupFromArray(loadedGroups, LoadDataModel.loadGroupId);

                    UserModel.userAction = UserModel.UserAction.LEAVE_GROUP;

                    //activity.onBackPressed();

                    if( activity.getClass() == GroupViewActivity.class) {
                        ((GroupViewActivity) activity).fabJoinGroup.setVisibility(View.VISIBLE);
                        ((GroupViewActivity) activity).isUserMemberOfGroup = false;

                        ApplicationUtils.removeMemberFromArray(ldm.loadedGroupMembers,um.user.userid);

                        StudentFragment sf = ((StudentFragment) ((GroupViewActivity) activity).adapter.getItem(ApplicationUtils.GROUPVIEW_MEMBERS_TAB_INDEX));
                        if( sf != null && sf.adapter != null) {
                            sf.adapter.notifyDataSetChanged();
                        }

                        Menu menu = ((GroupViewActivity) activity).menu;
                        MenuItem itemJoinGroup = menu.getItem(0);
                        MenuItem itemLeaveGroup = menu.getItem(2);

                        itemLeaveGroup.setVisible(false);
                        itemJoinGroup.setVisible(true);

                        Group g = ldm.loadedGroupForDetail.get(0);
                        int nums = Integer.valueOf(g.nummembers) - 1 ;
                        g.nummembers = String.valueOf(nums);
                        g.listIndex = ApplicationModel.loadedGroupIndex;
                        g.doUpdate = true;

                        UserModel.userAction = UserModel.UserAction.REMOVE_MEMBER_FROM_GROUP;

                    }

                }
                else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Leave Group Failed", null);
                }
            }
            else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_REMOVE_GROUPMEMBERSHIP)
            {
                if ( json_leavegroup != null && json_leavegroup.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_leavegroup.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    int id = ApplicationUtils.findUserInArray(ldm.loadedGroupMembers, LoadDataModel.loadUserId);

                    if( id >=0 )
                    {
                        ldm.loadedGroupMembers.remove(id);

                    }

                    StudentFragment sf = (StudentFragment)((GroupViewActivity) activity).adapter.getItem(ApplicationUtils.GROUPVIEW_MEMBERS_TAB_INDEX);

                    if( sf != null && sf.adapter!= null) {
                        sf.adapter.notifyDataSetChanged();
                    }
                    Group g = ldm.loadedGroupForDetail.get(0);
                    int nums = Integer.valueOf(g.nummembers) - 1 ;
                    g.nummembers = String.valueOf(nums);
                    g.listIndex = ApplicationModel.loadedGroupIndex;
                    g.doUpdate = true;

                    ((GroupViewActivity) activity).mMembersCount.setText(g.nummembers + " Members");


                    UserModel.userAction = UserModel.UserAction.REMOVE_MEMBER_FROM_GROUP;


                    //ApplicationUtils.showDoneDialog(activity,"User removed from the group");
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_LEAVE_GROUP && json_leavegroup == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Leave Group Failed", null);
            }
            else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_GROUP && json_deletegroup != null) {

                if ( json_deletegroup != null &&  json_deletegroup.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_deletegroup.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ArrayList<Group> loadedGroups;

                    loadedGroups = ldm.loadedAppUserGroups;
                    ApplicationUtils.removeGroupFromArray(loadedGroups, LoadDataModel.loadGroupId);
                    loadedGroups = ldm.loadedGroups;
                    ApplicationUtils.removeGroupFromArray(loadedGroups, LoadDataModel.loadGroupId);

                    ApplicationUtils.showDoneDialog(activity, "Group has been deleted");

                    UserModel.userAction = UserModel.UserAction.DELETE_GROUP;
                    activity.onBackPressed();
                } else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Delete Group Failed", null);
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_GROUP && json_deletegroup == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Delete Group Failed", null);
            }
            else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_THREAD && json_deletethread != null) {

                if (json_deletethread != null &&  json_deletethread.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_deletethread.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ArrayList<Discussion> loadedThreads;


                    loadedThreads = ldm.loadedUserThreads;
                    ApplicationUtils.removeThreadFromArray(loadedThreads, LoadDataModel.loadThreadId);
                    loadedThreads = ldm.loadedThreads;
                    ApplicationModel.removedThreadIndex = ApplicationUtils.removeThreadFromArray(loadedThreads, LoadDataModel.loadThreadId);
                    loadedThreads = ldm.loadedGroupThreads;
                    ApplicationModel.removedGroupThreadIndex = ApplicationUtils.removeThreadFromArray(loadedThreads, LoadDataModel.loadThreadId);

                    ApplicationUtils.showDoneDialog(activity, "Post Deleted");

                    UserModel.userAction = UserModel.UserAction.DELETE_THREAD;
                    activity.onBackPressed();
                } else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Delete Post Failed", null);
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_THREAD && json_deletethread == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Delete Post Failed", null);
            }

            else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_STAR_THREAD && json_start_thread != null) {

                if ( json_start_thread != null &&  json_start_thread.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_start_thread.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    ApplicationUtils.showDoneDialog(activity, "Starred");
                } else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Unable to connect", null);
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_STAR_THREAD && json_start_thread == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Unable to connect", null);
            }
            else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_REMOVE_STAR_THREAD && json_unstar_thread != null) {

                if ( json_unstar_thread != null && json_unstar_thread.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_unstar_thread.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    ApplicationUtils.showDoneDialog(activity, "Unstarred");
                } else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Unable to connect", null);
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_REMOVE_STAR_THREAD && json_unstar_thread == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Unable to connect", null);
            }

            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_TITLE &&
                    json_updatethreadtitle != null)
            {
                if (json_updatethreadtitle != null &&  json_updatethreadtitle.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_updatethreadtitle.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ldm.loadedThreadForDetail.clear();
                    //Discussion d;
                    //d.title = ((DiscussionViewActivity)activity).threadTitle.getText().toString();
                    ParserUtils.ParseDiscussionData(json_updatethreadtitle, ldm.loadedThreadForDetail);

                    Discussion d = ldm.loadedThreadForDetail.get(0);
                    ((DiscussionViewActivity)activity).threadTitle.setText(d.title);

                    ApplicationModel.updatedThreadIndex = ApplicationUtils.findThreadInArray(ldm.loadedThreads, d.id);
                    Discussion post = ldm.loadedThreads.get(ApplicationModel.updatedThreadIndex);
                    post.title = d.title;

                    UserModel.userAction = UserModel.UserAction.UPDATE_THREAD_TITLE;
                }
                else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Update failed", null);
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_TITLE && json_updatethreadtitle == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Update failed", null);
            }

            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_DESCRIPTION &&
                    json_updatethreadtitle != null)
            {
                if ( json_updatethreadtitle != null && json_updatethreadtitle.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_updatethreadtitle.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ldm.loadedThreadForDetail.clear();
                    //Discussion d;
                    //d.title = ((DiscussionViewActivity)activity).threadTitle.getText().toString();
                    ParserUtils.ParseDiscussionData(json_updatethreadtitle, ldm.loadedThreadForDetail);

                    Discussion d = ldm.loadedThreadForDetail.get(0);
                    ((DiscussionViewActivity)activity).threadDescription.setText(d.description);

                    ApplicationModel.updatedThreadIndex = ApplicationUtils.findThreadInArray(ldm.loadedThreads, d.id);
                    Discussion post = ldm.loadedThreads.get(ApplicationModel.updatedThreadIndex);
                    post.description = d.description;

                    UserModel.userAction = UserModel.UserAction.UPDATE_THREAD_TITLE;
                }
                else{
                    //pDialog.dismiss();
                    ApplicationUtils.showWarningDialog(activity, "Update failed", null);
                }
            }
            else if (uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_THREAD_DESCRIPTION && json_updatethreadtitle == null)
            {
                ApplicationUtils.showWarningDialog(activity, "Update failed", null);
            }


            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_DELETE_POST && json_delete_post != null)
            {
                if ( json_delete_post != null && json_delete_post.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_delete_post.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();

                    ApplicationUtils.removePostFromArray(ldm.loadedThreadPosts, LoadDataModel.loadPostId);

                    PostMessageFragment pm = (PostMessageFragment)((DiscussionViewActivity) activity).adapter.getItem(0);
                    pm.adapter.notifyItemRemoved(ApplicationModel.removedPostIndex);

                }
            }
            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_POST_TEXT && json_update_posttext != null)
            {
                if (json_update_posttext != null &&  json_update_posttext.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_update_posttext.getString(DataModel.KEY_SUCCESS)) == 1 )
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();

                    int index = ApplicationUtils.findPostInArray(ldm.loadedThreadPosts,LoadDataModel.loadPostId);

                    if(index >= 0 )
                    {
                        ArrayList<Post> arr = new ArrayList<>();

                        ParserUtils.ParsePostsData(json_update_posttext,arr);

                        Post p1 = ldm.loadedThreadPosts.get(index);
                        Post p = arr.get(0);
                        if( p != null) {
                            p1.text = p.text;
                            PostMessageFragment pm = (PostMessageFragment)((DiscussionViewActivity) activity).adapter.getItem(0);
                            pm.adapter.notifyItemChanged(index);
                        }
                    }
                }
            } else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_JOIN_GROUP && json_create_groupmembership != null)
            {
                if(json_create_groupmembership != null && json_create_groupmembership.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_create_groupmembership.getString(DataModel.KEY_SUCCESS)) == 1)
                {
                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ArrayList<Group> addedGroup = new ArrayList<>();

                    ParserUtils.ParseGroupData(json_create_groupmembership, addedGroup);

                    if( addedGroup.size() > 0 )
                    {
                        ldm.loadedAppUserGroups.add(addedGroup.get(0));
                        UserModel um = UserModel.getInstance();

                        ldm.loadedGroupMembers.add(um.user);
                    }

                    ApplicationUtils.showDoneDialog(activity, "You have joined the group");

                    //showToastLong("Group joined successfully");

                    if( activity.getClass() == GroupViewActivity.class) {
                        ((GroupViewActivity) activity).fabAddMember.setVisibility(View.GONE);
                        ((GroupViewActivity) activity).fabJoinGroup.setVisibility(View.GONE);
                        ((GroupViewActivity) activity).isUserMemberOfGroup = true;

                        StudentFragment sf = ((StudentFragment) ((GroupViewActivity) activity).adapter.getItem(ApplicationUtils.GROUPVIEW_MEMBERS_TAB_INDEX));

                        if( sf != null && sf.adapter != null) {
                            sf.adapter.notifyItemInserted(ldm.loadedGroupMembers.size());
                        }

                        Menu menu = ((GroupViewActivity) activity).menu;
                        MenuItem itemJoinGroup = menu.getItem(0);
                        MenuItem itemLeaveGroup = menu.getItem(2);

                        itemLeaveGroup.setVisible(true);
                        itemJoinGroup.setVisible(false);

                        Group g = ldm.loadedGroupForDetail.get(0);
                        int nums = Integer.valueOf(g.nummembers) + 1 ;
                        g.nummembers = String.valueOf(nums);
                        g.listIndex = ApplicationModel.loadedGroupIndex;
                        g.doUpdate = true;

                        ((GroupViewActivity) activity).mMembersCount.setText(g.nummembers + " Members");

                        UserModel.userAction = UserModel.UserAction.ADD_MEMBER_TO_GROUP;
                    }
                    else if(activity.getClass() == DiscussionViewActivity.class)
                    {
                        ((DiscussionViewActivity) activity).discussion_join_group.setVisibility(View.GONE);
                        ((DiscussionViewActivity) activity).discussion_post_reply_button.setVisibility(View.VISIBLE);
                        ((DiscussionViewActivity) activity).discussion_post_reply.setVisibility(View.VISIBLE);
                    }
                }
            }
            else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_CREATE_GROUPMEMBERSHIP && json_create_groupmembership != null)
            {
                if(json_create_groupmembership != null && json_create_groupmembership.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_create_groupmembership.getString(DataModel.KEY_SUCCESS)) == 1) {
                    if( activity.getClass() == SearchActivity.class)
                    {

                        LoadDataModel.groupViewUsersAdded = true;
                        activity.finish();
                    }
                }
            }
            else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_FORGOT_PASSWORD && json_forgot_password != null)
            {
                if(json_forgot_password != null && json_forgot_password.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_forgot_password.getString(DataModel.KEY_SUCCESS)) == 1)
                {
                    MaterialDialog m = new MaterialDialog.Builder(activity)
                            .title("Password Reset")
                            .content("Password reset email has been sent. Please check your email.")
                            .cancelable(false)
                            .positiveText("Ok")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    UserModel um = UserModel.getInstance();
                                    um.logout();

                                    DatabaseHelper db = DatabaseHelper.getInstance(activity);
                                    db.logoutUser();

                                    Intent intent = new Intent(activity,WelcomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    activity.startActivity(intent);
                                }
                            })
                            .show();
                }

            }else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_RESET_PASSWORD && json_update_reset_password != null)
            {
                if(json_update_reset_password != null && json_update_reset_password.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_update_reset_password.getString(DataModel.KEY_SUCCESS)) == 1)
                {
                    ApplicationUtils.showDoneDialog(activity, "Password changed successfully");
                }
            }

            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_CREATE_THREAD && json_create_thread != null)
            {
                if(json_create_thread != null && json_create_thread.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_create_thread.getString(DataModel.KEY_SUCCESS)) == 1)
                {
                    final ArrayList<Discussion> created_thread = new ArrayList<>();
                    ParserUtils.ParseDiscussionData(json_create_thread, created_thread);

                    if( created_thread.size() >0) {
                        ApplicationUtils.showDoneDialog(activity, "Post created");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //Activity parent = activity.getParent();

                                //activity.finish();

                                LoadDataModel.loadThreadId = created_thread.get(0).id;
                                LoadDataModel.loadThreadTitle = created_thread.get(0).title;

                                Intent intent = new Intent(activity, DiscussionViewActivity.class);

                                activity.startActivity(intent);
                            }
                        }, 3000);
                    }
                }
            }
            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_CREATE_POST && json_create_post != null)
            {
                if(json_create_post != null && json_create_post.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_create_post.getString(DataModel.KEY_SUCCESS)) == 1)
                {
                    if( activity.getClass() == DiscussionViewActivity.class)
                    {
                        PostMessageFragment pf = (PostMessageFragment)((DiscussionViewActivity)activity).adapter.getItem(0);
                        if( pf != null)
                        {
                            //pf.onRefresh();
                            LoadDataModel ldm = LoadDataModel.getInstance();
                            ParserUtils.ParsePostsData(json_create_post, ldm.loadedThreadPosts);

                            pf.adapter.notifyItemInserted(ldm.loadedThreadPosts.size());
                            pf.recyclerView.smoothScrollToPosition(pf.adapter.getItemCount() - 1);

                            ((DiscussionViewActivity)activity).discussion_post_reply.setText("");
                        }
                    }
                }
            }
            else if(uploadContext == LoadDataModel.UploadContext.UPLOAD_SEND_FEEDBACK && json_send_feedback != null)
            {
                if(json_send_feedback != null && json_send_feedback.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_send_feedback.getString(DataModel.KEY_SUCCESS)) == 1) {

                    ApplicationUtils.showDoneDialog(activity, "Thanks for your Feedback");
                }
            }
            else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_SEND_INVITE && json_send_invite!= null)
            {
                if(json_send_invite != null && json_send_invite.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_send_invite.getString(DataModel.KEY_SUCCESS)) == 1) {

                    ApplicationUtils.showDoneDialog(activity, "Invitation sent");
                }
            } else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_USER_PREFERENCES)
            {
                if(json_user_preferences!= null && json_user_preferences.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_user_preferences.getString(DataModel.KEY_SUCCESS)) == 1) {

                    LoadDataModel ldm = LoadDataModel.getInstance();
                    ldm.loadedUserForDetail.clear();
                    ParserUtils.ParseMembersData(json_user_preferences, ldm.loadedUserForDetail);

                    if( ldm.loadedUserForDetail.size() > 0) {
                        UserModel um = UserModel.getInstance();
                        User u = ldm.loadedUserForDetail.get(0);
                        um.user.degree = u.degree;
                        um.user.department = u.department;
                        um.user.program = u.program;
                        um.user.interests = u.interests;
                        um.user.showMobileNo = u.showMobileNo;
                        um.user.showEmailId = u.showEmailId;

                        ApplicationUtils.showDoneDialog(activity, "Updated");

                    }
                }
            } else if( uploadContext == LoadDataModel.UploadContext.UPLOAD_UPDATE_GROUPIMAGE)
            {
                if(json_group_imageurl != null && json_group_imageurl.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_group_imageurl.getString(DataModel.KEY_SUCCESS)) == 1) {
                    ApplicationUtils.showDoneDialog(activity, "Group Image Updated");
                }
            }
            else if ( uploadContext == LoadDataModel.UploadContext.UPLOAD_SET_FILE_SYNTIME)
            {
                if(json_set_file_synctime!= null && json_set_file_synctime.getString(DataModel.KEY_SUCCESS) != null &&
                        Integer.parseInt(json_set_file_synctime.getString(DataModel.KEY_SUCCESS)) == 1) {
                    //ApplicationUtils.showDoneDialog(activity, "Group Image Updated");
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        LoadDataModel.isCurrentDataLoadFinished = true;

        pDialog.dismiss();
    }
}