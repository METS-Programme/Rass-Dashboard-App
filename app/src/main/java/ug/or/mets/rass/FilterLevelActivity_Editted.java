package ug.or.mets.rass;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ug.or.mets.rass.db.Constants;
import ug.or.mets.rass.services.ConnectionClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FilterLevelActivity_Editted extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener{

    private Spinner mSpnLevel, mspnOrg_unity, mspnPeriod;
    ImageButton imgbtn;
    Button btnCancel, btnFilter;
    ProgressDialog dialog;
    String mSpnPeriod, mSpnEntity,mSpnerOrgs;

    ArrayList<String> nameslist = new ArrayList<>();
    ArrayList<String> countrycodelist = new ArrayList<>();
    ArrayAdapter<String> adapter_c;

    ArrayList<String> nameslistPeriod = new ArrayList<>();
    ArrayList<String> countrycodelistPeriod = new ArrayList<>();
    ArrayAdapter<String> adapter_Period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                View v = Utils.tryInflate(name, context, attrs);
                if (v instanceof TextView) {
                    Utils.setTypeFace((TextView) v, FilterLevelActivity_Editted.this);
                }
                return v;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);

        init();

    }
    @SuppressLint("WrongViewCast")
    public void init(){

        imgbtn = (ImageButton) findViewById(R.id.btn_cancel);
        imgbtn.setOnClickListener(this);

        btnCancel = (Button) findViewById(R.id.btn_ok_itecdialog_1);
        btnCancel.setOnClickListener(this);

        btnFilter = (Button) findViewById(R.id.btn_ok_itecdialog_2);
        btnFilter.setOnClickListener(this);

        mSpnLevel = (Spinner)
                findViewById(R.id.spnSelfCareProductType);
        mSpnLevel.setPrompt("---Select Org Level---");
        mSpnLevel.setOnItemSelectedListener(this);

        mspnOrg_unity = (Spinner)
        findViewById(R.id.spnOrg_unit);
        mspnOrg_unity.setPrompt("---Select Org Entity---");
        mspnOrg_unity.setOnItemSelectedListener(this);

        mspnPeriod = (Spinner)
                findViewById(R.id.spnPeriod);
        mspnPeriod.setPrompt("---Select Period---");
        mspnPeriod.setOnItemSelectedListener(this);

        adapter_c = new ArrayAdapter<String>(FilterLevelActivity_Editted.this, android.R.layout.simple_spinner_item);
        adapter_c.setDropDownViewResource(R.layout.spinner_layout);
        mspnOrg_unity.setAdapter(adapter_c);

        // adding for periods
        adapter_Period = new ArrayAdapter<String>(FilterLevelActivity_Editted.this, android.R.layout.simple_spinner_item);
        adapter_Period.setDropDownViewResource(R.layout.spinner_layout);
        mspnPeriod.setAdapter(adapter_Period);

        addData();

        new LoadPeriods().execute();
    }

    private void addData() {

        ArrayAdapter<String> dataAdapterReq = new ArrayAdapter<String>(
                FilterLevelActivity_Editted.this, R.layout.spinner_layout) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.spinnerTarget)).setTypeface(Utils.setFont(FilterLevelActivity_Editted.this));
                    ((TextView) v.findViewById(R.id.spinnerTarget)).setText("");
                    ((TextView) v.findViewById(R.id.spinnerTarget))
                            .setHint(getItem(getCount())); // "Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };

        dataAdapterReq.add("National");
        dataAdapterReq.add("Regional");
        dataAdapterReq.add("District");
        dataAdapterReq.add("---Select Org Level---");

        dataAdapterReq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnLevel.setAdapter(dataAdapterReq);
        mSpnLevel.setSelection(dataAdapterReq.getCount());

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (R.id.btn_cancel == view.getId()) {

            intent = new Intent(FilterLevelActivity_Editted.this, MainActivity.class);
            startActivity(intent);
            finish();

        }else if (R.id.btn_ok_itecdialog_1 == view.getId()){

            intent = new Intent(FilterLevelActivity_Editted.this, MainActivity.class);
            startActivity(intent);
            finish();

        }else if (R.id.btn_ok_itecdialog_2 == view.getId()){

            intent = new Intent(FilterLevelActivity_Editted.this, MainActivity.class);
            intent.putExtra("SelectValue_OrgUnit", mSpnerOrgs);
            intent.putExtra("SelectValue_Entity", mSpnEntity);
            String m = mSpnPeriod.substring(23);
            String x = m.replace((")"),"");
            intent.putExtra("SelectValue_Period", x);
            startActivity(intent);
            finish();
        }
    }
    public String removeLast(String s, int n) {
        if (null != s && !s.isEmpty()) {
            s = s.substring(0, s.length()-n);
        }
        return s;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        mSpnerOrgs = mSpnLevel.getSelectedItem().toString();
        Log.e("SelectValue_OrgUnit", mSpnerOrgs);
        if (mSpnerOrgs != null && mSpnerOrgs.equalsIgnoreCase("---Select Org Level---")){

        }else {
            //nameslist.clear();
            new LoadOrgs().execute();
        }

        mSpnEntity = null;
        if(mspnOrg_unity != null && mspnOrg_unity.getSelectedItem() !=null ) {
            mSpnEntity = (String)mspnOrg_unity.getSelectedItem();
            Log.e("SelectValue_Entity", mSpnEntity);
        } else  {

        }
        mSpnPeriod =null;
        if(mspnPeriod != null && mspnPeriod.getSelectedItem() !=null ) {
            mSpnPeriod = (String)mspnPeriod.getSelectedItem();
            Log.e("SelectValue_Period", mSpnPeriod);
        } else  {

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class LoadOrgs extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String data;
            try
            {

                ConnectionClass conn= new ConnectionClass();
                data= conn.makeOkHttpDirect(Constants.LoadOrgs+"&level="+mSpnerOrgs);
                Log.e("servicesresult", ""+data);
            }
            catch (Exception localException)
            {
                Log.e("servicesresult", localException.toString());
                data = "error";
            }
            return data;

        }

        @Override
        protected void onPostExecute(String results){

            if (results.equalsIgnoreCase("error")) {
                try {

                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(FilterLevelActivity_Editted.this);
                    localBuilder1.setMessage("Failed, check your internet connection")
                            .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                                    //performSendQr(mqrcode,mhubriderName,mphoneSerialNo);
                                }
                            });
                    localBuilder1.setCancelable(false);
                    localBuilder1.show();

                } catch (Exception localJSONException) {
                    localJSONException.printStackTrace();
                }
            } else {
                try {
                    Log.e("gettingjson here samp", "" + results);

                    JSONObject obj = new JSONObject(results);
                    if (obj.optString("status").equalsIgnoreCase("ok")) {
                        JSONArray jsonArraycat = obj.optJSONArray("results");
                        nameslist.clear();
                        adapter_c.clear();

                        for (int i = 0; i < jsonArraycat.length(); i++) {
                            JSONObject jsonObject = jsonArraycat.getJSONObject(i);
                            String item_id = jsonObject.getString("uid");
                            String item_name = jsonObject.getString("entity");

                           // nameslist.add(item_name);
                            countrycodelist.add(item_id);
                            adapter_c.add(item_name);
                        }
                        //  adapter_c.notifyDataSetChanged();



                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(dialog!=null) {
                dialog.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            /*dialog = new ProgressDialog(FilterLevelActivity_Editted.this);
            dialog.setMessage("Loading Entities...");
            dialog.setCancelable(false);
            dialog.show();*/

        }
    }

    public class LoadPeriods extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... strings) {

            String data;
            try
            {

                ConnectionClass conn= new ConnectionClass();
                data= conn.makeOkHttpDirect(Constants.LoadPeriods);
                Log.e("servicesresult", ""+data);
            }
            catch (Exception localException)
            {
                Log.e("servicesresult", localException.toString());
                data = "error";
            }
            return data;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String results){

            if (results.equalsIgnoreCase("error")) {
                try {

                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(FilterLevelActivity_Editted.this);
                    localBuilder1.setMessage("Failed, check your internet connection")
                            .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                                    //performSendQr(mqrcode,mhubriderName,mphoneSerialNo);
                                }
                            });
                    localBuilder1.setCancelable(false);
                    localBuilder1.show();

                } catch (Exception localJSONException) {
                    localJSONException.printStackTrace();
                }
            } else {

                try {
                    Log.e("gettingjson here Period", "" + results);

                    JSONObject obj = new JSONObject(results);
                    if (obj.optString("status").equalsIgnoreCase("ok")) {
                        JSONArray jsonArrayPeriod = obj.optJSONArray("results");
                        nameslistPeriod.clear();

                        for (int i = 0; i < jsonArrayPeriod.length(); i++) {
                            JSONObject jsonObject = jsonArrayPeriod.getJSONObject(i);
                            String item_no = jsonObject.getString("weekno");
                            String item_name = jsonObject.getString("week");
                            String item_year = jsonObject.getString("year");

                            Calendar calendar = Calendar.getInstance();
                            calendar.clear();
                            calendar.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(item_no));
                            calendar.set(Calendar.YEAR, Integer.valueOf(item_year));
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String outputDate = simpleDateFormat.format(calendar.getTime());

                            nameslistPeriod.add("Week"+" "+"("+item_name+")");
                            //nameslistPeriod.add(item_name);
                            countrycodelistPeriod.add(item_no);
                            adapter_Period.add("Week ending"+" "+ outputDate+"("+item_name+")");
                            //adapter_Period.add(item_name);
                        }
                        //  adapter_c.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(dialog!=null) {
                dialog.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(FilterLevelActivity_Editted.this);
            dialog.setMessage("Loading Periods...");
            dialog.setCancelable(false);
            dialog.show();

        }
    }

}
