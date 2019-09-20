package com.mets.rassdasshboard.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mets.rassdasshboard.app.adapters.SamplsAdapter;
import com.mets.rassdasshboard.app.db.Constants;
import com.mets.rassdasshboard.app.models.Category;
import com.mets.rassdasshboard.app.models.OrgsUnits;
import com.mets.rassdasshboard.app.models.referenceLabTests;
import com.mets.rassdasshboard.app.services.ConnectionClass;
import com.mets.rassdasshboard.app.services.InternetCheck;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mets.rassdasshboard.app.Utils.KEY_ENTITY_SET;
import static com.mets.rassdasshboard.app.Utils.KEY_REFERENCE_LAB_SET;

public class FilterLevelActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener{

    private Spinner mSpnLevel, mspnOrg_unity, mspnPeriod;
    ImageButton imgbtn;
    Button btnCancel, btnFilter;
    ProgressDialog dialog;
    String mSpnPeriod, mSpnEntity,mSpnerOrgs,ent_id;

    RelativeLayout layoutHep_more;

    ArrayList<String> nameslist = new ArrayList<>();
    ArrayList<String> countrycodelist = new ArrayList<>();
    ArrayList<Category> categories = new ArrayList<>();
    ArrayAdapter<String> adapter_c;

    ArrayList<OrgsUnits> ogUnits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                View v = Utils.tryInflate(name, context, attrs);
                if (v instanceof TextView) {
                    Utils.setTypeFace((TextView) v, FilterLevelActivity.this);
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

        layoutHep_more = (RelativeLayout) findViewById(R.id.rt_spiner_org_level);
        mspnOrg_unity = (Spinner) findViewById(R.id.spnOrg_unit);

        mspnOrg_unity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Lab id", ""+position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*mspnOrg_unity = (Spinner)
                findViewById(R.id.spnOrg_unit);
        mspnOrg_unity.setPrompt("---Select Org Entity---");
        mspnOrg_unity.setOnItemSelectedListener(this);*/

        /*mspnPeriod = (Spinner)
                findViewById(R.id.spnPeriod);
        mspnPeriod.setPrompt("---Select Period---");
        mspnPeriod.setOnItemSelectedListener(this);*/



       /* if(Utils.getSet(KEY_ENTITY_SET,this) != null){
            nameslist.clear();
            categories.clear();
            countrycodelist.clear();
            categories.addAll(Utils.getSet(KEY_ENTITY_SET,this));

            for(Category cat: categories){
                nameslist.add(cat.getItem_name());
                countrycodelist.add(cat.getItem_id());
            }


            final ArrayAdapter<String> adapter_c = new ArrayAdapter<String>(FilterLevelActivity.this, android.R.layout.simple_spinner_item,nameslist);
            adapter_c.setDropDownViewResource(R.layout.spinner_layout);
            mspnOrg_unity.setAdapter(adapter_c);


        }else {
            final ArrayAdapter<String> adapter_c = new ArrayAdapter<String>(FilterLevelActivity.this, android.R.layout.simple_spinner_item,nameslist);
            adapter_c.setDropDownViewResource(R.layout.spinner_layout);
            mspnOrg_unity.setAdapter(adapter_c);
        }
*/


        /*// adding for periods
        adapter_Period = new ArrayAdapter<String>(FilterLevelActivity.this, android.R.layout.simple_spinner_item);
        adapter_Period.setDropDownViewResource(R.layout.spinner_layout);
        mspnPeriod.setAdapter(adapter_Period);*/

        addData1();
        //new LoadPeriods().execute();

    }

