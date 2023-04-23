package com.example.textapp.notepad.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * 文件操作工具类
 */
public class FileUtil {
    /**
     * 根据uri获取真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        String filePath = "";
        String scheme = uri.getScheme();
        if (scheme == null)
            filePath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            filePath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            if (TextUtils.isEmpty(filePath)) {
                filePath = getFilePathForNonMediaUri(context, uri);
            }
        }
        return filePath;
    }

    /**
     * 非媒体文件中查找
     *
     * @param context
     * @param uri
     * @return
     */
    private static String getFilePathForNonMediaUri(Context context, Uri uri) {
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow("_data");
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }
    /**
     * 创建指定文件
     *
     * @return
     */
    public static String createJpgFile() {
        File file = new File(Environment.getExternalStorageDirectory() + "/mysport");
        if (!file.exists()) {
            file.mkdirs();
        }
        String savePath = Environment.getExternalStorageDirectory() + "/mysport/" + System.currentTimeMillis() + ".jpg";

        LogUtil.d("创建文件:" + savePath);

        return savePath;
    }
}
