package com.example.dal

import model.GeoPoint
import model.GeoData

interface GeoDataRepository {

    @Throws(DataContextException::class)
    fun getGeoObject(point: GeoPoint, zoomLevel: Int): GeoData?
}