    private void addData() {

        ArrayAdapter<String> dataAdapterReq = new ArrayAdapter<String>(
                FilterLevelActivity.this, R.layout.spinner_layout) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.spinnerTarget)).setTypeface(Utils.setFont(FilterLevelActivity.this));
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

    private void addData1() {

        ArrayAdapter<String> dataAdapterReq = new ArrayAdapter<String>(
                FilterLevelActivity.this, R.layout.spinner_layout) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.spinnerTarget)).setTypeface(Utils.setFont(FilterLevelActivity.this));
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


        if(Utils.getSet(KEY_REFERENCE_LAB_SET,this) != null){
            nameslist.clear();
            ogUnits.clear();
            countrycodelist.clear();
            ogUnits.addAll(Utils.getSET(KEY_REFERENCE_LAB_SET,this));

            for(OrgsUnits cat: ogUnits){
                nameslist.add(cat.getItem_name());
                countrycodelist.add(cat.getItem_id());
            }


            //final ArrayAdapter<String> adapter_c = new ArrayAdapter<String>(SurvillanceSampleActivity.this, android.R.layout.simple_spinner_item,nameslist);
            ArrayAdapter<String> adapter_c = new ArrayAdapter<String>(FilterLevelActivity.this, android.R.layout.simple_spinner_item,nameslist);
            adapter_c.setDropDownViewResource(R.layout.spinner_layout);
            mSpnLevel.setAdapter(adapter_c);

            new InternetCheck(new InternetCheck.Consumer() {
                @Override
                public void accept(Boolean internet) {
                    if(internet){
                        new LoadOrgUnits().execute();
                    }
                }
            });

        }else {
            new LoadOrgUnits().execute();
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (R.id.btn_cancel == view.getId()) {

            intent = new Intent(FilterLevelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }else if (R.id.btn_ok_itecdialog_1 == view.getId()){

            intent = new Intent(FilterLevelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }else if (R.id.btn_ok_itecdialog_2 == view.getId()){

            intent = new Intent(FilterLevelActivity.this, MainActivity.class);
            intent.putExtra("SelectValue_OrgUnit", mSpnerOrgs);
            intent.putExtra("SelectValue_Entity", mSpnEntity);
            String m = mSpnPeriod.replace(("Week"+" "+"("),"");
            String x = m.replace((")"),"");
            intent.putExtra("SelectValue_Period", x);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

       /* mSpnerOrgs = mSpnLevel.getSelectedItem().toString();
        Log.e("SelectValue_OrgUnit", mSpnerOrgs);

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

        if (mSpnerOrgs.equalsIgnoreCase("---Select Org Level---")){

        }else {
            new LoadOrgs().execute();


        }*/

        mSpnerOrgs = mSpnLevel.getItemAtPosition(i)
                .toString();
        ent_id = countrycodelist.get(i);

        //Log.e("Lab reference id", lab_id);

        if(ogUnits.get(i).getSampls() != null && ogUnits.get(i).getSampls().size() > 0 ){

            /*try {
               // Log.e("Lab reference id", ""+referenceLabs.get(position).getSampls().getJSONObject(0).getString("id"));

                Log.e("here dat",""+referenceLabs.get(position).getSampls());
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            SamplsAdapter samplsAdapter = new SamplsAdapter(FilterLevelActivity.this,ogUnits.get(i).getSampls());

            //samplsAdapter.setDropDownViewResource(R.layout.spinner_layout);
            mspnOrg_unity.setAdapter(samplsAdapter);
            layoutHep_more.setVisibility(View.VISIBLE);
        }else{
            layoutHep_more.setVisibility(View.GONE);
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

                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(FilterLevelActivity.this);
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

/*

                final ArrayAdapter<String> adapter_c = new ArrayAdapter<String>(FilterLevelActivity.this, android.R.layout.simple_spinner_item);
                adapter_c.setDropDownViewResource(R.layout.spinner_layout);


                mspnOrg_unity.setAdapter(adapter_c);
*/



                try {
                    Log.e("gettingjson here samp", "" + results);

                    JSONObject obj = new JSONObject(results);
                    if (obj.optString("status").equalsIgnoreCase("ok")) {
                        JSONArray jsonArraycat = obj.optJSONArray("results");
                        nameslist.clear();
                        /*nameslist.add("All");
                        countrycodelist.add(0);*/


                        for (int i = 0; i < jsonArraycat.length(); i++) {
                            JSONObject jsonObject = jsonArraycat.getJSONObject(i);
                            String item_id = jsonObject.getString("uid");
                            String item_name = jsonObject.getString("enitty");

                            nameslist.add(item_name);

                            countrycodelist.add(item_id);

                            Category category = new Category();
                            category.setItem_name(item_name);
                            category.setItem_id(item_id);

                            categories.add(category);

                            adapter_c.add(item_name);

                        }
                        adapter_c.notifyDataSetChanged();

                        Utils.saveSet(KEY_ENTITY_SET,categories,FilterLevelActivity.this);


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
            dialog = new ProgressDialog(FilterLevelActivity.this);
            dialog.setMessage("Loading Entities...");
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    //addiing sample from api
    public class LoadOrgUnits extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String data;
            try
            {
                ConnectionClass conn= new ConnectionClass();
                data= conn.makeOkHttpDirect(Constants.MyStuff);
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

                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(FilterLevelActivity.this);
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


                //final ArrayAdapter<String> adapter_c = new ArrayAdapter<String>(SurvillanceSampleActivity.this, android.R.layout.simple_spinner_item);
                adapter_c = new ArrayAdapter<String>(FilterLevelActivity.this, android.R.layout.simple_spinner_item,nameslist);
                adapter_c.setDropDownViewResource(R.layout.spinner_layout);


                mSpnLevel.setAdapter(adapter_c);

                try {
                    // Log.e("gettingjson here samp", "" + results);
                    Log.e("gettingjson here samp", "" + results);

                    JSONObject obj = new JSONObject(results);
                    if (obj.optString("status").equalsIgnoreCase("ok")) {
                        JSONArray jsonArraycat = obj.optJSONArray("results");
                        nameslist.clear();
                        //ogUnits.clear();


                        for (int i = 0; i < jsonArraycat.length(); i++) {
                            JSONObject jsonObject = jsonArraycat.getJSONObject(i);
                            String item_id = jsonObject.getString("1");
                            String item_name = jsonObject.getString("level");

                            nameslist.add(item_name);

                            countrycodelist.add(item_id);

                            OrgsUnits category = new OrgsUnits();
                            category.setItem_name(item_name);
                            category.setItem_id(item_id);

                            List<referenceLabTests> tx = new ArrayList<>();


                            if(jsonObject.has("sampls")){
                                for (int j = 0;  j < jsonObject.getJSONArray("sampls").length() ; j++)
                                {
                                    referenceLabTests reftest = new referenceLabTests();
                                    reftest.setId(jsonObject.getJSONArray("sampls").getJSONObject(j).getString("1"));

                                    Log.e("i want to see", ""+jsonObject.getJSONArray("sampls").getJSONObject(j).getString("entity"));
                                    reftest.setName(jsonObject.getJSONArray("sampls").getJSONObject(j).getString("entity"));
                                    tx.add(reftest);
                                }

                                Log.e(" here samp", "" + jsonObject.getJSONArray("sampls").length());
                                category.setSampls(tx);
                            }

                            ogUnits.add(category);

                            adapter_c.add(item_name);

                        }
                        adapter_c.notifyDataSetChanged();

                        Utils.saveSET(KEY_REFERENCE_LAB_SET,ogUnits, FilterLevelActivity.this);


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
            dialog = new ProgressDialog(FilterLevelActivity.this);
            dialog.setMessage("Loading Entities...");
            dialog.setCancelable(false);
            dialog.show();

        }
    }

    /*public class LoadPeriods extends AsyncTask<String, String, String> {


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

        @Override
        protected void onPostExecute(String results){

            if (results.equalsIgnoreCase("error")) {
                try {

                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(FilterLevelActivity.this);
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

                            nameslistPeriod.add("Week"+" "+"("+item_name+")");
                            //nameslistPeriod.add(item_name);
                            countrycodelistPeriod.add(item_no);
                            adapter_Period.add("Week"+" "+"("+item_name+")");
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
            *//*dialog = new ProgressDialog(FilterLevelActivity.this);
            dialog.setMessage("Loading Periods...");
            dialog.setCancelable(false);
            dialog.show();*//*

        }
    }*/

}
