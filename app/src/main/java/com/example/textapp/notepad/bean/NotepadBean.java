package com.example.textapp.notepad.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.textapp.notepad.utils.LogUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotepadBean implements Parcelable {
    private String id;
    private String notepadContent;//记录的内容
    private long notepadTime;//保存记录的时间
    private String notepadPhone;
    /**
     * 数据路径，多个，用","分隔
     */
    private String photos;

    /**
     * 定位名称
     */
    private String locationPlaceName;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;


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
        //图片
        if (map.containsKey("photos")) {
            this.photos = Objects.requireNonNull(map.get("photos")).toString();
        }
        //分类
        if (map.containsKey("type")) {
            this.type = Integer.parseInt(map.get("type").toString());
        }
        //定位
        if (map.containsKey("locationPlaceName")) {
            if(map.get("locationPlaceName")!=null) {
                this.locationPlaceName = Objects.requireNonNull(map.get("locationPlaceName")).toString();
                this.latitude = (double) map.get("latitude");
                this.longitude = (double) map.get("longitude");
            }
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

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocationPlaceName() {
        return locationPlaceName;
    }

    public void setLocationPlaceName(String locationPlaceName) {
        this.locationPlaceName = locationPlaceName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
        dest.writeString(this.photos);
        dest.writeString(this.locationPlaceName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.type);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.notepadContent = source.readString();
        this.notepadTime = source.readLong();
        this.notepadPhone = source.readString();
        this.photos = source.readString();
        this.locationPlaceName = source.readString();
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.type = source.readInt();
    }

    protected NotepadBean(Parcel in) {
        this.id = in.readString();
        this.notepadContent = in.readString();
        this.notepadTime = in.readLong();
        this.notepadPhone = in.readString();
        this.photos = in.readString();
        this.locationPlaceName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
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
