package com.ldceconnect.ldcecommunity.async;

/**
 * Created by Nevil on 1/8/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.util.ApplicationUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Async Task to check whether internet connection is working.
 **/

public class NetCheck extends AsyncTask<String,String,Boolean>
{
    private ProgressDialog nDialog;
    private AppCompatActivity activity;

    public NetCheck(AppCompatActivity activity)
    {
        this.activity = activity;
    }
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        LoadDataModel.isNetworkCheckSuccessful = false;
    }
    /**
     * Gets current device state and checks for working internet connection by trying Google.
     **/
    @Override
    protected Boolean doInBackground(String... args){

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(10000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }
    @Override
    protected void onPostExecute(Boolean th){

        if(th == true){
            //nDialog.dismiss();
            LoadDataModel.isNetworkCheckSuccessful = true;
        }
        else{
            //nDialog.dismiss();
            String msg = "Error in Network Connection";
            ApplicationUtils.showWarningDialog(activity,msg,null);
        }
    }

    void showToast(String msg) {

    }
}
