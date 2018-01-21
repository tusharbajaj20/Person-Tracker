package com.example.dell.personlocator;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Login extends AppCompatActivity {

    EditText e,ee,eee,eeee;
    Button b;
    SQLiteDatabase sql=null;

    private static final int REQUEST_CODE_PERMISSION = 2;
    GPSTracker gps;
    Button lat,lon;
    String serverurl="https://molded-capture.000webhostapp.com/Aquaguard/Insert_Location.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        gps = new GPSTracker(Login.this);
        b=(Button)findViewById(R.id.button);
        e = (EditText) findViewById(R.id.editText);
        ee = (EditText) findViewById(R.id.editText2);
        eee = (EditText) findViewById(R.id.editText3);
        eeee = (EditText) findViewById(R.id.editText4);
        sql = openOrCreateDatabase("myapp", MODE_PRIVATE, null);
        sql.execSQL("create table if not exists android (name varchar,email varchar,password varchar,number varchar)");
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lat = (Button) findViewById(R.id.button);
        lon=(Button)findViewById(R.id.button);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add Data To Login", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

    }

    public void click(View v){
        if(e.equals("")||ee.equals("")||eee.equals("")||eeee.equals("")){
            Toast.makeText(Login.this,"Fill all the data",Toast.LENGTH_LONG).show();
        }
        else {
            AsyncT asyncT = new AsyncT();
            asyncT.execute();
        }
    }
    class AsyncT extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... arg0) {

            try {



                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    String n = e.getText().toString();
                    String e = ee.getText().toString();
                    String p = eee.getText().toString();
                    String m = eeee.getText().toString();

                    URL url = new URL(serverurl); // here is your URL path

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("name",n);
                    postDataParams.put("email",e);
                    postDataParams.put("pass",p);
                    postDataParams.put("number",m);
                    postDataParams.put("latitude",latitude);
                    postDataParams.put("longitude",longitude);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                    if (responseCode==HttpsURLConnection.HTTP_OK) {

                        BufferedReader in=new BufferedReader(new
                                InputStreamReader(
                                conn.getInputStream()));

                        StringBuffer sb = new StringBuffer("");
                        String line="";

                        while ((line = in.readLine())!=null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    } else {
                        return new String("false : " + responseCode);
                    }
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result,
                    Toast.LENGTH_LONG).show();

            Intent i=new Intent(Login.this,MapsActivity.class);
            startActivity(i);
        }


    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
