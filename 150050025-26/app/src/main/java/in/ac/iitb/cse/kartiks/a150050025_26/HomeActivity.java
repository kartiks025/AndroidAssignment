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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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

import static in.ac.iitb.cse.kartiks.a150050025_26.R.id.contentPanel;
import static in.ac.iitb.cse.kartiks.a150050025_26.R.id.parent;

public class HomeActivity extends BaseActivity {

    private JSONArray allPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ShowPostTask followTask = new ShowPostTask();
        followTask.execute((Void) null);
    }


    public class ShowPostTask extends AsyncTask<Void, Void, JSONArray> {


        ShowPostTask() {
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
            Log.d("json", success.toString());
            allPosts=success;
            for(int i=0;i<allPosts.length();i++){
                LinearLayout home = (LinearLayout)findViewById(R.id.postlayout);
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
