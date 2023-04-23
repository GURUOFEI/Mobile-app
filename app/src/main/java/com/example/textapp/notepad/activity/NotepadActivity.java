package com.example.textapp.notepad.activity;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.textapp.R;
import com.example.textapp.notepad.adapter.NotepadAdapter;
import com.example.textapp.notepad.bean.NotepadBean;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.LogUtil;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.firebse.FirestoreDatabaseUtil;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotepadActivity extends BaseActivity {
    private ListView listView;
    //    private SQLiteHelper mSQLiteHelper;
    private List<NotepadBean> list;
    NotepadAdapter adapter;

    private TextView note_name;

    /**
     * 当前用户的uuid
     */
    private String uuid;

    /**
     * 当前列表分类
     * -1. all
     * 0. person
     * 1. life
     * 2. work
     * 3. finance
     * 4. other
     */
    private int type = -1;

    private PopupWindow menuPopupWindow;
    private RelativeLayout rl_title;

    private String[] typeArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        ImageView imageView = findViewById(R.id.add);
        initData();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotepadActivity.this, RecordActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotepadActivity.this, NewsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_upd_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NotepadActivity.this, EditMineActivity.class));
            }
        });

        //标题相关
        note_name = findViewById(R.id.note_name);
        rl_title = findViewById(R.id.rl_title);
        note_name.setOnClickListener(v -> showType());
        chooseType(-1);
    }

    /**
     * 显示分类选择菜单
     */
    private void showType() {
        if (menuPopupWindow == null) {
            View view = getLayoutInflater().inflate(R.layout.type_menu, null);
            TextView tv_all = (TextView) view.findViewById(R.id.tv_all);
            tv_all.setOnClickListener(v -> chooseType(-1));
            TextView tv_person = (TextView) view.findViewById(R.id.tv_person);
            tv_person.setOnClickListener(v -> chooseType(0));
            TextView tv_life = (TextView) view.findViewById(R.id.tv_life);
            tv_life.setOnClickListener(v -> chooseType(1));
            TextView tv_work = (TextView) view.findViewById(R.id.tv_work);
            tv_work.setOnClickListener(v -> chooseType(2));
            TextView tv_finance = (TextView) view.findViewById(R.id.tv_finance);
            tv_finance.setOnClickListener(v -> chooseType(3));
            TextView tv_other = (TextView) view.findViewById(R.id.tv_other);
            tv_other.setOnClickListener(v -> chooseType(4));

            menuPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            menuPopupWindow.setOutsideTouchable(true);
            menuPopupWindow.setAnimationStyle(R.style.popmenu_animation);
        }
        if (menuPopupWindow.isShowing()) {
            menuPopupWindow.dismiss();
        } else {
            menuPopupWindow.showAsDropDown(note_name);
        }
    }

    /**
     * 选择分类
     */
    private void chooseType(int chooseType) {
        //根据传入的分类显示标题
        String typeStr = getString(R.string.type_all);
        this.type = chooseType;
        if (chooseType != -1) {
            typeStr = typeArray[type];
        }
        //设置标题
        note_name.setText(String.format(getString(R.string.title), typeStr));
        //隐藏菜单
        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
        }
        getData();
    }

    public void initData() {
        uuid = (String) SharedPreUtil.getParam(NotepadActivity.this, SharedPreUtil.LOGIN_UUID, "");
        //读取分类数据
        typeArray = getResources().getStringArray(R.array.noteboot_type);

//        mSQLiteHelper = new SQLiteHelper(this);
        showQueryData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotepadBean notepadBean = list.get(position);
                Intent intent = new Intent(NotepadActivity.this, RecordActivity.class);
//                intent.putExtra("id",notepadBean.getId());
                intent.putExtra(RecordActivity.KEY_NOTEPAD, notepadBean);
                NotepadActivity.this.startActivityForResult(intent, 1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(NotepadActivity.this)
                        .setMessage("是否删除此记录?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NotepadBean notepadBean = list.get(position);
                                //TODO 删除
//                                if(mSQLiteHelper.deleteData(notepadBean.getId())){
//                                    list.remove(position);
//                                    adapter.notifyDataSetChanged();
//                                    Toast.makeText(NotepadActivity.this,"删除成功", Toast.LENGTH_LONG).show();
//                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    //查询数据库中存放的记录信息
    private void showQueryData() {
        if (list != null) {
            list.clear();
        } else {
            list = new ArrayList<>();
        }
        adapter = new NotepadAdapter(this, list);
        listView.setAdapter(adapter);

        //这里改为查询Firestore Database
//        list=mSQLiteHelper.query((String) SharedPreUtil.getParam(NotepadActivity.this, SharedPreUtil.LOGIN_DATA,""));
        //添加一个监听
        getData();
    }

    private void getData() {
        FirestoreDatabaseUtil.getInstance()
                .getUserNotebook(uuid)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }

                    list.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        //重新添加数据
                        NotepadBean notepadBean = new NotepadBean(doc.getId(), doc.getData());
                        //判断是否选择了分类,-1表示显示全部
                        if (type != -1) {
                            //只显示该分类
                            if (notepadBean.getType() == type) {
                                list.add(notepadBean);
                            }
                        } else {
                            //显示全部
                            list.add(notepadBean);
                        }
                    }
                    //刷新列表
                    adapter.notifyDataSetChanged();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            showQueryData();
        }
    }
}
