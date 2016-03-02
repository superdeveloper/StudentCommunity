package com.ldceconnect.ldcecommunity.async;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.util.AndroidMultiPartEntity;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Nevil on 12/22/2015.
 */
public class UploadImage extends AsyncTask<Void, Integer, JSONObject> {

    AppCompatActivity activity;
    private String mFilePath = null;
    long totalSize = 0;
    InputStream is = null;
    JSONObject jObj;
    String jsonString;
    String uploadedFileName;
    int mStatusCode;
    private ProgressDialog pDialog;
    boolean showProgressDialog = false;

    public static final String FILE_UPLOAD_URL = "http://www.therandomquestion.com/ImageUpload/fileUpload.php";

    public UploadImage(AppCompatActivity activity,String filePath,String uploadedFileName, boolean showProgressDialog)
    {
        this.activity = activity;
        this.mFilePath = filePath;
        this.uploadedFileName = uploadedFileName;
        this.showProgressDialog = showProgressDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        boolean netcheckResult= false;
        LoadDataModel.isNetworkCheckSuccessful = false;

        try
        {
            netcheckResult = new NetCheck(activity).execute().get();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex2) {
            ex2.printStackTrace();
        } catch ( CancellationException ce)
        {
            ce.printStackTrace();
        }

        if(netcheckResult) {

            LoadDataModel.isNetworkCheckSuccessful = true;

            if(showProgressDialog) {
                pDialog = new ProgressDialog(activity);
                pDialog.setMessage("Uploading...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if( pDialog != null && showProgressDialog) {
            // updating progress bar value
            pDialog.setProgress(progress[0]);
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        super.onPostExecute(s);


        if( s != null && mStatusCode == 200 )
        {
            if( pDialog != null && showProgressDialog) {
                pDialog.setMessage("Image Uploaded Successfully");
                pDialog.dismiss();
            }

            // Delete the local file if it exists and rename the new local file
            File dir = activity.getFilesDir();
            String dirPath = dir.getAbsolutePath();
            String localFile = dirPath + "/" + uploadedFileName;
            File f1 = new File(localFile);
            f1.delete();

            File f2 = new File(mFilePath);
            if( f2.exists())
            {
                File f3 = new File(dirPath + "/" + uploadedFileName);
                boolean flag = f2.renameTo(f3);
            }

        }
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        JSONObject result = uploadFile();

        if( result != null && mStatusCode == 200 ) {
            DatabaseHelper db = DatabaseHelper.getInstance(activity);

            UserFunctions uf = new UserFunctions();
            try {
                LoadDataModel.loadSynTimeForFile = uploadedFileName;
                JSONObject json_set_file_synctime = uf.setUploadedFileTimestamp(LoadDataModel.loadSynTimeForFile);
                if (json_set_file_synctime != null && json_set_file_synctime.has(DataModel.KEY_SUCCESS) && Integer.parseInt(json_set_file_synctime.getString(DataModel.KEY_SUCCESS)) == 1) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date synctime = format.parse(json_set_file_synctime.getString("servertime"));
                    String filename = json_set_file_synctime.getString("filename");
                    db.setDownloadFileSyncTimeStamp(filename, synctime);
                }
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (ParseException p) {
                p.printStackTrace();
            }
        }

        return result;
    }

    @SuppressWarnings("deprecation")
    private JSONObject uploadFile() {
        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(FILE_UPLOAD_URL);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            File sourceFile = new File(mFilePath);

            // Adding file data to http body
            entity.addPart("image", new FileBody(sourceFile));

            // Extra parameters if you want to pass to server
            entity.addPart("filename",
                    new StringBody(this.uploadedFileName));
            /*entity.addPart("email", new StringBody("abc@gmail.com"));*/

            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            mStatusCode = response.getStatusLine().getStatusCode();
            if (mStatusCode == 200) {
                // Server response
                is = r_entity.getContent();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    jsonString = sb.toString();
                    Log.e("JSON", jsonString);
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(jsonString);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

            } else {
                responseString = "Error occurred! Http Status Code: "
                        + mStatusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return jObj;

    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
