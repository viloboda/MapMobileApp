package com.example.dal.impl

import com.example.dal.*
import model.*
import org.locationtech.jts.geom.*

class GeoDataRepositoryImpl
constructor(private val dataContextFactory: DataContextFactory) : GeoDataRepository {

    @Throws(DataContextException::class)
    override fun getGeoObject(point: GeoPoint, zoomLevel: Int): GeoData? {
        return getGeoObject(point, zoomLevel, ObjectType.None)
    }

    @Throws(DataContextException::class)
    fun getGeoObject(point: GeoPoint, zoomLevel: Int, objectType: ObjectType): GeoData? {
        val jtsPoint = point.toGeometry()

        val query = "SELECT gd.id, gd.geometry, gd.attributes, gd.type " +
                "FROM ${TableNames.GEO_INDEX} gi JOIN ${TableNames.GEO_DATA} gd on gi.Id = gd.id " +
                "WHERE minX <= ? AND maxX >= ? AND minY <= ? AND maxY >= ? "
        try {
            this.dataContextFactory.createReadOnly().use { dataContext ->
                ObjectDataCursor(dataContext.executeCursor(query, point.lon, point.lon, point.lat, point.lat)
                ).use { cursor ->
                    return selectGeometry(jtsPoint, objectType, zoomLevel, cursor)
                }
            }
        } catch (e: Exception) {
            throw DataContextException(e)
        }
    }

    private fun selectGeometry(jtsPoint: Geometry, objectType: ObjectType, zoomLevel: Int, cursor: Iterable<GeoData>): GeoData? {
        var minDistance = Double.MAX_VALUE
        var nearestGeoObject: GeoData? = null
        var objectTypePriority = Int.MAX_VALUE
        var objectTypeDimension = Dimension.DONTCARE

        val nearestObjects = mutableMapOf<Int, GeoData>()

        cursor
                .filter {

                    var geometry = it.geometry
                    if (geometry.dimension == Dimension.P) {
                        geometry = geometry.buffer(0.0001)
                    } else if (geometry.dimension == Dimension.L) {
                        geometry = geometry.buffer(0.00005)
                    }

                    geometry != null
                            && (objectType == ObjectType.None || objectType == it.objectType)
                            && isAllowedForZoomLevel(zoomLevel, it.objectType)
                            && within(jtsPoint, geometry)
                }
                .forEach {
                    val distance = jtsPoint.distance(it.geometry)
                    val priority = geoPriorities[it.objectType] ?: Int.MAX_VALUE

                    if (priority < objectTypePriority) {
                        nearestObjects.clear()
                    }

                    if (priority == objectTypePriority) {
                        nearestObjects[it.geometry.dimension] = it
                    }

                    var condition = false
                    // Если объект приоритетнее - выбираем его
                    if (priority < objectTypePriority) {
                        condition = true
                    } else if (priority == objectTypePriority) {
                        // Если приоритеты равны, и текущий объект - точка, а ближайший объект - линия (или полигон), то
                        // при попадании пина в буфер точки примерно 5 метров выбираем точку.
                        // Расстояние в этом случае значения не имеет, точка всегда приоритетнее
                        if (it.geometry.dimension == Dimension.P && (objectTypeDimension == Dimension.L || objectTypeDimension == Dimension.A)) {
                            condition = jtsPoint.within(it.geometry.buffer(0.00005))
                            // Если наоборот, текущий объект - линия (полигон), а ближайший объект - точка, то
                            // линию выберем только в случае, если расстояние до неё меньше, и пин не попадает в буфер точки
                        } else if ((it.geometry.dimension == Dimension.L || it.geometry.dimension == Dimension.A) && objectTypeDimension == Dimension.P) {
                            condition =  distance < minDistance &&
                                    !jtsPoint.within(nearestGeoObject!!.geometry.buffer(0.00005))
                        } else if (it.geometry.dimension == Dimension.L && objectTypeDimension == Dimension.A) {
                            condition = jtsPoint.within(it.geometry.buffer(0.00001))
                        } else if (it.geometry.dimension == Dimension.A && objectTypeDimension == Dimension.L) {
                            condition =  distance < minDistance &&
                                    !jtsPoint.within(nearestGeoObject!!.geometry.buffer(0.00001))
                        }
                        else if (distance < minDistance) {
                            condition = true
                        }

                        if (!condition && distance == 0.0 && minDistance == 0.0 && nearestGeoObject != null) {
                            var targetIntersection = nearestGeoObject!!.geometry
                            if (targetIntersection is GeometryCollection)
                                targetIntersection = targetIntersection.buffer(0.00001)
                            val intersectArea = it.geometry.intersection(targetIntersection).area
                            condition = it.geometry.area - intersectArea < targetIntersection.area - intersectArea
                        }
                    }

                    if (condition) {
                        minDistance = distance
                        objectTypePriority = priority
                        nearestGeoObject = it
                        objectTypeDimension = it.geometry.dimension
                    }
                }

        return nearestGeoObject
    }

    private fun isAllowedForZoomLevel(zoomLevel: Int, geoType: ObjectType): Boolean {

        if(zoomLevel >= 15)
        {
            return true
        }

        if(zoomLevel >= 11)
        {
            return geoType == ObjectType.City
        }

        return geoType == ObjectType.City
    }

    private fun within(g1: Geometry, g2: Geometry): Boolean {
        if (g2.dimension != Dimension.A) {
            return g1.within(g2)
        }

        if (g2.numGeometries == 1) {
            return g1.within(g2)
        }

        for(i in 0 until g2.numGeometries) {
            val result = g1.within(g2.getGeometryN(i))
            if (result) {
                return true
            }
        }

        return false
    }

    private val geoPriorities = hashMapOf(
            Pair(ObjectType.House, 10),
            Pair(ObjectType.City, 100)
    )

}