package com.mets.rassdasshboard.app;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Map;
import com.anychart.core.map.series.Choropleth;
import com.anychart.core.ui.ColorRange;
import com.anychart.enums.SelectionMode;
import com.anychart.enums.SidePosition;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mets.rassdasshboard.app.services.CombComdtyRTK;
import com.mets.rassdasshboard.app.services.CombComdtySTKC;
import com.mets.rassdasshboard.app.services.GetAllData;
import com.mets.rassdasshboard.app.services.RTKSData;
import com.mets.rassdasshboard.app.services.RTKStockOutBar;
import com.mets.rassdasshboard.app.services.STKAStockOutBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExpoTabFragment extends Fragment {
    private CombinedChart mChart;
    RTKStockOutBar mRTKStockOutBar;
    ProgressDialog dialog;
    RTKSData mRTKSData;
    CombComdtyRTK mCombComdtyRTK;

    TextView txtpag,txtPg, txtName, txtRptrate, txtRptPg, txtRptDls, txtAdultsNm,txtTNm_d;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expo_tab_layout,null);
        initView(rootView);

        Bundle bundle = getArguments();
        if(bundle!= null) {
            if (bundle.getString("SelectValue_OrgUnit")!=null && bundle.getString("SelectValue_Entity")!=null && bundle.getString("SelectValue_Period")!=null) {
                myvalue1 = getArguments().getString("SelectValue_OrgUnit");
                myvalue2 = getArguments().getString("SelectValue_Entity");
                myvalue3 = getArguments().getString("SelectValue_Period");
                Log.e("here data", "" + myvalue1);
            }else{
                myvalue1 = "National";
                myvalue2 = "Uganda";
                myvalue3 = "2019W32";
            }
        }
        getRTKsData(myvalue1,myvalue2,myvalue3);
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

        mChart = (CombinedChart) view.findViewById(R.id.chart1);

        /// drawing abar chart grap strt here
        barChart = (BarChart)view.findViewById(R.id.chart);
        barChart.setHighlightFullBarEnabled(false);


        // Any graph code here
        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));
        map = AnyChart.map();
        //Choropleth series = map.choropleth(getData());
        map.geoData("anychart.maps.uganda");
        map.interactivity().selectionMode(SelectionMode.NONE);
        map.padding(0, 0, 0, 0);

        /*data.add(new ExpoTabFragment.CustomDataEntry("Abim", "Abim District", 38.4));
        data.add(new ExpoTabFragment.CustomDataEntry("Agago", "Agago District", 70.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Bukedea", "Bukedea District", 10));
        data.add(new ExpoTabFragment.CustomDataEntry("Kalangala", "Kalangala District", 90.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Busia", "Busia District", 20.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Butambala", "Butambala District", 15.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Ibanda", "Ibanda District", 10.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Amuru", "Amuru District", 5.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Amudat", "Amudat District", 30.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Dokolo", "Dokolo District", 25.8));*/


        data.add(new ExpoTabFragment.CustomDataEntry("Adjumani", "Adjumani District", 38.4));
        data.add(new ExpoTabFragment.CustomDataEntry("Alebtong", "Alebtong District", 70.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Amolatar", "Amolatar District", 10));
        data.add(new ExpoTabFragment.CustomDataEntry("Amuria", "Amuria District", 90.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Apac", "Apac District", 20.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Arua", "Arua District", 15.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Budaka", "Budaka District", 10.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Bududa", "Bududa District", 5.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Bugiri", "Bugiri District", 30.8));


        data.add(new ExpoTabFragment.CustomDataEntry("Buhweju", "Buhweju District", 25.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Buikwe", "Buikwe District", 10));
        data.add(new ExpoTabFragment.CustomDataEntry("Bukomansimbi", "Bukomansimbi District", 90.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Bukwo", "Bukwo District", 20.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Bulambuli", "Bulambuli District", 15.0));
        data.add(new ExpoTabFragment.CustomDataEntry("Buliisa", "Buliisa District", 10.8));
        data.add(new ExpoTabFragment.CustomDataEntry("Bundibugyo", "Bundibugyo District", 5.0));

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
    }


    // other functions for the combined graph
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



    // function for any chart view static
    class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String id, String name, Number value) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
        }
        public CustomDataEntry(String id, String name, Number value, ExpoTabFragment.LabelDataEntry label) {
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


    public void getRTKsData(final String mLevel, final String mEntity,final String mWeek){
        mRTKSData = new RTKSData() {
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

                                getRTKGraphData("Regional",Wk);


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
        mRTKSData.execute(mLevel, mEntity, mWeek);

    }

    public void getRTKGraphData(final String mLevel,final String mWeek){
        mRTKStockOutBar = new RTKStockOutBar() {
            @Override
            protected void onPostExecute(String results) {
                dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {

                            Log.e("here GraphData", ""+results);

                            JSONArray res = obj.getJSONArray("results");

                            String[] months = new String[res.length()];

                            for(int i= 0; i< res.length();i++){
                                JSONArray array = res.getJSONArray(i);
                                barEntries.add(new BarEntry(i+1, array.getInt(0)));
                                barEntries1.add(new BarEntry(i+1, (float) array.getDouble(1)));
                                months[i] = array.getString(2);

                            }


                            BarDataSet barDataSet = new BarDataSet(barEntries,"Stockouts");
                            barDataSet.setColor(Color.parseColor("#7CB5EC"));
                            BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Clients at risk(*100)");
                            barDataSet1.setColors(Color.parseColor("#000000"));


                            BarData data = new BarData(barDataSet,barDataSet1);
                            barChart.setData(data);

                            XAxis xAxisB = barChart.getXAxis();
                            xAxisB.setValueFormatter(new IndexAxisValueFormatter(months));
                            barChart.getAxisLeft().setAxisMinimum(0);
                            xAxisB.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxisB.setGranularity(1);
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
                dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                dialog.setCancelable(true);
            }

        };
        mRTKStockOutBar.execute(mLevel, mWeek);

    }




    // My function for generating the combined graph for RTK data
    public void getRTKCommodSTKA(final String mUid,final String mYear, final String mWeekNo){
        mCombComdtyRTK = new CombComdtyRTK() {
            @Override
            protected void onPostExecute(String results) {
                dialog.dismiss();
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
                /*dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                dialog.setCancelable(true);*/
            }

        };
        mCombComdtyRTK.execute(mUid,mYear,mWeekNo);

    }


}