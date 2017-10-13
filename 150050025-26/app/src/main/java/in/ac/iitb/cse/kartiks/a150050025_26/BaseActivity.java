package in.ac.iitb.cse.kartiks.a150050025_26;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import in.ac.iitb.cse.kartiks.a150050025_26.R;

public class BaseActivity extends AppCompatActivity {

    // Activity code here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        String classname = this.getClass().getName();
        switch (item.getItemId()) {
            case R.id.action_logout:
                LogoutTask loutTask = new LogoutTask();
                loutTask.execute((Void) null);
                break;
            case R.id.action_viewPost:
                if("in.ac.iitb.cse.kartiks.a150050025_26.HomeActivity".equals(classname)) {
                    break;
                }
                intent = new Intent(this, HomeActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_addPost:
                if("in.ac.iitb.cse.kartiks.a150050025_26.addPost".equals(classname)) {
                    break;
                }
                intent = new Intent(this, addPost.class);
                this.startActivity(intent);
                break;
            case R.id.action_search:
                if("in.ac.iitb.cse.kartiks.a150050025_26.SearchActivity".equals(classname)) {
                    break;
                }
                intent = new Intent(this, SearchActivity.class);
                this.startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public class LogoutTask extends AsyncTask<Void, Void, JSONObject> {

        LogoutTask() {

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url+"Logout";
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
                int responseCode = urlConnection.getResponseCode();
                result = Helper.IstreamToString(urlConnection.getInputStream());
                JSONObject jsonObj = new JSONObject(result);

                return jsonObj;
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
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            try {
                if(success.getBoolean("status")){
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
                else if("Invalid session".equals(success.getString("message"))){
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
