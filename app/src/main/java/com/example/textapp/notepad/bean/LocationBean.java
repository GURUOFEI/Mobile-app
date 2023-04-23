package com.example.textapp.notepad.bean;

/**
 * 定位数据
 */
public class LocationBean {
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
}
