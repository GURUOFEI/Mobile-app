package com.example.textapp.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textapp.R;
import com.example.textapp.notepad.bean.NotepadBean;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.DateUtil;
import com.example.textapp.notepad.utils.LogUtil;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.ToastUtil;
import com.example.textapp.notepad.utils.firebse.FirestoreDatabaseUtil;

import java.util.HashMap;
import java.util.Map;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView note_back;
    TextView note_time;
    EditText content;
    ImageView delete;
    ImageView note_save;
    TextView noteName;
    ;
    private SQLiteHelper mSQLiteHelper;
    //    private String id;
    private NotepadBean notepadBean;

    /**
     * 当前用户的uuid
     */
    private String uuid;

    public final static String KEY_NOTEPAD = "key_notepad";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        note_back = (ImageView) findViewById(R.id.note_back);//后退键
        note_time = (TextView) findViewById(R.id.tv_time);//保存记录的时间
        content = (EditText) findViewById(R.id.note_content);//记录的内容
        delete = (ImageView) findViewById(R.id.delete);//清空的按钮
        note_save = (ImageView) findViewById(R.id.note_save);//保存的按钮
        noteName = (TextView) findViewById(R.id.note_name);//标题的名称
        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        initData();


    }

    public void initData() {
        uuid = (String) SharedPreUtil.getParam(RecordActivity.this, SharedPreUtil.LOGIN_UUID, "");

        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText("添加记录");
        Intent intent = getIntent();
        if (intent != null) {
            notepadBean = intent.getParcelableExtra(KEY_NOTEPAD);
            if (notepadBean != null) {
                noteName.setText("修改记录");
                content.setText(notepadBean.getNotepadContent());
                note_time.setText(DateUtil.formate(notepadBean.getNotepadTime()));
                note_time.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note_back:
                finish();
                break;
            case R.id.delete:
                content.setText(" ");
                break;
            case R.id.note_save:
                String noteContent = content.getText().toString().trim();
                if (notepadBean != null) {
                    //修改记录的功能
                    if (noteContent.length() > 0) {
                        LogUtil.d("修改");
//                        if (mSQLiteHelper.updateData(id, noteContent, DBUtils.getTime())) {
//                            showToast("修改成功");
//                            setResult(2);
//                            finish();
//                        } else {
//                            showToast("修改失败");
//                        }

                        //更新的内容
                        Map<String, Object> updateContent = new HashMap<>();
                        updateContent.put("notepadContent", noteContent);//内容
                        updateContent.put("notepadTime", System.currentTimeMillis());//更新时间

                        // Add a new document with a generated ID
                        FirestoreDatabaseUtil.getInstance()
                                .getUserNotebook(uuid)
                                .document(notepadBean.getId())
                                .update(updateContent)
                                .addOnSuccessListener(unused -> {
                                    ToastUtil.show(R.string.modify_success);
                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                });
                    } else {
                        ToastUtil.show(R.string.edit_content_modify_tip);
                    }
                } else {
                    //添加记录的功能
                    if (noteContent.length() > 0) {
                        LogUtil.d("添加");
//                        if (mSQLiteHelper.insertData(noteContent,DBUtils.getTime(), (String) SharedPreUtil.getParam(RecordActivity.this, SharedPreUtil.LOGIN_DATA,""))){
//                            showToast("保存成功");
//                            setResult(2);
//                            finish();
//                        }else{
//                            showToast("保存失败");
//                        }
                        NotepadBean notepadBean = new NotepadBean();
                        notepadBean.setNotepadContent(noteContent);
                        notepadBean.setNotepadTime(System.currentTimeMillis());
                        notepadBean.setNotepadPhone((String) SharedPreUtil.getParam(RecordActivity.this, SharedPreUtil.LOGIN_DATA, ""));

                        // Add a new document with a generated ID
                        FirestoreDatabaseUtil.getInstance()
                                .getUserNotebook(uuid)
                                .add(notepadBean)
                                .addOnSuccessListener(unused -> {
                                    ToastUtil.show(R.string.save_success);
                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                });

                    } else {
                        ToastUtil.show(R.string.edit_content_save_tip);
                    }
                }
                break;
            default:
                break;
        }
    }

}
