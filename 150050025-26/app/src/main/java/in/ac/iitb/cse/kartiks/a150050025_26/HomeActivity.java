package in.ac.iitb.cse.kartiks.a150050025_26;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.concurrent.ExecutionException;

import static in.ac.iitb.cse.kartiks.a150050025_26.R.id.contentPanel;
import static in.ac.iitb.cse.kartiks.a150050025_26.R.id.parent;

public class HomeActivity extends BaseActivity {

    private JSONArray allPosts;
    private int offset = -1;
    private int limit = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        offset = -1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ShowPostTask followTask = new ShowPostTask();
        followTask.execute((Void) null);
        Button previous = (Button) findViewById(R.id.previousbutton);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(offset>0)
                    offset-=limit;
                ShowPostTask followTask = new ShowPostTask();
                try {
                    followTask.execute((Void) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                ((ScrollView) findViewById(R.id.scrollview)).fullScroll(View.FOCUS_UP);

            }
        });
        Button next = (Button) findViewById(R.id.nextbutton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset+=limit;
                ShowPostTask followTask = new ShowPostTask();
                try {
                    followTask.execute((Void) null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                ((ScrollView) findViewById(R.id.scrollview)).fullScroll(View.FOCUS_UP);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ShowPostTask followTask = new ShowPostTask();
        followTask.execute((Void) null);
    }

    public class ShowPostTask extends AsyncTask<Void, Void, JSONObject> {


        ShowPostTask() {
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String http = Helper.url + "SeePosts";
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

                String param = "offset="+offset+"&limit="+limit;
                byte[] postData = param.getBytes( StandardCharsets.UTF_8 );
                urlConnection.setRequestProperty( "Content-Length", Integer.toString( postData.length));

                DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                wr.write( postData );

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
                    allPosts=success.getJSONArray("data");
                    offset=success.getInt("offset");
                    LinearLayout home = (LinearLayout)findViewById(R.id.postlayout);
                    home.removeAllViews();
                    if(offset>0)
                        findViewById(R.id.previousbutton).setVisibility(View.VISIBLE);
                    else
                        findViewById(R.id.previousbutton).setVisibility(View.GONE);
                    if(allPosts.length()==20)
                        findViewById(R.id.nextbutton).setVisibility(View.VISIBLE);
                    else
                        findViewById(R.id.nextbutton).setVisibility(View.GONE);
                    for(int i=0;i<allPosts.length();i++){
                        View post = getLayoutInflater().inflate(R.layout.list_html, null);

                        TextView userIdView = (TextView) post.findViewById(R.id.postuserid);
                        TextView timeView = (TextView) post.findViewById(R.id.posttime);
                        TextView posttextView = (TextView) post.findViewById(R.id.posttext);
                        ImageView imageView = (ImageView) post.findViewById(R.id.postimage);

                        try {
                            String userId = allPosts.getJSONObject(i).getString("uid");
                            userIdView.setText(userId);
                            String time = allPosts.getJSONObject(i).getString("timestamp");
                            timeView.setText(time);
                            String posttext = allPosts.getJSONObject(i).getString("text");
                            posttextView.setText(posttext);
                            String imagedata = allPosts.getJSONObject(i).getString("encode");

                            if("".equals(imagedata)){
                                imageView.setVisibility(View.GONE);
                            }
                            else{
                                byte data[] = Base64.decode(imagedata, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                imageView.setImageBitmap(bitmap);
                            }


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
