package com.example.prashantgajera.tallyerpsystem.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prashantgajera.tallyerpsystem.R;
import com.example.prashantgajera.tallyerpsystem.VoucherProfileCatagory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Prashant Gajera on 26-Feb-19.
 */

public class AdapterVoucherCatagory extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public  Context context;
    List<VoucherProfileCatagory> items = new ArrayList<>();
    List<VoucherProfileCatagory> filteritems = new ArrayList<>();

    private AdapterVoucherCatagory.OnItemClickListener onItemClickListener;

   public AdapterVoucherCatagory(Context context, List<VoucherProfileCatagory> items)
    {
        this.context=context;
        this.items=items;
        filteritems.addAll(items);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, VoucherProfileCatagory obj, int position);
    }

    public void setOnItemClickListener(AdapterVoucherCatagory.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_profile_card,parent,false);
        vh = new AdapterVoucherCatagory.OriginalViewHolder(v);
        return vh;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView date;
        public View layout_parent;
        public TextView VoucherNo;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.vouchername);
            layout_parent = (View) itemView.findViewById(R.id.lyt_parent);
            date=(TextView) itemView.findViewById(R.id.voucherdate);
            VoucherNo = (TextView) itemView.findViewById(R.id.voucherNo);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterVoucherCatagory.OriginalViewHolder) {
            final AdapterVoucherCatagory.OriginalViewHolder originalViewHolder = (AdapterVoucherCatagory.OriginalViewHolder) holder;
            VoucherProfileCatagory c = items.get(position);

            originalViewHolder.name.setText(c.getName());
            originalViewHolder.date.setText(c.getDate());
            originalViewHolder.VoucherNo.setText(c.getVoucherNo());
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
            for(VoucherProfileCatagory lc : filteritems)
            {
                if(lc.getName().toLowerCase(Locale.getDefault()).contains(filterText) ) {
                    items.add(lc);
                }

            }
        }
        notifyDataSetChanged();
    }

    public void filterDate(String Startdate,String Enddate)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date d1=new Date(),d2=new Date(),d3=new Date();
        try {
            d1 = sdf.parse(Startdate.replaceAll("\\s+",""));
            d2=sdf.parse(Enddate.replaceAll("\\s+",""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        items.clear();
        if(Startdate.equals("Start date") || Enddate.equals("End date"))
        {
            items.addAll(filteritems);
        }
        else
        {
            for(VoucherProfileCatagory lc : filteritems)
            {
               String getdate = lc.getDate();
               try{d3= sdf.parse(getdate);}catch (Exception e){}
                if(d3.compareTo(d1)>=0  && d3.compareTo(d2)<=0) {
                    items.add(lc);
                }

            }
        }
        notifyDataSetChanged();
    }

    public void clearSearch()
    {
        items.clear();
        items.addAll(filteritems);
        notifyDataSetChanged();
    }

}
