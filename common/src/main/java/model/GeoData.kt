package model

import org.locationtech.jts.geom.Geometry

class GeoData(var id: Int,
              var geometry: Geometry,
              var objectType: ObjectType)
