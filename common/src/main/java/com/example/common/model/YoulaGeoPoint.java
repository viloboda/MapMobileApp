package com.example.common.model;

import com.google.gson.annotations.SerializedName;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Locale;

public class YoulaGeoPoint {

    @SerializedName("lon")
    private double lon;

    @SerializedName("lat")
    private double lat;

    public YoulaGeoPoint(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    // X координата
    public double getLon() {
        return lon;
    }

    // Y координата
    public double getLat() {
        return lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Lat = %s, Lon = %s",
                Double.toString(getLat()),
                Double.toString(getLon()));
    }

    public Geometry toGeometry() {
        return new GeometryFactory().createPoint(toCoordinate());
    }

    public Coordinate toCoordinate() {
        return new Coordinate(lon, lat);
    }
}

