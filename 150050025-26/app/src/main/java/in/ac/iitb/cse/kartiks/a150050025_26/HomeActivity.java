package in.ac.iitb.cse.kartiks.a150050025_26;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HomeActivity extends BaseActivity {

    private ShowPostTask followTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        Bundle bundle = getIntent().getExtras();
//        String id = bundle.getString("id");
//        TextView t = (TextView) findViewById(R.id.textView);
//        t.append(id);



        ListView list = (ListView) findViewById(android.R.id.list);

        PostAdapter adapter = new PostAdapter(this);
        list.setAdapter(adapter);


        showPost(adapter);

    }


    private JSONObject showPost(PostAdapter adapter){
        if (followTask != null) {
            return null;
        }
        followTask = new ShowPostTask(adapter);
        followTask.execute((Void) null);
        return null;
    }




    public class ShowPostTask extends AsyncTask<Void, Void, JSONArray> {

        private final PostAdapter adapter;

        ShowPostTask(PostAdapter adapter1) {
            adapter = adapter1;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url + "SeePosts";
            String result = "";

            HttpURLConnection urlConnection=null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Accept", "application/json");

                Log.d("id", "whatthefuck");
//                    Log.d("password", mPassword);

                int responseCode = urlConnection.getResponseCode();
//                    Log.d("response",String.valueOf(responseCode));
                result = Helper.IstreamToString(urlConnection.getInputStream());
                Log.d("response",result);
                JSONObject jsonObj = new JSONObject(result);
                Log.d("response",jsonObj.toString());
                if(jsonObj.getBoolean("status")) {
                    Log.d("status","true");
                    JSONArray data = jsonObj.getJSONArray("data");
                    return data;
                }
            }
            catch (IOException e) {
                System.out.println(result);
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONArray success) {
            followTask = null;
            Log.d("json", success.toString());
            adapter.upDateEntries(success);
        }
    }

}
