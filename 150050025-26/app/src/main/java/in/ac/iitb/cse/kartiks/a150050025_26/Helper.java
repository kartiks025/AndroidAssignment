package in.ac.iitb.cse.kartiks.a150050025_26;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by kartik on 4/10/17.
 */

public class Helper {
    public static String ip = "http://192.168.0.109";
    public static String port = "8080";
    public static String url = ip +":"+port+"/Android/";

    public static String IstreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
        String line = null;
        String sb = "";
        while ((line = br.readLine()) != null) {
            sb += line;
        }
        br.close();
        return sb.toString();
    }



}
