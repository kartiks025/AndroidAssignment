package in.ac.iitb.cse.kartiks.a150050025_26;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by kartik on 4/10/17.
 */

public class PostAdapter extends BaseAdapter {

    private JSONArray allPosts= new JSONArray();

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public PostAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return allPosts.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return allPosts.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout itemView;
        if (convertView == null) {
            itemView = (RelativeLayout) mLayoutInflater.inflate(
                    R.layout.list_html, parent, false);

        } else {
            itemView = (RelativeLayout) convertView;
        }
        TextView userIdView = (TextView) itemView.findViewById(R.id.postuserid);
        TextView timeView = (TextView) itemView.findViewById(R.id.posttime);
        TextView posttextView = (TextView) itemView.findViewById(R.id.posttext);
        ListView commentView = (ListView) itemView.findViewById(android.R.id.list);

        try {
            String userId = allPosts.getJSONObject(position).getString("uid");
            userIdView.setText(userId);
            String time = allPosts.getJSONObject(position).getString("timestamp");
            timeView.setText(time);
            String posttext = allPosts.getJSONObject(position).getString("text");
            posttextView.setText(posttext);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button addCommentButton = (Button) itemView.findViewById(R.id.addcommentbutton);
        try {
            addCommentButton.setTag(allPosts.getJSONObject(position).getString("postid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addCommentButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, addComment.class);
                intent.putExtra("id",view.getTag().toString());
                mContext.startActivity(intent);
            }
        });
        return itemView;
    }

    public void upDateEntries(JSONArray posts) {
        allPosts = posts;
        notifyDataSetChanged();
    }
}
