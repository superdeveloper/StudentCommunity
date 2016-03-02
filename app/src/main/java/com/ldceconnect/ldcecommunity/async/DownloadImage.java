package com.ldceconnect.ldcecommunity.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Nevil on 12/22/2015.
 */
// DownloadImage AsyncTask
public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    public String imageURL;
    private Context mContext;
    private User user;

    public DownloadImage(Context context, User user, String url)
    {
        this.imageURL = url;
        this.mContext = context;
        this.user = user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        Bitmap bitmap = null;
        boolean fileAlreadyExist = false;
        String fileName = ParserUtils.getFileNameFromPath(imageURL);
        String filePath;

        try {

            if( this.user == null)
                return null;

            if(this.user.profilePictureLocalPath != null && !this.user.profilePictureLocalPath.isEmpty()) {
                File file = new File(this.user.profilePictureLocalPath);
                if (file.exists())
                {
                    fileAlreadyExist = true;
                }
            }

            if(!fileAlreadyExist) {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);

                File dir = mContext.getFilesDir();
                File file = new File(dir, fileName);

                this.user.profilePictureLocalPath = file.getPath();
                filePath = this.user.profilePictureLocalPath;

                String ext = ParserUtils.getFileExtension(this.user.profilePictureLocalPath);
                Bitmap.CompressFormat cf;

                if( ext.equals(".jpeg") || ext.equals(".jpg"))
                    cf = Bitmap.CompressFormat.JPEG;
                else if(ext.equals(".png"))
                    cf = Bitmap.CompressFormat.PNG;
                else
                    cf = Bitmap.CompressFormat.JPEG;

                FileOutputStream stream = new FileOutputStream(filePath);
                bitmap.compress(cf, 100, stream);
                stream.close();
            }
            else
            {
                filePath = ImageFilePath.getAbsolutePath(mContext,fileName);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;


                bitmap = BitmapFactory.decodeFile(filePath, options);

                options.inJustDecodeBounds = false;
                options.inSampleSize = ImageUtils.calculateInSampleSize(options, 300, 300);

                InputStream input = new FileInputStream(filePath);
                File file = new File(filePath);
                long bytes = file.length();

                byte[] bytess = new byte[(int)bytes];
                //ByteArrayInputStream buffer = new ByteArrayInputStream(input);
                input.read(bytess);

                input.close();

                if( filePath != null)
                bitmap = BitmapFactory.decodeFile(filePath,  options);

                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // Set the bitmap into ImageView

    }
}
