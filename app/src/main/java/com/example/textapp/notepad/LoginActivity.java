package com.example.textapp.notepad;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textapp.R;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.TimeCount;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private SQLiteHelper dbHelper;
    private Button check_user;
    private EditText username, userpassword;
    private ImageView login_head;
    private TextView register_user;

    //加载User实例
    //User user = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        dbHelper = new SQLiteHelper(this);

        check_user = (Button) findViewById(R.id.check_user);

        username = (EditText) findViewById(R.id.login_username);
        userpassword = (EditText) findViewById(R.id.login_password);

        login_head = (ImageView) findViewById(R.id.login_head);

        register_user = (TextView) findViewById(R.id.register_user);

        login_head.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.head));

        check_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String username_str = username.getText().toString();
                String userpassword_str = userpassword.getText().toString();

                Cursor cursor = db.rawQuery("select * from User where name=?", new String[]{username_str});
                if (cursor.getCount() == 0) {
                    Toast.makeText(LoginActivity.this, "用户名不存在！", Toast.LENGTH_SHORT).show();
                } else {
                    if (cursor.moveToFirst()) {
                        String userpassword_db = cursor.getString(cursor.getColumnIndex("password"));
                        if (userpassword_str.equals(userpassword_db)) {
                            SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.IS_LOGIN, true);
                            SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_DATA, username_str);
                            SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_PWD_DATA, userpassword_str);
                            //user.setUsername(username_str);
                            //user.setPassword(userpassword_str);
                            Intent intent = new Intent(LoginActivity.this, NotepadActivity.class);
                            TimeCount.getInstance().setTime(System.currentTimeMillis());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "密码错误，请重新登录", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                cursor.close();
                db.close();
            }
        });

        register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}
