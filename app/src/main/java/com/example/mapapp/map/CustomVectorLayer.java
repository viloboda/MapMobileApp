package com.example.mapapp.map;

import android.util.Log;

import org.locationtech.jts.geom.Dimension;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.oscim.core.Box;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.Drawable;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.PolygonDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

public class CustomVectorLayer extends VectorLayer {

    CustomVectorLayer(Map map) {
        super(map);
    }

    @Override
    public synchronized void add(Drawable drawable) {
        mDrawables.insert(bbox(drawable.getGeometry()), drawable);
    }

    @Override
    public synchronized void add(Geometry geometry, Style style) {
        switch (geometry.getDimension()) {
            case Dimension.A:
                add(new PolygonDrawable(geometry, style));
                break;
            case Dimension.L:
                add(new LineDrawable(geometry, style));
                break;
            case Dimension.P:
                add(new CustomPointDrawable(geometry, style));
                break;
        }
    }

    @Override
    protected void processFeatures(Task t, Box bbox) {
        if (Double.isNaN(bbox.xmin))
            return;

        // reduce lines points min distance
        mMinX = ((bbox.xmax - bbox.xmin) / mMap.getWidth());
        mMinY = ((bbox.ymax - bbox.ymin) / mMap.getHeight());

        mConverter.setPosition(t.position.x, t.position.y, t.position.scale);

        double shift = 0.001;
        bbox.xmin = bbox.xmin - shift;
        bbox.ymin = bbox.ymin - shift;
        bbox.xmax = bbox.xmax + shift;
        bbox.ymax = bbox.ymax + shift;
        bbox.scale(1E6);

        int level = 0;
        Style lastStyle = null;

        // go through features, find the matching style and draw
        synchronized (this) {
            tmpDrawables.clear();

            try {
                mDrawables.search(bbox, tmpDrawables);
            } catch (NullPointerException npe) {
                Log.e("YVL", npe.getMessage());
            }

            for (Drawable d : tmpDrawables) {
                Style style = d.getStyle();

                if (style != lastStyle) {
                    level += 2;
                    lastStyle = style;
                }

                try {
                    draw(t, level, d, style);
                } catch (Exception e) {
                    Log.e("YVL", "Error while drawing geometry");
                    if (e.getMessage() != null) {
                        Log.e("YVL", e.getMessage());
                    }
                }
            }
        }
    }

    public void removeGeometry(Geometry g) {
        Box bbox = bbox(g);

        synchronized (this) {
            tmpDrawables.clear();
            mDrawables.search(bbox, tmpDrawables);
            for (Drawable d : tmpDrawables) {
                if (d.getGeometry().getUserData().equals(g.getUserData())) {
                    mDrawables.remove(bbox, d);
                }
            }
        }
    }

    // copypaste from base class
    private Box bbox(Geometry geometry) {
        Envelope e = geometry.buffer(0.0001).getEnvelopeInternal();
        Box bbox = new Box(e.getMinX(), e.getMinY(), e.getMaxX(), e.getMaxY());
        bbox.scale(1E6);
        return bbox;
    }

    public void clear() {
        synchronized (tmpDrawables) {
            mDrawables.clear();
        }
    }
}