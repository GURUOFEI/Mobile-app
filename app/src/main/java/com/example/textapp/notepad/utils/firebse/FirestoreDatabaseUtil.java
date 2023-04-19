package com.example.textapp.notepad.utils.firebse;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

public class FirestoreDatabaseUtil {
    private volatile static FirestoreDatabaseUtil mInstance;
    private FirebaseFirestore db = null;

    private FirestoreDatabaseUtil() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirestoreDatabaseUtil getInstance() {
        if (mInstance == null) {
            synchronized (Singleton.class) {
                if (mInstance == null) {
                    mInstance = new FirestoreDatabaseUtil();
                }
            }
        }
        return mInstance;
    }

}
