package com.skch.khmd.khmdreader0x;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.skch.khmd.khmdreader0x.model.FeedItem;



public class ArticlesList extends ActionBarActivity {

    private Toolbar toolbar;
    private ArrayList<FeedItem> feedList = null;
    private ProgressBar progressbar = null;
    private ListView feedListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        String url = "http://pipes.yahoo.com/pipes/pipe.run?_id=b7836ddf37201097635727c10845d841&_render=JSON";
        new DownloadFilesTask().execute(url);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

    }

    public void updateList() {
        feedListView= (ListView) findViewById(R.id.custom_list);
        feedListView.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);

        feedListView.setAdapter(new CustomListAdapter(this, feedList));
        feedListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
                Object o = feedListView.getItemAtPosition(position);
                FeedItem newsData = (FeedItem) o;

                Intent intent = new Intent(ArticlesList.this, FeedDetails.class);
                intent.putExtra("feed", newsData);
                startActivity(intent);
            }
        });
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != feedList) {
                updateList();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            // getting JSON string from URL
            JSONObject json = getJSONFromUrl(url);

            //parsing json data
            parseJson(json);
            return null;
        }
    }

    public JSONObject getJSONFromUrl(String url) {
        InputStream is = null;
        JSONObject jObj = null;
        String json = null;

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                sb.append((char) cp);
            }
            is.close();
            json = sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
    //Parse the json

    public void parseJson(JSONObject json) {
        JSONObject items  = null;
        try {

            // parsing json object
            if (json.getInt("count")>0) {
                items = json.getJSONObject("value");
                JSONArray posts = items.getJSONArray("items");

                feedList = new ArrayList<FeedItem>();
                //Log.d("Article Count",Integer.toString(posts.length()));
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = (JSONObject) posts.getJSONObject(i);
                    FeedItem item = new FeedItem();
                    JSONObject author = post.getJSONObject("author");
                    JSONObject content = post.getJSONObject("content");
                    item.setTitle(post.getString("title"));
                    item.setAuthor(author.getString("name"));
                    item.setId(post.getString("id"));
                    item.setUrl(post.getString("link"));
                    item.setContent(content.getString("content"));

                    if (post.has("media:thumbnail") && !post.isNull("media:thumbnail")) {
                        JSONObject thumb = post.getJSONObject("media:thumbnail");
                        item.setAttachmentUrl(thumb.getString("url"));
                    }
                    else {
                        item.setAttachmentUrl("http://lh3.googleusercontent.com/-L-SGKiNt3ZQ/VJBN4QRpmYI/AAAAAAAAMm8/1CgVgSu9vL4/s72-c/blogger-image--823560845.jpg");
                    }



                    feedList.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
