package com.ldceconnect.ldcecommunity.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class UserModel {
    private static UserModel userData = new UserModel();

    private UserModel() {

    }

    public static UserModel getInstance() {
        return userData;
    }

    public User user = new User();

    private boolean loggedIn;

    public ArrayList<Integer> groupsSubscribed;

    public ArrayList<Integer> groupsOwned;

    public ArrayList<Integer> topDiscussions;

    public ArrayList<Integer> threadsOwned;

    public String queryUser(String email)
    {
        return ApplicationModel.Message.getErrorMessage(ApplicationModel.Message.INVALID_USER);
    }

    public void logout()
    {
        loggedIn = false;
        user.email = "";
        user.profilePictureUrl = "";
        user.profilePictureLocalPath = "";
        user.program = null;
        user.mobile = "";
        user.about = "";
        user.degree = null;
        user.department = null;
        user.fname = "";
        user.lname = "";
        user.interests = "";
        user.mGoogleAccount = null;
        user.password = null;
        user.userid = "";
        user.username = "";
        user.userProfilePicBitmap = null;
        user.profilePictureLocalThumbPath = "";
        user.profilePictureThumbUrl = "";
        user.groupType = "";
    }

    public boolean isLoggedIn()
    {
        return loggedIn;
    }

    public void login()
    {
        loggedIn = true;
    }

    public enum SortGroups
    {
        SORT_BY_IDATE,
        SORT_BY_TRENDING,
        SORT_BY_GROUP_PREFERENCE
    }

    public enum SortThreads
    {
        SORT_BY_IDATE,
        SORT_BY_TRENDING,
        SORT_BY_THREAD_PREFERENCE
    }

    public String GetGroupSortType()
    {
        if(sortGroups == SortGroups.SORT_BY_IDATE)
            return "1";
        else if(sortGroups == SortGroups.SORT_BY_TRENDING)
            return "2";
        else if(sortGroups == SortGroups.SORT_BY_GROUP_PREFERENCE)
            return "3";

        return "1";
    }

    public String GetThreadSortType()
    {
        if(sortThreads == SortThreads.SORT_BY_IDATE)
            return "1";
        else if(sortThreads == SortThreads.SORT_BY_TRENDING)
            return "2";
        else if(sortThreads == SortThreads.SORT_BY_THREAD_PREFERENCE)
            return "3";

        return "1";
    }

    public String GetGroupTypePreference()
    {
        return groupTypePreference;
    }

    public String GetThreadTypePreference()
    {
        return threadTypePreference;
    }

    public String groupTypePreference;
    public String threadTypePreference;

    public SortGroups sortGroups = SortGroups.SORT_BY_IDATE;
    public SortThreads sortThreads = SortThreads.SORT_BY_IDATE;

    public enum UserAction
    {
        LEAVE_GROUP,
        DELETE_GROUP,
        DELETE_THREAD,
        UPDATE_THREAD_TITLE,
        REMOVE_MEMBER_FROM_GROUP,
        ADD_MEMBER_TO_GROUP,
        UPDATE_GROUP_IMAGE,
        CREATE_NEW_GROUP,
        NONE
    }

    public static UserAction userAction = UserAction.NONE;

    public static void resetUserAction()
    {
        userAction = UserAction.NONE;
    }

    public static boolean isGoogleLogin = false;

}
