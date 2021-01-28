package com.example.podgorze_krakow;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.makeramen.roundedimageview.RoundedDrawable.TAG;

public class DrugiPost extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String WORDPRESS_SITE_URL = "http://www.kspodgorze-krakow.pl"; //Use http://10.0.2.2 to access Wordpress running on localhost from emulator on same PC
    private static final String WORDPRESS_REST_API_URL = WORDPRESS_SITE_URL + "/wp-json/wp/v2";
    private static final String WORDPRESS_REST_API_POSTS_URL = WORDPRESS_REST_API_URL + "/posts?per_page=1&offset=1";
    private static final String NEW_LINE = "\n";
    private static final String SEPARATOR = "____________________________________";

    private TextView contentView;
    private LoadWordpressPostsTask loadWordpressPostsTask_dwa;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugi_post);

        getSupportActionBar().hide();
        contentView = findViewById(R.id.content);

        loadPostsFromWordpress();
    }
    private void loadPostsFromWordpress () {
        //Create a AsyncTask which will load data on a background thread
        loadWordpressPostsTask_dwa = new LoadWordpressPostsTask();
        loadWordpressPostsTask_dwa.setListener(new LoadWordpressPostsTask.LoadListener() {
            @Override
            public void onLoadSuccess(List<WordpressPost> data) {
                showPosts(data);
            }

            @Override
            public void onLoadFailure(String errorMessage) {
                showLoadError(errorMessage);
            }
        });
        loadWordpressPostsTask_dwa.execute(WORDPRESS_REST_API_POSTS_URL);
    }

    private void showLoadError (String errorMessage){
        if (contentView != null) {
            contentView.setText(errorMessage);
        }

    }


    private void showPosts (List <com.example.podgorze_krakow.WordpressPost> posts) {
        if (contentView != null) {
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            for (com.example.podgorze_krakow.WordpressPost post : posts) {
                stringBuilder.append(NEW_LINE);
                stringBuilder.append(post.getTitle());
                stringBuilder.append(NEW_LINE);
                stringBuilder.append(NEW_LINE);
                stringBuilder.append(NEW_LINE);
                stringBuilder.append(post.getContent());
                stringBuilder.append(NEW_LINE);
                stringBuilder.append(NEW_LINE);

            }
            contentView.setText(stringBuilder.toString().replaceAll("\\<.*?>", ""));
            contentView.setText(stringBuilder.toString().replaceAll("(?i)<[^>]*>", " "));
            contentView.setMovementMethod(new ScrollingMovementMethod());
        }

    }




    protected void onDestroy () {
        //Remove listener and AsyncTask to avoid memory leak
        if (loadWordpressPostsTask_dwa != null) {
            loadWordpressPostsTask_dwa.setListener(null);
            loadWordpressPostsTask_dwa = null;
        }
        super.onDestroy();
    }
}


  class LoadWordpressPostsTask_dwa extends AsyncTask<String, Void, LoadWordpressPostsTask_dwa.LoadResult> {
    private LoadListener listener;

    interface LoadListener {
        void onLoadSuccess(List<com.example.podgorze_krakow.WordpressPost> result);

        void onLoadFailure(String errorMessage);
    }

    void setListener(LoadListener listener) {
        this.listener = listener;
    }

    class LoadResult {
        private List<com.example.podgorze_krakow.WordpressPost> posts;
        private String errorMessage;

        LoadResult(List<com.example.podgorze_krakow.WordpressPost> posts) {
            this.posts = posts;
        }

        LoadResult(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        boolean hasError() {
            return errorMessage != null;
        }
    }

    @Override
    protected LoadResult doInBackground(String... urls) {
        LoadResult result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                String jsonData = loadJsonDataFromWordpress(urlString);
                Log.d(TAG, String.format(Locale.getDefault(), "Got data from %s : %s", urlString, jsonData));

                if (!isCancelled()) {
                    List<com.example.podgorze_krakow.WordpressPost> posts = parseJsonData(jsonData);
                    result = new LoadResult(posts);
                }

            } catch (IOException | JSONException e) {
                result = new LoadResult(e.getMessage());
            }
        }
        return result;
    }

    private List<com.example.podgorze_krakow.WordpressPost> parseJsonData(String jsonData) throws JSONException {
        List<com.example.podgorze_krakow.WordpressPost> wordpressPosts = new ArrayList<>();
        JSONArray posts = new JSONArray(jsonData);
        for (int i = 0; i < posts.length(); i++) {
            //For  response properties see: https://developer.wordpress.org/rest-api/reference/posts/#schema
            JSONObject postObject = posts.getJSONObject(i);

            JSONObject titleObject = postObject.getJSONObject("title");
            String title = titleObject.getString("rendered");

            JSONObject contentObject = postObject.getJSONObject("content");
            String content = contentObject.getString("rendered");

            com.example.podgorze_krakow.WordpressPost wordpressPost = new com.example.podgorze_krakow.WordpressPost(title, content);
            wordpressPosts.add(wordpressPost);
        }
        return wordpressPosts;
    }

    @Override
    protected void onPostExecute(LoadResult result) {
        if (!isCancelled() && listener != null) {
            if (result != null) {
                if (result.hasError()) {
                    listener.onLoadFailure(result.errorMessage);
                } else {
                    List<com.example.podgorze_krakow.WordpressPost> posts = result.posts;
                    listener.onLoadSuccess(posts);
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
