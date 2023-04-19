package com.example.textapp.notepad.utils.firebse;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

public class RealtimeDatabaseUtil {
    private volatile static RealtimeDatabaseUtil mInstance;
    private FirebaseDatabase database = null;
    private DatabaseReference userRef;

    public DatabaseReference getUserRef() {
        return userRef;
    }

    private RealtimeDatabaseUtil() {
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("user_list");
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

}
