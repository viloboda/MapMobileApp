package model

import org.locationtech.jts.geom.Geometry

class GeoData(var id: Long,
              var geometry: Geometry,
              var objectType: ObjectType,
              var attributes: String?)
