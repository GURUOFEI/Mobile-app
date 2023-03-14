package com.example.textapp.notepad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.textapp.R;
import com.example.textapp.notepad.bean.NewsBean;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private List<NewsBean> newsBeans;
    private Context mContext;

    public NewsAdapter(List<NewsBean> newsBeans, Context mContext){
        this.newsBeans = newsBeans;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        return newsBeans == null?0:newsBeans.size();
    }

    @Override
    public Object getItem(int i) {
        return newsBeans.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder =null;
        if(view==null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_news_layout,
                    null,false);
            viewHolder.tvTitle = view.findViewById(R.id.tv_title);
            viewHolder.imageView = view.findViewById(R.id.iv_head);
            viewHolder.tvSource = view.findViewById(R.id.tv_source);
            viewHolder.tvTime = view.findViewById(R.id.tv_time);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.tvTitle.setText(newsBeans.get(i).getTitle());
        viewHolder.tvSource.setText(newsBeans.get(i).getAnthor_name());
        viewHolder.tvTime.setText(newsBeans.get(i).getDate());
        Glide.with(mContext).load(newsBeans.get(i).getThumbnail_pic_s()).into(viewHolder.imageView);
        return view;
    }

    class ViewHolder{
        public ImageView imageView;
        public TextView tvTitle;
        public TextView tvSource;
        public TextView tvTime;
    }
}
