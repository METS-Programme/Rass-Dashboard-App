package ug.or.mets.rass;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ug.or.mets.rass.models.Category;
import ug.or.mets.rass.models.OrgsUnits;

import java.util.ArrayList;


public class Utils {


public static final String MyPREFERENCES = "rass_app";
public static final String KEY_ENTITY_SET = "entity_set";
public static final String USER_TOKEN_KEY = "TokenKey";
public static final String KEY_REFERENCE_LAB_SET = "reference_lab_set";

    public static Typeface setBoldFont(Context activity){
        Typeface typeFace= Typeface.createFromAsset(activity.getAssets(), "fonts/asap_bold.ttf");
        return typeFace;
    }
    public static Typeface setFont(Context activity){
        Typeface typeFace= Typeface.createFromAsset(activity.getAssets(), "fonts/asap_regular.ttf");
        return typeFace;
    }

    public static View tryInflate(String name, Context context, AttributeSet attrs) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = null;
        try {
            v = li.createView(name, null, attrs);
        } catch (Exception e) {
            try {
                v = li.createView("android.widget." + name, null, attrs);
            } catch (Exception e1) {
            }
        }
        return v;
    }
    public static void setTypeFace(TextView tv,Context ac) {
        tv.setTypeface(setFont(ac));
    }

    public static void setTypeFaceBold(TextView tv,Context ac) {
        tv.setTypeface(setBoldFont(ac));
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public static boolean isValidMobile(String phone) {
        if(phone.length() > 6){
            return  Patterns.PHONE.matcher(phone).matches();
        }else{
            return false;
        }
    }






    public static void save(String key,String value,Activity givenActivity){
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        prefs.edit().putString(key, value).commit();

    }


    public static void saveSet(String key, ArrayList<Category> value, Activity givenActivity){
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        String json = new Gson().toJson(value);
        prefs.edit().putString(key, json).apply();

    }

    public static void saveSET(String key, ArrayList<OrgsUnits> value, Activity givenActivity){
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        String json = new Gson().toJson(value);
        prefs.edit().putString(key, json).apply();

    }


    public static ArrayList<Category> getSet(String key, Activity givenActivity){
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        Gson gson = new Gson();
        String json = prefs.getString(key, "");

        return gson.fromJson(json, new TypeToken<ArrayList<Category>>(){}.getType());

    }
    public static ArrayList<OrgsUnits> getSET(String key, Activity givenActivity){
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        Log.e("here to see", ""+json);
        return gson.fromJson(json, new TypeToken<ArrayList<OrgsUnits>>(){}.getType());



    }


    public static SharedPreferences getSharedPreferencesWithin(Context givenActivity) {
        return givenActivity.getSharedPreferences(
                MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public static void saveToken(String token, Activity givenActivity) {
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        prefs.edit().putString(USER_TOKEN_KEY, token).commit();
    }


    public static String getAuthorizationToken(Context activity) {
        SharedPreferences prefs = activity.getSharedPreferences(
                Utils.MyPREFERENCES, Context.MODE_PRIVATE);
        String token = prefs.getString(Utils.USER_TOKEN_KEY, "");
        return String.format("Token %s", token);
    }



    public static String getSaved(String key,Activity givenActivity){
        SharedPreferences prefs = getSharedPreferencesWithin(givenActivity);
        String js = prefs.getString(key, "");

        return js;

    }

    /*public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void isNetworkAvailable(final Handler handler, final int timeout) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)

        new Thread() {
            private boolean responded = false;
            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        HttpGet requestForTest = new HttpGet("http://m.google.com");
                        try {
                            new DefaultHttpClient().execute(requestForTest); // can last...
                            responded = true;
                        }
                        catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while(!responded && (waited < timeout)) {
                        sleep(100);
                        if(!responded ) {
                            waited += 100;
                        }
                    }
                }
                catch(InterruptedException e) {} // do nothing
                finally {
                    if (!responded) { handler.sendEmptyMessage(0); }
                    else { handler.sendEmptyMessage(1); }
                }
            }
        }.start();
    }*/
}
