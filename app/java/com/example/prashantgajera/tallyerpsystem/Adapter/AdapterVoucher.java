package com.example.prashantgajera.tallyerpsystem.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prashantgajera.tallyerpsystem.R;
import com.example.prashantgajera.tallyerpsystem.VoucherCatagory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashant Gajera on 20-Feb-19.
 */

public class AdapterVoucher extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private Context ctx;
    private AdapterVoucher.OnItemClickListener onItemClickListener;
    List<VoucherCatagory> items = new ArrayList<>();

    public AdapterVoucher(Context context, List<VoucherCatagory> items) {
        this.ctx = context;
        this.items = items;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, VoucherCatagory obj, int position);
    }

    public void setOnItemClickListener(AdapterVoucher.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView Vouchername;
        public View layout_parent;
        public ImageView dotview;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            Vouchername = (TextView) itemView.findViewById(R.id.vouchername);
            layout_parent = (View) itemView.findViewById(R.id.lyt_parent);
            dotview =(ImageView) itemView.findViewById(R.id.dot);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_card, parent, false);
        vh = new AdapterVoucher.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final  int position) {
        if (holder instanceof AdapterVoucher.OriginalViewHolder) {
            final AdapterVoucher.OriginalViewHolder originalViewHolder = (AdapterVoucher.OriginalViewHolder) holder;
            VoucherCatagory c = items.get(position);

            originalViewHolder.Vouchername.setText(c.getVouchername());
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

}
