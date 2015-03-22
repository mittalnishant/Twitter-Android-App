package com.example.nishant.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 15-Mar-15.
 */
public class Activity2 extends ActionBarActivity {
    TextView un;
    ListView lv;
    myAdapter ma;
    ProgressDialog mProgressDialog;
    ArrayList<ParseObject> display_tweet;
    ArrayList<String> display_user;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);
        //Intent intentObject = getIntent();
        ParseUser pu = ParseUser.getCurrentUser();
        String userName = pu.getUsername();
        //String userName = intentObject.getStringExtra("username");
        un = (TextView) findViewById(R.id.un);
        un.setText("Welcome " + userName);
        lv = (ListView) findViewById(R.id.listView);
        ma = new myAdapter();

        display_tweet = new ArrayList<>();
        display_user = new ArrayList<>();
        loadingStart("Please wait...", "loading");
        lv.setVisibility(View.VISIBLE);
        lv.setAdapter(ma);
        final ParseUser user = ParseUser.getCurrentUser();
        final String TOP_SCORES_LABEL = "tweets";
        boolean internet_conn = isInternetAvailable();
        if (internet_conn) {
            ParseRelation<ParseObject> relation = user.getRelation("following");
            relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> results, ParseException e) {
                    if (e != null) {
                        loadingFinish();
                        Toast.makeText(Activity2.this, "Error in finding follow", Toast.LENGTH_SHORT).show();
                    } else {
                        ParseQuery<ParseObject> tweet_query = ParseQuery.getQuery("tweets");
                        tweet_query.whereContainedIn("user", results);
                        tweet_query.orderByDescending("createdAt");
                        tweet_query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(final List<ParseObject> parseObjects, ParseException e) {
                                if (e == null) {
                                    ParseObject.unpinAllInBackground(TOP_SCORES_LABEL, parseObjects, new DeleteCallback() {
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Toast.makeText(Activity2.this, "Error in Unpining", Toast.LENGTH_SHORT).show();
                                            }

                                            else{
                                                ParseObject.pinAllInBackground(TOP_SCORES_LABEL, parseObjects);
                                                Toast.makeText(Activity2.this, "Pining Success", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                    for (int i = 0; i < parseObjects.size(); i++) {
                                        final ParseObject tweett = parseObjects.get(i);
                                        tweett.getParseObject("user")
                                                .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                                    public void done(ParseObject userr, ParseException e) {
                                                        String userN = userr.getString("username");
                                                        display_user.add(userN);
                                                        display_tweet.add(tweett);
                                                        ma.refresh();
                                                        // Do something with your new title variable
                                                    }
                                                });

                                        ma.refresh();
                                        //Toast.makeText(viewTweet.this,"succ",Toast.LENGTH_SHORT).show();


                                    }
                                    loadingFinish();
                                    ma.refresh();
                                } else {
                                    loadingFinish();
                                    Toast.makeText(Activity2.this, "error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            });
            ma.refresh();

        }
        else{
            Toast.makeText(Activity2.this, "Error Netw", Toast.LENGTH_SHORT).show();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("tweets");
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < parseObjects.size(); i++) {
                            final ParseObject tweett = parseObjects.get(i);
                            tweett.getParseObject("user")
                                    .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                        public void done(ParseObject userr, ParseException e) {
                                            String userN = userr.getString("username");
                                            display_user.add(userN);
                                            display_tweet.add(tweett);
                                            ma.refresh();
                                            // Do something with your new title variable
                                        }
                                    });

                            ma.refresh();
                            //Toast.makeText(viewTweet.this,"succ",Toast.LENGTH_SHORT).show();


                        }
                        loadingFinish();
                        ma.refresh();
                        // Results were successfully found from the local datastore.
                    } else {
                        Toast.makeText(Activity2.this, "something", Toast.LENGTH_SHORT).show();
                        // There was an error.
                    }
                }
            });
            loadingFinish();

        }
    }

    protected void loadingStart(String title, String message) {
        mProgressDialog = new ProgressDialog(Activity2.this);
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
    public class myAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            Log.v("detect",display_user.size()+"");
            return display_tweet.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.listview,null);
            TextView tu = (TextView)v.findViewById(R.id.username);
            TextView td = (TextView)v.findViewById(R.id.desc);
            //Log.v("abc", position + "");
            String mu = display_user.get(position);
            String md = display_tweet.get(position).getString("tweet");
            tu.setText(mu);
            td.setText(md);
            return v;
        }

        public void refresh()
        {
            notifyDataSetChanged();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id)
        {
            case R.id.newtweet :
                Intent opennewTweetActivity = new Intent(getApplicationContext(), addNewTweet.class);
                startActivity(opennewTweetActivity);
                break;
            case R.id.follow :
                Intent openFollowActivity = new Intent(getApplicationContext(), Follow.class);
                startActivity(openFollowActivity);
                break;
            case R.id.unfollow :
                Intent openUnfollowActivity = new Intent(getApplicationContext(), Unfollow.class);
                startActivity(openUnfollowActivity);
                break;
            case R.id.logout :
                ParseUser.logOut();
                Intent openMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(openMainActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }
}