package com.gomtel.util;

import java.io.Serializable;

/**
 * Created by lixiang on 15-12-1.
 */
public class PositionInfo implements Serializable {
    private double lon;
    private double lat;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }



}
