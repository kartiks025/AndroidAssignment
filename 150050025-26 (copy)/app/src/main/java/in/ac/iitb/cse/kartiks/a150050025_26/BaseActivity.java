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
        Log.d("classname", classname);
        switch (item.getItemId()) {
            case R.id.action_logout:
                LogoutTask loutTask = new LogoutTask();
                loutTask.execute((Void) null);
                break;
            case R.id.action_viewPost:
                if("in.ac.iitb.cse.kartiks.a150050025_26.HomeActivity".equals(classname)) {
                    Log.d("classname", "yes");
                    break;
                }
                Log.d("classname", "no");
                intent = new Intent(this, HomeActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_addPost:
                if("in.ac.iitb.cse.kartiks.a150050025_26.addPost".equals(classname)) {
                    Log.d("classname", "yes");
                    break;
                }
                intent = new Intent(this, addPost.class);
                this.startActivity(intent);
                break;
            case R.id.action_search:
                if("in.ac.iitb.cse.kartiks.a150050025_26.SearchActivity".equals(classname)) {
                    Log.d("classname", "yes");
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

    public class LogoutTask extends AsyncTask<Void, Void, Boolean> {

        LogoutTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = "http://192.168.0.109:8080/Android/Logout";
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
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
    }
}
