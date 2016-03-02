package com.ldceconnect.ldcecommunity.util;

/**
 * Author :Raj Amal
 * Email  :raj.amalw@learn2crack.com
 * Website:www.learn2crack.com
 **/

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class UserFunctions {

    private JSONParser jsonParser;

    private static String registerURL = "http://www.therandomquestion.com/LogInAPI/index.php";
    //URL of the PHP API
    private static String loginURL = "http://www.therandomquestion.com/LogInAPI/index.php";
    private static String forpassURL = "http://www.therandomquestion.com/LogInAPI/index.php";
    private static String chgpassURL = "http://www.therandomquestion.com/LogInAPI/index.php";

    // File upload url (replace the ip with your server address)
    public static final String FILE_UPLOAD_URL = "http://www.therandomquestion.com/ImageUpload/fileUpload.php";

    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String forpass_tag = "forpass";
    private static String chgpass_tag = "chgpass";
    private static String creategroup_tag = "creategroup";


    // constructor
    public UserFunctions() {
        jsonParser = new JSONParser();
    }

    /**
     * Function to Login
     **/

    public JSONObject loginUser(String email, String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public JSONObject getServerTime()
    {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getservertime"));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public JSONObject setUploadedFileTimestamp(String filename)
    {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "setuploadedfiletimestamp"));
        params.add(new BasicNameValuePair("filename", filename));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    public JSONObject getUploadedFileTimestamp(String filename)
    {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getuploadedfiletimestamp"));
        params.add(new BasicNameValuePair("filename", filename));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    /**
     * Function to get user
     **/

    public JSONObject getUser(String email) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getuser"));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    /**
     * Function to get user
     **/

    public JSONObject getUserById(String userid) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getuserbyid"));
        params.add(new BasicNameValuePair("userid", userid));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    /**
     * Function to change password
     **/

    public JSONObject chgPass(String newpas, String email) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", chgpass_tag));

        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
        return json;
    }


    /**
     * Function to reset the password
     **/

    public JSONObject forPass(String userid,String username, String email) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", forpass_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("username", username));
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
        return json;
    }


    /**
     * Function to  Register
     **/
    public JSONObject registerUser(String fname, String lname, String email, String uname, String password) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("uname", uname));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    /**
     * Function to  Register
     **/
    public JSONObject addUserData(String email, String mobile, String degreeid, String programid,String departmentid, String interests,String profilePicUrl,String showemail, String showmobile) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "adduserdata"));
        params.add(new BasicNameValuePair("mobile", mobile));
        params.add(new BasicNameValuePair("departmentid", departmentid));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("degreeid", degreeid));
        params.add(new BasicNameValuePair("programid", programid));
        params.add(new BasicNameValuePair("interests", interests));
        params.add(new BasicNameValuePair("profilepicurl", profilePicUrl));
        params.add(new BasicNameValuePair("showemail", showemail));
        params.add(new BasicNameValuePair("showmobile", showmobile));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject updateForgotPasswordStatus(String email,String status) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "updateforgotpasswordstatus"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("status", status));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject resetPassword(String email,String oldPassword, String newPassword) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "resetpassword"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("oldpassword", oldPassword));
        params.add(new BasicNameValuePair("newpassword", newPassword));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     */
    public boolean logoutUser(Context context) {
        DatabaseHelper db = DatabaseHelper.getInstance(context);
        db.logoutUser();
        return true;
    }

    public JSONObject createGroup(String grouptitle, String groupinfo, String groupadmin, ArrayList<User> groupMemberIds) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", creategroup_tag));
        params.add(new BasicNameValuePair("grouptitle", grouptitle));
        params.add(new BasicNameValuePair("groupinfo", groupinfo));
        params.add(new BasicNameValuePair("userid", groupadmin));

        for( int i = 0; i < groupMemberIds.size(); i++)
        {
            Integer integer = new Integer(i);
            params.add(new BasicNameValuePair(integer.toString(), groupMemberIds.get(i).userid));
        }

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject createGroupMembership(String groupid, ArrayList<User> groupMemberIds) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "creategroupmembership"));
        params.add(new BasicNameValuePair("groupid", groupid));

        for( int i = 0; i < groupMemberIds.size(); i++)
        {
            Integer integer = new Integer(i);
            params.add(new BasicNameValuePair(integer.toString(), groupMemberIds.get(i).userid));
        }

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject loadDepartments()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loaddepartments"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedDepartments.size())));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadGroups()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadgroups"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedGroups.size())));
        params.add(new BasicNameValuePair("sortcontext", um.GetGroupSortType()));

        if(um.sortGroups == UserModel.SortGroups.SORT_BY_GROUP_PREFERENCE)
            params.add(new BasicNameValuePair("grouptype", um.GetGroupTypePreference()));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadThreads()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadthreads"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedThreads.size())));
        params.add(new BasicNameValuePair("sortcontext", um.GetThreadSortType()));

        if(um.sortThreads == UserModel.SortThreads.SORT_BY_THREAD_PREFERENCE)
            params.add(new BasicNameValuePair("contenttype", um.GetThreadTypePreference()));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject loadDegrees()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loaddegrees"));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadPrograms()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadprograms"));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadInterests()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadinterests"));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadGroupMembers(String groupId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadgroupmembers"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedGroupMembers.size())));
        params.add(new BasicNameValuePair("groupid", groupId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadDepartmentMembers(String deptId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loaddepartmentmembers"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedDepartmentMembers.size())));
        params.add(new BasicNameValuePair("deptid", deptId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadDepartmentDetails(String deptId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loaddepartmentdetails"));
        params.add(new BasicNameValuePair("deptid", deptId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadGroupDetails(String groupId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadgroupdetails"));
        params.add(new BasicNameValuePair("groupid", groupId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadGroupThreads(String groupId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadgroupthreads"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedGroupThreads.size())));
        params.add(new BasicNameValuePair("groupid", groupId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadGroupCategories()
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadgroupcategories"));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadUserThreads(String userId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loaduserthreads"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedUserThreads.size())));
        params.add(new BasicNameValuePair("sortcontext", um.GetThreadSortType()));
        params.add(new BasicNameValuePair("userid", userId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadUserGroups(String userId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadusergroups"));
        // assuming no user will join more than 30 groups
        String maxUserGroups = "30";
        params.add(new BasicNameValuePair("currentpageload", maxUserGroups));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedUserGroups.size())));
        params.add(new BasicNameValuePair("sortcontext", um.GetGroupSortType()));
        params.add(new BasicNameValuePair("userid", userId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadAppUserGroups(String userId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadusergroups"));
        // assuming no user will join more than 30 groups
        String maxUserGroups = "30";
        params.add(new BasicNameValuePair("currentpageload", maxUserGroups));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedAppUserGroups.size())));
        params.add(new BasicNameValuePair("sortcontext", um.GetGroupSortType()));
        params.add(new BasicNameValuePair("userid", userId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject leaveGroup(String userid, String groupid)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "leavegroup"));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("groupid", groupid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject deleteGroup(String userid, String groupid)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "deletegroup"));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("groupid", groupid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject deleteThread(String userid, String threadid)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "deletethread"));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("threadid", threadid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject starThread(String userid, String threadid)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "starthread"));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("threadid", threadid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject removeStarThread(String userid, String threadid)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "removestarthread"));
        params.add(new BasicNameValuePair("userid", userid));
        params.add(new BasicNameValuePair("threadid", threadid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadStarredThreads(String userid)
    {
        //TODO: make it a get next starred threads
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadstarredthreads"));
        params.add(new BasicNameValuePair("userid", userid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject updateThreadTitle(String threadId, String threadTitle)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "updatethreadtitle"));
        params.add(new BasicNameValuePair("threadid", threadId));
        params.add(new BasicNameValuePair("threadtitle", threadTitle));
        params.add(new BasicNameValuePair("userid", um.user.userid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject updateThreadDescription(String threadId, String threadDesc)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "updatethreaddescription"));
        params.add(new BasicNameValuePair("threadid", threadId));
        params.add(new BasicNameValuePair("threaddescription", threadDesc));
        params.add(new BasicNameValuePair("userid", um.user.userid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject updateGroupTitle(String groupId, String groupTitle)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "updategrouptitle"));
        params.add(new BasicNameValuePair("groupid", groupId));
        params.add(new BasicNameValuePair("grouptitle", groupTitle));
        params.add(new BasicNameValuePair("userid", um.user.userid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject updateGroupImageUrl(String groupId, String groupImageUrl)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "updategroupimageurl"));
        params.add(new BasicNameValuePair("groupid", groupId));
        params.add(new BasicNameValuePair("groupimageurl", groupImageUrl));
        params.add(new BasicNameValuePair("userid", um.user.userid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject updatePostText(String postId, String text)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "updateposttext"));
        params.add(new BasicNameValuePair("postid", postId));
        params.add(new BasicNameValuePair("posttext", text));
        params.add(new BasicNameValuePair("userid", um.user.userid));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject deletePost(String postId)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "deletepost"));
        params.add(new BasicNameValuePair("userid", um.user.userid));
        params.add(new BasicNameValuePair("postid", postId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadThreadDetails(String threadId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadthreaddetails"));
        params.add(new BasicNameValuePair("threadid", threadId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject loadThreadPosts(String threadId)
    {
        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "loadthreadposts"));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadedThreadPosts.size())));
        params.add(new BasicNameValuePair("threadid", threadId));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public Cursor searchUser(String query, MatrixCursor matrixCursor)
    {
        /*String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(columns);*/

        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchuser"));
        params.add(new BasicNameValuePair("query", query));
        UserModel um = UserModel.getInstance();
        if(um.user.userid != null)
            params.add(new BasicNameValuePair("userid",um.user.userid));
        else
            params.add(new BasicNameValuePair("userid","-1"));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);

        dm.loadSearchUsers.clear();
        ParserUtils.ParseMembersData(json, dm.loadSearchUsers);

        // Add Loaded users
        for ( int i = 0 ; i < dm.loadSearchUsers.size(); i++)
        {
            User u = dm.loadSearchUsers.get(i);
            if( u != null)
            {
                if(!u.userid.equals(um.user.userid))
                    matrixCursor.addRow(new Object[] { u.userid, u.userid,u.fname + " " + u.lname,u.email});
            }
        }

        return matrixCursor;
    }

    public JSONObject sendInvite(String fromEmail,String fromName,String toEmail,String toName)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "sendinvite"));
        params.add(new BasicNameValuePair("toemail", toEmail));
        params.add(new BasicNameValuePair("fromemail", fromEmail));
        params.add(new BasicNameValuePair("toname", toName));
        params.add(new BasicNameValuePair("fromname", fromName));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject sendFeedback(String fromEmail,String text)
    {
        UserModel um = UserModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "sendfeedback"));
        params.add(new BasicNameValuePair("fromemail", fromEmail));
        params.add(new BasicNameValuePair("text", text));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public MatrixCursor searchThread(String query)
    {
        /*String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(columns);*/

        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchthread"));
        params.add(new BasicNameValuePair("query", query));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadSearchThreads.size())));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);

        ParserUtils.ParseDiscussionData(json, dm.loadSearchThreads);

        MatrixCursor mx = ApplicationUtils.getCursorFromThreadArray(dm.loadSearchThreads);

        return mx;
    }

    public MatrixCursor searchGroup(String query)
    {
        /*String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(columns);*/

        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchgroup"));
        params.add(new BasicNameValuePair("query", query));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadSearchGroups.size())));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);

        ParserUtils.ParseGroupData(json, dm.loadSearchGroups);

        MatrixCursor mx = ApplicationUtils.getCursorFromGroupArray(dm.loadSearchGroups);

        return mx;
    }

    public MatrixCursor searchPost(String query)
    {
        /*String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(columns);*/

        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchpost"));
        params.add(new BasicNameValuePair("query", query));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadSearchPosts.size())));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);

        ParserUtils.ParsePostsData(json, dm.loadSearchPosts);

        MatrixCursor mx = ApplicationUtils.getCursorFromPostArray(dm.loadSearchPosts);

        return mx;
    }

    public MatrixCursor searchDepartment(String query)
    {
        /*String[] columns = new String[] { "_id", "userid", "name","email" };
        MatrixCursor mx = new MatrixCursor(columns);*/

        LoadDataModel dm = LoadDataModel.getInstance();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchdepartment"));
        params.add(new BasicNameValuePair("query", query));
        params.add(new BasicNameValuePair("currentpageload", LoadDataModel.currentPageLoad));
        params.add(new BasicNameValuePair("offset", String.valueOf(dm.loadSearchDepartments.size())));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);

        ParserUtils.ParseDepartmentData(json, dm.loadSearchDepartments);

        MatrixCursor mx = ApplicationUtils.getCursorFromDepartmentArray(dm.loadSearchDepartments);

        return mx;
    }

    public JSONObject createThread(String threadtitle, String threadinfo, String threadowner, String ispublicthread, String threadgroup, ArrayList<String> hashtags) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "createthread"));
        params.add(new BasicNameValuePair("threadtitle", threadtitle));
        params.add(new BasicNameValuePair("threadinfo", threadinfo));
        params.add(new BasicNameValuePair("threadowner", threadowner));
        params.add(new BasicNameValuePair("ispublicthread", ispublicthread));
        params.add(new BasicNameValuePair("threadgroup", threadgroup));

        for( int i = 0; i < hashtags.size(); i++)
        {
            Integer integer = new Integer(i);
            params.add(new BasicNameValuePair(integer.toString(), hashtags.get(i)));
        }

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject createPost(String text, String parentthread, String postowner) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "createpost"));
        params.add(new BasicNameValuePair("posttext", text));
        params.add(new BasicNameValuePair("parentthread", parentthread));
        params.add(new BasicNameValuePair("postowner", postowner));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }

    public JSONObject uploadUserPreferences(String email, String interests, String department, String degree, String program) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "uploaduserpreferences"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("interests", interests));
        params.add(new BasicNameValuePair("departmentid", department));
        params.add(new BasicNameValuePair("degreeid", degree));
        params.add(new BasicNameValuePair("programid", program));

        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        return json;
    }
}

