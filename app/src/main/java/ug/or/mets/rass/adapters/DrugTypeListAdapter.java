package ug.or.mets.rass.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import ug.or.mets.rass.R;
import ug.or.mets.rass.models.DrugTypeResults;


public class DrugTypeListAdapter extends BaseAdapter {

    List<DrugTypeResults> drugTypeResults =  new ArrayList<DrugTypeResults>();
    Context context;

    public DrugTypeListAdapter(Context context, List<DrugTypeResults> doctorCategoryResults){
        this.context = context;
        this.drugTypeResults = doctorCategoryResults;

    }

    @Override
    public int getCount() {
        return drugTypeResults.size();
    }

    @Override
    public DrugTypeResults getItem(int position) {
        return drugTypeResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.drug_item_list, null);
        }

        TextView Drugtem		= (TextView) convertView.findViewById(R.id.txtDrug_name);

        final DrugTypeResults Consult = drugTypeResults.get(position);
        Drugtem.setText(Consult.getCommodity());

        /*llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String position = Consult.getGabbage_id();
                Intent localIntent = new Intent(context, RequestActivity.class);
                localIntent.putExtra("cat_id", position);
                localIntent.putExtra("cat_name", Consult.getCategory_name());
                localIntent.putExtra("cat_price", Consult.getPricing());
                context.startActivity(localIntent);
                //Toast.makeText(context,position,Toast.LENGTH_SHORT).show();

            }
        });*/

        return convertView;
    }
}
