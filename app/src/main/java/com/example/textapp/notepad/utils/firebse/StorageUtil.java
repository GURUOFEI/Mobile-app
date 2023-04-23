package com.example.textapp.notepad.utils.firebse;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import javax.inject.Singleton;

/**
 * 文件保存工具类，用于文件的保存到云端
 */
public class StorageUtil {
    private volatile static StorageUtil mInstance;
    /**
     * 文件存储类
     */
    private FirebaseStorage storage;
    /**
     * 引用
     */
    private StorageReference storageRef;

    private final static String IMAGES = "images";


    private StorageUtil() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public static StorageUtil getInstance() {
        if (mInstance == null) {
            synchronized (Singleton.class) {
                if (mInstance == null) {
                    mInstance = new StorageUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 上传文件
     *
     * @param key
     * @return
     */
    public StorageReference getRef(String key,String fileName) {
        return storageRef.child(key).child(IMAGES + "/" + fileName);
    }

}
