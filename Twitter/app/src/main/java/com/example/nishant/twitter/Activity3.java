package com.example.nishant.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by nishant on 15-Mar-15.
 */
public class Activity3 extends Activity {
    EditText un,email,pw,rpw;
    Button register_button;
    ProgressDialog mProgressDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity3);
        un = (EditText)findViewById(R.id.editText3);
        email = (EditText)findViewById(R.id.editText4);
        pw = (EditText)findViewById(R.id.editText5);
        rpw = (EditText)findViewById(R.id.editText6);
        register_button = (Button)findViewById(R.id.button3);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usern = un.getText().toString();
                final String eid = email.getText().toString();
                final String pass = pw.getText().toString();
                final String rpass = rpw.getText().toString();
                if (usern.isEmpty()) {
                    Toast.makeText(Activity3.this, "Username Empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (eid.isEmpty()) {
                    Toast.makeText(Activity3.this, "Email Empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.isEmpty()) {
                    Toast.makeText(Activity3.this, "Password Empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if ((pass.compareTo(rpass))!=0) {
                    Toast.makeText(Activity3.this, "Password not same in both", Toast.LENGTH_LONG).show();
                    return;
                }
                // check for internet connection
                /*if (!NetworkHelper.isNetworkAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "No internet Connection", Toast.LENGTH_LONG).show();
                    return;
                }*/

                loadingStart("Please wait...","loading");
                ParseUser user = new ParseUser();
                user.setUsername(usern);
                user.setPassword(pass);
                user.setEmail(eid);
                //user.put("emailVerified",false);
                user.put("mobile","9939872251");

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            loadingFinish();
                            Intent openNewActivity = new Intent(getApplicationContext(), MainActivity.class);
                            //openNewActivity.putExtra("username", usern);
                            startActivity(openNewActivity);

                        }
                        else {
                            String error;
                            loadingFinish();
                            switch (e.getCode()) {
                                case ParseException.INVALID_EMAIL_ADDRESS:
                                    //showAlert(R.string.error_invalid_email);
                                    error="Invalid Email";
                                    break;
                                // for sign up user name and email are same,
                                // so error message will be same
                                case ParseException.USERNAME_TAKEN:
                                    error="Username Taken";
                                    break;
                                case ParseException.EMAIL_TAKEN:
                                    //showAlert(R.string.error_email_taken);
                                    error="Email Taken";

                                    break;
                                default:
                                    //showAlert(R.string.error_sign_up_unknown);
                                    error="Unknown Error";
                            }
                            Toast.makeText(Activity3.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
    protected void loadingStart(String title, String message) {
        mProgressDialog = new ProgressDialog(Activity3.this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    protected void loadingFinish() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}