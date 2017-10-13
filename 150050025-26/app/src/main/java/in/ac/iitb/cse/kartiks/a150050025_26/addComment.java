package in.ac.iitb.cse.kartiks.a150050025_26;

import android.content.Intent;
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

    public class AddCommentTask extends AsyncTask<Void, Void, JSONObject> {

        private final String postid;
        private final String comment;

        AddCommentTask(String pid, String comm ) {
            postid = pid;
            comment = comm;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
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
                JSONObject jsonObj = new JSONObject(result);
                wr.flush();
                wr.close();
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
                    addComment.this.finish();
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
