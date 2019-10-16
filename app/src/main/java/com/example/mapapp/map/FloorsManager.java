package com.example.mapapp.map;

import android.graphics.Color;

import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

import java.util.HashMap;
import java.util.List;

import model.FloorData;
import model.GeoData;
import model.ObjectType;

public class FloorsManager {
    private Map map;
    private CustomVectorLayer indoorLayer;
    private FloorData floorData;
    private HashMap<ObjectType, Style> styles = new HashMap<>();

    public FloorsManager(FloorData floorData) {
        this.floorData = floorData;

        styles.put(ObjectType.Floor, org.oscim.layers.vector.geometries.Style.builder()
                .fillColor(Color.GRAY)
                .build());

        styles.put(ObjectType.FloorArea, org.oscim.layers.vector.geometries.Style.builder()
                .fillColor(Color.MAGENTA)
                .build());

        styles.put(ObjectType.FloorWall, org.oscim.layers.vector.geometries.Style.builder()
                .strokeColor(Color.BLACK)
                .strokeWidth(2)
                .build());
    }

    public void init(Map map) {
        this.map = map;
    }

    public void drawFloor(int floorId) {

        hideFloors();

        indoorLayer = new CustomVectorLayer(this.map);

        List<GeoData> geoObjects = this.floorData.getFloorData(floorId);

        for (GeoData geo: geoObjects) {
            indoorLayer.add(geo.getGeometry(), styles.get(geo.getObjectType()));
        }

        this.map.layers().add(indoorLayer);

        indoorLayer.update();
    }

    public void hideFloors() {
        if (this.map == null) {
            return;
        }

        if (indoorLayer != null) {
            this.map.layers().remove(indoorLayer);

            indoorLayer = null;
        }

        this.map.updateMap(true);
    }

    public boolean isIndoorLayerActive() {
        return indoorLayer != null;
    }
}
