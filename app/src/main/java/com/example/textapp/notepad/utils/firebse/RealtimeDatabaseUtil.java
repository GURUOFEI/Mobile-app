package com.example.textapp.notepad.utils.firebse;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

/**
 * 实时数据库工具，用于登录、注册
 */
public class RealtimeDatabaseUtil {
    private volatile static RealtimeDatabaseUtil mInstance;
    private FirebaseDatabase database = null;
    private DatabaseReference userRef;

    public final static String USER_LIST = "user_list";

    private RealtimeDatabaseUtil() {
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child(USER_LIST);
    }

    public static RealtimeDatabaseUtil getInstance() {
        if (mInstance == null) {
            synchronized (Singleton.class) {
                if (mInstance == null) {
                    mInstance = new RealtimeDatabaseUtil();
                }
            }
        }
        return mInstance;
    }

    public DatabaseReference getUserRef(String key) {
        return userRef.child(key);
    }

    public DatabaseReference getReference(){
        return database.getReference();
    }

}
