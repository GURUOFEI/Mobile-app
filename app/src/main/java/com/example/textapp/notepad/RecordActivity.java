package com.example.textapp.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.textapp.R;
import com.example.textapp.notepad.bean.NotepadBean;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.DBUtils;
import com.example.textapp.notepad.utils.LogUtil;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.ToastUtil;
import com.example.textapp.notepad.utils.firebse.FirestoreDatabaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private String id;

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
        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText("添加记录");
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            if (id != null) {
                noteName.setText("修改记录");
                content.setText(intent.getStringExtra("content"));
                note_time.setText(intent.getStringExtra("time"));
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
                if (id != null) {
                    //修改记录的功能
                    if (noteContent.length() > 0) {
                        if (mSQLiteHelper.updateData(id, noteContent, DBUtils.getTime())) {
                            showToast("修改成功");
                            setResult(2);
                            finish();
                        } else {
                            showToast("修改失败");
                        }
                    } else {
                        showToast("修改的记录内容不能为空");
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
                        notepadBean.setNotepadContent("content");
                        notepadBean.setNotepadTime(System.currentTimeMillis() + "");
                        notepadBean.setNotepadPhone("13800138000");

                        // Add a new document with a generated ID
                        FirestoreDatabaseUtil.getInstance()
                                .getNotebook()
                                .add(notepadBean)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        LogUtil.d("保存成功");
                                        ToastUtil.show("保存成功");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        LogUtil.d("保存失败");
                                        ToastUtil.show("保存失败，请重试");
                                    }
                                });

                    } else {
                        showToast("保存的记录内容不能为空");
                    }
                }
                break;
            default:
                break;
        }
    }

    public void showToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_LONG).show();
    }

}
