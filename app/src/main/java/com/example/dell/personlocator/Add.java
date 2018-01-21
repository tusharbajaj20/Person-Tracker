package com.example.dell.personlocator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Add extends AppCompatActivity {

    EditText e;
    Button b;
    SQLiteDatabase sql=null;
    ArrayList a=new ArrayList();
    ListView l;
    int length;
    ArrayAdapter ar;
    String url = "https://molded-capture.000webhostapp.com/AquaGuard/login_location.php";
    ArrayList<HashMap<String, String>> contactlist = new ArrayList<>();
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add your friends", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        l=(ListView)findViewById(R.id.list);
        b=(Button)findViewById(R.id.button4);
        e = (EditText)findViewById(R.id.editText4);
        sql = openOrCreateDatabase("add", MODE_PRIVATE, null);
        sql.execSQL("create table if not exists android (email varchar)");
        a.clear();
        ArrayAdapter ar=new ArrayAdapter(Add.this,android.R.layout.simple_dropdown_item_1line,a);
        l.setAdapter(ar);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetEmail getemail = new GetEmail(parent,position);
                getemail.execute();
            }
        });
    }

    public void click(View v) {
        a.clear();
        String email=e.getText().toString();
        sql.execSQL("insert into android values ('"+email+"')");
        Snackbar.make(v,"Inserted",Snackbar.LENGTH_LONG).show();
        Cursor c = sql.rawQuery("select * from android", null);
        c.moveToFirst();
        do {

            a.add(c.getString(c.getColumnIndex("email")));
        }
        while (c.moveToNext());

        ArrayAdapter ar=new ArrayAdapter(Add.this,android.R.layout.simple_dropdown_item_1line,a);
        l.setAdapter(ar);

    }
    class GetEmail extends AsyncTask<Void,Void,String> {
        AdapterView<?> p;
        int pos;

        public GetEmail(AdapterView<?> parent, int position) {
            p=parent;
            pos=position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Add.this);
            pd.setTitle("Verifying");
            pd.setMessage("Loading...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpHandler handler=new HttpHandler();
            String jsondata=handler.makeServiceCall(url);


            try {
                JSONObject jsonObject = new JSONObject(jsondata);
                JSONArray jsonArray=jsonObject.getJSONArray("contacts");
                length=jsonArray.length();
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject j1 = jsonArray.getJSONObject(i);
                    String email = j1.getString("email");

                    HashMap<String, String> map = new HashMap<>();
                    map.put("email", email);

                    contactlist.add(map);
                    Log.d("list", String.valueOf(contactlist));
                }
            }

            catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            String email1;
            int i=0;
            while(i<length) {

                email1 = contactlist.get(i).get("email");
                String email2=p.getItemAtPosition(pos).toString();

                if (email1.equals(email2)) {
                    Intent x = new Intent(Add.this, MapsActivity.class);
                    x.putExtra("email", email1);
                    x.putExtra("i",(int)i);
                    startActivity(x);
                    break;
                }
                i++;
            }

            if(i==13){

                Toast.makeText(Add.this, "Email does not match with any user, Select another user", Toast.LENGTH_LONG).show();

            }
        }
    }

}
