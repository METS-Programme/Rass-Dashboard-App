package com.mets.rassdasshboard.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
//import android.icu.util.Calendar;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.core.map.series.Choropleth;
import com.anychart.core.ui.ColorRange;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Orientation;
import com.anychart.enums.ScaleStackMode;
import com.anychart.enums.SelectionMode;
import com.anychart.enums.SidePosition;
import com.anychart.graphics.vector.text.HAlign;
import com.anychart.scales.Linear;
import com.anychart.scales.LinearColor;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.anychart.charts.Map;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;
import com.mets.rassdasshboard.app.models.AdultsModel;
import com.mets.rassdasshboard.app.services.CombComdtySTKA;
import com.mets.rassdasshboard.app.services.CombComdtySTKC;
import com.mets.rassdasshboard.app.services.GetAllData;
import com.mets.rassdasshboard.app.services.GetPaediatricData;
import com.mets.rassdasshboard.app.services.STKAMapData;
import com.mets.rassdasshboard.app.services.STKAStockOutBar;
import com.mets.rassdasshboard.app.services.STKAStockOutBarDist;
import com.mets.rassdasshboard.app.services.STKCRegionalMapData;
import com.mets.rassdasshboard.app.services.STKCSingleMapData;
import com.mets.rassdasshboard.app.services.STKCStockOutBar;
import com.mets.rassdasshboard.app.services.STKCStockOutBarDist;
import com.mets.rassdasshboard.app.services.STKCStockOutBarRegional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoTabFragment  extends Fragment {
    ProgressDialog dialog;
    private CombinedChart mChart;
    STKCStockOutBar mSTKCStockOutBar;
    STKCStockOutBarDist mSTKCStockOutBarDist;
    CombComdtySTKC mCombComdtySTKC;
    STKCSingleMapData mSTKCSingleMapData;
    STKCRegionalMapData mSTKCRegionalMapData;
    STKCStockOutBarRegional mSTKCStockOutBarRegional;
    ProgressBar barProg;
    GetPaediatricData mGetPaediatricData;
    TextView txtpag,txtPg, txtName, txtRptrate, txtRptPg, txtRptDls, txtAdultsNm,txtTNm_d, TxtTitle_commodity_skt;

    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<BarEntry> barEntries1 = new ArrayList<>();
    BarChart barChart;
    String myvalue1,myvalue2,myvalue3;

    ArrayList<Entry> ReportingRates = new ArrayList<>();
    ArrayList<Entry> StockoutRates = new ArrayList<>();
    ArrayList<BarEntry> ClientRisk = new ArrayList<>();

    Map map;
    AnyChartView anyChartView;
    List<DataEntry> data = new ArrayList<>();
    ArrayList<BarEntry> data1 = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auto_tab_layout,null);

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
                myvalue3 = String.format("%dW%d",  cal.get(Calendar.YEAR), cal.get(Calendar.WEEK_OF_YEAR));
            }
        }
        initView(rootView);
        getPaediatricData(myvalue1,myvalue2,myvalue3);

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
        barProg = (ProgressBar)view.findViewById(R.id.progress_barg);
        mChart = (CombinedChart) view.findViewById(R.id.chart1);


        /// drawing abar chart grap strt here
        barChart = (BarChart)view.findViewById(R.id.chart);
        barChart.setHighlightFullBarEnabled(false);


        // Any graph code here
        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

    }

    // other functions for the combined graph
    private LineData generateLineData() {

        LineData d = new LineData();
        LineDataSet set = new LineDataSet(ReportingRates, " ARVs (Reporting Rates)");
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
        LineDataSet set1 = new LineDataSet(StockoutRates, "  ARVs (Stockout Rates)");
        set1.setColors(Color.parseColor("#434348"));
        set1.setLineWidth(2.5f);
        set1.setCircleColor(Color.parseColor("#434348"));
        set1.setCircleRadius(2f);
        set1.setFillColor(Color.parseColor("#434348"));
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setDrawValues(true);

        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        d2.addDataSet(set1);

        return d2;
    }
    private BarData generateBarData() {
        BarDataSet set1 = new BarDataSet(ClientRisk, "Client (at Risk)");
        set1.setColors(Color.parseColor("#7CB5EC"));
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        float barWidth = 0.8f; // x2 dataset
        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);
        return d;
    }


    // function for any chart view static
    class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String id, String name, Number value) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
        }
        public CustomDataEntry(String id, String name, Number value, AutoTabFragment.LabelDataEntry label) {
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



    public void getPaediatricData(final String mLevel, final String mEntity,final String mWeek){
        mGetPaediatricData = new GetPaediatricData() {
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
                                txtpag.setText(rptStock_out+" "+"of"+" "+rptRecieved+" "+"Health Facilities Stocked Out");

                                int x = Integer.parseInt(rptStock_out);
                                int y = Integer.parseInt(rptRecieved);
                                double z = ((double) x/y)*100;
                                DecimalFormat v = new DecimalFormat("#.#");
                                txtPg.setText(v.format(z)+"%");
                                txtName.setText("Stock Out Rate :"+Wk+"("+Enty+")");

                                txtRptrate.setText("Reporting Rate : "+Wk+"("+Enty+")");
                                int a = Integer.parseInt(rptRecieved);
                                int b = Integer.parseInt(rptExpt);
                                double result = ((double) a/b)*100;

                                Log.e("txt", String.valueOf(result));
                                DecimalFormat value = new DecimalFormat("#.#");
                                txtRptPg.setText(value.format(result)+"%");
                                txtRptDls.setText(rptRecieved +" "+"of"+" "+rptExpt+" "+"Health Facilities Reported [Missing Reports]");

                                txtAdultsNm.setText("Paediatrics"+" "+"("+Wk+")");
                                txtTNm_d.setText("Paediatrics"+" "+"("+Wk+")");

                                //getSTKCGraphData(mlevel,Wk);

                                TxtTitle_commodity_skt.setText("HIV Commodity Stockout rates - Last 12 Weeks"+" "+"("+Enty+")");
                                // check for selection
                                if (myvalue1.equalsIgnoreCase("National")){
                                    // this shows all Uganda data for every graph
                                    getSTKCGraphData("Regional",Wk);
                                    getAdultsMapData("District",Wk);
                                }else if (myvalue1.equalsIgnoreCase("Regional")){
                                    // this shows all Regional data for every graph
                                    //getSTKCGraphData("District",Wk);
                                   // getRegionalMapData(myvalue2,Wk);
                                    getRegionalGrapSTKC(myvalue2,Wk);
                                    getRegionalDistrictData(myvalue2,Wk);

                                }else if (myvalue1.equalsIgnoreCase("District")){
                                    // this shows all District data for every graph
                                    getSTKCDistGraphData("District",Wk,myvalue2);
                                    // single district level map
                                    getSTKCSelectedMapData("District",Wk,myvalue2);
                                }
                                getPaedCommodSTKA(mUid,mYear,mWeekNo);

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
        mGetPaediatricData.execute(mLevel, mEntity, mWeek);

    }

    public void getSTKCGraphData(final String mLevel,final String mWeek){
        mSTKCStockOutBar = new STKCStockOutBar() {
            @Override
            protected void onPostExecute(String results) {
               // dialog.dismiss();
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
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            Collections.sort(barEntries, new EntryXComparator());
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100 -Paediatrics)");
                            barDataSet1.setColors(Color.parseColor("#000000"));
                            Collections.sort(barEntries1, new EntryXComparator());


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
               // dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
               // dialog.setCancelable(true);
            }

        };
        mSTKCStockOutBar.execute(mLevel, mWeek);

    }

    public void getSTKCDistGraphData(final String mLevel,final String mWeek, final String mEntity){
        mSTKCStockOutBarDist = new STKCStockOutBarDist() {
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
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100) -Paediatrics");
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
        mSTKCStockOutBarDist.execute(mLevel, mWeek,mEntity);

    }

    //regional district graph data
    public void getRegionalDistrictData(final String mRegion,final String mWeek){
        mSTKCStockOutBarRegional = new STKCStockOutBarRegional() {
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
                               // ["UcOzqLVFJVo",0,0,"Kyotera District"]

                            BarDataSet barDataSet = new BarDataSet(barEntries,"Stockouts");
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            Collections.sort(barEntries, new EntryXComparator());
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100) -Paediatrics");
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
        mSTKCStockOutBarRegional.execute(mRegion, mWeek);

    }

    // My function for generating the combined graph for STKC data
    public void getPaedCommodSTKA(final String mUid,final String mYear, final String mWeekNo){
        mCombComdtySTKC = new CombComdtySTKC() {
            @Override
            protected void onPostExecute(String results) {
               // dialog.dismiss();
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
        mCombComdtySTKC.execute(mUid,mYear,mWeekNo);

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
                                data.add(new AutoTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));
                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(data);
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

    // Selected regional District map data
    public void getRegionalMapData(final String mRegion,final String mWeek){
        mSTKCRegionalMapData = new STKCRegionalMapData() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("Regional MapData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] drugs = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                data.add(new AutoTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));
                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(data);
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
        mSTKCRegionalMapData.execute(mRegion, mWeek);

    }
    public void getRegionalGrapSTKC(final String mRegion,final String mWeek){
        mSTKCStockOutBarRegional = new STKCStockOutBarRegional() {
            @Override
            protected void onPostExecute(String results) {
                //dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("Regional GraphData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] drugs = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                data.add(new AutoTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));
                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(data);
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
        mSTKCStockOutBarRegional.execute(mRegion, mWeek);

    }

    // all national maps data
    // function for generating map data
    public void getAdultsMapData(final String mLevel,final String mWeek){
        mSTKCStockOutBar = new STKCStockOutBar() {
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
                                data.add(new AutoTabFragment.CustomDataEntry(array.getString(0), array.getString(3), (float) array.getDouble(1)));
                            }
                            map = AnyChart.map();
                            //Choropleth series = map.choropleth(getData());
                            map.geoData("anychart.maps.uganda");
                            map.interactivity().selectionMode(SelectionMode.NONE);
                            map.padding(0, 0, 0, 0);

                            Choropleth series = map.choropleth(data);
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
        mSTKCStockOutBar.execute(mLevel, mWeek);

    }






























}