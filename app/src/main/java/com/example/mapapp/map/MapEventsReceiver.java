package com.example.mapapp.map;

import android.app.AlertDialog;
import android.content.Context;

import org.oscim.backend.CanvasAdapter;
import org.oscim.core.*;
import org.oscim.event.*;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.mapfile.*;
import org.oscim.utils.GeoPointUtils;

import java.util.List;

public class MapEventsReceiver extends Layer implements GestureListener {

    private static final int TOUCH_RADIUS = 32;
    private Context context;
    private MapFileTileSource tileSource;

    public MapEventsReceiver(Context context, Map map, MapFileTileSource tileSource) {
        super(map);
        this.context = context;
        this.tileSource = tileSource;
    }

    @Override
    public boolean onGesture(Gesture g, MotionEvent e) {
        if (g instanceof Gesture.Tap) {
            GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());

            // Определяем координаты клика и находим тайлы в его зоне
            float touchRadius = TOUCH_RADIUS * CanvasAdapter.getScale();
            long mapSize = MercatorProjection.getMapSize((byte) mMap.getMapPosition().getZoomLevel());
            double pixelX = MercatorProjection.longitudeToPixelX(p.getLongitude(), mapSize);
            double pixelY = MercatorProjection.latitudeToPixelY(p.getLatitude(), mapSize);
            int tileXMin = MercatorProjection.pixelXToTileX(pixelX - touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
            int tileXMax = MercatorProjection.pixelXToTileX(pixelX + touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
            int tileYMin = MercatorProjection.pixelYToTileY(pixelY - touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
            int tileYMax = MercatorProjection.pixelYToTileY(pixelY + touchRadius, (byte) mMap.getMapPosition().getZoomLevel());
            Tile upperLeft = new Tile(tileXMin, tileYMin, (byte) mMap.getMapPosition().getZoomLevel());
            Tile lowerRight = new Tile(tileXMax, tileYMax, (byte) mMap.getMapPosition().getZoomLevel());

            //Получаем данные из базы, указав левый верхний и правый нижний тайлы
            MapDatabase mapDatabase = ((MapDatabase) ((OverzoomTileDataSource) tileSource.getDataSource()).getDataSource());
            MapReadResult mapReadResult = mapDatabase.readLabels(upperLeft, lowerRight);

            StringBuilder sb = new StringBuilder();

            // Фильтруем полученные POI с учётом области клика
            sb.append("*** POI ***");
            for (PointOfInterest pointOfInterest : mapReadResult.pointOfInterests) {
                Point layerXY = new Point();
                mMap.viewport().toScreenPoint(pointOfInterest.position, false, layerXY);
                Point tapXY = new Point(e.getX(), e.getY());
                if (layerXY.distance(tapXY) > touchRadius) {
                    continue;
                }
                sb.append("\n");
                List<Tag> tags = pointOfInterest.tags;
                for (Tag tag : tags) {
                    sb.append("\n").append(tag.key).append("=").append(tag.value);
                }
            }

            // Фильтруем геометрии, попавший в область клика
            sb.append("\n\n").append("*** WAYS ***");
            for (Way way : mapReadResult.ways) {
                if (way.geometryType != GeometryBuffer.GeometryType.POLY
                        || !GeoPointUtils.contains(way.geoPoints[0], p)) {
                    continue;
                }
                sb.append("\n");
                List<Tag> tags = way.tags;
                for (Tag tag : tags) {
                    sb.append("\n").append(tag.key).append("=").append(tag.value);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle("Click results");
            builder.setMessage(sb);
            builder.setPositiveButton("OK", null);
            builder.show();

            return true;
        }
        return false;
    }
}
