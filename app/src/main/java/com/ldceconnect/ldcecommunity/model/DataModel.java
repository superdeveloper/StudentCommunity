package com.ldceconnect.ldcecommunity.model;

import java.util.ArrayList;

/**
 * Created by Nevil on 12/1/2015.
 */
public class DataModel {
    public static DataModel dataModel = new DataModel();

    public DataModel()
    {
    }

    public static DataModel getInstance() {
        return dataModel;
    }

    // Database Info
    public static final String DATABASE_NAME = "ldceDatabase";
    public static final int DATABASE_VERSION = 1;

    public static String KEY_SUCCESS = "success";
    public static String KEY_ERROR = "error";
    public static String KEY_NUMROWS = "numrows";

    public static final String BASE_COLUMN = "_id";

    // Post Table Columns
    public static final String TABLE_POSTS = "posts";
    public static final String POSTS_COLUMN_KEY_POST_ID = "id";
    public static final String POSTS_COLUMN_KEY_POST_USER_ID_FK = "userId";
    public static final String POSTS_COLUMN_KEY_POST_TEXT = "text";
    public static final String POSTS_COLUMN_POST_ADDTEXT = "addtext";

    public static final String TABLE_REFRESH_TIMESTAMP = "refreshtimestamp";
    public static final String REFRESH_TIMESTAMP_TIME = "refreshtime";

    public static final String TABLE_DOWNLOAD_FILES_TIMESTAMP = "downloadfilestimestamp";
    public static final String DOWNLOAD_FILES_TIMESTAMP_FILENAME = "filename";
    public static final String DOWNLOAD_FILES_TIMESTAMP_SYNCTIME = "synctime";

    // User Table Columns
    public static final String TABLE_USERS = "users";
    public static final String USERS_COLUMN_KEY_USER_ID = "id";
    public static final String USERS_COLUMN_KEY_USER_NAME = "userName";
    public static final String USERS_COLUMN_KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    // Login table name
    public static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    public static final String LOGIN_KEY_ID = "id";
    public static final String LOGIN_KEY_FIRSTNAME = "fname";
    public static final String LOGIN_KEY_LASTNAME = "lname";
    public static final String LOGIN_KEY_EMAIL = "email";
    public static final String LOGIN_KEY_USERNAME = "uname";
    public static final String LOGIN_KEY_UID = "uid";
    public static final String LOGIN_KEY_USERID = "userid";
    public static final String LOGIN_KEY_IDATE = "idate";

    // Department Table Columns names
    public static final String TABLE_DEPARTMENT = "department";
    public static final String DEPARTMENT_COLUMN_ID = "deptid";
    public static final String DEPARTMENT_COLUMN_NAME= "deptname";
    public static final String DEPARTMENT_COLUMN_DESCRIPTION = "deptinfo";
    public static final String DEPARTMENT_COLUMN_EMAIL = "deptemail";
    public static final String DEPARTMENT_COLUMN_CONTACT = "deptcontactno";
    public static final String DEPARTMENT_COLUMN_IMAGEURL = "deptimageurl";
    public static final String DEPARTMENT_COLUMN_IDATE = "idate";
    public static final String DEPARTMENT_NUM_COUNT = "deptcount";

    // Group Table Columns names
    public static final String TABLE_GROUP = "`group`";
    public static final String GROUP_COLUMN_ID = "groupid";
    public static final String GROUP_COLUMN_NAME= "grouptitle";
    public static final String GROUP_COLUMN_DESCRIPTION = "groupinfo";
    public static final String GROUP_COLUMN_ADMIN = "groupadmin";
    public static final String GROUP_COLUMN_IDATE = "idate";
    public static final String GROUP_COLUMN_IMAGEURL = "groupimageurl";
    public static final String GROUP_NUM_COUNT = "groupcount";

    // Thread Table Columns names
    public static final String TABLE_THREAD = "thread";
    public static final String THREAD_COLUMN_ID = "threadid";
    public static final String THREAD_COLUMN_TITLE= "threadtitle";
    public static final String THREAD_COLUMN_DESCRIPTION = "threaddescription";
    public static final String THREAD_COLUMN_OWNER = "threadowner";
    public static final String THREAD_COLUMN_PARENTGROUP = "parentgroup";
    public static final String THREAD_COLUMN_NUMPOSTS = "numposts";
    public static final String THREAD_COLUMN_NUMSTARS = "numstars";
    public static final String THREAD_COLUMN_IDATE = "idate";
    public static final String THREAD_NUM_COUNT = "threadcount";

    // Post Table Columns names
    public static final String TABLE_POST = "post";
    public static final String POST_COLUMN_ID = "postid";
    public static final String POST_COLUMN_TEXT= "text";
    public static final String POST_COLUMN_THREADID = "threadid";
    public static final String POST_COLUMN_OWNER = "postowner";
    public static final String POST_COLUMN_IDATE = "idate";
    public static final String POST_COLUMN_UDATE = "udate";
    public static final String POST_NUM_COUNT = "threadcount";

    // Department Degree Columns names
    public static final String TABLE_DEGREE = "degree";
    public static final String DEGREE_COLUMN_ID = "degreeid";
    public static final String DEGREE_COLUMN_TITLE= "degreetitle";
    public static final String DEGREE_COLUMN_IDATE = "idate";
    public static final String DEGREE_NUM_COUNT = "degreecount";

    // Department Program Columns names
    public static final String TABLE_PROGRAM = "program";
    public static final String PROGRAM_COLUMN_ID = "programid";
    public static final String PROGRAM_COLUMN_NAME = "programname";
    public static final String PROGRAM_COLUMN_INFO = "programinfo";
    public static final String PROGRAM_COLUMN_IDATE = "idate";
    public static final String PROGRAM_NUM_COUNT = "programcount";

    // User Table Columns
    public static final String TABLE_USER = "user";
    public static final String USER_COLUMN_ID = "userid";
    public static final String USER_COLUMN_FNAME = "firstname";
    public static final String USER_COLUMN_LNAME = "lastname";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_MOBILE = "mobile";
    public static final String USER_COLUMN_ISMOBILEVISIBLE = "ismobilevisible";
    public static final String USER_COLUMN_INTERESTS = "interests";
    public static final String USER_COLUMN_USERNAME = "username";
    public static final String USER_COLUMN_DEPTID = "departmentid";
    public static final String USER_COLUMN_DEGREEID = "degreeid";
    public static final String USER_COLUMN_PROGRAMID = "programid";
    public static final String USER_COLUMN_PROFILEPIC = "profilePictureUrl";
    public static final String USER_COLUMN_SHOWEMAIL = "showemail";
    public static final String USER_COLUMN_SHOWMOBILE = "showmobile";

    public static Integer NUM_DEPARTMENTS;
    public static Integer NUM_POSTS;
    public static Integer NUM_THREADS;
    public static Integer NUM_GROUPS;
    public static Integer NUM_MEMBERS;

    public static Integer MAX_LOADED_DEPARTMENTS = 50;
    public static Integer MAX_LOADED_POSTS = 300;
    public static Integer MAX_LOADED_THREADS = 300;
    public static Integer MAX_LOADED_GROUPS = 300;
    public static Integer MAX_LOADED_MEMBERS = 1000;

    public static Integer NUM_LOAD_ITEMS_ON_PAGE = 20;

    public static String uploadImageURL = "http://www.therandomquestion.com/ImageUpload/uploads/";
}
