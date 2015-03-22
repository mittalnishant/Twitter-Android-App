package com.example.nishant.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by nishant on 19-Mar-15.
 */
public class userProfile extends Activity {
    TextView un,ud;
    Button foll;
    ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprofile);
        un = (TextView)findViewById(R.id.username);
        ud = (TextView)findViewById(R.id.desc);
        foll = (Button)findViewById(R.id.button4);
        Intent intentObject = getIntent();
        final String userName = intentObject.getStringExtra("username");
        String email = intentObject.getStringExtra("email");
        final int type = intentObject.getIntExtra("type",0);
        if(type==1)
            foll.setText("Follow");
        else
            foll.setText("Unfollow");
        un.setText(userName);
        ud.setText(email);
        foll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingStart("Please wait...","loading");
                ParseQuery<ParseObject> get_user = ParseQuery.getQuery("_User");
                get_user.whereEqualTo("username",userName);
                get_user.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        ParseObject u = parseObjects.get(0);
                        ParseUser user = ParseUser.getCurrentUser();
                        ParseRelation<ParseObject> relation = user.getRelation("following");
                        if(type==1) {
                            relation.add(u);
                            ParsePush.subscribeInBackground(userName);
                        }
                        else {
                            relation.remove(u);
                            ParsePush.unsubscribeInBackground(userName);
                        }
                        user.saveInBackground();
                        loadingFinish();
                        Intent openNewActivity = new Intent(getApplicationContext(), Activity2.class);
                        startActivity(openNewActivity);


                    }
                });

            }
        });
    }
    protected void loadingStart(String title, String message) {
        mProgressDialog = new ProgressDialog(userProfile.this);
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
