package com.ldceconnect.ldcecommunity.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nevil on 12/1/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DataModel.DATABASE_NAME, null, DataModel.DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + DataModel.TABLE_POSTS +
                "(" +
                DataModel.POSTS_COLUMN_KEY_POST_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                DataModel.POSTS_COLUMN_KEY_POST_USER_ID_FK + " INTEGER REFERENCES " + DataModel.TABLE_USERS + "," + // Define a foreign key
                DataModel.POSTS_COLUMN_KEY_POST_TEXT + " TEXT ," + DataModel.POSTS_COLUMN_POST_ADDTEXT + " TEXT " +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + DataModel.TABLE_USERS +
                "(" +
                DataModel.USERS_COLUMN_KEY_USER_ID + " INTEGER PRIMARY KEY," +
                DataModel.USERS_COLUMN_KEY_USER_NAME + " TEXT," +
                DataModel.USERS_COLUMN_KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + DataModel.TABLE_LOGIN + "("
                + DataModel.LOGIN_KEY_ID + " INTEGER PRIMARY KEY,"
                + DataModel.LOGIN_KEY_FIRSTNAME + " TEXT,"
                + DataModel.LOGIN_KEY_LASTNAME + " TEXT,"
                + DataModel.LOGIN_KEY_EMAIL + " TEXT UNIQUE,"
                + DataModel.LOGIN_KEY_USERNAME + " TEXT,"
                + DataModel.LOGIN_KEY_UID + " TEXT,"
                + DataModel.LOGIN_KEY_USERID + " TEXT,"
                + DataModel.LOGIN_KEY_IDATE + " TEXT" + ")";

        String CREATE_REFRESH_TIMESTAMP_TABLE = "CREATE TABLE " + DataModel.TABLE_REFRESH_TIMESTAMP + "("
                + DataModel.REFRESH_TIMESTAMP_TIME + " TEXT )";

        String CREATE_TABLE_DOWNLOAD_FILES_TIMESTAMP = "CREATE TABLE " + DataModel.TABLE_DOWNLOAD_FILES_TIMESTAMP + "("
                + DataModel.DOWNLOAD_FILES_TIMESTAMP_FILENAME + " TEXT,"
                + DataModel.DOWNLOAD_FILES_TIMESTAMP_SYNCTIME  + " TEXT )";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_REFRESH_TIMESTAMP_TABLE);
        db.execSQL(CREATE_TABLE_DOWNLOAD_FILES_TIMESTAMP);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + DataModel.TABLE_POSTS);
            db.execSQL("DROP TABLE IF EXISTS " + DataModel.TABLE_USERS);
            onCreate(db);
        }
    }


    // Insert a post into the database
    public void addPost(Post post) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            long userId = addorUpdateUser(post.user);

            ContentValues values = new ContentValues();
            values.put(DataModel.POSTS_COLUMN_KEY_POST_USER_ID_FK, userId);
            values.put(DataModel.POSTS_COLUMN_KEY_POST_TEXT, post.text);
            values.put(DataModel.POSTS_COLUMN_POST_ADDTEXT, "+");

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(DataModel.TABLE_POSTS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Error", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addorUpdateUser(User user) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DataModel.USERS_COLUMN_KEY_USER_NAME, user.username);
            values.put(DataModel.USERS_COLUMN_KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(DataModel.TABLE_USERS, values, DataModel.USERS_COLUMN_KEY_USER_NAME + "= ?", new String[]{user.username});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        DataModel.USERS_COLUMN_KEY_USER_ID, DataModel.TABLE_USERS, DataModel.USERS_COLUMN_KEY_USER_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(user.username)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(DataModel.TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("Error", "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }


    // Get all posts in the database
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.DataModel.POSTS_COLUMN_KEY_POST_USER_ID_FK = USERS.DataModel.USERS_COLUMN_KEY_USER_ID
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        DataModel.TABLE_POSTS,
                        DataModel.TABLE_USERS,
                        DataModel.TABLE_POSTS, DataModel.POSTS_COLUMN_KEY_POST_USER_ID_FK,
                        DataModel.TABLE_USERS, DataModel.USERS_COLUMN_KEY_USER_ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    User newUser = new User();
                    newUser.username = cursor.getString(cursor.getColumnIndex(DataModel.USERS_COLUMN_KEY_USER_NAME));
                    newUser.profilePictureUrl = cursor.getString(cursor.getColumnIndex(DataModel.USERS_COLUMN_KEY_USER_PROFILE_PICTURE_URL));

                    Post newPost = new Post();
                    newPost.text = cursor.getString(cursor.getColumnIndex(DataModel.POSTS_COLUMN_KEY_POST_TEXT));
                    newPost.user = newUser;
                    posts.add(newPost);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Error", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }

    // Update the user's profile picture url
    public int updateUserProfilePicture(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataModel.USERS_COLUMN_KEY_USER_PROFILE_PICTURE_URL, user.profilePictureUrl);

        // Updating profile picture url for user with that userName
        return db.update(DataModel.TABLE_USERS, values, DataModel.USERS_COLUMN_KEY_USER_NAME + " = ?",
                new String[] { String.valueOf(user.username) });
    }

    // Delete all posts and users in the database
    public void deleteAllPostsAndUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(DataModel.TABLE_POSTS, null, null);
            db.delete(DataModel.TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Error", "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getPosts()
    {
        String POSTS_SELECT_QUERY =
                String.format("SELECT %s as _id , %s, %s  FROM %s ",
                        DataModel.POSTS_COLUMN_KEY_POST_ID,
                        DataModel.POSTS_COLUMN_KEY_POST_TEXT,
                        DataModel.POSTS_COLUMN_POST_ADDTEXT,
                        DataModel.TABLE_POSTS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);

        if(cursor!=null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor getPost(String string)
    {
        String POST_SELECT_QUERY =
                String.format("SELECT %s as _id , %s, %s  FROM %s WHERE text like '%s%%'",
                        DataModel.POSTS_COLUMN_KEY_POST_ID,
                        DataModel.POSTS_COLUMN_KEY_POST_TEXT,
                        DataModel.POSTS_COLUMN_POST_ADDTEXT,
                        DataModel.TABLE_POSTS,
                        string);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POST_SELECT_QUERY, null);

        if(cursor!=null)
        {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void logoutUser()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(DataModel.TABLE_LOGIN, null, null);
        db.close();

        UserModel user = UserModel.getInstance();
        user.logout();
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String fname, String lname, String email, String uname,String uid, String userid, String created_at) {

        if( getUser() != null)
        {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataModel.LOGIN_KEY_FIRSTNAME, fname); // FirstName
        values.put(DataModel.LOGIN_KEY_LASTNAME, lname); // LastName
        values.put(DataModel.LOGIN_KEY_EMAIL, email); // Email
        values.put(DataModel.LOGIN_KEY_USERNAME, uname); // UserName
        values.put(DataModel.LOGIN_KEY_UID, uid); // Email
        values.put(DataModel.LOGIN_KEY_IDATE, created_at); // Created At
        values.put(DataModel.LOGIN_KEY_USERID, userid);

        UserModel user = UserModel.getInstance();
        user.user.email = email;
        user.user.userid = userid;
        user.user.fname = fname;
        user.user.lname = lname;
        user.login();

        // Inserting Row
        db.insert(DataModel.TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    public User getUser() {

        String USER_SELECT_QUERY =
                String.format("SELECT %s as _id,%s,%s,%s,%s,%s,%s,%s  FROM %s",
                        DataModel.LOGIN_KEY_ID,
                        DataModel.LOGIN_KEY_FIRSTNAME,
                        DataModel.LOGIN_KEY_LASTNAME,
                        DataModel.LOGIN_KEY_EMAIL,
                        DataModel.LOGIN_KEY_USERNAME,
                        DataModel.LOGIN_KEY_UID,
                        DataModel.LOGIN_KEY_IDATE,
                        DataModel.LOGIN_KEY_USERID,
                        DataModel.TABLE_LOGIN);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(USER_SELECT_QUERY,null);

        if(cursor!=null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String id = cursor.getString(0);
            String fname = cursor.getString(1);
            String lname = cursor.getString(2);
            String email = cursor.getString(3);
            String username = cursor.getString(4);
            String uid = cursor.getString(5);
            String idate = cursor.getString(6);
            String userid = cursor.getString(7);


            UserModel user = UserModel.getInstance();
            user.user.email = email;
            user.user.userid = userid;
            user.user.fname = fname;
            user.user.lname = lname;
            user.login();

            return user.user;

        }

        db.close();

        return null;
    }

    public void updateRefreshTimeStamp()
    {
        String UPDATE_TIME_QUERY = "";
        String INSERT_TIME_QUERY = "";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = getWritableDatabase();


            if( getRefreshTimeStamp() == null)
            {
                INSERT_TIME_QUERY = "INSERT INTO " + DataModel.TABLE_REFRESH_TIMESTAMP + " (" + DataModel.REFRESH_TIMESTAMP_TIME + " )  VALUES( strftime('%Y-%m-%d %H:%M:%S','now'));";
                db.execSQL(INSERT_TIME_QUERY);
            }
            else {
                UPDATE_TIME_QUERY = "UPDATE " + DataModel.TABLE_REFRESH_TIMESTAMP + " SET " + DataModel.REFRESH_TIMESTAMP_TIME + " = strftime('%Y-%m-%d %H:%M:%S','now');";

                db.execSQL(UPDATE_TIME_QUERY);
            }
    }

    public Date getRefreshTimeStamp()
    {
        String UPDATE_TIME_QUERY =
                String.format("SELECT * FROM " + DataModel.TABLE_REFRESH_TIMESTAMP);

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(UPDATE_TIME_QUERY, null);

        if(cursor!=null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String datetime = cursor.getString(0);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(datetime);
                if( date != null)
                {
                    return date;
                }
            }
            catch (ParseException pe)
            {
                pe.printStackTrace();
            }

        }

        return null;
    }

    public Date getDownloadFileSyncTimeStamp(String filename)
    {
        String GET_FILE_SYNC_TIME = "SELECT * FROM " + DataModel.TABLE_DOWNLOAD_FILES_TIMESTAMP + " WHERE " + DataModel.DOWNLOAD_FILES_TIMESTAMP_FILENAME + " = '" + filename + "';";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(GET_FILE_SYNC_TIME, null);

        if(cursor!=null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            String datetime = cursor.getString(1);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(datetime);
                if( date != null)
                {
                    return date;
                }
            }
            catch (ParseException pe)
            {
                pe.printStackTrace();
            }

        }

        return null;
    }

    public void setDownloadFileSyncTimeStamp(String filename,Date syncDate)
    {
        String UPDATE_TIME_QUERY = "";
        String INSERT_TIME_QUERY = "";
        SQLiteDatabase db = getWritableDatabase();
        DateFormat dateformate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateformate.format(syncDate);

        if( getDownloadFileSyncTimeStamp(filename) == null)
        {


            INSERT_TIME_QUERY = "INSERT INTO " + DataModel.TABLE_DOWNLOAD_FILES_TIMESTAMP + " (" +
                    DataModel.DOWNLOAD_FILES_TIMESTAMP_FILENAME + "," + DataModel.DOWNLOAD_FILES_TIMESTAMP_SYNCTIME
                    + ")  VALUES( '" + filename + "', '" + datetime + "');" ;
            db.execSQL(INSERT_TIME_QUERY);
        }
        else {
            UPDATE_TIME_QUERY = "UPDATE " + DataModel.TABLE_DOWNLOAD_FILES_TIMESTAMP + " SET " + DataModel.DOWNLOAD_FILES_TIMESTAMP_SYNCTIME
                    + " = '" + datetime + "' WHERE " + DataModel.DOWNLOAD_FILES_TIMESTAMP_FILENAME + " = '" + filename + "';";

            db.execSQL(UPDATE_TIME_QUERY);
        }
    }

    public Date getCurrentTime()
    {
        String GET_TIME = "SELECT strftime('%Y-%m-%d %H:%M:%S','now');";

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(GET_TIME, null);

        if(cursor!=null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String datetime = cursor.getString(0);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(datetime);
                if (date != null) {
                    return date;
                }
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }
        return null;
    }

    public void createGroup(String groupname, String groupinfo, String adminid)
    {

    }
}