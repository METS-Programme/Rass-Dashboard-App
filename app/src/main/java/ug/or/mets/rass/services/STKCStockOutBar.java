package ug.or.mets.rass.services;

import android.os.AsyncTask;
import android.util.Log;

import ug.or.mets.rass.db.Constants;

import java.net.URLEncoder;
import java.util.HashMap;

public abstract class STKCStockOutBar extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... strings) {
        String mLevel = strings[0];
        String mWeek = strings[1];

        String data;
        try
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("cmd", "PaedGraphs");
            map.put("level", mLevel);
            map.put("week", mWeek);

            Log.e("sent d", mLevel);

            String url = Constants.API+"?cmd=PaedGraphs&level="+ URLEncoder.encode(mLevel, "UTF-8")+"&week="+URLEncoder.encode(mWeek, "UTF-8");

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