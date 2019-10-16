package model

import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.WKTReader

import java.util.ArrayList
import java.util.HashMap

class FloorData {
    private lateinit var floorObjectGeo: Geometry
    private val floorsData = HashMap<Int, List<GeoData>>()

    init {
        try {
            floorObjectGeo = WKTReader().read("GEOMETRYCOLLECTION (POLYGON ((32.4396906 34.774678, 32.4396872 34.7748582, 32.4396864 34.7748943, 32.440036 34.7748989, 32.4400401 34.7746826, 32.4396906 34.774678)), POLYGON ((32.4393951 34.7747013, 32.4395917 34.7747037, 32.4395961 34.7744603, 32.4394432 34.7744586, 32.4394408 34.7745846, 32.4393971 34.7745841, 32.4393953 34.7746742, 32.4393951 34.7747013)), POLYGON ((32.4390081 34.7743335, 32.4390643 34.7743341, 32.4391283 34.7743349, 32.4391312 34.7741727, 32.4391319 34.7741367, 32.4391345 34.7739925, 32.4390143 34.773991, 32.4390081 34.7743335)), POLYGON ((32.4392063 34.7743358, 32.4392046 34.774426, 32.4393234 34.7744274, 32.4393207 34.7745831, 32.4393643 34.7745837, 32.4393971 34.7745841, 32.4394408 34.7745846, 32.4394432 34.7744586, 32.4394451 34.7743387, 32.4394123 34.7743383, 32.4392063 34.7743358)), POLYGON ((32.4397712 34.7750305, 32.4398356 34.7750854, 32.4399459 34.7750328, 32.4399477 34.7749427, 32.4400351 34.7749439, 32.440036 34.7748989, 32.4396864 34.7748943, 32.4396856 34.7749392, 32.439773 34.7749404, 32.4397712 34.7750305)), POLYGON ((32.4400401 34.7746826, 32.4400407 34.774655, 32.4400382 34.7746446, 32.4400311 34.7746359, 32.4400205 34.77463, 32.4400079 34.7746279, 32.439626 34.7746231, 32.439625 34.7746772, 32.4396906 34.774678, 32.4400401 34.7746826)), POLYGON ((32.4392344 34.7745164, 32.4392333 34.774582, 32.4393207 34.7745831, 32.4393234 34.7744274, 32.4392046 34.774426, 32.4392042 34.7744529, 32.4391059 34.7744518, 32.4391054 34.7744788, 32.4392023 34.7744799, 32.4392016 34.774516, 32.4392344 34.7745164)), POLYGON ((32.4396246 34.7747041, 32.4395917 34.7747037, 32.4393951 34.7747013, 32.4395673 34.7748385, 32.439568 34.7748025, 32.4396227 34.7748032, 32.4396246 34.7747041)), POLYGON ((32.4390643 34.7743341, 32.4390622 34.7744512, 32.4391059 34.7744518, 32.4392042 34.7744529, 32.4392046 34.774426, 32.4392063 34.7743358, 32.4391283 34.7743349, 32.4390643 34.7743341)), POLYGON ((32.4396906 34.774678, 32.439625 34.7746772, 32.4396246 34.7747041, 32.4396227 34.7748032, 32.4396216 34.7748574, 32.4396872 34.7748582, 32.4396906 34.774678)), POLYGON ((32.4394123 34.7743383, 32.4394451 34.7743387, 32.4394483 34.7741765, 32.4394155 34.7741761, 32.4394123 34.7743383)), POLYGON ((32.4393971 34.7745841, 32.4393643 34.7745837, 32.4393625 34.7746738, 32.4393953 34.7746742, 32.4393971 34.7745841)), POLYGON ((32.4391312 34.7741727, 32.4391748 34.7741733, 32.4391755 34.7741372, 32.4391319 34.7741367, 32.4391312 34.7741727)), POLYGON ((32.4391755 34.7741372, 32.4391748 34.7741733, 32.4394155 34.7741761, 32.4394483 34.7741765, 32.4397588 34.7741803, 32.4398246 34.7743556, 32.4399707 34.7743183, 32.4398446 34.7739825, 32.4393422 34.773977, 32.4393414 34.7740221, 32.4393394 34.7741392, 32.4391755 34.7741372)), POLYGON ((32.4391755 34.7741372, 32.4393394 34.7741392, 32.4393414 34.7740221, 32.4391776 34.7740201, 32.4391755 34.7741372)))")

            var id = 0

            val geoBuffer = floorObjectGeo.buffer(0.0)
            val floor1Objects = ArrayList<GeoData>()
            floor1Objects.add(GeoData(id++, geoBuffer, ObjectType.Floor))
            floor1Objects.add(GeoData(id++, geoBuffer.boundary, ObjectType.FloorWall))
            floor1Objects.add(GeoData(id++, floorObjectGeo.getGeometryN(0), ObjectType.FloorArea))
            floor1Objects.add(GeoData(id++, floorObjectGeo.getGeometryN(0).boundary, ObjectType.FloorWall))
            floorsData[1] = floor1Objects

            for (i in 1 until floorObjectGeo.numGeometries) {
                val floorObjects = ArrayList<GeoData>()
                floorObjects.add(floor1Objects[0])

                val geoComponent = floorObjectGeo.getGeometryN(i)
                floorObjects.add(GeoData(id++, geoComponent, ObjectType.FloorArea))
                floorObjects.add(GeoData(id++, geoComponent.boundary, ObjectType.FloorWall))

                floorsData[i + 1] = floorObjects
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    fun getFloorData(floorId: Int): List<GeoData> {
        return floorsData[floorId]!!
    }

    fun getFloors(): List<FloorItem> {
        return floorsData.keys.map { x -> FloorItem(x) }.sortedBy { x -> x.id }.toList()
    }

    fun getFloorBuilding(): Geometry {
        return floorObjectGeo
    }
}
