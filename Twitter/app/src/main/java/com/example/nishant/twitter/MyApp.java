package com.example.nishant.twitter;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by nishant on 15-Mar-15.
 */
public class MyApp extends Application {

    public static final String APPLICATION_ID = "S0xY0XSOFi3RaEw2brxlcNgB8S9ftDbxAPqzQm5A";
    public static final String CLIENT_ID = "SMTLmKnCNJap6DoSw3nxwsZKz7RtcsXfBfhyPjVt";

    @Override
    public void onCreate() {
        Parse.enableLocalDatastore(this);
        // for data
        Parse.initialize(this, APPLICATION_ID, CLIENT_ID);
        //for push notifications
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }
}
