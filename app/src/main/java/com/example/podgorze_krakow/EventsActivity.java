package com.example.podgorze_krakow;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.makeramen.roundedimageview.RoundedDrawable.TAG;

public class EventsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String WORDPRESS_SITE_URL = "http://www.kspodgorze-krakow.pl"; //Use http://10.0.2.2 to access Wordpress running on localhost from emulator on same PC
    private static final String WORDPRESS_REST_API_URL = WORDPRESS_SITE_URL + "/wp-json/sportspress/v2";
    private static final String WORDPRESS_REST_API_POSTS_URL = WORDPRESS_REST_API_URL + "/events";
    private static final String NEW_LINE = "\n";
    private static final String SEPARATOR = "______________________________________";

    private TextView contentView;
    private LoadWordpressEventsTask loadWordpressEventsTask;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.action_table:
                        startActivity(new Intent(getApplicationContext(), TableActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.action_events:
                        return true;

                    case R.id.action_team:
                        startActivity(new Intent(getApplicationContext(), TeamActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.action_map:
                        startActivity(new Intent(getApplicationContext(), MapActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }

        });

        //getSupportActionBar().hide();
        setTitle("       TERMINARZ MECZY - 3 LIGA");
        contentView = findViewById(R.id.content);

        loadEventsFromWordpress();
    }
    private void loadEventsFromWordpress () {
        //Create a AsyncTask which will load data on a background thread
        loadWordpressEventsTask = new LoadWordpressEventsTask();
        loadWordpressEventsTask.setListener(new LoadWordpressEventsTask.LoadListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLoadSuccess(List<WordpressEvents> data) throws ParseException {
                showEvents(data);
            }

            @Override
            public void onLoadFailure(String errorMessage) {
                showLoadError(errorMessage);
            }
        });
        loadWordpressEventsTask.execute(WORDPRESS_REST_API_POSTS_URL);
    }


        private void showLoadError (String errorMessage){
            if (contentView != null) {
                contentView.setText(errorMessage);
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void showEvents (List < WordpressEvents > events) throws ParseException {

            if (contentView != null) {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                for (WordpressEvents event : events) {
                    stringBuilder.append(NEW_LINE);
                    stringBuilder.append(NEW_LINE);
                    stringBuilder.append(NEW_LINE);
                    stringBuilder.append(event.getTitle());
                    stringBuilder.append(NEW_LINE);
                    stringBuilder.append(NEW_LINE);
                    stringBuilder.append(event.getContent());
                    stringBuilder.append(NEW_LINE);

                }
                contentView.setText(stringBuilder.toString().replaceAll("\\<.*?>", ""));
                contentView.setText(stringBuilder.toString().replaceAll("(?i)<[^>]*>", " "));
                contentView.setMovementMethod(new ScrollingMovementMethod());
            }

        }


        protected void onDestroy () {
            //Remove listener and AsyncTask to avoid memory leak
            if (loadWordpressEventsTask != null) {
                loadWordpressEventsTask.setListener(null);
                loadWordpressEventsTask = null;
            }
            super.onDestroy();
        }
    }


     class LoadWordpressEventsTask extends AsyncTask<String, Void, LoadWordpressEventsTask.LoadResult> {
         private LoadWordpressEventsTask.LoadListener listener;

         interface LoadListener {
             void onLoadSuccess(List<WordpressEvents> result) throws ParseException;

             void onLoadFailure(String errorMessage);
         }

         void setListener(LoadWordpressEventsTask.LoadListener listener) {
             this.listener = listener;
         }

         class LoadResult {
             private List<WordpressEvents> events;
             private String errorMessage;

             LoadResult(List<WordpressEvents> events) {
                 this.events = events;
             }

             LoadResult(String errorMessage) {
                 this.errorMessage = errorMessage;
             }

             boolean hasError() {
                 return errorMessage != null;
             }
         }

         @RequiresApi(api = Build.VERSION_CODES.O)
         @Override
         protected LoadWordpressEventsTask.LoadResult doInBackground(String... urls) {
             LoadWordpressEventsTask.LoadResult result = null;
             if (!isCancelled() && urls != null && urls.length > 0) {
                 String urlString = urls[0];
                 try {
                     String jsonData = loadJsonDataFromWordpress(urlString);
                     Log.d(TAG, String.format(Locale.getDefault(), "Got data from %s : %s", urlString, jsonData));

                     if (!isCancelled()) {
                         List<WordpressEvents> events = parseJsonData(jsonData);
                         result = new LoadWordpressEventsTask.LoadResult(events);
                     }

                 } catch (IOException | JSONException | ParseException e) {
                     result = new LoadResult(e.getMessage());
                 }
             }
             return result;
         }

         @RequiresApi(api = Build.VERSION_CODES.O)
         private List<WordpressEvents> parseJsonData(String jsonData) throws JSONException, ParseException {
             final List<WordpressEvents> wordpressEvents = new ArrayList<>();
             JSONArray posts = new JSONArray(jsonData);
             for (int i = 0; i < posts.length(); i++) {
                 //For  response properties see: https://developer.wordpress.org/rest-api/reference/posts/#schema
                 final JSONObject postObject = posts.getJSONObject(i);

                 JSONObject titleObject = postObject.getJSONObject("title");
                 final String title = titleObject.getString("rendered");

                 JSONObject contentObject = postObject.getJSONObject("content");
                 final String content = contentObject.getString("rendered");


                 WordpressEvents wordpressEvent = new WordpressEvents(title, content);
                 wordpressEvents.add(wordpressEvent);
             }
             return wordpressEvents;
         }


        @Override
        protected void onPostExecute(LoadWordpressEventsTask.LoadResult result) {
            if (!isCancelled() && listener != null) {
                if (result != null) {
                    if (result.hasError()) {
                        listener.onLoadFailure(result.errorMessage);
                    } else {
                        List<WordpressEvents> events = result.events;
                        try {
                            listener.onLoadSuccess(events);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    listener.onLoadFailure("No result!");
                }
            }
        }

        private String loadJsonDataFromWordpress(String urlString) throws IOException {
            String jsonData = null;
            HttpURLConnection connection = null; //Change this to HttpsURLConnection when using HTTPS, which you should be using!
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                try (InputStream stream = connection.getInputStream()) {
                    if (stream != null) {
                        Reader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                            StringBuilder buffer = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                buffer.append(line);
                            }
                            jsonData = buffer.toString();
                        }
                    }
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return jsonData;
        }


    }






