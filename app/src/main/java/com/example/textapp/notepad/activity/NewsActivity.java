package com.example.textapp.notepad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.textapp.R;
import com.example.textapp.notepad.adapter.NewsAdapter;
import com.example.textapp.notepad.bean.NewsBean;
import com.example.textapp.notepad.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//http://v.juhe.cn/toutiao/index?type=%s&key=e634bbb5538e8de173f8c1dbe0988f16
/**
 * 注意，网络请求需要给权限NetDemo
 */
public class NewsActivity extends Activity {

    private String NEWS_URL = "http://v.juhe.cn/toutiao/index?type=%s&key=e634bbb5538e8de173f8c1dbe0988f16";
    private static final String NEWS_TYPE="top";
    ListView lvNews;
    private List<NewsBean> newsBeanList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsBeanList = new ArrayList<>();
        lvNews = findViewById(R.id.lv_news);
        //利用通配符动态的替换字符串里面的值
        NEWS_URL = String.format(NEWS_URL, NEWS_TYPE);
        //调用工具类实现联网请求
        HttpUtil.getUrl2Net(this, NEWS_URL, new HttpUtil.OnHttpRepsonLinstener() {
            @Override
            public void onGetString(String json) {
                parsonJson(json);
                initView();
            }
        });
    }

    /**
     * 将解析完成获得的数据源于适配器绑定
     */
    private void initView(){
        NewsAdapter newsAdapter = new NewsAdapter(newsBeanList,this);
        lvNews.setAdapter(newsAdapter);
    }
    /**
     * 进行json数据解析
     */
    private void parsonJson(String json){
        //如何解析Json，你需要去观察串的结构
        //json的表像是利用key-value这种格式保存数据，因此冒号左边是key（String），右边是value
        //你需要层层剥皮
        //vlaue的状态三种：一种是大括号{}，这个还是JSON对象需要继续解析；
        // []中括号是数组；
        // 直接一个字符串值，这个就是value的最终数据
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject resultObj = jsonObject.getJSONObject("result");
            JSONArray jsonArray = resultObj.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemObj = jsonArray.getJSONObject(i);
                NewsBean newsBean = new NewsBean();
                newsBean.setAnthor_name(itemObj.getString("author_name"));
                newsBean.setCategory(itemObj.getString("category"));
                newsBean.setDate(itemObj.getString("date"));
                newsBean.setUniquekey(itemObj.getString("uniquekey"));
                newsBean.setIs_content(itemObj.getInt("is_content"));
                newsBean.setTitle(itemObj.getString("title"));
                newsBean.setUrl(itemObj.getString("url"));
                newsBean.setThumbnail_pic_s(itemObj.getString("thumbnail_pic_s"));
                newsBeanList.add(newsBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
