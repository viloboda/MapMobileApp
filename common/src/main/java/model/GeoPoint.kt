package model

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory

import java.util.Locale

open class GeoPoint internal constructor(
        // X координата
        val lon: Double,
        // Y координата
        val lat: Double) {

    override fun toString(): String {
        return String.format(Locale.getDefault(), "Lat = %s, Lon = %s",
                java.lang.Double.toString(lat),
                java.lang.Double.toString(lon))
    }

    fun toGeometry(): Geometry {
        return GeometryFactory().createPoint(toCoordinate())
    }

    private fun toCoordinate(): Coordinate {
        return Coordinate(lon, lat)
    }
}

