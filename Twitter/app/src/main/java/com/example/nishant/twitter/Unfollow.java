package com.example.nishant.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 17-Mar-15.
 */
public class Unfollow extends Activity{
    ListView lv;
    ArrayList<ParseObject> follow_user;
    myAdapter ma;
    ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unfollow);
        lv = (ListView)findViewById(R.id.listView2);
        loadingStart("Please wait...","loading");
        ma = new myAdapter();
        follow_user = new ArrayList<>();
        lv.setVisibility(View.VISIBLE);
        lv.setAdapter(ma);

        final ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> relation = user.getRelation("following");
        relation.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e ==null){
                    for(int i=0;i<parseObjects.size();i++)
                    {
                        follow_user.add(parseObjects.get(i));
                    }
                    ma.refresh();

                }else{
                    Toast.makeText(Unfollow.this, "Error in finding follow", Toast.LENGTH_SHORT).show();
                }
                loadingFinish();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openNewActivity = new Intent(getApplicationContext(), userProfile.class);
                String mu = follow_user.get(position).getString("username");
                String md = follow_user.get(position).getString("email");
                openNewActivity.putExtra("username", mu);
                openNewActivity.putExtra("email", md);
                openNewActivity.putExtra("type",0);
                startActivity(openNewActivity);
                //Toast.makeText(Follow.this,mu+md,Toast.LENGTH_SHORT).show();
            }
        });
        ma.refresh();
    }
    protected void loadingStart(String title, String message) {
        mProgressDialog = new ProgressDialog(Unfollow.this);
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
            Log.v("yash", follow_user.size() + "");
            return follow_user.size();
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
            String mu = follow_user.get(position).getString("username");
            String md = follow_user.get(position).getString("email");
            tu.setText(mu);
            td.setText(md);
            return v;
        }

        public void refresh()
        {
            notifyDataSetChanged();
        }
    }

}
