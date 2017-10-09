package in.ac.iitb.cse.kartiks.a150050025_26;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static in.ac.iitb.cse.kartiks.a150050025_26.R.*;
import static in.ac.iitb.cse.kartiks.a150050025_26.R.layout.*;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends BaseActivity {

    List<String> followedusers;
    final String[] userid = {null};
    final String[] name = { null };
    final String[] email = { null };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_search);

        followedusers = new ArrayList<String>();

        AllFollowersTask afTask = new AllFollowersTask();
        afTask.execute((Void) null);

        final ListView listx = (ListView) findViewById(id.userlist);
        final UserAdapter adapter = new UserAdapter(this);
        listx.setAdapter(adapter);
        SearchView simpleSearchView = (SearchView) findViewById(id.simpleSearchView);

        simpleSearchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    ShowUsersTask usersTask = new ShowUsersTask(newText, adapter);
                    usersTask.execute((Void) null);
//                    listx.bringToFront();
                return false;
            }
        });
        listx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SearchView) findViewById(R.id.simpleSearchView)).setIconified(true);
//                ((SearchView) findViewById(R.id.simpleSearchView)).setQuery("",false);
                JSONObject selected = (JSONObject) parent.getItemAtPosition(position);
                Log.d("selected",selected.toString());
                try {
                    userid[0] = selected.getString("uid");
                    name[0] = selected.getString("name");
                    email[0] = selected.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((TextView) findViewById(R.id.selectid)).setText(userid[0]);
                ((TextView) findViewById(R.id.selectname)).setText(name[0]);
                ((TextView) findViewById(R.id.selectemail)).setText(email[0]);
                ((TextView) findViewById(R.id.selectid)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.selectname)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.selectemail)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.follow)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.cancel)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.showpost)).setVisibility(View.VISIBLE);
                if(followedusers.contains(userid[0]))
                    ((Button) findViewById(R.id.follow)).setText("Unfollow");
                else
                    ((Button) findViewById(R.id.follow)).setText("Follow");
            }
        });
        Button followButton = (Button) findViewById(id.follow);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userid[0]!=null){
                    if(followedusers.contains(userid[0])) {
                        UnfollowTask ufTask = new UnfollowTask(userid[0]);
                        ufTask.execute((Void) null);
                    }
                    else{
                        FollowTask fTask = new FollowTask(userid[0]);
                        fTask.execute((Void) null);
                    }
                }
            }
        });
        Button cancelButton = (Button) findViewById(id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userid[0]!=null){
                    Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });
        Button userPostButton = (Button) findViewById(id.showpost);
        userPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userid[0]!=null){
                    Intent intent = new Intent(SearchActivity.this, UserPostActicity.class);
                    intent.putExtra("id",userid[0]);
                    startActivity(intent);
                }
            }
        });
    }

    public class ShowUsersTask extends AsyncTask<Void, Void, JSONArray> {

        private final String search;
        private final UserAdapter adapter;

        ShowUsersTask(String ss, UserAdapter ua ) {
            search = ss;
            adapter = ua;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url+"SearchUser";
            String result = "";

            HttpURLConnection urlConnection=null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");

                String param = "searchstring="+search;
                byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postData.length));

                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );

                int responseCode = urlConnection.getResponseCode();
                    Log.d("response",String.valueOf(responseCode));
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
            Log.d("json", success.toString());
            adapter.upDateEntries(success);
        }
    }

    public class FollowTask extends AsyncTask<Void, Void, Boolean> {

        private final String uid;

        FollowTask(String user ) {
            uid = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url+"Follow";
            String result = "";

            HttpURLConnection urlConnection=null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");

                String param = "uid="+uid;
                byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postData.length));

                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );

                int responseCode = urlConnection.getResponseCode();
                Log.d("response",String.valueOf(responseCode));
                result = Helper.IstreamToString(urlConnection.getInputStream());
                Log.d("response",result);
                JSONObject jsonObj = new JSONObject(result);
                Log.d("response",jsonObj.toString());
                if(jsonObj.getBoolean("status")) {
                    Log.d("status","true");
                    return true;
                }
            }
            catch (IOException e) {
                System.out.println(result);
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("json", success.toString());
            AllFollowersTask afTask = new AllFollowersTask();
            afTask.execute((Void) null);
        }
    }

    public class UnfollowTask extends AsyncTask<Void, Void, Boolean> {

        private final String uid;

        UnfollowTask(String user ) {
            uid = user;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url+"Unfollow";
            String result = "";

            HttpURLConnection urlConnection=null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");

                String param = "uid="+uid;
                byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postData.length));

                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );

                int responseCode = urlConnection.getResponseCode();
                Log.d("response",String.valueOf(responseCode));
                result = Helper.IstreamToString(urlConnection.getInputStream());
                Log.d("response",result);
                JSONObject jsonObj = new JSONObject(result);
                Log.d("response",jsonObj.toString());
                if(jsonObj.getBoolean("status")) {
                    Log.d("status","true");
                    return true;
                }
            }
            catch (IOException e) {
                System.out.println(result);
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("json", success.toString());
            AllFollowersTask afTask = new AllFollowersTask();
            afTask.execute((Void) null);
        }
    }

    public class AllFollowersTask extends AsyncTask<Void, Void, JSONArray> {


        AllFollowersTask( ) {
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url+"UserFollow";
            String result = "";

            HttpURLConnection urlConnection=null;
            try {
                URL url = new URL(http);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Accept", "application/json");


                int responseCode = urlConnection.getResponseCode();
                Log.d("response",String.valueOf(responseCode));
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
            followedusers = new ArrayList<String>();
            Log.d("json", success.toString());
            for(int i=0;i<success.length();i++){
                try {
                    followedusers.add(success.getJSONObject(i).getString("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("followed",followedusers.toString());
            if(followedusers.contains(userid[0]))
                ((Button) findViewById(R.id.follow)).setText("Unfollow");
            else
                ((Button) findViewById(R.id.follow)).setText("Follow");
        }
    }

}
