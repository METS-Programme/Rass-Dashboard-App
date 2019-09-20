package com.mets.rassdasshboard.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mets.rassdasshboard.app.R;
import com.mets.rassdasshboard.app.models.referenceLabTests;

import java.util.List;


public class SamplsAdapter extends BaseAdapter {

    Context context;
    List<referenceLabTests> smaplesList;

    public SamplsAdapter(Context context,List<referenceLabTests> smaplesList){
        this.context = context;
        this.smaplesList = smaplesList;
    }


    @Override
    public int getCount() {
        return smaplesList.size();
    }

    @Override
    public String getItem(int position) {
         return    smaplesList.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null);
        }

        TextView text = (TextView) convertView.findViewById(R.id.spinnerTarget);


        text.setText(smaplesList.get(position).getName());

        return convertView;
    }
}
