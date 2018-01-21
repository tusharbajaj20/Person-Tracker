package com.example.dell.personlocator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap;
    ProgressDialog pd = null;
    String email="";
    int j;
    String url = "https://molded-capture.000webhostapp.com/Aquaguard/Login_Location.php";
    static ArrayList<HashMap<String, String>> contactlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            email = getIntent().getExtras().getString("email");
            j=getIntent().getExtras().getInt("i");
            Log.d(" found",email+"");
        }
        catch(Exception e)
        {
            Log.d("not found",e+"");

        }
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a=new Intent(MapsActivity.this,Add.class);
                startActivity(a);
            }
        });

        new GetContact().execute();





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(28.6139, 77.2090);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Delhi"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            }
        });
    }

    class GetContact extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd=new ProgressDialog(MapsActivity.this);
            pd.setTitle("Wait");
            pd.setMessage("Locating...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpHandler handler=new HttpHandler();
            String jsondata=handler.makeServiceCall(url);
            if(jsondata!=null)
            {
                try {
                    JSONObject jsonObject = new JSONObject(jsondata);
                    JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j1 = jsonArray.getJSONObject(i);
                        String name = j1.getString("name");
                        String email = j1.getString("email");
                        String latitude = j1.getString("latitude");
                        String longitude = j1.getString("longitude");

                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", name);
                        map.put("email",email);
                        map.put("latitude", latitude);
                        map.put("longitude", longitude);
                        contactlist.add(map);
                        Log.d("error", String.valueOf(contactlist));
                    }
                }
                catch(Exception e){

                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pd.isShowing())
            {
                pd.dismiss();
            }

            if(!email.equalsIgnoreCase("")){

                double lat = Double.parseDouble(contactlist.get(j).get("latitude"));
                double lon = Double.parseDouble(contactlist.get(j).get("longitude"));
                String name = contactlist.get(j).get("name");
                LatLng sydney = new LatLng(lat, lon);
                Log.d("data", String.valueOf(contactlist));
                Log.d("location", lat + "=" + lon);
                mMap.addMarker(new MarkerOptions().position(sydney).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


            }

        }
    }

}
