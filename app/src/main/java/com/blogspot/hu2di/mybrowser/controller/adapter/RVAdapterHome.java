package com.blogspot.hu2di.mybrowser.controller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.hu2di.mybrowser.R;
import com.blogspot.hu2di.mybrowser.model.HomeItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by HUNGDH on 5/17/2017.
 */

public class RVAdapterHome extends RecyclerView.Adapter<RVAdapterHome.ViewHolder> {

    private Context context;
    private ArrayList<HomeItem> list;

    public RVAdapterHome(Context context, ArrayList<HomeItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RVAdapterHome.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hd_row_home_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RVAdapterHome.ViewHolder holder, int position) {
        holder.llHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Glide.with(context).load(list.get(position).getIcon()).centerCrop().crossFade().into(holder.ivIcon);
        holder.tvTitle.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llHome;
        public ImageView ivIcon;
        public TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            llHome = (LinearLayout) itemView.findViewById(R.id.llHome);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}