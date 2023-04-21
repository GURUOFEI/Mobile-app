package com.example.textapp.notepad.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class NotepadBean implements Parcelable {
    private String id;
    private String notepadContent;//记录的内容
    private long notepadTime;//保存记录的时间
    private String notepadPhone;

    /**
     * 分类
     * -1. 没有分类
     * 0. person
     * 1. life
     * 2. work
     * 3. finance
     * 4. other
     */
    private int type = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public NotepadBean() {
    }

    public NotepadBean(String id, Map<String, Object> map) {
        this.id = id;
        this.notepadContent = map.get("notepadContent").toString();
        this.notepadPhone = map.get("notepadPhone").toString();
        this.notepadTime = Long.parseLong(map.get("notepadTime").toString());
        if(map.containsKey("type")) {
            this.type = Integer.parseInt(map.get("type").toString());
        }
    }

    public String getNotepadContent() {
        return notepadContent;
    }

    public void setNotepadContent(String notepadContent) {
        this.notepadContent = notepadContent;
    }

    public long getNotepadTime() {
        return notepadTime;
    }

    public void setNotepadTime(long notepadTime) {
        this.notepadTime = notepadTime;
    }

    public String getNotepadPhone() {
        return notepadPhone;
    }

    public void setNotepadPhone(String notepadPhone) {
        this.notepadPhone = notepadPhone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.notepadContent);
        dest.writeLong(this.notepadTime);
        dest.writeString(this.notepadPhone);
        dest.writeInt(this.type);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.notepadContent = source.readString();
        this.notepadTime = source.readLong();
        this.notepadPhone = source.readString();
        this.type = source.readInt();
    }

    protected NotepadBean(Parcel in) {
        this.id = in.readString();
        this.notepadContent = in.readString();
        this.notepadTime = in.readLong();
        this.notepadPhone = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<NotepadBean> CREATOR = new Creator<NotepadBean>() {
        @Override
        public NotepadBean createFromParcel(Parcel source) {
            return new NotepadBean(source);
        }

        @Override
        public NotepadBean[] newArray(int size) {
            return new NotepadBean[size];
        }
    };
}
