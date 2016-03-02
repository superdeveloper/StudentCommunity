package com.ldceconnect.ldcecommunity.model;

import android.graphics.Bitmap;
import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;
import android.widget.ArrayAdapter;

import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nevil on 12/18/2015.
 */
public class LoadDataModel {

    public static LoadDataModel dataModel = new LoadDataModel();

    public LoadDataModel()
    {
    }

    public static LoadDataModel getInstance() {
        return dataModel;
    }

    public enum LoadContext
    {
        LOAD_INITIAL_DATA,
        LOAD_DEPARTMENTS,
        LOAD_DEPARTMENT_DETAILS,
        LOAD_DEPARTMENT_MEMBERS,
        LOAD_GROUPS,
        LOAD_GROUP_DETAILS,
        LOAD_REFRESH_GROUP_DETAILS,
        LOAD_GROUP_MEMBERS,
        LOAD_GROUP_THREADS,
        LOAD_THREADS,
        LOAD_STARRED_THREADS,
        LOAD_STARRED_THREADS_FOR_RECYCLER,
        LOAD_THREAD_DETAILS,
        LOAD_REFRESH_THREAD_DETAILS,
        LOAD_THREAD_POSTS,
        LOAD_USERS,
        LOAD_USER_DETAILS,
        LOAD_USER_DATA,
        LOAD_USER_GROUPS,
        LOAD_USER_GROUPS_THREADS,
        LOAD_USER_THREADS,
        LOAD_DEGREES_PROGRAMS,
        LOAD_SEARCH_MEMBERS,
        LOAD_GROUPCATEGORIES,
        LOAD_CONTENT_HASH_TAGS,
        LOAD_PREFERENCES_DATA,
        LOAD_SERVERTIME,
        LOAD_GET_FILE_SYNCTIME,
        LOAD_NONE
    }

    public enum LoadMembersContext
    {
        LOAD_GROUP_MEMBERS,
        LOAD_DEPARTMENT_MEMBERS,
        LOAD_THREAD_MEMBERS,
        LOAD_ALL_MEMBERS
    }

    public enum LoadThreadsContext
    {
        LOAD_USER_THREADS,
        LOAD_ALL_THREADS
    }

    public enum UploadContext
    {
        UPLOAD_USER_DETAILS,
        UPLOAD_LEAVE_GROUP,
        UPLOAD_DELETE_GROUP,
        UPLOAD_DELETE_THREAD,
        UPLOAD_UPDATE_THREAD,
        UPLOAD_UPDATE_THREAD_TITLE,
        UPLOAD_UPDATE_THREAD_DESCRIPTION,
        UPLOAD_CREATE_POST,
        UPLOAD_DELETE_POST,
        UPLOAD_UPDATE_POST,
        UPLOAD_UPDATE_GROUP_TITLE,
        UPLOAD_UPDATE_POST_TEXT,
        UPLOAD_CREATE_GROUPMEMBERSHIP,
        UPLOAD_REMOVE_GROUPMEMBERSHIP,
        UPLOAD_JOIN_GROUP,
        UPLOAD_CREATE_THREAD,
        UPLOAD_SEND_INVITE,
        UPLOAD_REMOVE_STAR_THREAD,
        UPLOAD_STAR_THREAD,
        UPLOAD_FORGOT_PASSWORD,
        UPLOAD_FORGOT_PASSWORD_STATUS,
        UPLOAD_RESET_PASSWORD,
        UPLOAD_SEND_FEEDBACK,
        UPLOAD_USER_PREFERENCES,
        UPLOAD_UPDATE_GROUPIMAGE,
        UPLOAD_SET_FILE_SYNTIME

    }

    public enum LoginContext
    {
        LOGIN_GETUSER,
        LOGIN_LOGIN
    }

    public ArrayList<Discussion> loadedThreads = new ArrayList<>();

    public ArrayList<Department> loadedDepartments = new ArrayList<>();

    public ArrayList<Department> loadedDepartmentsForSpinner = new ArrayList<>();

    public ArrayList<Group> loadedGroups = new ArrayList<>();

    public ArrayList<User> loadedDepartmentMembers = new ArrayList<>();

