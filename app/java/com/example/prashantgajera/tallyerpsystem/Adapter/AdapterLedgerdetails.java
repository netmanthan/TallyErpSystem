package com.example.prashantgajera.tallyerpsystem.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prashantgajera.tallyerpsystem.LedgerCatagory;
import com.example.prashantgajera.tallyerpsystem.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Prashant Gajera on 03-Feb-19.
 */

public class AdapterLedgerdetails extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private OnItemClickListener onItemClickListener;
    List<LedgerCatagory> items = new ArrayList<>();
    List<LedgerCatagory> filteritems = new ArrayList<>();

    public AdapterLedgerdetails(Context context, List<LedgerCatagory> items) {
        this.ctx = context;
        this.items = items;
        filteritems.addAll(items);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, LedgerCatagory obj, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name, openingbalance, closingbalance, group;
        public View layout_parent;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.clientname);
            openingbalance = (TextView) itemView.findViewById(R.id.openingbalance);
            closingbalance = (TextView) itemView.findViewById(R.id.closingbalance);
            group = (TextView) itemView.findViewById(R.id.group);
            layout_parent = (View) itemView.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ledger_cardview, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder originalViewHolder = (OriginalViewHolder) holder;
            LedgerCatagory c = items.get(position);

            originalViewHolder.imageView.setImageResource(R.drawable.ic_contacts);
            originalViewHolder.name.setText(c.getName());
            originalViewHolder.openingbalance.setText(c.getOpeningBalance());
            originalViewHolder.closingbalance.setText(c.getClosingBalance());
            originalViewHolder.group.setText(c.getGroup());
            originalViewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, items.get(position), position);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void filter(String filterText)
    {
         filterText = filterText.toLowerCase(Locale.getDefault());
         items.clear();
         if(filterText.length()==0)
         {
             items.addAll(filteritems);
         }
         else
         {
             for(LedgerCatagory lc : filteritems)
             {
                 if(lc.getName().toLowerCase(Locale.getDefault()).contains(filterText) || lc.getGroup().toLowerCase(Locale.getDefault()).contains(filterText))
                 {
                     items.add(lc);
                 }

             }
         }
         notifyDataSetChanged();
    }
}
