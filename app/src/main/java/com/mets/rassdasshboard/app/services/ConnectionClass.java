package com.mets.rassdasshboard.app.services;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionClass {

	public String performPostCall(String requestURL,
            HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
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
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="error";

            }
        } catch (Exception e) {
            e.printStackTrace();
            response="error";
        }

        return response;
    } 
	
	private HashMap<String, String> checkParams(HashMap<String, String> map){
        Iterator<Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> pairs = (Entry<String, String>)it.next();
            if(pairs.getValue()==null){
                map.put(pairs.getKey(), "");
                Log.d("creatingparams", pairs.getKey() + "_has null value");
            }
        }
        return map;             
    }
	
	private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        
        params= checkParams(params);
        
        boolean first = true;
        for(Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String makeOKHttpConnection(String url, HashMap<String, String> params/*, String authentication*/){
        String resp;
        HashMap<String, String> map;
        map= checkParams(params);

        // encode data on your side using BASE64
        //byte[]   bytesEncoded = Base64.decode(authentication,0);
        //String authenticationVal= new String(bytesEncoded );

        try {
            FormBody.Builder formBuilder = new FormBody.Builder();
            for(Entry<String, String> entry : map.entrySet()){
                // dynamically add more parameter like this:
                formBuilder.add(URLEncoder.encode(entry.getKey(), "UTF-8"), URLEncoder.encode(entry.getValue(), "UTF-8"));

            }

            RequestBody formBody = formBuilder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    //.addHeader("authorization", "Basic "+authenticationVal)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();




            final OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            Log.e("here..", "" + response.toString());
            if (response.isSuccessful())
            {
                resp= response.body().string();
                Log.d("TAG", resp);

            }else{
                resp= "error";
                Log.d("TAG", response.body().string());
            }
        } catch (UnsupportedEncodingException e) {
            resp= "error";
            e.printStackTrace();
        } catch (IOException e) {
            resp= "error";
            e.printStackTrace();
        }

        return resp;

    }
    public String makeOkHttpDirect(String url){
        String resp;

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    //.addHeader("authorization", "Basic "+authenticationVal)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .build();

            final OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful())
            {
                resp= response.body().string();
                //Log.d("TAG", resp);

            }else{
                resp= "error";
                Log.d("TAG", response.body().string());
            }
        } catch (UnsupportedEncodingException e) {
            resp= "error";
            e.printStackTrace();
        } catch (IOException e) {
            resp= "error";
            e.printStackTrace();
        }

        return resp;

    }

}
