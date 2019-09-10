package com.mets.rassdasshboard.app.services;

import android.os.AsyncTask;
import android.util.Log;


import com.mets.rassdasshboard.app.db.Constants;

import java.net.URLEncoder;
import java.util.HashMap;


public abstract class GetAllData extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... strings) {
        String mLevel = strings[0];
        String mEntity = strings[1];
        String mWeek = strings[2];

        String data;
        try
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("cmd", "getAdultsData");
            map.put("level", mLevel);
            map.put("entity", mEntity);
            map.put("week", mWeek);

            Log.e("sent d", mLevel);

            String url = Constants.API+"?cmd=getAdultsData&level="+URLEncoder.encode(mLevel, "UTF-8")+"&entity="+URLEncoder.encode(mEntity, "UTF-8")+"&week="+URLEncoder.encode(mWeek, "UTF-8");
            //String url ="http://192.168.43.53:8080/rass-api/?cmd=getAdultsData&level=National&entity=Uganda&week=2018W35";
            Log.e("sent url", url);

            ConnectionClass conn= new ConnectionClass();
            data= conn.makeOkHttpDirect(url);

            Log.e("servicesresult", data);


        }
        catch (Exception localException)
        {
            Log.d("servicesresult", localException.toString());
            data = "error";
            localException.printStackTrace();
        }
        return data;

    }

    protected abstract void onPostExecute(String results) ;
    protected abstract void onPreExecute() ;
}