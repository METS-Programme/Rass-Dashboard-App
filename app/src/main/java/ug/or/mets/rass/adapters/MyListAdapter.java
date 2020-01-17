package ug.or.mets.rass.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import ug.or.mets.rass.R;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] drugType;

    public MyListAdapter(Activity context, String[] drugType) {
        super(context, R.layout.drug_item_list, drugType);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.drugType=drugType;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.drug_item_list, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.txtDrug_name);

        titleText.setText(drugType[position]);


        return rowView;

    };
}