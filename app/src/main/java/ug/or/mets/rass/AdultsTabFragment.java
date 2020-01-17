package ug.or.mets.rass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Map;
import com.anychart.core.map.series.Choropleth;
import com.anychart.enums.SelectionMode;
import com.anychart.scales.LinearColor;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import ug.or.mets.rass.services.AdultsDistrictMap;
import ug.or.mets.rass.services.CombComdtySTKA;
import ug.or.mets.rass.services.GetAllData;
import ug.or.mets.rass.services.STKAMapData;
import ug.or.mets.rass.services.STKAStockOutBar;
import ug.or.mets.rass.services.STKAStockOutBarDist;
import ug.or.mets.rass.services.STKAStockOutBarRegional;
import ug.or.mets.rass.services.STKCSingleMapData;
import ug.or.mets.rass.services.getCurrentWeek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdultsTabFragment extends Fragment {
    private CombinedChart mChart;
    ProgressDialog dialog;
    GetAllData mGetAllData;
    STKAStockOutBar mSTKAStockOutBar;
    AdultsDistrictMap mAdultsDistrictMap;
    STKAStockOutBarDist mSTKAStockOutBarDist;
    STKAMapData mSTKAMapData;
    CombComdtySTKA mCombComdtySTKA;
    getCurrentWeek mgetCurrentWeek;
    STKAStockOutBarRegional mSTKAStockOutBarRegional;

    STKCSingleMapData mSTKCSingleMapData;

    TextView txtpag,txtPg, txtName, txtRptrate, txtRptPg, txtRptDls, txtAdultsNm,txtTNm_d, TxtTitle_commodity_skt;

    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<BarEntry> barEntries1 = new ArrayList<>();
    String myvalue1,myvalue2,myvalue3;


    ArrayList<Entry> ReportingRates = new ArrayList<>();
    ArrayList<Entry> StockoutRates = new ArrayList<>();
    ArrayList<BarEntry> ClientRisk = new ArrayList<>();

    BarChart barChart;
    ProgressBar barProg;
    Map map;
    AnyChartView anyChartView;

    List<DataEntry> MapData = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lifestyle_tab_layout,null);


        Bundle bundle = getArguments();
        if(bundle!= null) {
            if (bundle.getString("SelectValue_OrgUnit")!=null && bundle.getString("SelectValue_Entity")!=null && bundle.getString("SelectValue_Period")!=null) {
                myvalue1 = getArguments().getString("SelectValue_OrgUnit");
                myvalue2 = getArguments().getString("SelectValue_Entity");
                myvalue3 = getArguments().getString("SelectValue_Period");
                Log.e("here data", "" + myvalue1);
            }else{
                Calendar cal = Calendar.getInstance();
                myvalue1 = "National";
                myvalue2 = "Uganda";
                myvalue3 = String.format("%dW%d",  cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.WEEK_OF_YEAR));
            }
        }

        initView(rootView);
        //detect internet and show the data
        if(isNetworkStatusAvialable (getActivity())) {
            //Toast.makeText(getActivity(), "Internet detected", Toast.LENGTH_SHORT).show();
            getAdultsData(myvalue1,myvalue2,myvalue3);
        } else {
            // Toast.makeText(getActivity(), "Please check your Internet Connection", Toast.LENGTH_LONG).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final ViewGroup viewGroup = rootView.findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(rootView.getContext()).inflate(R.layout.custom_dialog, viewGroup, false);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            dialogView.findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //your business logic
                    getAdultsData(myvalue1,myvalue2,myvalue3);
                    alertDialog.dismiss();

                }
            });


        }
        return rootView;
    }

    private void initView(View view) {


        txtpag = (TextView) view.findViewById(R.id.txtdetails);
        txtPg = (TextView) view.findViewById(R.id.txtphone);
        txtName = (TextView) view.findViewById(R.id.txtName);

        txtRptrate = (TextView) view.findViewById(R.id.txtNamerpt);
        txtRptPg = (TextView) view.findViewById(R.id.txtpercentagerpt);
        txtRptDls = (TextView) view.findViewById(R.id.txtdetailsrpt);

        txtAdultsNm = (TextView) view.findViewById(R.id.txtNm);
        txtTNm_d = (TextView) view.findViewById(R.id.txtNm_d);
        TxtTitle_commodity_skt = (TextView)view.findViewById(R.id.txtTitle_commodity_skt) ;

        // combined graph code starts here
        mChart = (CombinedChart) view.findViewById(R.id.chart1);
        barProg = (ProgressBar)view.findViewById(R.id.progress_barg);
        /// drawing abar chart grap strt here
        barChart = (BarChart)view.findViewById(R.id.chart);
        barChart.setHighlightFullBarEnabled(false);



        // Any graph code here
        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));
        // map = AnyChart.map();


    }


    private LineData generateLineData() {

        LineData d = new LineData();
        LineDataSet set = new LineDataSet(ReportingRates, " Adult ARVs (Reporting Rates)");
        set.setColors(Color.parseColor("#90ed7d"));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.parseColor("#90ed7d"));
        set.setCircleRadius(2f);
        set.setFillColor(Color.parseColor("#90ed7d"));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private LineData generateLineData2() {

        LineData d2 = new LineData();
        LineDataSet set = new LineDataSet(StockoutRates, "  Adult ARVs (Stockout Rates)");
        set.setColors(Color.parseColor("#434348"));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.parseColor("#434348"));
        set.setCircleRadius(2f);
        set.setFillColor(Color.parseColor("#434348"));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d2.addDataSet(set);

        return d2;
    }

    private BarData generateBarData() {
        BarDataSet set1 = new BarDataSet(ClientRisk, "Client (at Risk)-");
        set1.setColors(Color.parseColor("#7CB5EC"));
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        float barWidth = 0.8f; // x2 dataset
        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);
        return d;
    }

    class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String id, String name, Number value) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
        }
        public CustomDataEntry(String id, String name, Double value, LifeStyleTabFragment.LabelDataEntry label) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
            setValue("label", label);
        }
    }
    class LabelDataEntry extends DataEntry {
        LabelDataEntry(Boolean enabled) {
            setValue("enabled", enabled);
        }
    }

    public void getAdultsData(final String mLevel, final String mEntity,final String mWeek){
        mGetAllData = new GetAllData() {
            @Override
            protected void onPostExecute(String results) {
                dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("here life 1", ""+results);
                            if (obj.getJSONArray("results").length() > 0){
                                final String rptRecieved = obj.getJSONArray("results").getJSONObject(0).getString("receivedreports");
                                final String rptExpt = obj.getJSONArray("results").getJSONObject(0).getString("expectedreports");
                                final String Enty = obj.getJSONArray("results").getJSONObject(0).getString("entity");
                                final String Wk = obj.getJSONArray("results").getJSONObject(0).getString("week");
                                final String rptStock_out = obj.getJSONArray("results").getJSONObject(0).getString("rso");
                                final String mlevel = obj.getJSONArray("results").getJSONObject(0).getString("level");

                                // my parameters for the combined graph are defined here
                                final String mUid = obj.getJSONArray("results").getJSONObject(0).getString("uid");
                                final String mYear = obj.getJSONArray("results").getJSONObject(0).getString("yr");
                                final String mWeekNo = obj.getJSONArray("results").getJSONObject(0).getString("weekno");



                                Log.e("Received Reports", rptRecieved);
                                txtpag.setText(rptRecieved +" "+"of"+" "+rptExpt+" "+"Health Facilities Reported [Missing Reports]");

                                int a = Integer.parseInt(rptRecieved);
                                int b = Integer.parseInt(rptExpt);
                                double result = ((double) a/b)*100;

                                Log.e("txt", String.valueOf(result));
                                DecimalFormat value = new DecimalFormat("#.#");
                                txtPg.setText(value.format(result)+"%");
                                txtName.setText("Stock Out Rate :"+Wk+"("+Enty+")");

                                txtRptrate.setText("Reporting Rate : "+Wk+"("+Enty+")");
                                int x = Integer.parseInt(rptStock_out);
                                int y = Integer.parseInt(rptRecieved);
                                double z = ((double) x/y)*100;
                                DecimalFormat v = new DecimalFormat("#.#");
                                txtRptPg.setText(v.format(z)+"%");
                                txtRptDls.setText(rptStock_out+" "+"of"+" "+rptRecieved+" "+"Health Facilities Stocked Out");

                                txtAdultsNm.setText("Adults"+" "+"("+Wk+")");
                                txtTNm_d.setText("Adults"+" "+"("+Wk+")");

                                TxtTitle_commodity_skt.setText("HIV Commodity Stockout rates - Last 12 Weeks"+" "+"("+Enty+")");
                                // check for selection
                                if (myvalue1.equalsIgnoreCase("National")){
                                    // this shows REgional Uganda graph data for every graph
                                    getAdultsGraphData("Regional",Wk);
                                    getAdultsMapData("District",Wk);
                                }else if (myvalue1.equalsIgnoreCase("Regional")){
                                    // this shows all Regional data for every graph
                                    getAdults_GraphData(myvalue2, Wk);
                                    getRegionalDistrictDataSTKA(myvalue2,Wk);


                                }else if (myvalue1.equalsIgnoreCase("District")){
                                    // this shows all District data for every graph
                                    getAdultsDistGraphData("District",Wk,myvalue2);
                                    //getAdults_GraphData("District", Wk, myvalue2);
                                    getSTKCSelectedMapData("District",Wk,myvalue2);
                                }
                                getAdultsCommodSTKA(mUid,mYear,mWeekNo);

                            }

                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }else {
                    AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(getActivity());
                    localBuilder1.setMessage("Failed, check your internet connection")
                            .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                                    getAdultsData(myvalue1,myvalue2,myvalue3);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                            getActivity().finish();
                        }
                    });
                }
            }
            @Override
            protected void onPreExecute()
            {
                dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                dialog.setCancelable(true);
            }

        };
        mGetAllData.execute(mLevel, mEntity, mWeek);

    }

    //Graph
    public void getAdultsGraphData(final String mLevel,final String mWeek){
        mSTKAStockOutBar = new STKAStockOutBar() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("here GraphData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] months = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                barEntries.add(new BarEntry(i+1, array.getInt(1)));
                                barEntries1.add(new BarEntry(i+1, (float) array.getDouble(2)));
                                months[i] = array.getString(3);

                            }


                            BarDataSet barDataSet = new BarDataSet(barEntries,"Stockouts");
                            Collections.sort(barEntries, new EntryXComparator());
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100)");
                            barDataSet1.setColors(Color.parseColor("#000000"));
                            Collections.sort(barEntries1, new EntryXComparator());

                            barProg.setVisibility(View.GONE);
                            BarData data = new BarData(barDataSet,barDataSet1);
                            barChart.setData(data);

                            barProg.setVisibility(View.GONE);

                            XAxis xAxisB = barChart.getXAxis();
                            xAxisB.setValueFormatter(new IndexAxisValueFormatter(months));
                            barChart.getAxisLeft().setAxisMinimum(0);
                            xAxisB.setPosition(XAxis.XAxisPosition.BOTTOM);

                            xAxisB.setGranularity(1);
                            xAxisB.setLabelRotationAngle(45);
                            xAxisB.setCenterAxisLabels(true);
                            xAxisB.setGranularityEnabled(true);

                            barChart.setFitBars(true);

                            float barSpace = 0.01f;
                            float groupSpace = 0.1f;
                            int groupCount = 12;

                            //IMPORTANT *****
                            data.setBarWidth(0.15f);
                            barChart.getXAxis().setAxisMinimum(0);
                            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
                            //***** IMPORTANT

                            Log.e("here GraphData 2", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mSTKAStockOutBar.execute(mLevel, mWeek);

    }

    public void getAdultsDistGraphData(final String mLevel,final String mWeek, final String mEntity){
        mSTKAStockOutBarDist = new STKAStockOutBarDist() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("District GraphData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] drugs = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                barEntries.add(new BarEntry(i+1,    (float) array.getDouble(2)));
                                barEntries1.add(new BarEntry(i+1, (float) array.getDouble(1)));
                                drugs[i] = array.getString(0);

                            }


                            BarDataSet barDataSet = new BarDataSet(barEntries,"Stockouts");
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            Collections.sort(barEntries, new EntryXComparator());
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100)");
                            barDataSet1.setColors(Color.parseColor("#000000"));
                            Collections.sort(barEntries1, new EntryXComparator());


                            BarData data = new BarData(barDataSet,barDataSet1);
                            barChart.setData(data);

                            barProg.setVisibility(View.GONE);

                            XAxis xAxisB = barChart.getXAxis();
                            xAxisB.setValueFormatter(new IndexAxisValueFormatter(drugs));
                            barChart.getAxisLeft().setAxisMinimum(0);
                            xAxisB.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxisB.setGranularity(1);
                            xAxisB.setLabelRotationAngle(45);
                            xAxisB.setCenterAxisLabels(true);
                            xAxisB.setGranularityEnabled(true);

                            barChart.setFitBars(true);

                            float barSpace = 0.01f;
                            float groupSpace = 0.1f;
                            int groupCount = 12;

                            //IMPORTANT *****
                            data.setBarWidth(0.15f);
                            barChart.getXAxis().setAxisMinimum(0);
                            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
                            //***** IMPORTANT

                            Log.e("here GraphData 2", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mSTKAStockOutBarDist.execute(mLevel, mWeek,mEntity);

    }

    // function for generating map data
    public void getAdultsMapData(final String mLevel,final String mWeek){
        mSTKAMapData = new STKAMapData() {
            @Override
            protected void onPostExecute(String results) {
                // dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {

                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("District GraphData", ""+results);
                            JSONArray res = obj.getJSONArray("results");
                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                MapData.add(new AdultsTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));

                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(MapData);
                            LinearColor linearColor = LinearColor.instantiate();
                            linearColor.colors(new String[]{ "#ADFF2F", "#FFFF00", "#FF0000", "#bf0000"});
                            series.colorScale(linearColor);
                            series.hovered()
                                    .fill("#f48fb1")
                                    .stroke("#f99fb9");
                            series.selected()
                                    .fill("#c2185b")
                                    .stroke("#c2185b");
                            series.labels().enabled(true);
                            series.labels().fontSize(10);
                            series.labels().fontColor("#212121");
                            series.labels().format("{%Value}");
                            series.tooltip()
                                    .useHtml(true)
                                    .format("function() {\n" +
                                            "            return '<span style=\"font-size: 13px\">' + this.value + ' %</span>';\n" +
                                            "          }");



                            anyChartView.addScript("file:///android_asset/uganda.js");
                            anyChartView.addScript("file:///android_asset/proj4.js");
                            anyChartView.setChart(map);


                            Log.e("here GraphData 2", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mSTKAMapData.execute(mLevel, mWeek);

    }

    // My function for generating the combined graph for STKA data
    public void getAdultsCommodSTKA(final String mUid,final String mYear, final String mWeekNo){
        mCombComdtySTKA = new CombComdtySTKA() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject objAdults = new JSONObject(results);
                        if(objAdults.getString("status").equalsIgnoreCase("ok")) {
                            Log.e("here CombData", ""+results);
                            JSONArray mRes = objAdults.getJSONArray("results");

                            String[] weeks = new String[mRes.length()];

                            for(int i= 0; i< mRes.length();i++){
                                JSONArray array = mRes.getJSONArray(i);
                                ClientRisk.add(new BarEntry(i+1, array.getInt(2)));
                                StockoutRates.add(new Entry(i+1,(float) array.getDouble(1)));
                                ReportingRates.add(new Entry(i+1, (float) array.getDouble(3)));
                                weeks[i] = array.getString(0);

                            }
                            Collections.sort(ClientRisk, new EntryXComparator());
                            Collections.sort(StockoutRates, new EntryXComparator());
                            Collections.sort(ReportingRates, new EntryXComparator());
                            mChart.setDrawGridBackground(true);
                            mChart.setDrawBarShadow(false);
                            mChart.setClickable(false);
                            mChart.setHighlightFullBarEnabled(false);
                            // draw bars behind lines
                            mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                                    CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
                            });

                            mChart.setOnTouchListener(new View.OnTouchListener(){
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    return false;
                                }
                            });
                            //Set the Legends Orientation
                            Legend l = mChart.getLegend();
                            l.setWordWrapEnabled(true);
                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

                            //Set Right And Left Axis
                            YAxis rightAxis = mChart.getAxisRight();
                            rightAxis.setDrawGridLines(false);
                            rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
                            //rightAxis.setAxisMaximum(80000f);

                            YAxis leftAxis = mChart.getAxisLeft();
                            leftAxis.setDrawGridLines(false);
                            leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
                            leftAxis.setAxisMaximum(80f);

                            //Set X-Axis Lables
                            XAxis xAxis = mChart.getXAxis();
                            xAxis.setAxisMinimum(0f);
                            xAxis.setGranularity(1f);
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(weeks));
                            xAxis.setLabelRotationAngle(45);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setGranularityEnabled(true);
                            xAxis.setLabelCount(12);


                            CombinedData combdata = new CombinedData();
                            combdata.setData(generateLineData());
                            combdata.setData(generateLineData2());
                            combdata.setData(generateBarData());
                            xAxis.setAxisMaximum(combdata.getXMax() + 0.25f);
                            mChart.setData(combdata);
                            mChart.invalidate();



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mCombComdtySTKA.execute(mUid,mYear,mWeekNo);

    }

    // Get Current week
    public void getCurrentWeek(){
        mgetCurrentWeek = new getCurrentWeek() {
            @Override
            protected void onPostExecute(String results) {
                dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("here life 1", ""+results);
                            if (obj.getJSONArray("results").length() > 0){

                                // my parameters for the combined graph are defined here
                                final String mWeek = obj.getJSONArray("results").getJSONObject(0).getString("week");
                                final String mWeekNo = obj.getJSONArray("results").getJSONObject(0).getString("weekno");
                                getAdultsData("National","Uganda",mWeek);


                            }

                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                dialog.setCancelable(true);
            }

        };
        mgetCurrentWeek.execute();

    }

    // function for generating Regional district graph data
    public void getAdultsReginGraph(final String mRegion,final String mWeek){
        mSTKAStockOutBarRegional = new STKAStockOutBarRegional() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("Regional GraphData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] Dist = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                barEntries.add(new BarEntry(i+1,    (float) array.getDouble(0)));
                                barEntries1.add(new BarEntry(i+1, (float) array.getDouble(1)));
                                Dist[i] = array.getString(2);

                            }


                            BarDataSet barDataSet = new BarDataSet(barEntries,"Stockouts");
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            Collections.sort(barEntries, new EntryXComparator());
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100)");
                            barDataSet1.setColors(Color.parseColor("#000000"));
                            Collections.sort(barEntries1, new EntryXComparator());

                            BarData data = new BarData(barDataSet,barDataSet1);
                            barChart.setData(data);

                            barProg.setVisibility(View.GONE);

                            XAxis xAxisB = barChart.getXAxis();
                            xAxisB.setValueFormatter(new IndexAxisValueFormatter(Dist));
                            barChart.getAxisLeft().setAxisMinimum(0);
                            xAxisB.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxisB.setGranularity(1);
                            xAxisB.setLabelRotationAngle(45);
                            xAxisB.setCenterAxisLabels(true);
                            xAxisB.setGranularityEnabled(true);

                            barChart.setFitBars(true);

                            float barSpace = 0.01f;
                            float groupSpace = 0.1f;
                            int groupCount = 12;

                            //IMPORTANT *****
                            data.setBarWidth(0.15f);
                            barChart.getXAxis().setAxisMinimum(0);
                            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
                            //***** IMPORTANT

                            Log.e("Reg Graph", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mSTKAStockOutBarRegional.execute(mRegion, mWeek);
    }

    //Graph for each district
    public void getAdults_GraphData(final String mRegion,final String mWeek){
        mAdultsDistrictMap = new AdultsDistrictMap() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("Single MapData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            //String[] drugs = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                MapData.add(new AdultsTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));
                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(MapData);
                            LinearColor linearColor = LinearColor.instantiate();
                            linearColor.colors(new String[]{ "#ADFF2F", "#FFFF00", "#FF0000", "#bf0000"});
                            series.colorScale(linearColor);
                            series.hovered()
                                    .fill("#f48fb1")
                                    .stroke("#f99fb9");
                            series.selected()
                                    .fill("#c2185b")
                                    .stroke("#c2185b");
                            series.labels().enabled(true);
                            series.labels().fontSize(10);
                            series.labels().fontColor("#212121");
                            series.labels().format("{%Value}");
                            series.tooltip()
                                    .useHtml(true)
                                    .format("function() {\n" +
                                            "            return '<span style=\"font-size: 13px\">' + this.value + ' %</span>';\n" +
                                            "          }");



                            anyChartView.addScript("file:///android_asset/uganda.js");
                            anyChartView.addScript("file:///android_asset/proj4.js");
                            anyChartView.setChart(map);


                            Log.e("here GraphData 2", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mAdultsDistrictMap.execute(mRegion, mWeek);

    }

    //regional district graph function
    public void getRegionalDistrictDataSTKA(final String mRegion,final String mWeek){
        mAdultsDistrictMap = new AdultsDistrictMap() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("District GraphData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] drugs = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                barEntries.add(new BarEntry(i+1,    (float) array.getDouble(2)));
                                barEntries1.add(new BarEntry(i+1, (float) array.getDouble(1)));
                                drugs[i] = array.getString(3);
                            }
                            BarDataSet barDataSet = new BarDataSet(barEntries,"Stockouts");
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            Collections.sort(barEntries, new EntryXComparator());
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100)");
                            barDataSet1.setColors(Color.parseColor("#000000"));
                            Collections.sort(barEntries1, new EntryXComparator());
                            barProg.setVisibility(View.GONE);
                            BarData data = new BarData(barDataSet,barDataSet1);
                            barChart.setData(data);

                            barProg.setVisibility(View.GONE);

                            XAxis xAxisB = barChart.getXAxis();
                            xAxisB.setValueFormatter(new IndexAxisValueFormatter(drugs));
                            barChart.getAxisLeft().setAxisMinimum(0);
                            xAxisB.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxisB.setGranularity(1);
                            xAxisB.setLabelRotationAngle(45);
                            xAxisB.setCenterAxisLabels(true);
                            xAxisB.setGranularityEnabled(true);

                            barChart.setFitBars(true);

                            float barSpace = 0.01f;
                            float groupSpace = 0.1f;
                            int groupCount = 12;

                            //IMPORTANT *****
                            data.setBarWidth(0.15f);
                            barChart.getXAxis().setAxisMinimum(0);
                            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                            barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
                            //***** IMPORTANT

                            Log.e("here GraphData 2", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mAdultsDistrictMap.execute(mRegion, mWeek);

    }

    @Override
    public void onResume() {
        super.onResume();
        //getAdultsData(myvalue1,myvalue2,myvalue3);
    }
    // Selected District map
    public void getSTKCSelectedMapData(final String mLevel,final String mWeek, final String mEntity){
        mSTKCSingleMapData = new STKCSingleMapData() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("Single MapData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] drugs = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                MapData.add(new AdultsTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));
                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(MapData);
                            LinearColor linearColor = LinearColor.instantiate();
                            linearColor.colors(new String[]{ "#ADFF2F", "#FFFF00", "#FF0000", "#bf0000"});
                            series.colorScale(linearColor);
                            series.hovered()
                                    .fill("#f48fb1")
                                    .stroke("#f99fb9");
                            series.selected()
                                    .fill("#c2185b")
                                    .stroke("#c2185b");
                            series.labels().enabled(true);
                            series.labels().fontSize(10);
                            series.labels().fontColor("#212121");
                            series.labels().format("{%Value}");
                            series.tooltip()
                                    .useHtml(true)
                                    .format("function() {\n" +
                                            "            return '<span style=\"font-size: 13px\">' + this.value + ' %</span>';\n" +
                                            "          }");



                            anyChartView.addScript("file:///android_asset/uganda.js");
                            anyChartView.addScript("file:///android_asset/proj4.js");
                            anyChartView.setChart(map);


                            Log.e("here GraphData 2", ""+res.length());



                        }else{
                            Toast.makeText(getActivity(), "message failed!", Toast.LENGTH_SHORT).show();
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }
                    } catch (JSONException localJSONException) {
                        Log.e("gettingjson", localJSONException.toString());
                        localJSONException.printStackTrace();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }
            }
            @Override
            protected void onPreExecute()
            {
                //dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                //dialog.setCancelable(true);
            }

        };
        mSTKCSingleMapData.execute(mLevel, mWeek,mEntity);

    }
    //check internet connection
    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
            {
                return netInfos.isConnected();
            }
        }
        return false;
    }
}