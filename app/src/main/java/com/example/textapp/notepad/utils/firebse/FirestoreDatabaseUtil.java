package com.example.textapp.notepad.utils.firebse;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

/**
 * 数据库工具，用户笔记的保存到云端
 */
public class FirestoreDatabaseUtil {
    private volatile static FirestoreDatabaseUtil mInstance;
    private FirebaseFirestore db = null;

    private final static String DB_NOTEBOOK = "db_notebook";
    public final static String USER_NOTEBOOKD = "user_notebook";

    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * 根据UUID获取key=USER_NOTEBOOKD的集合
     *
     * @param uuid
     * @return
     */
    public CollectionReference
    getUserNotebook(String uuid) {
        return db.collection(DB_NOTEBOOK).document(uuid).collection(USER_NOTEBOOKD);
    }

    /**
     * 根据UUID获取document
     *
     * @param uuid
     * @return
     */
    public DocumentReference getDocument(String uuid) {
        return db.collection(DB_NOTEBOOK).document(uuid);
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
