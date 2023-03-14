package com.example.textapp.notepad;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textapp.R;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.SharedPreUtil;

import androidx.appcompat.app.AppCompatActivity;


public class EditMineActivity extends AppCompatActivity {

    private EditText update_username, update_password, update_repassword;

    private TextView update_user;

    private ImageView iv_back;

    private SQLiteHelper dbHelper;

    //User user = User.getInstance();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mine);

        initView();

        dbHelper = new SQLiteHelper(this);

        username = (String) SharedPreUtil.getParam(EditMineActivity.this, SharedPreUtil.LOGIN_DATA, "");
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
        update_user =(TextView) findViewById(R.id.update_user);
        update_username = (EditText) findViewById(R.id.update_username);
        update_password = (EditText) findViewById(R.id.update_password);
        update_repassword = (EditText) findViewById(R.id.update_repassword);

    }

    private void updateUser() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String update_username_str = update_username.getText().toString();
        String update_password_str = update_password.getText().toString();
        String update_repassword_str = update_repassword.getText().toString();

        if (update_password_str.equals(update_repassword_str)) {
            db.execSQL("update User set name = ?,password = ? where name = ?",
                    new String[]{update_username_str, update_password_str, username});
            Toast.makeText(EditMineActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(EditMineActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
        }

    }

}
