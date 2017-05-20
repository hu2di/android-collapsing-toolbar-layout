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
import com.blogspot.hu2di.mybrowser.model.GoogleNews;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by HUNGDH on 5/17/2017.
 */

public class RVAdapterNews extends RecyclerView.Adapter<RVAdapterNews.ViewHolder> {

    private Context context;
    private ArrayList<GoogleNews> googleNewsArrayList;

    public RVAdapterNews(Context context, ArrayList<GoogleNews> list) {
        this.context = context;
        this.googleNewsArrayList = list;
    }

    @Override
    public RVAdapterNews.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hd_row_news_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RVAdapterNews.ViewHolder holder, int position) {
        holder.llNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.tvTitle.setText(googleNewsArrayList.get(position).getNewsTitle());
        holder.tvDate.setText(googleNewsArrayList.get(position).getPublishDate());

        Glide.with(context)
                .load(googleNewsArrayList.get(position).getDescription())
                .crossFade()
                .centerCrop()
                .into(holder.imvIcon);
    }

    @Override
    public int getItemCount() {
        return googleNewsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llNews;
        public ImageView imvIcon;
        public TextView tvTitle, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            llNews = (LinearLayout) itemView.findViewById(R.id.llNews);
            imvIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            tvTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            tvDate = (TextView) itemView.findViewById(R.id.textViewDate);
        }
    }
}

