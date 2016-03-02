/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ldceconnect.ldcecommunity.util;

import android.content.ContentProvider;
import android.net.Uri;
import android.text.format.Time;

import com.ldceconnect.ldcecommunity.model.ApplicationModel;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Discussion;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.GroupCategory;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Post;
import com.ldceconnect.ldcecommunity.model.Program;
import com.ldceconnect.ldcecommunity.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Various utility methods used by.
 */
public class ParserUtils {
    /** Used to sanitize a string to be {@link Uri} safe. */
    private static final Pattern sSanitizePattern = Pattern.compile("[^a-z0-9-_]");

    /**
     * Sanitize the given string to be {@link Uri} safe for building
     * {@link ContentProvider} paths.
     */
    public static String sanitizeId(String input) {
        if (input == null) {
            return null;
        }
        return sSanitizePattern.matcher(input.replace("+", "plus").toLowerCase()).replaceAll("");
    }

    /**
     * Parse the given string as a RFC 3339 timestamp, returning the value as
     * milliseconds since the epoch.
     */
    public static long parseTime(String timestamp) {
        final Time time = new Time();
        time.parse3339(timestamp);
        return time.toMillis(false);
    }

    public static String joinStrings(String connector, ArrayList<String> strings, StringBuilder recycle) {
        if (strings.size() <= 0) {
            return "";
        }
        if (recycle == null) {
            recycle = new StringBuilder();
        } else {
            recycle.setLength(0);
        }
        recycle.append(strings.get(0));
        for (int i = 1; i < strings.size(); i++) {
            recycle.append(connector);
            recycle.append(strings.get(i));
        }
        return recycle.toString();
    }

    public static String getFileExtension(String filePath)
    {
        int i = filePath.lastIndexOf(".");

        return filePath.substring(i, filePath.length());
    }

    public static String getFileThumbName(String filePath)
    {
        int i = filePath.lastIndexOf(".");

        String ext = filePath.substring(i, filePath.length());
        String file = filePath.substring(0, i);

        return file + "_thumb" + ext;
    }

    public static String getFileNameFromPath(String filePath)
    {
        int i = filePath.lastIndexOf("/");

        return filePath.substring(i+1,filePath.length());
    }

    public static String replaceFileExtension(String filePath,String newExt)
    {
        String oldExt = getFileExtension(filePath);

        filePath = filePath.replace(oldExt, newExt);

        return filePath;
    }

