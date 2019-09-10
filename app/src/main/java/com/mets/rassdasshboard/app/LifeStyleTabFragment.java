package com.mets.rassdasshboard.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.core.map.series.Choropleth;
import com.anychart.core.ui.ColorRange;
import com.anychart.enums.SelectionMode;
import com.anychart.enums.SidePosition;
import com.anychart.scales.LinearColor;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.anychart.charts.Map;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mets.rassdasshboard.app.models.AdultsModel;
import com.mets.rassdasshboard.app.services.CombComdtySTKA;
import com.mets.rassdasshboard.app.services.GetAllData;
import com.mets.rassdasshboard.app.services.STKAMapData;
import com.mets.rassdasshboard.app.services.STKAStockOutBar;
import com.mets.rassdasshboard.app.services.getCurrentWeek;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LifeStyleTabFragment extends Fragment  {
    private CombinedChart mChart;
    ProgressDialog dialog;
    GetAllData mGetAllData;
    STKAStockOutBar mSTKAStockOutBar;
    STKAMapData mSTKAMapData;
    CombComdtySTKA mCombComdtySTKA;
    getCurrentWeek mgetCurrentWeek;

    TextView txtpag,txtPg, txtName, txtRptrate, txtRptPg, txtRptDls, txtAdultsNm,txtTNm_d;

    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<BarEntry> barEntries1 = new ArrayList<>();
    String myvalue1,myvalue2,myvalue3;


    ArrayList<Entry> ReportingRates = new ArrayList<>();
    ArrayList<Entry> StockoutRates = new ArrayList<>();
    ArrayList<BarEntry> ClientRisk = new ArrayList<>();

    BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.lifestyle_tab_layout,null);
        initView(rootView);

        Bundle bundle = getArguments();
        if(bundle!= null)
        {
            myvalue1 = getArguments().getString("SelectValue_OrgUnit");
            myvalue2 = getArguments().getString("SelectValue_Entity");
            myvalue3 = getArguments().getString("SelectValue_Period");
            Log.e("here data",""+myvalue1);

        }else
        {
            //getCurrentWeek();
        }
        getAdultsData(myvalue1,myvalue2,myvalue3);
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

        // combined graph code starts here
        mChart = (CombinedChart) view.findViewById(R.id.chart1);

        /// drawing abar chart grap strt here
        barChart = (BarChart)view.findViewById(R.id.chart);
        barChart.setHighlightFullBarEnabled(false);

        // Any graph code here

        AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));
        Map map = AnyChart.map();
        map.geoData("anychart.maps.uganda");
        //map.geoData("anychart.maps.united_states_of_america");

        map.interactivity().selectionMode(SelectionMode.NONE);
        map.padding(0, 0, 0, 0);
        //Choropleth series = map.choropleth(getData());
        Choropleth series = map.choropleth(getData());
        LinearColor linearColor = LinearColor.instantiate();
        linearColor.colors(new String[]{ "#c2e9fb", "#81d4fa", "#01579b", "#002746"});
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
                        "            return '<span style=\"font-size: 13px\">' + this.value + ' litres per capita</span>';\n" +
                        "          }");



        anyChartView.addScript("file:///android_asset/uganda.js");
        anyChartView.setChart(map);
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

    // function for any chart view static
    private List<DataEntry> getData() {
        List<DataEntry> data = new ArrayList<>();

        data.add(new CustomDataEntry("US.MN", "Minnesota", 8.4));
        data.add(new CustomDataEntry("US.MT", "Montana", 8.5));
        data.add(new CustomDataEntry("US.ND", "North Dakota", 5.1));
        data.add(new CustomDataEntry("US.ID", "Idaho", 8));
        data.add(new CustomDataEntry("US.WA", "Washington", 13.1));
        data.add(new CustomDataEntry("US.AZ", "Arizona", 9.7));
        data.add(new CustomDataEntry("US.CA", "California", 14));
        data.add(new CustomDataEntry("US.CO", "Colorado", 8.7));
        data.add(new CustomDataEntry("US.NV", "Nevada", 14.7));
        data.add(new CustomDataEntry("US.NM", "New Mexico", 6.9));
        data.add(new CustomDataEntry("US.OR", "Oregon", 12.2));
        data.add(new CustomDataEntry("US.UT", "Utah", 3.2));
        data.add(new CustomDataEntry("US.WY", "Wyoming", 5.2));
        data.add(new CustomDataEntry("US.AR", "Arkansas", 4.2));
        data.add(new CustomDataEntry("US.IA", "Iowa", 4.7));
        data.add(new CustomDataEntry("US.KS", "Kansas", 3.2));
        data.add(new CustomDataEntry("US.MO", "Missouri", 7.2));
        data.add(new CustomDataEntry("US.NE", "Nebraska", 5));
        data.add(new CustomDataEntry("US.OK", "Oklahoma", 4.5));
        data.add(new CustomDataEntry("US.SD", "South Dakota", 5));
        data.add(new CustomDataEntry("US.LA", "Louisiana", 5.7));
        data.add(new CustomDataEntry("US.TX", "Texas", 5));
        data.add(new CustomDataEntry("US.CT", "Connecticut", 14.4, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.MA", "Massachusetts", 16.9, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.NH", "New Hampshire", 19.6));
        data.add(new CustomDataEntry("US.RI", "Rhode Island", 14, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.VT", "Vermont", 17.5));
        data.add(new CustomDataEntry("US.AL", "Alabama", 6));
        data.add(new CustomDataEntry("US.FL", "Florida", 12.4));
        data.add(new CustomDataEntry("US.GA", "Georgia", 5.9));
        data.add(new CustomDataEntry("US.MS", "Mississippi", 2.8));
        data.add(new CustomDataEntry("US.SC", "South Carolina", 6.1));
        data.add(new CustomDataEntry("US.IL", "Illinois", 10.2));
        data.add(new CustomDataEntry("US.IN", "Indiana", 6.1));
        data.add(new CustomDataEntry("US.KY", "Kentucky", 3.9));
        data.add(new CustomDataEntry("US.NC", "North Carolina", 6.6));
        data.add(new CustomDataEntry("US.OH", "Ohio", 7.2));
        data.add(new CustomDataEntry("US.TN", "Tennessee", 5.4));
        data.add(new CustomDataEntry("US.VA", "Virginia", 10.7));
        data.add(new CustomDataEntry("US.WI", "Wisconsin", 9.1));
        data.add(new CustomDataEntry("US.WY", "Wyoming", 5.2, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.WV", "West Virginia", 2.4));
        data.add(new CustomDataEntry("US.DE", "Delaware", 13.5, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.DC", "District of Columbia", 25.7, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.MD", "Maryland", 8.9, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.NJ", "New Jersey", 14.9, new LabelDataEntry(false)));
        data.add(new CustomDataEntry("US.NY", "New York", 11.9));
        data.add(new CustomDataEntry("US.PA", "Pennsylvania", 5.6));
        data.add(new CustomDataEntry("US.ME", "Maine", 10.4));
        data.add(new CustomDataEntry("US.HI", "Hawaii", 13.1));
        data.add(new CustomDataEntry("US.AK", "Alaska", 10.9));
        data.add(new CustomDataEntry("US.MI", "Michigan", 7.6));

        return data;
    }

    class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String id, String name, Number value) {
            setValue("id", id);
            setValue("name", name);
            setValue("value", value);
        }
        public CustomDataEntry(String id, String name, Number value, LabelDataEntry label) {
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

                                getAdultsGraphData("Regional",Wk);
                                getAdultsMapData("District",Wk);
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
                dialog = ProgressDialog.show(getActivity(), "", "Loading Data...", true);
                dialog.setCancelable(true);
            }

        };
        mSTKAStockOutBar.execute(mLevel, mWeek);

    }

    // function for generating map data
    public void getAdultsMapData(final String mLevel,final String mWeek){
        mSTKAMapData = new STKAMapData() {
            @Override
            protected void onPostExecute(String results) {
                dialog.dismiss();
                if (!results.equalsIgnoreCase("error")) {
                    try {
                        JSONObject obj = new JSONObject(results);
                        if(obj.getString("status").equalsIgnoreCase("ok")) {
                            Log.e("here MapData", ""+results);
                            JSONArray res = obj.getJSONArray("results");








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
        mSTKAMapData.execute(mLevel, mWeek);

    }

    // My function for generating the combined graph for STKA data
    public void getAdultsCommodSTKA(final String mUid,final String mYear, final String mWeekNo){
        mCombComdtySTKA = new CombComdtySTKA() {
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

    @Override
    public void onResume() {
        super.onResume();
        //getAdultsData(myvalue1,myvalue2,myvalue3);
    }


}