    public ArrayList<User> loadedGroupMembers = new ArrayList<>();

    public ArrayList<User> loadedMembers = new ArrayList<>();

    public ArrayList<Degree> loadedDegrees = new ArrayList<>();

    public ArrayList<Program> loadedPrograms = new ArrayList<>();

    public ArrayList<String> loadedHashTags= new ArrayList<>();

    public ArrayList<Discussion> loadedGroupThreads = new ArrayList<>();

    public ArrayList<Discussion> loadedUserThreads = new ArrayList<>();

    public ArrayList<Group> loadedUserGroups = new ArrayList<>();

    public ArrayList<Discussion> loadedAppUserThreads = new ArrayList<>();

    public ArrayList<Group> loadedAppUserGroups = new ArrayList<>();

    public ArrayList<Group> loadedGroupForDetail = new ArrayList<>();

    public ArrayList<ThreadHashTag> loadedThreadHashTags = new ArrayList<>();

    public ArrayList<Post> loadedPosts = new ArrayList<>();

    public ArrayList<Post> loadedThreadPosts = new ArrayList<>();

    public ArrayList<Discussion> loadedThreadForDetail = new ArrayList<>();

    public ArrayList<User> loadSearchUsers = new ArrayList<>();
    public ArrayList<User> loadSearchSelectedUsers = new ArrayList<>();

    public ArrayList<User> loadedUserForDetail = new ArrayList<>();

    public ArrayList<GroupCategory> loadedGroupCategories = new ArrayList<>();

    public ArrayList<Department> loadedDepartmentForDetail = new ArrayList<>();

    public Map<String,Bitmap> loadedUserThumbs = new HashMap<>();
    public Map<String,RoundedAvatarDrawable> loadedDepartmentImages = new HashMap<>();
    public Map<String,RoundedAvatarDrawable> loadedGroupImageThumbs = new HashMap<>();

    public RoundedAvatarDrawable loadedDefaultGroupThumb;
    public RoundedAvatarDrawable loadedDefaultDepartmentThumb;

    public ArrayList<Discussion> loadSearchThreads = new ArrayList<>();

    public ArrayList<Group> loadSearchGroups = new ArrayList<>();

    public ArrayList<Post> loadSearchPosts = new ArrayList<>();

    public ArrayList<Department> loadSearchDepartments = new ArrayList<>();

    public Map<String,String> localFilePaths = new HashMap<>();

    public ArrayList<Discussion> loadedStarredThreads = new ArrayList<>();

    public static String currentPageLoad = "20";

    public static boolean isCurrentDataLoadFinished = true;
    public static boolean isNetworkCheckSuccessful = false;

    public static boolean isCurrentSearchFinished = true;

    public static String loadGroupId;
    public static String loadGroupName;

    public static String loadUserId;
    public static String loadUserName;

    public static String loadThreadId;
    public static String loadThreadTitle;
    public static String loadThreadDescription;
    public static String loadThreadParentGroup;
    public static String loadThreadParentGroupName;
    public static String loadThreadVisibilityOpen;

    public static String loadPostId;
    public static String loadPostText;

    public static String loadDepartmentId;
    public static String loadDepartmentTitle;
    public static String loadDepartmentSubTitle;
    public static String loadDepartmentNumMembers;

    public static boolean detachCardItemOnTouchListener = false;
    public static String searchQuery = "";

    public static String uploadImageLocalSourcePath;
    public static String uploadImageFileName;

    //public static String studentProfileCaller = "";
    public static int activeStudentProfileTab = -1;
    //public static int studentProfileViewContext = -1;

    // 0 for Group Image 1 for members image
    public static int downloadGroupViewImageContext = 0;

    public static ArrayList<String> starredThreadsList = new ArrayList<>();

    //public static ArrayList<String> starredGroupThreadsList = new ArrayList<>();

    public static int threadTouchItemId = -1;

    public static boolean groupViewUsersAdded = false;

    public static String defaultImageServerPath = "http://www.therandomquestion.com/ImageUpload/uploads/";

    public static Date serverTime;

    public static String loadSynTimeForFile;
}
