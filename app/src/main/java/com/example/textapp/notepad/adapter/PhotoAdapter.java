package com.example.textapp.notepad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.textapp.R;

import java.util.List;


/**
 * blacklist adapter
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<String> lists;


    private Callback callback;

    /**
     * @param callback
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private PhotoAdapter() {
    }

    /**
     * @param data
     */
    public PhotoAdapter(List<String> data) {
        this.lists = data;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_photo_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.iv_delete.setVisibility(View.GONE);
        if (position == lists.size() - 1) {
            //添加图片按钮
            holder.iv_photo.setImageResource(R.drawable.add_photo);
            holder.iv_photo.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onItemClick(position);
                }
            });
        } else {
            holder.iv_delete.setVisibility(View.VISIBLE);
            //显示图片
            Glide.with(holder.itemView.getContext())
                    .load(lists.get(position))
                    .centerCrop()
                    .placeholder(R.drawable.baseline_insert_photo_24)
                    .into(holder.iv_photo);
            holder.iv_photo.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onItemClick(position);
                }
            });
            holder.iv_delete.setOnClickListener(v -> {
                if (callback != null) {
                    callback.delete(position);
                }
            });
        }

    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return lists != null ? lists.size() : 0;
    }

    /**
     * Item VieHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 图片
         */
        private ImageView iv_photo;
        /**
         * 删除按钮
         */
        private ImageView iv_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }

    /**
     * Event Callback
     */
    public interface Callback {
        /**
         * Item Click
         *
         * @param position
         */
        void onItemClick(int position);

        /**
         * 删除指定position的图片
         *
         * @param position
         */
        void delete(int position);
    }

}
