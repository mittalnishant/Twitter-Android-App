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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

/**
 * Created by nishant on 17-Mar-15.
 */
public class addNewTweet extends Activity{
    Button post_tweet;
    EditText tweet;
    ProgressDialog mProgressDialog;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnewtweet);
        post_tweet = (Button)findViewById(R.id.post_tweet_button);
        tweet = (EditText)findViewById(R.id.tweet_message);
        post_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tweet_msg = tweet.getText().toString();
                if(tweet_msg.isEmpty())
                {
                    Toast.makeText(addNewTweet.this, "Tweet Empty", Toast.LENGTH_LONG).show();
                    return;
                }
                loadingStart("Loading","Please Wait");
                final ParseUser user = ParseUser.getCurrentUser();
                ParseObject tweett = new ParseObject("tweets");
                tweett.put("tweet",tweet_msg);
                tweett.put("user",user);
                tweett.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParsePush push = new ParsePush();
                            ParseQuery pushQuery = ParseInstallation.getQuery();
                            pushQuery.whereEqualTo("channels",user.getUsername());
                            //push.setChannel(user.getUsername());
                            push.setMessage(user.getUsername()+" : "+tweet_msg);
                            push.sendInBackground(new SendCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null)
                                        Toast.makeText(addNewTweet.this, "Push Successful!!", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(addNewTweet.this, "Push fail!!", Toast.LENGTH_SHORT).show();

                                }
                            });
                            loadingFinish();
                            Toast.makeText(addNewTweet.this, "Tweet Successful!!", Toast.LENGTH_SHORT).show();
                            Intent openNewActivity = new Intent(getApplicationContext(), Activity2.class);
                            openNewActivity.putExtra("username", user.getUsername());
                            startActivity(openNewActivity);

                        }
                        else {
                            loadingFinish();
                            Toast.makeText(addNewTweet.this, "Fail!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
    protected void loadingStart(String title, String message) {
        mProgressDialog = new ProgressDialog(addNewTweet.this);
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
