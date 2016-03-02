package com.ldceconnect.ldcecommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ldceconnect.ldcecommunity.async.DownloadImages;
import com.ldceconnect.ldcecommunity.async.LoadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadDataAsync;
import com.ldceconnect.ldcecommunity.async.UploadImage;
import com.ldceconnect.ldcecommunity.model.DataModel;
import com.ldceconnect.ldcecommunity.model.DatabaseHelper;
import com.ldceconnect.ldcecommunity.model.Degree;
import com.ldceconnect.ldcecommunity.model.Department;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.Program;
import com.ldceconnect.ldcecommunity.model.User;
import com.ldceconnect.ldcecommunity.model.UserModel;
import com.ldceconnect.ldcecommunity.util.ImageFilePath;
import com.ldceconnect.ldcecommunity.util.ImageUtils;
import com.ldceconnect.ldcecommunity.util.ParserUtils;
import com.ldceconnect.ldcecommunity.util.RoundedAvatarDrawable;
import com.ldceconnect.ldcecommunity.util.StreamDrawable;
import com.ldceconnect.ldcecommunity.util.UserFunctions;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EditProfileActivity extends AppCompatActivity {

    private boolean isDataLoadComplete = false;
    private boolean isImageUploadComplete = false;

    private static final int CORNER_RADIUS = 55; // dips
    private static final int MARGIN = 12; // dips

    public int mCornerRadius = 0;
    public int mMargin = 0;

    ScrollView mScrollView;
    Spinner degreeSpinner;
    Spinner programSpinner;
    Spinner departmentSpinner;
    private ProgressDialog pDialog;
    public ImageView mImageView;
    private TextView mUploadIcon;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private Button mFinishButton;
    private EditText mobileNo;
    private MultiAutoCompleteTextView autoCompleteTags;
    public ArrayAdapter<Degree> dadapter;
    public ArrayAdapter<Program> padapter;
    public ArrayAdapter<Department> departmentadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        MaterialDialog m = new MaterialDialog.Builder(this)
                .title("LDCE Community")
                .content("Welcome to LDCE Community. A good way to Learn, Share and collaborate. Please complete your profile.")
                .autoDismiss(true)
                .show();

        mScrollView = (ScrollView)findViewById(R.id.edit_profile_form);
        degreeSpinner = (Spinner) findViewById(R.id.degree_spinner);
        programSpinner = (Spinner) findViewById(R.id.program_spinner);
        departmentSpinner = (Spinner) findViewById(R.id.department_spinner);

        mFinishButton = (Button) findViewById(R.id.next_button);
        mobileNo = (EditText) findViewById(R.id.edit_profile_contact);
        autoCompleteTags=(MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompleteTextView);

        /* Make the image round */
        mImageView = (ImageView)findViewById(R.id.header_imageview);
        mUploadIcon = (TextView)findViewById(R.id.header_uploadbutton);
        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.avatar_small);
        RoundedAvatarDrawable d = new RoundedAvatarDrawable(image);
        mImageView.setImageDrawable(d);

        mUploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(EditProfileActivity.this);
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isAllDataFilled()) {

                    if( LoadDataModel.uploadImageLocalSourcePath != null && !LoadDataModel.uploadImageLocalSourcePath.isEmpty() &&
                            LoadDataModel.uploadImageFileName != null && !LoadDataModel.uploadImageFileName.isEmpty()) {
                        //Upload image to server
                        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                        db.getUser();

                        UserModel um = UserModel.getInstance();
                        String extn = ParserUtils.getFileExtension(LoadDataModel.uploadImageFileName);

                        LoadDataModel.uploadImageFileName = um.user.email + extn;

                        new UploadImage(EditProfileActivity.this, LoadDataModel.uploadImageLocalSourcePath, LoadDataModel.uploadImageFileName, false).execute();
                        Bitmap src = BitmapFactory.decodeFile(LoadDataModel.uploadImageLocalSourcePath);
                        src = Bitmap.createScaledBitmap(src,120,120,false);


                        try{

                            um.user.profilePictureUrl = DataModel.uploadImageURL + um.user.email + ".jpg";
                            um.user.profilePictureLocalPath = getApplicationContext().getFilesDir() + "/" + ParserUtils.getFileNameFromPath(um.user.profilePictureUrl);
                            um.user.profilePictureLocalThumbPath = ParserUtils.getFileThumbName(um.user.profilePictureLocalPath);

                            String ext = ParserUtils.getFileExtension(LoadDataModel.uploadImageLocalSourcePath);
                            Bitmap.CompressFormat cf;

                            if( ext.equals(".jpeg") || ext.equals(".jpg"))
                                cf = Bitmap.CompressFormat.JPEG;
                            else if(ext.equals(".png"))
                                cf = Bitmap.CompressFormat.PNG;
                            else
                                cf = Bitmap.CompressFormat.JPEG;

                            FileOutputStream stream = new FileOutputStream(um.user.profilePictureLocalThumbPath);
                            src.compress(cf, 100, stream);
                            stream.close();
                        }catch(IOException e)
                        {

                        }

                        new UploadImage(EditProfileActivity.this, um.user.profilePictureLocalThumbPath, ParserUtils.getFileThumbName(LoadDataModel.uploadImageFileName), false).execute();

                    }

                    UserModel um = UserModel.getInstance();

                    if(um.user.userid == null) {
                        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                        db.getUser();
                    }

                    um.user.interests = autoCompleteTags.getText().toString();
                    um.user.mobile = mobileNo.getText().toString();
                    um.user.degree = (Degree) degreeSpinner.getSelectedItem();
                    um.user.program = (Program) programSpinner.getSelectedItem();
                    um.user.department = (Department) departmentSpinner.getSelectedItem();
                    um.user.showEmailId = "1";
                    um.user.showMobileNo = "0";

                    ArrayList<Object> userId = new ArrayList<>();
                    userId.add(um.user.userid);
                    new UploadDataAsync(EditProfileActivity.this, LoadDataModel.UploadContext.UPLOAD_USER_DETAILS,userId).execute();
                }
                else
                {
                    showToastLong("Please fill required information");
                }
            }
        });

        new LoadDataAsync(this, LoadDataModel.LoadContext.LOAD_DEGREES_PROGRAMS,null).execute();

        LoadDataModel ldm = LoadDataModel.getInstance();

        // Spinners
        dadapter = new ArrayAdapter<Degree>(this,android.R.layout.simple_spinner_item, ldm.loadedDegrees);
        // Specify the layout to use when the list of choices appears
        dadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        degreeSpinner.setAdapter(dadapter);

        padapter = new ArrayAdapter<Program>(this,android.R.layout.simple_spinner_item, ldm.loadedPrograms);
        // Specify the layout to use when the list of choices appears
        padapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        programSpinner.setAdapter(padapter);

        departmentadapter = new ArrayAdapter<Department>(this,android.R.layout.simple_spinner_item, ldm.loadedDepartmentsForSpinner);
        // Specify the layout to use when the list of choices appears
        departmentadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        departmentSpinner.setAdapter(departmentadapter);

        degreeSpinner.setSelection(0);
        programSpinner.setSelection(0);
        departmentSpinner.setSelection(0);

        // Hash tags
        autoCompleteTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        autoCompleteTags.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, ldm.loadedHashTags));
        autoCompleteTags.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());


        UserModel um = UserModel.getInstance();
        if(um.user.mGoogleAccount != null)
        {
            String host = "https://" + um.user.mGoogleAccount.getPhotoUrl().getHost();
            //um.user.mGoogleAccount.getPhotoUrl().getPath().getChars(0,um.user.mGoogleAccount.getPhotoUrl().getPath().length(),arraychar,0);
            um.user.profilePictureUrl = host + um.user.mGoogleAccount.getPhotoUrl().getPath();
            ArrayList<String> profileImage = new ArrayList<>();
            profileImage.add(um.user.profilePictureUrl);
            new DownloadImages(this,profileImage, DownloadImages.DownloadImagesContext.DOWNLOAD_STUDENT_PROFILE_IMAGE).execute();
        }

    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private boolean isAllDataFilled()
    {
        if( degreeSpinner.getSelectedItemPosition() == 0 || programSpinner.getSelectedItemPosition() == 0
                || departmentSpinner.getSelectedItemPosition() == 0)
        {
            return false;
        }

        Degree d = (Degree)degreeSpinner.getSelectedItem();
        Program p = (Program) programSpinner.getSelectedItem();
        String mobile = mobileNo.getText().toString();
        String tags = autoCompleteTags.getText().toString();

        return d != null && p != null && !mobile.isEmpty() && !tags.isEmpty();

    }

    private void beginCrop(Uri source) {
        UserModel um = UserModel.getInstance();
        String imageFile = ImageFilePath.getPath(this, source);
        String ext = ParserUtils.getFileExtension(imageFile);

        if(um.user.email == null) {
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            User user = db.getUser();
            if(user!= null)
            {
                Uri destination = Uri.fromFile(new File(getFilesDir(), user.email + ext));
                Crop.of(source, destination).asSquare().start(this);
            }
        }else
        {
            Uri destination = Uri.fromFile(new File(getFilesDir(), um.user.email + ext));
            Crop.of(source, destination).asSquare().start(this);
        }


    }

    private boolean handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);

            String abPath = ImageFilePath.getPath(this, uri);

            Bitmap b = BitmapFactory.decodeFile(abPath);
            if(b.getHeight() < 300 || b.getWidth() < 300)
                b = Bitmap.createScaledBitmap(b,300,300,false);

            UserModel um = UserModel.getInstance();
            String fileName;
            fileName = ParserUtils.getFileNameFromPath(abPath);
            if( um.user.email == null || um.user.email.isEmpty())
                fileName = "tempFile";

            LoadDataModel.uploadImageLocalSourcePath = abPath;
            LoadDataModel.uploadImageFileName = fileName;

            RoundedAvatarDrawable rd = new RoundedAvatarDrawable(b);
            mImageView.setImageDrawable(rd);
            //mImageView.setImageURI(uri);

            return true;
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }

    }

    @Override
    public void onBackPressed()
    {
        UserModel um = UserModel.getInstance();
        um.user.mGoogleAccount = null;

        super.onBackPressed();
    }

}
