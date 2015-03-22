package com.example.nishant.twitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class MainActivity extends ActionBarActivity {
    ProgressDialog mProgressDialog;
    EditText username;
    EditText password;
    Button login_button;
    Button register_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ParseAnalytics.trackAppOpened(getIntent());
        if(ParseUser.getCurrentUser()!=null)
        {
            Intent openNewActivity = new Intent(getApplicationContext(), Activity2.class);
            openNewActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            openNewActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openNewActivity);
        }
        login_button = (Button)findViewById(R.id.button);
        register_button = (Button)findViewById(R.id.button2);
        username = (EditText)findViewById(R.id.editText2);
        password = (EditText)findViewById(R.id.editText);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usern = username.getText().toString();
                final String pass = password.getText().toString();
                if (usern.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username Empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Password Empty", Toast.LENGTH_LONG).show();
                    return;
                }
                // check for internet connection
                /*if (!NetworkHelper.isNetworkAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "No internet Connection", Toast.LENGTH_LONG).show();
                    return;
                }*/

                loadingStart("Please wait...","loading");

                ParseUser.logInInBackground(usern, pass, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // if success
                            ParseUser userr = ParseUser.getCurrentUser();
                            ParseInstallation install = ParseInstallation.getCurrentInstallation();
                            install.put("username",userr.getUsername());
                            install.remove("channels");
                            install.saveInBackground();
                            ParsePush.subscribeInBackground("");

                            ParseRelation<ParseObject> relation = userr.getRelation("following");
                            relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> parseObjects, ParseException e) {
                                    if(e ==null){
                                        for(int i=0;i<parseObjects.size();i++)
                                        {
                                            ParsePush.subscribeInBackground(parseObjects.get(i).getString("username"));
                                        }


                                    }else{
                                        Toast.makeText(MainActivity.this, "Error in finding follow", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                            loadingFinish();
                            Intent openNewActivity = new Intent(getApplicationContext(), Activity2.class);
                            openNewActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            openNewActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(openNewActivity);
                        } else {

                            // if failure show proper message
                            if (e != null) {
                                String error;
                                loadingFinish();
                                e.printStackTrace();
                                if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                                    // extra.setText("Invalid Credentials");
                                    error = "Invalid Credentials";
                                } else {
                                    //extra.setText("Unknown login error");
                                    error = "Unknown login error";
                                }
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });

            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openNewActivity1 = new Intent(getApplicationContext(), Activity3.class);
                startActivity(openNewActivity1);
            }
        });

    }
    protected void loadingStart(String title, String message) {
        mProgressDialog = new ProgressDialog(MainActivity.this);
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
