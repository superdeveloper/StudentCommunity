package com.ldceconnect.ldcecommunity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ldceconnect.ldcecommunity.async.ProcessLogin;
import com.ldceconnect.ldcecommunity.model.LoadDataModel;
import com.ldceconnect.ldcecommunity.model.UserModel;

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener , GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    public GoogleSignInAccount mGoogleSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Plus.PlusOptions plusOptions = new Plus.PlusOptions.Builder().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        setGooglePlusButtonText(signInButton, "Sign in");

        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        TextView tt = (TextView)findViewById(R.id.welcomeTitle);
        TextView tt2 = (TextView)findViewById(R.id.textView4);
        TextView tt3 = (TextView)findViewById(R.id.textView3);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/nexa-rust-script.otf");
        tt.setTypeface(tf);
        tt2.setTypeface(tf);
        tt3.setTypeface(tf);
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                tv.setPadding(5,0,0,0);
                //tv.setTextAlignment(View.TEXT);
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {

    }

    @Override
    public void onConnectionSuspended(int cause)
    {

    }

    @Override
    public void onConnected (Bundle connectionHint)
    {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 0) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SignIn", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            mGoogleSignInAccount = result.getSignInAccount();
            UserModel um = UserModel.getInstance();
            um.user.mGoogleAccount = mGoogleSignInAccount;
            um.user.email = mGoogleSignInAccount.getEmail();
            um.user.fname = mGoogleSignInAccount.getDisplayName();
            //new ProcessLogin(this,LoadDataModel.LoginContext.LOGIN_GETUSER,um.user.email,"").execute();

            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            intent.putExtra("login","googlelogin");
            startActivity(intent);

            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    public void OnSignIn(View view)
    {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void OnRegister(View view)
    {
        Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void OnGoogleSignIn(View view)
    {
        signIn();
    }

}
