package com.example.common.model;

import com.google.gson.annotations.SerializedName;

public class YoulaGeoLocation extends YoulaGeoPoint {
    @SerializedName("accuracy")
    private float accuracy;

    @SerializedName("bearing")
    private Float bearing;

    public YoulaGeoLocation(double lon, double lat, float accuracy, Float bearing) {
        super(lon, lat);
        this.accuracy = accuracy;
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }

    @Override
    public String toString() {
        return "YoulaGeoLocation{" +
                "lon=" + getLon() +
                "lat=" + getLat() +
                '}';
    }
}

