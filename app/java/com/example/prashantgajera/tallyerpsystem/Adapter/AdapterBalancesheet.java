package com.example.prashantgajera.tallyerpsystem.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prashantgajera.tallyerpsystem.BalancesheetCatagory;
import com.example.prashantgajera.tallyerpsystem.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Prashant Gajera on 09-Mar-19.
 */

public  class AdapterBalancesheet  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

     public Context context;
     List<BalancesheetCatagory> items = new ArrayList<>();
     List<BalancesheetCatagory> filteritems = new ArrayList<>();


    public AdapterBalancesheet(Context context, List<BalancesheetCatagory> items) {
        this.context = context;
        this.items = items;
        filteritems.addAll(items);
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView balancename;
        public View layout_parent;
        public ImageView dotview;
        public TextView balancevalue;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            balancename = (TextView) itemView.findViewById(R.id.balancename);
            layout_parent = (View) itemView.findViewById(R.id.lyt_parent);
            dotview = (ImageView) itemView.findViewById(R.id.dot);
            balancevalue = (TextView) itemView.findViewById(R.id.balancevalue);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.balancesheet_cardlayout, parent, false);
        vh = new AdapterBalancesheet.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterBalancesheet.OriginalViewHolder) {
            final AdapterBalancesheet.OriginalViewHolder originalViewHolder = (AdapterBalancesheet.OriginalViewHolder) holder;
            BalancesheetCatagory c = items.get(position);

            originalViewHolder.balancename.setText(c.getBalancename());
            originalViewHolder.balancevalue.setText(c.getBalancevalue());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void  myfilter(String filterText)
    {
        filterText = filterText.toLowerCase(Locale.getDefault());
        items.clear();
        if(filterText.length()==0)
        {
            items.addAll(filteritems);
        }
        else
        {
            for(BalancesheetCatagory lc : filteritems)
            {
                if(lc.getBalancename().toLowerCase(Locale.getDefault()).contains(filterText) )
                {
                    items.add(lc);
                }

            }
        }
        notifyDataSetChanged();
    }

}



