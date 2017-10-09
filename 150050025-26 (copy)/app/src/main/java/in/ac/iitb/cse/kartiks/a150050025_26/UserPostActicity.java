package in.ac.iitb.cse.kartiks.a150050025_26;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserPostActicity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post_acticity);
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        TextView t = (TextView) findViewById(R.id.selecteduid);
        t.append(id);
    }
}
