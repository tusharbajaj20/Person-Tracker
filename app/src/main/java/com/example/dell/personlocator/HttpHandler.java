package com.example.dell.personlocator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dell on 7/19/2017.
 */

public class HttpHandler {
    HttpHandler()
    {

    }
    public String makeServiceCall(String requrl)
    {
        String response=null;
        try{
            URL url=new URL(requrl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in=new BufferedInputStream(conn.getInputStream());
            response=convertStreamtoString(in);
        }
        catch (Exception e)
        {

        }
        return response;

    }

    private String convertStreamtoString(InputStream in) {
        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
        StringBuilder sb=new StringBuilder();
        String line="";
        try{
            while ((line=reader.readLine())!=null)
            {
                sb.append(line).append("\n");
            }
        }
        catch (Exception e){

        }
        return  sb.toString();
    }
}
