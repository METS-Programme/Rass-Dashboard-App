package com.mets.rassdasshboard.app;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class Utils {


public static final String MyPREFERENCES = "mobi-futa";
public static final String USER_OBJECT_KEY = "user_object";
public static final String USER_TOKEN_KEY = "TokenKey";

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
}
