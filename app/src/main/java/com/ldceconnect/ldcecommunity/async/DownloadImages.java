package com.ldceconnect.ldcecommunity.async;

/**
 * Created by Nevil on 1/11/2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import com.ldceconnect.ldcecommunity.DepartmentView;
import com.ldceconnect.ldcecommunity.EditProfileActivity;
import com.ldceconnect.ldcecommunity.ExploreCommunity;
import com.ldceconnect.ldcecommunity.GroupViewActivity;
import com.ldceconnect.ldcecommunity.MyContentActivity;
import com.ldceconnect.ldcecommunity.R;
import com.ldceconnect.ldcecommunity.fragments.DepartmentFragment;
import com.ldceconnect.ldcecommunity.fragments.GroupFragment;
import com.ldceconnect.ldcecommunity.fragments.StudentFragment;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.Group;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nevil on 12/22/2015.
 */
// DownloadImages AsyncTask
public class DownloadImages extends AsyncTask<String, Void, Map<String,Bitmap>> {

    public String imageFolderURL;
    private Context mContext;
    private ArrayList<String> fileNames ;
    private Map<String,Bitmap> mBitmaps =  new HashMap<>();
    private ArrayList<String> localFilePaths = new ArrayList<>();
    private DownloadImagesContext mDownloadContext;

    public enum DownloadImagesContext
    {
        DOWNLOAD_STUDENT_THUMBS,
        DOWNLOAD_STUDENT_IMAGES,
        DOWNLOAD_STUDENT_PROFILE_IMAGE,
        DOWNLOAD_DEPARTMENT_THUMBS,
        DOWNLOAD_DEPARTMENT_IMAGES,
        DOWNLOAD_DEPARTMENT_PROFILE_IMAGE,
        DOWNLOAD_GROUP_THUMBS,
        DOWNLOAD_GROUP_IMAGES,
        DOWNLOAD_GROUP_PROFILE_IMAGE
    }

