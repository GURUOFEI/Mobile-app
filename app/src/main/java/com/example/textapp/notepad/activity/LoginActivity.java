package com.example.textapp.notepad.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textapp.R;
import com.example.textapp.notepad.APP;
import com.example.textapp.notepad.bean.User;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.MD5Utils;
import com.example.textapp.notepad.utils.SharedPreUtil;
import com.example.textapp.notepad.utils.TimeCount;
import com.example.textapp.notepad.utils.ToastUtil;
import com.example.textapp.notepad.utils.firebse.RealtimeDatabaseUtil;

import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends BaseActivity {
    private SQLiteHelper dbHelper;
    private Button check_user;
    private EditText username, userpassword;
    //    private ImageView login_head;
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

//        //TODO Debug模式，自己把用户名和密码填上，方便测试
//        if (APP.isDebut) {
//            username.setText("user");
//            userpassword.setText("123456");
//        }

//        login_head = (ImageView) findViewById(R.id.login_head);

        register_user = (TextView) findViewById(R.id.register_user);

//        login_head.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.head));

        check_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String username_str = username.getText().toString().trim();
                String userpassword_str = userpassword.getText().toString().trim();

                if (TextUtils.isEmpty(username_str)) {
                    ToastUtil.show(R.string.login_enter_username_tip);
                    return;
                }

                if (TextUtils.isEmpty(userpassword_str)) {
                    ToastUtil.show(R.string.login_enter_password_tip);
                    return;
                }
                //提交到Firebase的Realtime Database
                showLoadingDialog(R.string.logining);
                RealtimeDatabaseUtil.getInstance()
                        .getUserRef(username_str)
                        .get()
                        .addOnCompleteListener(task -> {//回调监听
                            if (task.isSuccessful()) {
                                //获取数据并转换成实体
                                User user = task.getResult().getValue(User.class);
                                //判断该用户是否已经注册
                                if (user == null) {
                                    ToastUtil.show(R.string.login_no_user);
                                } else {
                                    //比较密码是否一致
                                    if (MD5Utils.getMD5String(userpassword_str).equals(user.getPassword())) {
                                        SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.IS_LOGIN, true);
                                        SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_DATA, username_str);
                                        //把UUID保存到配置文件
                                        SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_UUID, user.getUuid());
                                        SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_PWD_DATA, userpassword_str);
                                        //user.setUsername(username_str);
                                        //user.setPassword(userpassword_str);
                                        //跳转到主页面
                                        Intent intent = new Intent(LoginActivity.this, NotepadActivity.class);
                                        TimeCount.getInstance().setTime(System.currentTimeMillis());
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        ToastUtil.show(R.string.login_error_password);
                                    }
                                }
                            }
                            dismissLoadingDialog();
                        });

//                Cursor cursor = db.rawQuery("select * from User where name=?", new String[]{username_str});
//                if (cursor.getCount() == 0) {
//                    Toast.makeText(LoginActivity.this, "用户名不存在！", Toast.LENGTH_SHORT).show();
//                } else {
//                    if (cursor.moveToFirst()) {
//                        String userpassword_db = cursor.getString(cursor.getColumnIndex("password"));
//                        if (userpassword_str.equals(userpassword_db)) {
//                            SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.IS_LOGIN, true);
//                            SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_DATA, username_str);
//                            SharedPreUtil.setParam(LoginActivity.this, SharedPreUtil.LOGIN_PWD_DATA, userpassword_str);
//                            //user.setUsername(username_str);
//                            //user.setPassword(userpassword_str);
//                            Intent intent = new Intent(LoginActivity.this, NotepadActivity.class);
//                            TimeCount.getInstance().setTime(System.currentTimeMillis());
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(LoginActivity.this, "密码错误，请重新登录", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                cursor.close();
//                db.close();
            }
        });

        register_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        initPermissions();

    }


    /**
     * 需要申请的权限
     */
    private String[] perms = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
            Manifest.permission.READ_EXTERNAL_STORAGE, //存储权限
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,//存储权限
            Manifest.permission.ACCESS_FINE_LOCATION,//定位权限
            Manifest.permission.CAMERA//相册权限
    };

    /**
     * 判断是否需要申请权限
     */
    private void initPermissions() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_tip), 1001, perms);
        }
        requestmanageexternalstoragePermission();
    }

    /** 权限回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 权限申请
     */
    private void requestmanageexternalstoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Android VERSION  R OR ABOVE，NO MANAGE_EXTERNAL_STORAGE GRANTED!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, 1002);
            }
        }
    }
}
