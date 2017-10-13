package in.ac.iitb.cse.kartiks.a150050025_26;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class addComment extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        Bundle bundle = getIntent().getExtras();
        final String id = bundle.getString("id");
        Log.d("postid",id);

        Button acButton = (Button) findViewById(R.id.commentbutton);
        acButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edit = (EditText)findViewById(R.id.editText);
                AddCommentTask addTask = new AddCommentTask(id,edit.getText().toString());
                addTask.execute((Void) null);
            }
        });
    }

    public class AddCommentTask extends AsyncTask<Void, Void, Boolean> {

        private final String postid;
        private final String comment;

        AddCommentTask(String pid, String comm ) {
            postid = pid;
            comment = comm;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url + "NewComment";
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

                String param = "postid="+postid+"&content="+comment;
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
                wr.flush();
                wr.close();
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
                addComment.this.finish();
            }
        }
    }
}
