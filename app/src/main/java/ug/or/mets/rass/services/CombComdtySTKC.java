package ug.or.mets.rass.services;

import android.os.AsyncTask;
import android.util.Log;

import ug.or.mets.rass.db.Constants;

import java.net.URLEncoder;
import java.util.HashMap;

public abstract class CombComdtySTKC extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... strings) {
        String mUid = strings[0];
        String mYear = strings[1];
        String mWeekNo = strings[2];

        String data;
        try
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("cmd", "PaedCommodSTKA");
            map.put("uid", mUid);
            map.put("yr", mYear);
            map.put("wk", mWeekNo);

            Log.e("sent d", mUid);

            String url = Constants.API+"?cmd=PaedCommodSTKA&uid="+ URLEncoder.encode(mUid, "UTF-8")+"&yr="+URLEncoder.encode(mYear, "UTF-8")+"&wk="+URLEncoder.encode(mWeekNo, "UTF-8");

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