    public DownloadImages(Context context, ArrayList<String> fileNames, DownloadImagesContext downloadContext)
    {
        this.imageFolderURL = DataModel.uploadImageURL;
        this.mContext = context;
        this.fileNames = fileNames;
        this.mDownloadContext = downloadContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Map<String,Bitmap> doInBackground(String... URL) {

        for( int i = 0 ;i < fileNames.size() ; i++) {

            Bitmap bitmap = null;
            boolean fileAlreadyExist = false;
            String filePath = mContext.getFilesDir() + "/" + ParserUtils.getFileNameFromPath(fileNames.get(i));

            localFilePaths.add(filePath);

            LoadDataModel.loadSynTimeForFile = ParserUtils.getFileNameFromPath(fileNames.get(i));

            if( LoadDataModel.loadSynTimeForFile == null || LoadDataModel.loadSynTimeForFile.equals("null"))
                continue;


            try {

                    DatabaseHelper db = DatabaseHelper.getInstance(mContext);
                    Date localFileTime = db.getDownloadFileSyncTimeStamp(LoadDataModel.loadSynTimeForFile);
                    if (localFileTime != null) {
                        UserFunctions uf = new UserFunctions();
                        JSONObject json_get_file_synctime = uf.getUploadedFileTimestamp(LoadDataModel.loadSynTimeForFile);

                        if (json_get_file_synctime != null && json_get_file_synctime.has(DataModel.KEY_SUCCESS) && Integer.parseInt(json_get_file_synctime.getString(DataModel.KEY_SUCCESS)) == 1) {
                            try {
                                if (json_get_file_synctime.has("servertime")) {
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date synctime = format.parse(json_get_file_synctime.getString("servertime"));
                                    String filename = json_get_file_synctime.getString("filename");

                                    if (localFileTime.compareTo(synctime) < 0) {
                                        File f = new File(filePath);
                                        if (f.exists()) {
                                            f.delete();
                                        }
                                    }

                                    db.setDownloadFileSyncTimeStamp(filename, synctime);
                                }
                            } catch (ParseException p) {
                                p.printStackTrace();
                            }
                        }
                    } else {
                        UserFunctions uf = new UserFunctions();
                        JSONObject json_get_servertime = uf.getServerTime();
                        if (json_get_servertime != null && json_get_servertime.has(DataModel.KEY_SUCCESS) && Integer.parseInt(json_get_servertime.getString(DataModel.KEY_SUCCESS)) == 1) {
                            try {
                                if (json_get_servertime.has("servertime")) {
                                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date servertime = format.parse(json_get_servertime.getString("servertime"));

                                    db.setDownloadFileSyncTimeStamp(LoadDataModel.loadSynTimeForFile, servertime);
                                }
                            } catch (ParseException p) {
                                p.printStackTrace();
                            }
                        }
                    }



                UserModel um = UserModel.getInstance();
                File f = new File(filePath);

                if( f.exists())
                {
                    fileAlreadyExist = true;
                    Bitmap b = BitmapFactory.decodeFile(filePath);
                    if( b != null) {
                        mBitmaps.put(fileNames.get(i), b);
                    }
                }

                if (!fileAlreadyExist) {
                    // Download Image from URL
                    String imageURL = /*imageFolderURL +*/ fileNames.get(i);

                    //java.net.URL uploadURL = new java.net.URL(imageURL);
                    //String checkFile = uploadURL.getQuery();
                    InputStream input = null;

                    if(remoteFileExists(imageURL))
                    {
                        //TODO : check better alternative to openstream
                        // open stream doen not check connection timeout
                        input = new java.net.URL(imageURL).openStream();
                        bitmap = BitmapFactory.decodeStream(input);
                    }else
                    {
                        imageURL = ParserUtils.replaceFileExtension(imageURL,".png");
                        filePath = ParserUtils.replaceFileExtension(filePath, ".png");

                        input = new java.net.URL(imageURL).openStream();
                        bitmap = BitmapFactory.decodeStream(input);
                    }

                    if( bitmap != null) {

                        File dir = mContext.getFilesDir();
                        String tempFileName;

                        tempFileName = ParserUtils.getFileNameFromPath(filePath);

                        File file = new File(dir, tempFileName);

                        filePath = file.getPath();

                        String ext = ParserUtils.getFileExtension(filePath);
                        Bitmap.CompressFormat cf;

                        if (ext.equals(".jpeg") || ext.equals(".jpg"))
                            cf = Bitmap.CompressFormat.JPEG;
                        else if (ext.equals(".png"))
                            cf = Bitmap.CompressFormat.PNG;
                        else
                            cf = Bitmap.CompressFormat.JPEG;

                        FileOutputStream stream = new FileOutputStream(filePath);
                        bitmap.compress(cf, 100, stream);
                        stream.close();
                    }


                /*byte[] byteArray = stream.toByteArray();

                if( byteArray.length > 0) {
                    FileOutputStream outputStream = new FileOutputStream(filePath);
                    outputStream.write(byteArray);
                    outputStream.close();
                }*/
                } else {

                    if (filePath != null)
                        bitmap = BitmapFactory.decodeFile(filePath);

                }

            } catch(UnknownHostException ue)
            {
                ue.printStackTrace();
            }catch(FileNotFoundException fe)
            {
                fe.printStackTrace();
            }
            catch(Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }


            mBitmaps.put(fileNames.get(i),bitmap);
        }
        return mBitmaps;
    }

    @Override
    protected void onPostExecute(Map<String,Bitmap> result) {
        // Set the bitmap into ImageView
        LoadDataModel ldm = LoadDataModel.getInstance();
        if(mBitmaps != null && mBitmaps.size() > 0 && fileNames!= null && fileNames.size() >0 ) {

            if (mDownloadContext == DownloadImagesContext.DOWNLOAD_STUDENT_THUMBS) {

                for (int i = 0; i < fileNames.size(); i++) {
                    Bitmap b = mBitmaps.get(fileNames.get(i));
                    String key = ParserUtils.removeExtFromPath(ParserUtils.getFileNameFromPath(ParserUtils.removeThumbFromPath(fileNames.get(i))));
                    Bitmap alreadyB = ldm.loadedUserThumbs.get(key);

                    if (b != null && alreadyB == null) {
                        ldm.loadedUserThumbs.put(key, mBitmaps.get(fileNames.get(i)));
                    }
                }

                StudentFragment sf = null;
                if (mContext.getClass() == GroupViewActivity.class) {
                    sf = (StudentFragment) ((GroupViewActivity) mContext).adapter.getItem(1);
                } else if(mContext.getClass() == DepartmentView.class)
                {
                    sf = (StudentFragment) ((DepartmentView) mContext).adapter.getItem(1);
                }

                if (sf != null && sf.adapter != null && sf.adapter.dataModels != null) {
                    for (int i = 0; i < sf.adapter.dataModels.size(); i++) {
                        User u = (User) sf.adapter.dataModels.get(i);

                        if (u != null) {
                            sf.adapter.notifyItemChanged(i);
                        }
                    }
                }
            }

            if (mDownloadContext == DownloadImagesContext.DOWNLOAD_GROUP_PROFILE_IMAGE) {

                for (int i = 0; i < fileNames.size(); i++) {
                    Bitmap b = mBitmaps.get(fileNames.get(i));
                    String key = ParserUtils.removeExtFromPath(ParserUtils.getFileNameFromPath(ParserUtils.removeThumbFromPath(fileNames.get(i))));
                    /*RoundedAvatarDrawable alreadyB = ldm.loadedGroupImages.get(key);
                    RoundedAvatarDrawable rd = null;

                    if (b != null && alreadyB == null) {
                        b = ImageUtils.scaleImageTo(b, 300, 300);
                        rd = new RoundedAvatarDrawable(b);
                        ldm.loadedGroupImages.put(key, rd);
                    }
                    else if( (b== null && alreadyB != null) || (b != null && alreadyB != null))
                    {
                        rd = alreadyB;
                    }
                    else if( b == null && alreadyB == null)
                    {
                        ldm.loadedGroupImages.put(key, null);
                    }

                    if( b!= null && rd != null)
                    {
                        ((GroupViewActivity) mContext).mImageView.setBackground(rd);
                    }*/

                    if( b != null)
                    {
                        String filePath = mContext.getFilesDir() + "/" + ParserUtils.getFileNameFromPath(fileNames.get(i));
                        if( ldm.localFilePaths.get(key) == null)
                        {
                            ldm.localFilePaths.put(key, filePath);
                        }
                        Bitmap b2 = ImageUtils.scaleImageTo(b, 300, 300);
                        RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b2);
                        ((GroupViewActivity) mContext).mImageView.setBackground(rd);
                    }
                }



            }

                if (mDownloadContext == DownloadImagesContext.DOWNLOAD_GROUP_IMAGES ||
                        mDownloadContext == DownloadImagesContext.DOWNLOAD_GROUP_THUMBS) {
                    for (int i = 0; i < fileNames.size(); i++) {
                        Bitmap b = mBitmaps.get(fileNames.get(i));
                        String key = ParserUtils.removeExtFromPath(ParserUtils.getFileNameFromPath(ParserUtils.removeThumbFromPath(fileNames.get(i))));
                        RoundedAvatarDrawable alreadyB = ldm.loadedGroupImageThumbs.get(key);
                        RoundedAvatarDrawable rd = null;
                        if (b != null && alreadyB == null) {

                                // add larger images to a map
                                /*b = ImageUtils.scaleImageTo(b, 300, 300);
                                rd = new RoundedAvatarDrawable(b);
                                ldm.loadedGroupImages.put(key, rd);*/

                                // add thumbs to a map
                                b = ImageUtils.scaleImageTo(b, 120, 120);
                                rd = new RoundedAvatarDrawable(b);
                                ldm.loadedGroupImageThumbs.put(key, rd);

                        }
                    }

                    GroupFragment gf = null;
                    if (mContext.getClass() == ExploreCommunity.class) {
                        gf = (GroupFragment) ((ExploreCommunity) mContext).adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_GROUPS_TAB_INDEX);
                    }
                    else if( mContext.getClass() == MyContentActivity.class)
                    {
                        gf = (GroupFragment) ((ExploreCommunity)mContext).adapter.getItem(ApplicationUtils.MY_CONTENT_GROUPS_TAB_INDEX);
                    }

                    if (gf != null && gf.adapter != null && gf.adapter.dataModels != null) {
                        for (int i = 0; i < gf.adapter.dataModels.size(); i++) {
                            Group g = (Group) gf.adapter.dataModels.get(i);
                            if (g != null) {
                                gf.adapter.notifyItemChanged(i);
                            }
                        }
                    }
                }

                if (mDownloadContext == DownloadImagesContext.DOWNLOAD_STUDENT_PROFILE_IMAGE) {

                    String picPath = "";
                    Bitmap b;

                    if( localFilePaths.size() > 0)
                    {
                        picPath = localFilePaths.get(0);

                        UserModel um = UserModel.getInstance();
                        um.user.profilePictureLocalPath = picPath;

                        if (um.user.profilePictureLocalPath != null && !um.user.profilePictureLocalPath.isEmpty()) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;

                            BitmapFactory.decodeFile(um.user.profilePictureLocalPath, options);

                            options.inJustDecodeBounds = false;
                            options.inSampleSize = ImageUtils.calculateInSampleSize(options, 300, 300);

                            b = BitmapFactory.decodeFile(um.user.profilePictureLocalPath, options);

                            if (b != null && (options.outHeight < 300 || options.outWidth < 300)) {
                                //Bitmap.createScaledBitmap(b,300,300,false);
                                b = ImageUtils.scaleImageTo(b, 300, 300);
                                um.user.userProfilePicBitmap = b;

                                LoadDataModel.uploadImageLocalSourcePath = picPath;
                                LoadDataModel.uploadImageFileName = ParserUtils.getFileNameFromPath(LoadDataModel.uploadImageLocalSourcePath);

                                if (mContext.getClass() == EditProfileActivity.class) {
                                    RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                                    ((EditProfileActivity) mContext).mImageView.setImageDrawable(rd);
                                }
                            }
                        }
                    }
                    else if( mBitmaps.size() > 0)
                    {
                        b = mBitmaps.get(fileNames.get(0));
                        if (mContext.getClass() == EditProfileActivity.class) {
                            RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                            ((EditProfileActivity) mContext).mImageView.setImageDrawable(rd);
                        }
                    }

                }

