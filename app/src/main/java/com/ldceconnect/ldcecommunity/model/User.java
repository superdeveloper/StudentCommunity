package com.ldceconnect.ldcecommunity.model;

import android.graphics.Bitmap;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Date;

/**
 * Created by Nevil on 12/1/2015.
 */
public class User {
    public String username;
    public String userid;
    public String fname;
    public String lname;
    public String email;
    public String password;
    public String mobile;
    public String about;
    public String interests;
    public Degree degree;
    public Program program;
    public Department department;
    public String profilePictureUrl;
    public String profilePictureThumbUrl;
    public String profilePictureLocalPath;
    public String profilePictureLocalThumbPath;
    public Date idate;
    public String iby;
    public Date udate;
    public String uby;
    public boolean selectedflag;
	public boolean removedflag;
    public GoogleSignInAccount mGoogleAccount;
    public Bitmap userProfilePicBitmap;
    public String groupType;
    public String forgotpassrequest;
    public String showEmailId;
    public String showMobileNo;
}
