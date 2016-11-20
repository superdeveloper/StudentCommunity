package com.ldceconnect.ldcecommunity.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEVIL on 11/26/2015.
 */
public class ApplicationModel {

    public enum Tabs {TAB_DEPARTMENT, TAB_GROUP, TAB_DISCUSSION, TAB_NONE}

    public enum ProfileTabs {TAB_ABOUT, TAB_GROUPS, TAB_POSTS, TAB_NONE}

    public enum GroupDetailTabs {TAB_ABOUT, TAB_MEMBERS, TAB_POSTS, TAB_NONE}

    public enum Screen
    {
        SCREEN_EXPLORE_COMMUNITY,
        SCREEN_DEPARTMENT_DETAILS,
        SCREEN_THREAD_DETAILS,
        SCREEN_STARRED_THREADS,
        SCREEN_GROUP_DETAILS,
        SCREEN_STUDENT_PROFILE,
        SCREEN_CREATE_GROUP,
        SCREEN_CREATE_THREAD,
        SCREEN_MY_CONTENT
    }

    public enum CardLayout
    {
        CARD_DEPARTMENT,
        CARD_GROUP,
        CARD_GROUP_BIG,
        CARD_DISCUSSION,
        CARD_DISCUSSION_SMALL,
        CARD_STUDENT,
        CARD_STUDENT_NO_IMAGE,
        CARD_POST_MESSAGE,
        CARD_NONE,
        CARD_PROGRESS,
        CARD_ERROR,
        CARD_CALENDARITEM
    }

    public enum SearchContext
    {
        SEARCH_DEPARTMENT,
        SEARCH_GROUP,
        SEARCH_THREAD,
        SEARCH_POST,
        SEARCH_USER,
        SEARCH_USER_ADD,
        SEARCH_EVERYTHING,
        SEARCH_NONE
    }


    public static class SearchModel
    {
        public static class SearchResult
        {
            public static ArrayList<Integer> rowIndexList = new ArrayList<Integer>();
            public static ArrayList<String> rowItemIdList = new ArrayList<String>();
            public static ArrayList<Integer> selectedRowIndexList = new ArrayList<Integer>();
            public static ArrayList<String> selectedRowItemIdList = new ArrayList<String>();
            public static int clickedRowPosition;
            public static String clickedRowItemId;
        }

        private static SearchContext searchContext;

        public static String[] user_columns = new String[] { "_id", "userid", "name","email" };

        public static String[] thread_columns = new String[] { "_id", "threadid", "threadtitle","threaddescription" };

        public static String[] group_columns = new String[] { "_id", "groupid", "grouptitle","groupinfo" };

        public static String[] post_columns = new String[] { "_id", "postid", "text","postowner" };

        public static String[] department_columns = new String[] { "_id", "deptid", "deptname","deptemail" };

        public static SearchContext getSearchContext()
        {
            return searchContext;
        }

        public static void setSearchContext(SearchContext id)
        {
            searchContext = id;
        }

        private static boolean addUserMode = false;

        public static boolean isAddUserMode() { return addUserMode;}
        public static void setAddUserMode(boolean flag) { addUserMode = flag;}

    }


    private static ApplicationModel appmodel = new ApplicationModel();

    private ApplicationModel() {
    }

    public static ApplicationModel getInstance() {
        return appmodel;
    }

    public static class AppEventModel {

        private static int departmentTabClickId;
        private static int groupTabClickId;
        private static int discussionTabClickId;

        private static Tabs activeTab;
        private static ProfileTabs profileActiveTab;
        private static GroupDetailTabs groupdetailActiveTab;
        private static Screen activeScreen;

        public static int getDepartmentTabClickId() {
            return departmentTabClickId;
        }

        public static void setDepartmentTabClickId(int id) {
            departmentTabClickId = id;
        }

        public static int getGroupTabClickId() {
            return groupTabClickId;
        }

        public static void setGroupTabClickId(int id) {
            groupTabClickId = id;
        }

        public static int getDiscussionTabClickId() {
            return discussionTabClickId;
        }

        public static void setDiscussionTabClickId(int id) {
            discussionTabClickId = id;
        }

        public static Tabs getActiveTab()
        {
            return activeTab;
        }

        public static void setActiveTab(Tabs id)
        {
            activeTab = id;
        }

        public static void setProfileActiveTab(ProfileTabs id)
        {
            profileActiveTab = id;
        }

        public static ProfileTabs getProfileActiveTab()
        {
            return profileActiveTab;
        }

        public static GroupDetailTabs getGroupDetailActiveTab()
        {
            return groupdetailActiveTab;
        }

        public static void setGroupDetailActiveTab(GroupDetailTabs id)
        {
            groupdetailActiveTab = id;
        }

        public static Screen getActiveScreen()
        {
            return activeScreen;
        }

        public static void setActiveScreen(Screen id)
        {
            activeScreen = id;
        }

    }

    public static class Defines{
        public static int MAX_LIST_ROWS_DEPARTMENT = 100;
        public static int MAX_LIST_ROWS_GROUPS = 100;
        public static int MAX_LIST_ROWS_DISCUSSIONS = 100;

        public static int MAX_ROWS_LIMIT = 1000;

    }

    public static class Message {
        public static final int INVALID_USER = 1;
        public static final int INVALID_PASSWORD = 2;

        public static String getErrorMessage(int id) {
            String error;
            switch (id) {
                case 1:
                    error = "User doesn't exist.";
                    break;
                case 2:
                    error = "Wrong password.";
                    break;

                default:
                    error = "Something went wrong.";
            }
            return error;
        }
    }

    public static int removedGroupIndex;
    public static int removedThreadIndex;
    public static int updatedThreadIndex;
    public static int removedGroupThreadIndex;
    public static int removedMemberIndex;
    public static int removedPostIndex;
    public static int loadedGroupIndex;

}