                if (mDownloadContext == DownloadImagesContext.DOWNLOAD_DEPARTMENT_PROFILE_IMAGE)
                {
                    for (int i = 0; i < fileNames.size(); i++) {
                        Bitmap b = mBitmaps.get(fileNames.get(i));
                        RoundedAvatarDrawable alreadyB = ldm.loadedDepartmentImages.get(fileNames.get(i));
                        String key = ParserUtils.removeExtFromPath(ParserUtils.getFileNameFromPath(ParserUtils.removeThumbFromPath(fileNames.get(i))));

                        if (b != null && alreadyB == null) {

                                b = ImageUtils.scaleImageTo(b, 180, 180);
                                BitmapDrawable bd = new BitmapDrawable(b);
                                //RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);

                                //ldm.loadedDepartmentImages.put(key, rd);

                            ((DepartmentView) mContext).mImageView.setBackground(bd);

                            }
                        else if(alreadyB != null)
                        {
                            ((DepartmentView) mContext).mImageView.setBackground(alreadyB);
                        }
                    }
                }

                if (mDownloadContext == DownloadImagesContext.DOWNLOAD_DEPARTMENT_IMAGES)
                {
                    for (int i = 0; i < fileNames.size(); i++) {
                        Bitmap b = mBitmaps.get(fileNames.get(i));
                        RoundedAvatarDrawable alreadyB = ldm.loadedDepartmentImages.get(fileNames.get(i));
                        String key = ParserUtils.removeExtFromPath(ParserUtils.getFileNameFromPath(ParserUtils.removeThumbFromPath(fileNames.get(i))));

                        if (b != null && alreadyB == null) {

                            b = ImageUtils.scaleImageTo(b, 100, 100);
                            RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
                            ldm.loadedDepartmentImages.put(key, rd);

                        }
                    }

                    DepartmentFragment df = null;
                    if (mContext.getClass() == ExploreCommunity.class) {
                        df = (DepartmentFragment) ((ExploreCommunity) mContext).adapter.getItem(ApplicationUtils.EXPLORE_COMMUNITY_DEPARTMENTS_TAB_INDEX);
                    }

                    if (df != null && df.adapter != null && df.adapter.dataModels != null) {
                        for (int i = 0; i < df.adapter.dataModels.size(); i++) {
                            Department d = (Department) df.adapter.dataModels.get(i);
                            if (d != null) {
                                df.adapter.notifyItemChanged(i);
                            }
                        }
                    }
                }
        }

        // Call to garbage collector so as to avoid unnecessory memory consumption
        System.gc();
    }

    public boolean remoteFileExists(String URLName){
        try {
            //HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //        HttpURLConnection.setInstanceFollowRedirects(false)
            HttpURLConnection con =
                    (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}

