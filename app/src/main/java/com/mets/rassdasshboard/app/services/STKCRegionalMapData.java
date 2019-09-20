package com.mets.rassdasshboard.app.services;

import android.os.AsyncTask;
import android.util.Log;

import com.mets.rassdasshboard.app.db.Constants;

import java.net.URLEncoder;
import java.util.HashMap;

public abstract class STKCRegionalMapData extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... strings) {
        String mRegion = strings[0];
        String mWeek = strings[1];

        String data;
        try
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("cmd", "myRegionalDistStuff");
            map.put("region", mRegion);
            map.put("week", mWeek);
            Log.e("sent d", mRegion);

            String url = Constants.API+"?cmd=PaedRegGraphs&level="+ URLEncoder.encode(mRegion, "UTF-8")+"&week="+URLEncoder.encode(mWeek, "UTF-8");

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