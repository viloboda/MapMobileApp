package model

class GeoLocation(lon: Double, lat: Double,
                  val accuracy: Float,
                  var bearing: Float?) : GeoPoint(lon, lat) {

    override fun toString(): String {
        return "GeoLocation{" +
                "lon=" + lon +
                "lat=" + lat +
                '}'.toString()
    }
}

