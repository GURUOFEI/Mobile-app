package com.example.textapp.notepad.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textapp.R;
import com.example.textapp.notepad.bean.User;
import com.example.textapp.notepad.database.SQLiteHelper;
import com.example.textapp.notepad.utils.AlbumUtil;
import com.example.textapp.notepad.utils.MD5Utils;
import com.example.textapp.notepad.utils.ToastUtil;
import com.example.textapp.notepad.utils.firebse.RealtimeDatabaseUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends BaseActivity {

    private SQLiteHelper dbHelper;

    private TextView save_user;
    private ImageView shangchuan_head, iv_back;
    private EditText username, userpassword, repassword;
    private CheckBox checkBox;

    private static final int CHOSSE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new SQLiteHelper(this);

        save_user = (TextView) findViewById(R.id.save_user);
        shangchuan_head = (ImageView) findViewById(R.id.shangchuan_head);
        iv_back = (ImageView) findViewById(R.id.iv_back);

        username = (EditText) findViewById(R.id.register_username);
        userpassword = (EditText) findViewById(R.id.register_password);
        repassword = (EditText) findViewById(R.id.register_repassword);
        checkBox = (CheckBox) findViewById(R.id.checkbox_tiaokuan);

        shangchuan_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        save_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                String username_str = username.getText().toString();
                String userpassword_str = userpassword.getText().toString();
                String repassword_str = repassword.getText().toString();

                if (TextUtils.isEmpty(username_str)) {
                    ToastUtil.show(R.string.login_enter_username_tip);
                    return;
                }

                if (TextUtils.isEmpty(userpassword_str)) {
                    ToastUtil.show(R.string.login_enter_password_tip);
                    return;
                }

                if (TextUtils.isEmpty(repassword_str)) {
                    ToastUtil.show(R.string.login_enter_password_tip2);
                    return;
                }

                if (!userpassword_str.equals(repassword_str)) {
                    ToastUtil.show(R.string.login_password_diff_tip);
                    return;
                }

                if (!checkBox.isChecked()) {
                    ToastUtil.show(R.string.register_checkbox_tip);
                    return;
                }

//                        ContentValues values = new ContentValues();
//                        //组装数据
//                        values.put("name", username_str);
//                        values.put("password", userpassword_str);
//
//                        db.insert("User", null, values);

                //用户信息，注意密码需要MD5加密
                User user = new User(UUID.randomUUID().toString(), username_str, MD5Utils.getMD5String(userpassword_str));
                showLoadingDialog(R.string.registering);
                RealtimeDatabaseUtil.getInstance()
                        .getUserRef(user.getName())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //判断该用户是否已经注册
                                if (task.getResult().getValue() == null) {
                                    //没有注册，把数据输入到服务器
                                    RealtimeDatabaseUtil.getInstance()
                                            .getUserRef(user.getName())
                                            .setValue(user)
                                            .addOnSuccessListener(unused -> {
                                                ToastUtil.show(R.string.register_success);
                                                //返回登录页面
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }).addOnFailureListener(e -> ToastUtil.show(R.string.register_failure));
                                } else {
                                    ToastUtil.show(R.string.register_user_exist);
                                }
                            }
                            dismissLoadingDialog();
                        });
            }
//                    db.close();
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOSSE_PHOTO);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOSSE_PHOTO:
                if (resultCode == -1) {
                    String imgPath = AlbumUtil.handleImageOnKitKat(this, data);
                    setHead(imgPath);
                }
                break;
            default:
                break;
        }
    }

    private void setHead(String imgPath) {
        if (imgPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            Bitmap round = AlbumUtil.toRoundBitmap(bitmap);
            try {
                String path = getCacheDir().getPath();
                File file = new File(path, "user_head");
                round.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            shangchuan_head.setImageBitmap(round);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
