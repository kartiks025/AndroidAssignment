package in.ac.iitb.cse.kartiks.a150050025_26;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class UserAdapter extends BaseAdapter {

    private JSONArray allUsers= new JSONArray();

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    public UserAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return allUsers.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return allUsers.getJSONObject(position);
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
                    R.layout.user_item, parent, false);

        } else {
            itemView = (RelativeLayout) convertView;
        }
        TextView userIdView = (TextView) itemView.findViewById(R.id.userid);
        TextView userNameView = (TextView) itemView.findViewById(R.id.username);
        TextView userEmailView = (TextView) itemView.findViewById(R.id.useremail);
        try {
            String userId = allUsers.getJSONObject(position).getString("uid");
            userIdView.setText(userId);
            String userName = allUsers.getJSONObject(position).getString("name");
            userNameView.setText(userName);
            String userEmail = allUsers.getJSONObject(position).getString("email");
            userEmailView.setText(userEmail);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return itemView;
    }

    public void upDateEntries(JSONArray users) {
        allUsers = users;
        notifyDataSetChanged();
    }
}
