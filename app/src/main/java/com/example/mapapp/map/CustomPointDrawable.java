package com.example.mapapp.map;

import org.locationtech.jts.geom.Geometry;
import org.oscim.layers.vector.geometries.PointDrawable;
import org.oscim.layers.vector.geometries.Style;

class CustomPointDrawable extends PointDrawable {

    CustomPointDrawable(Geometry geo, Style style) {
        super(1, 1, style);
        this.geometry = geo;
    }
}
