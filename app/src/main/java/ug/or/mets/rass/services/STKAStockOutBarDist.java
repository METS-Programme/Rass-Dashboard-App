package ug.or.mets.rass.services;

import android.os.AsyncTask;
import android.util.Log;

import ug.or.mets.rass.db.Constants;

import java.net.URLEncoder;
import java.util.HashMap;

public abstract class STKAStockOutBarDist extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... strings) {
        String mLevel = strings[0];
        String mWeek = strings[1];
        String mEntity = strings[2];

        String data;
        try
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("cmd", "AdultsDistGraphs");
            map.put("level", mLevel);
            map.put("week", mWeek);
            map.put("entity", mEntity);

            Log.e("sent d", mLevel);

            String url = Constants.API+"?cmd=AdultsDistGraphs&level="+ URLEncoder.encode(mLevel, "UTF-8")+"&week="+URLEncoder.encode(mWeek, "UTF-8")+"&entity="+URLEncoder.encode(mEntity, "UTF-8");

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