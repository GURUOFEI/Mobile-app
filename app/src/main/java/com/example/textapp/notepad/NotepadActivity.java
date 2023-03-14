package com.example.textapp.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.textapp.R;
import com.example.textapp.notepad.adapter.NotepadAdapter;
import com.example.textapp.notepad.bean.NotepadBean;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.SharedPreUtil;

import java.util.List;

public class NotepadActivity extends AppCompatActivity {
    private ListView listView;
    private SQLiteHelper mSQLiteHelper;
    private List<NotepadBean> list;
    NotepadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listview);
        ImageView imageView=findViewById(R.id.add);
        initData();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(NotepadActivity.this,RecordActivity.class);
                startActivityForResult(intent,1);
            }
        });
        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(NotepadActivity.this,NewsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_upd_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotepadActivity.this, EditMineActivity.class));
            }
        });
    }
    public void initData(){
        mSQLiteHelper=new SQLiteHelper(this);
        showQueryData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent,View view,int position,long id ){
                NotepadBean notepadBean=list.get(position);
                Intent intent=new Intent(NotepadActivity.this,RecordActivity.class);
                intent.putExtra("id",notepadBean.getId());
                intent.putExtra("content",notepadBean.getNotepadContent());
                intent.putExtra("time",notepadBean.getNotepadTime());
                NotepadActivity.this.startActivityForResult(intent,1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(NotepadActivity.this)
                        .setMessage("是否删除此记录?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotepadBean notepadBean=list.get(position);
                                if(mSQLiteHelper.deleteData(notepadBean.getId())){
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(NotepadActivity.this,"删除成功", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog=builder.create();
                dialog.show();
                return true;
            }
        });
    }

    //查询数据库中存放的记录信息
    private void showQueryData(){
        if(list!=null){
            list.clear();
        }
        list=mSQLiteHelper.query((String) SharedPreUtil.getParam(NotepadActivity.this, SharedPreUtil.LOGIN_DATA,""));
        adapter=new NotepadAdapter(this,list);
        listView.setAdapter(adapter);
    }
    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==1&&resultCode==2){
            showQueryData();
        }
    }
}
