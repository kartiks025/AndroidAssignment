package in.ac.iitb.cse.kartiks.a150050025_26;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class addPost extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Button acButton = (Button) findViewById(R.id.postbutton);
        acButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit = (EditText)findViewById(R.id.postText);
                AddPostTask addTask = new AddPostTask(edit.getText().toString());
                addTask.execute((Void) null);
            }
        });
    }

    public class AddPostTask extends AsyncTask<Void, Void, Boolean> {

        private final String content;

        AddPostTask(String comm ) {
            content = comm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url + "CreatePost";
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

                String param = "content="+content;
                byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postData.length));

                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );

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
            Log.d("json",success.toString());
            if(success){
                EditText edit = (EditText)addPost.this.findViewById(R.id.postText);
                edit.setText("");
            }
        }
    }
}
