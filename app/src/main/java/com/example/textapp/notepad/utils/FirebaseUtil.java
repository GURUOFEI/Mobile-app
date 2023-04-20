package com.example.textapp.notepad.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

public class FirebaseUtil {
    private volatile static FirebaseUtil mInstance;
    private FirebaseDatabase database = null;
    private DatabaseReference userRef;


    private FirebaseUtil() {
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("user_list");
    }

    public static FirebaseUtil getInstance() {
        if (mInstance == null) {
            synchronized (Singleton.class) {
                if (mInstance == null) {
                    mInstance = new FirebaseUtil();
                }
            }
        }
        return mInstance;
    }

    public DatabaseReference getUserRef(String key) {
        return userRef.child(key);
    }

}
