package com.example.textapp.notepad.activity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.textapp.R;

/**
 * 查看照片页面
 */
public class PhotoActivity extends BaseActivity {
    //显示的照片
    private ImageView iv_photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo);
        //绑定控件
        iv_photo = findViewById(R.id.iv_photo);

        //获取传进来的照片ip
        String url = getIntent().getStringExtra("photo_url");
        //显示图片
        Glide.with(this)
                .load(url)
                .centerInside()
                .placeholder(R.drawable.baseline_insert_photo_24)
                .into(iv_photo);
    }
}