    public static void ParseDiscussionData(JSONObject json, ArrayList<Discussion> loadedThreads)
    {
            /* Department Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ){
                    JSONArray ja = json.getJSONArray("threads");
                    DataModel dm = DataModel.getInstance();
                    Integer threadcount = -1;
                    for (int i = 0; i < ja.length(); i++) {
                        Discussion d = new Discussion();
                        JSONObject jo = ja.getJSONObject(i);
                        if (threadcount < 0) {
                            threadcount = 25; //jo.getInt(DataModel.DEPARTMENT_NUM_COUNT);
                        }
                        d.id = jo.getString(DataModel.THREAD_COLUMN_ID);
                        d.title = jo.getString(DataModel.THREAD_COLUMN_TITLE);
                        d.description = jo.getString(DataModel.THREAD_COLUMN_DESCRIPTION);
                        d.owner = jo.getString(DataModel.THREAD_COLUMN_OWNER);
                        d.parentgroup = jo.getString(DataModel.THREAD_COLUMN_PARENTGROUP);

                        if( jo.has(DataModel.GROUP_COLUMN_NAME))
                            d.parentgroupname = jo.getString(DataModel.GROUP_COLUMN_NAME);

                        if(jo.has(DataModel.THREAD_COLUMN_NUMPOSTS))
                            d.numposts = jo.getString(DataModel.THREAD_COLUMN_NUMPOSTS);

                        if(jo.has(DataModel.THREAD_COLUMN_NUMSTARS))
                            d.numstars = jo.getString(DataModel.THREAD_COLUMN_NUMSTARS);

                        if(jo.has("publicthread"))
                            d.ispublic = jo.getString("publicthread");

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        d.idate = format.parse(jo.getString(DataModel.THREAD_COLUMN_IDATE));

                        loadedThreads.add(d);
                    }

                    DataModel.NUM_THREADS = threadcount;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static void ParseMembersData(JSONObject json,ArrayList<User> loadedMembers)
    {
        /* Members Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("members");

                    for (int i = 0; i < ja.length(); i++) {
                        User u = new User();
                        JSONObject jo = ja.getJSONObject(i);

                        u.userid = jo.getString(DataModel.USER_COLUMN_ID);
                        u.username = jo.getString(DataModel.USER_COLUMN_USERNAME);
                        u.email = jo.getString(DataModel.USER_COLUMN_EMAIL);
                        u.fname = jo.getString(DataModel.USER_COLUMN_FNAME);
                        u.lname = jo.getString(DataModel.USER_COLUMN_LNAME);
                        u.mobile = jo.getString(DataModel.USER_COLUMN_MOBILE);
                        u.department = new Department();
                        u.department.id = jo.getString(DataModel.USER_COLUMN_DEPTID);
                        u.degree = new Degree();
                        u.degree.id = jo.getString(DataModel.USER_COLUMN_DEGREEID);
                        u.program = new Program();
                        u.program.id = jo.getString(DataModel.USER_COLUMN_PROGRAMID);
                        u.interests = jo.getString(DataModel.USER_COLUMN_INTERESTS);
                        u.profilePictureUrl = jo.getString(DataModel.USER_COLUMN_PROFILEPIC);


                        if(jo.has("passwordrecoveryrequest"))
                            u.forgotpassrequest = jo.getString("passwordrecoveryrequest");

                        if(jo.has(DataModel.USER_COLUMN_SHOWEMAIL))
                            u.showEmailId = jo.getString(DataModel.USER_COLUMN_SHOWEMAIL);

                        if(jo.has(DataModel.USER_COLUMN_SHOWMOBILE))
                            u.showMobileNo = jo.getString(DataModel.USER_COLUMN_SHOWMOBILE);

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        u.idate = format.parse(jo.getString(DataModel.THREAD_COLUMN_IDATE));

                        loadedMembers.add(u);
                    }

                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static void ParsePostsData(JSONObject json, ArrayList<Post> loadedPosts)
    {
            /* Department Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("posts");
                    DataModel dm = DataModel.getInstance();

                    for (int i = 0; i < ja.length(); i++) {
                        Post d = new Post();
                        JSONObject jo = ja.getJSONObject(i);

                        d.id = jo.getString(DataModel.POST_COLUMN_ID);
                        d.text = jo.getString(DataModel.POST_COLUMN_TEXT);
                        d.threadid = jo.getString(DataModel.POST_COLUMN_THREADID);
                        d.postowner = jo.getString(DataModel.POST_COLUMN_OWNER);

                        if( jo.has("name"))
                            d.postownername = jo.getString("name");

                        if(jo.has(DataModel.POST_COLUMN_IDATE) && !jo.getString(DataModel.POST_COLUMN_IDATE).equals("null")) {
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            d.idate = format.parse(jo.getString(DataModel.POST_COLUMN_IDATE));
                        }
                        if(jo.has(DataModel.POST_COLUMN_UDATE) && !jo.getString(DataModel.POST_COLUMN_UDATE).equals("null")) {
                            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            d.udate = format1.parse(jo.getString(DataModel.POST_COLUMN_UDATE));
                        }

                        loadedPosts.add(d);
                    }

                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static void ParseDepartmentData(JSONObject json,ArrayList<Department> loadedDepartments)
    {
            /* Department Data*/
        if(json != null) {
            try {

                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("departments");
                    DataModel dm = DataModel.getInstance();

                    Integer deptcount = -1;

                    for (int i = 0; i < ja.length(); i++) {
                        Department d = new Department();
                        JSONObject jo = ja.getJSONObject(i);
                        if (deptcount < 0) {
                            deptcount = 15; //jo.getInt(DataModel.DEPARTMENT_NUM_COUNT);
                        }
                        d.id = jo.getString(DataModel.DEPARTMENT_COLUMN_ID);
                        d.name = jo.getString(DataModel.DEPARTMENT_COLUMN_NAME);
                        d.descritpion = jo.getString(DataModel.DEPARTMENT_COLUMN_DESCRIPTION);
                        d.email = jo.getString(DataModel.DEPARTMENT_COLUMN_EMAIL);
                        d.contact = jo.getString(DataModel.DEPARTMENT_COLUMN_CONTACT);
                        d.nummembers = jo.getString("nummembers");
                        if( d.nummembers.equals("null"))
                            d.nummembers = "0";
                        d.deptimageurl = jo.getString(DataModel.DEPARTMENT_COLUMN_IMAGEURL);

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        d.idate = format.parse(jo.getString(DataModel.DEPARTMENT_COLUMN_IDATE));

                        loadedDepartments.add(d);
                    }

                    DataModel.NUM_DEPARTMENTS = deptcount;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static void ParseGroupData(JSONObject json, ArrayList<Group> loadedGroups)
    {
            /* Department Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("groups");
                    DataModel dm = DataModel.getInstance();
                    Integer groupcount = -1;
                    for (int i = 0; i < ja.length(); i++) {
                        Group d = new Group();
                        JSONObject jo = ja.getJSONObject(i);
                        if (groupcount < 0) {
                            groupcount = 15; //jo.getInt(DataModel.DEPARTMENT_NUM_COUNT);
                        }
                        d.id = jo.getString(DataModel.GROUP_COLUMN_ID);
                        d.name = jo.getString(DataModel.GROUP_COLUMN_NAME);
                        d.descritpion = jo.getString(DataModel.GROUP_COLUMN_DESCRIPTION);
                        d.admin = jo.getString(DataModel.GROUP_COLUMN_ADMIN);
                        d.groupimageurl = jo.getString(DataModel.GROUP_COLUMN_IMAGEURL);

                        if( jo.has("nummembers"))
                            d.nummembers = jo.getString("nummembers");

                        if(jo.has("numthreads"))
                            d.numthreads = jo.getString("numthreads");

                        if( jo.has("groupcategory"))
                            d.category = jo.getString("groupcategory");

                        if( jo.has("groupadminname"))
                            d.adminname = jo.getString("groupadminname");

                        if( d.nummembers!= null && d.nummembers.equals("null"))
                            d.nummembers = "0";

                        if(d.numthreads != null && d.numthreads.equals("null"))
                            d.numthreads = "0";

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        d.idate = format.parse(jo.getString(DataModel.GROUP_COLUMN_IDATE));

                        loadedGroups.add(d);
                    }

                    DataModel.NUM_GROUPS = groupcount;
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }



    public static void ParseDegreesData(JSONObject json,ArrayList<Degree> loadedDegrees)
    {
        /* Department Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("degrees");

                    /*Degree dg = new Degree();
                    dg.id = "0";
                    dg.title = "Degree";
                    loadedDegrees.add(dg);*/

                    Integer deptcount = -1;
                    for (int i = 0; i < ja.length(); i++) {
                        Degree d = new Degree();
                        JSONObject jo = ja.getJSONObject(i);
                        if (deptcount < 0) {
                            deptcount = 15; //jo.getInt(DataModel.DEPARTMENT_NUM_COUNT);
                        }
                        d.id = jo.getString(DataModel.DEGREE_COLUMN_ID);
                        d.title = jo.getString(DataModel.DEGREE_COLUMN_TITLE);

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        d.idate = format.parse(jo.getString(DataModel.DEGREE_COLUMN_IDATE));

                        loadedDegrees.add(d);
                    }

                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static void ParseProgramsData(JSONObject json,ArrayList<Program> loadedPrograms)
    {
            /* Department Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("programs");

                    /*Program p = new Program();
                    p.id = "0";
                    p.name = "Program";
                    loadedPrograms.add(p);*/

                    Integer deptcount = -1;
                    for (int i = 0; i < ja.length(); i++) {
                        Program d = new Program();
                        JSONObject jo = ja.getJSONObject(i);
                        if (deptcount < 0) {
                            deptcount = 15; //jo.getInt(DataModel.DEPARTMENT_NUM_COUNT);
                        }
                        d.id = jo.getString(DataModel.PROGRAM_COLUMN_ID);
                        d.name = jo.getString(DataModel.PROGRAM_COLUMN_NAME);
                        d.info = jo.getString(DataModel.PROGRAM_COLUMN_INFO);

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        d.idate = format.parse(jo.getString(DataModel.PROGRAM_COLUMN_IDATE));

                        loadedPrograms.add(d);
                    }

                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static void ParseHashtagsData(JSONObject json,ArrayList<String> loadedHashTags)
    {
        if(json != null) {
            try {
                JSONArray ja = json.getJSONArray("interests");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    loadedHashTags.add(jo.getString("tag"));
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public static void ParseStarData(JSONObject json,ArrayList<String> starredThreads)
    {
        if(json != null) {
            try {
                JSONArray ja = json.getJSONArray("threads");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    starredThreads.add(jo.getString("threadid"));
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }


    public static void ParseGroupCategoryData(JSONObject json, ArrayList<GroupCategory> loadedGroupCategories)
    {
            /* Department Data*/
        if(json != null) {
            try {
                if( json.getString(DataModel.KEY_SUCCESS) != null && Integer.parseInt(json.getString(DataModel.KEY_SUCCESS)) == 1 &&
                        json.getString(DataModel.KEY_NUMROWS) != null &&  Integer.parseInt(json.getString(DataModel.KEY_NUMROWS)) > 0 ) {
                    JSONArray ja = json.getJSONArray("groupcategory");
                    LoadDataModel dm = LoadDataModel.getInstance();

                    GroupCategory gc = new GroupCategory();
                    gc.title = "Category";
                    dm.loadedGroupCategories.add(gc);

                    for (int i = 0; i < ja.length(); i++) {
                        GroupCategory d = new GroupCategory();
                        JSONObject jo = ja.getJSONObject(i);

                        d.id = jo.getString("id");
                        d.title = jo.getString("title");

                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        d.idate = format.parse(jo.getString(DataModel.GROUP_COLUMN_IDATE));

                        loadedGroupCategories.add(d);
                    }

                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            catch(ParseException p)
            {
                p.printStackTrace();
            }
        }
    }

    public static ArrayList<String> getUsernameArrayFromUserArray(ArrayList<User> users)
    {
        ArrayList<String> userNames = new ArrayList<>();

        for(int i = 0 ; i < users.size(); i++)
        {
            User u = users.get(i);

            if( u != null && u.email != null)
            {
                userNames.add(u.email);
            }
        }

        return userNames;
    }

    public static ArrayList<String> getFilenamesFromUserNames(ArrayList<String> users,String fileNameEndDescriptor )
    {
        ArrayList<String> fileNames = new ArrayList<>();

        for(int i = 0 ; i < users.size(); i++)
        {
            String u = users.get(i);

            if( u != null)
            {
                fileNames.add(u + fileNameEndDescriptor + ".jpg");
            }
        }

        return fileNames;
    }

    public static ArrayList<String> getUploadFilePathsFromFilenames(ArrayList<String> files )
    {
        ArrayList<String> filePaths = new ArrayList<>();
        String str;
        for(int i = 0 ; i < files.size(); i++)
        {
            String u = files.get(i);
            if( u != null)
            {
                str = new String(u.toCharArray());
                str = DataModel.uploadImageURL + u;
                filePaths.add(str);
            }
        }

        return filePaths;
    }

    public static String removeThumbFromPath(String path)
    {
        String str = new String(path.replace("_thumb",""));
        return str;
    }

    public static String removeExtFromPath(String path)
    {
        int index = path.lastIndexOf(".");
        if(index > 0) {
            path = new String(path.substring(0, path.lastIndexOf(".") ));
        }
        return path;
    }

    public static ArrayList<String> parseHashTagsFromString(String string)
    {
        ArrayList<String> hashtags = new ArrayList<>();

        String[] tags = string.split(",");

        for(int i = 0; i < tags.length; i++) {
            if( !tags[i].trim().isEmpty())
                hashtags.add(tags[i].trim());
        }

        return  hashtags;
    }

    public static int findStringInList(ArrayList<String> list, String str)
    {
        for(int i = 0 ; i < list.size(); i++)
        {
            if( str.equals(list.get(i)))
            {
                return i;
            }
        }
        return -1;
    }

}
