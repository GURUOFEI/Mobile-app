package com.example.textapp.notepad;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textapp.R;
import com.example.textapp.notepad.bean.User;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.FirebaseUtil;
import com.example.textapp.notepad.utils.MD5Utils;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.ToastUtil;
import com.example.textapp.notepad.utils.firebse.RealtimeDatabaseUtil;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;


public class EditMineActivity extends AppCompatActivity {

    private EditText update_password, update_repassword;
    private TextView update_username;

    private TextView update_user;

    private ImageView iv_back;

    private SQLiteHelper dbHelper;

    //User user = User.getInstance();
    String username;
    //用户的uuid
    private String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mine);

        initView();

        dbHelper = new SQLiteHelper(this);

        username = (String) SharedPreUtil.getParam(EditMineActivity.this, SharedPreUtil.LOGIN_DATA, "");
        uuid = (String) SharedPreUtil.getParam(EditMineActivity.this, SharedPreUtil.LOGIN_UUID, "");
        update_username.setText(username);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
                //finish();
            }
        });
    }

    private void initView() {
        update_user = (TextView) findViewById(R.id.update_user);
        update_username = (TextView) findViewById(R.id.update_username);
        update_password = (EditText) findViewById(R.id.update_password);
        update_repassword = (EditText) findViewById(R.id.update_repassword);

    }

    private void updateUser() {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String update_username_str = update_username.getText().toString();
        String update_password_str = update_password.getText().toString();
        String update_repassword_str = update_repassword.getText().toString();


        if (TextUtils.isEmpty(update_password_str)) {
            ToastUtil.show(R.string.modify_password_enter_new_password);
            return;
        }

        if (TextUtils.isEmpty(update_repassword_str)) {
            ToastUtil.show(R.string.modify_password_enter_confirm_password);
            return;
        }

        if (!update_password_str.equals(update_repassword_str)) {
            ToastUtil.show(R.string.login_password_diff_tip);
            return;
        }

//        if (update_password_str.equals(update_repassword_str)) {
//            db.execSQL("update User set name = ?,password = ? where name = ?",
//                    new String[]{update_username_str, update_password_str, username});
//            Toast.makeText(EditMineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            Toast.makeText(EditMineActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
//        }

        //组装要更新的参数
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("password", MD5Utils.getMD5String(update_password_str));
        userMap.put("username", username);
        userMap.put("uuid", uuid);
        //组装更新的节点
        Map<String, Object> map = new HashMap<>();
        map.put("/" + RealtimeDatabaseUtil.USER_LIST + "/" + update_username_str, userMap);
        //更新实时数据库
        RealtimeDatabaseUtil.getInstance()
                .getReference()
                .updateChildren(map, (error, ref) -> {
                    ToastUtil.show(R.string.modify_success);
                    finish();
                });

    }

}
