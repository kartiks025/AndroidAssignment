package in.ac.iitb.cse.kartiks.a150050025_26;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UserPostActicity extends AppCompatActivity {

    private JSONArray allPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_acticity);
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        ShowUserPostTask followTask = new ShowUserPostTask(id);
        followTask.execute((Void) null);
    }

    public class ShowUserPostTask extends AsyncTask<Void, Void, JSONArray> {

        private final String uid;


        ShowUserPostTask(String i) {
            uid =i;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url + "SeeUserPosts";
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
                urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty( "charset", "utf-8");
                urlConnection.setRequestProperty("Accept", "application/json");

                String param = "uid="+uid;
                byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postData.length));

//                    Log.d("id", mEmail);
//                    Log.d("password", mPassword);
                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );

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
            Log.d("json", success.toString());
            allPosts=success;
            for(int i=0;i<allPosts.length();i++){
                LinearLayout home = (LinearLayout)findViewById(R.id.upostlayout);
                View post = getLayoutInflater().inflate(R.layout.list_html, null);

                TextView userIdView = (TextView) post.findViewById(R.id.postuserid);
                TextView timeView = (TextView) post.findViewById(R.id.posttime);
                TextView posttextView = (TextView) post.findViewById(R.id.posttext);

                try {
                    String userId = allPosts.getJSONObject(i).getString("uid");
                    userIdView.setText(userId);
                    String time = allPosts.getJSONObject(i).getString("timestamp");
                    timeView.setText(time);
                    String posttext = allPosts.getJSONObject(i).getString("text");
                    posttextView.setText(posttext);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Button addCommentButton = (Button) post.findViewById(R.id.addcommentbutton);
                try {
                    addCommentButton.setTag(allPosts.getJSONObject(i).getString("postid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                addCommentButton.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), addComment.class);
                        intent.putExtra("id",view.getTag().toString());
                        startActivity(intent);
                    }
                });

                JSONArray comments = new JSONArray();

                try {
                    comments = allPosts.getJSONObject(i).getJSONArray("Comment");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for(int j=0;j<comments.length() && j< 3 ;j++){
                    LinearLayout commentLayout = (LinearLayout) post.findViewById(R.id.commentlayout);
                    View comment = getLayoutInflater().inflate(R.layout.comment, null);

                    TextView cuserIdView = (TextView) comment.findViewById(R.id.commentuserid);
                    TextView ctimeView = (TextView) comment.findViewById(R.id.commenttime);
                    TextView cposttextView = (TextView) comment.findViewById(R.id.commenttext);

                    try {
                        String userId = comments.getJSONObject(j).getString("uid");
                        cuserIdView.setText(userId);
                        String time = comments.getJSONObject(j).getString("timestamp");
                        ctimeView.setText(time);
                        String posttext = comments.getJSONObject(j).getString("text");
                        cposttextView.setText(posttext);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    commentLayout.addView(comment);
                }
                if(comments.length()<=3){

                }
                else{

                    final TextView moreLink = (TextView) post.findViewById(R.id.morelink);
                    moreLink.setVisibility(View.VISIBLE);
                    moreLink.setTag(i);

                    moreLink.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            View post = (View) view.getParent();
                            int postposition = Integer.parseInt(view.getTag().toString());
                            JSONArray comments = new JSONArray();

                            try {
                                comments = allPosts.getJSONObject(postposition).getJSONArray("Comment");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for(int j=3;j<comments.length() ;j++){
                                LinearLayout commentLayout = (LinearLayout) post.findViewById(R.id.commentlayout);
                                View comment = getLayoutInflater().inflate(R.layout.comment, null);

                                TextView cuserIdView = (TextView) comment.findViewById(R.id.commentuserid);
                                TextView ctimeView = (TextView) comment.findViewById(R.id.commenttime);
                                TextView cposttextView = (TextView) comment.findViewById(R.id.commenttext);

                                try {
                                    String userId = comments.getJSONObject(j).getString("uid");
                                    cuserIdView.setText(userId);
                                    String time = comments.getJSONObject(j).getString("timestamp");
                                    ctimeView.setText(time);
                                    String posttext = comments.getJSONObject(j).getString("text");
                                    cposttextView.setText(posttext);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                commentLayout.addView(comment);
                            }
                            moreLink.setVisibility(View.GONE);
                        }
                    });
                }
                home.addView(post);
            }
        }
    }
}
