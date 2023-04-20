package com.example.textapp.notepad.utils.firebse;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

public class FirestoreDatabaseUtil {
    private volatile static FirestoreDatabaseUtil mInstance;
    private FirebaseFirestore db = null;

    private final static String DB_NOTEBOOK = "db_notebook";

    public CollectionReference getNotebook() {
        return db.collection(DB_NOTEBOOK);
    }

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
