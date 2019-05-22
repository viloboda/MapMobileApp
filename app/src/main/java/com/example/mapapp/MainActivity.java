package com.example.mapapp;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mapapp.map.MapEventsReceiver;

import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.Tile;
import org.oscim.layers.LocationLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MapInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Map map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mapView = findViewById(R.id.map_view);
        this.map = mapView.map();

        //MultiMapFileTileSource mmtilesource = new MultiMapFileTileSource();

        File baseMapFile = getMapFile("cyprus.map");
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFile(baseMapFile.getAbsolutePath());
        //mmtilesource.add(tileSource);

        MapFileTileSource worldTileSource = new MapFileTileSource();

        File worldMapFile = getMapFile("world.map");
        worldTileSource.setMapFile(worldMapFile.getAbsolutePath());
        //mmtilesource.add(worldTileSource);

        VectorTileLayer layer = this.map.setBaseMap(tileSource);
        MapInfo info = tileSource.getMapInfo();
        if (info != null) {
            MapPosition pos = new MapPosition();
            pos.setByBoundingBox(info.boundingBox, Tile.SIZE * 4, Tile.SIZE * 4);
            this.map.setMapPosition(pos);
        }

        this.map.setTheme(VtmThemes.DEFAULT);

        this.map.layers().add(new BuildingLayer(this.map, layer));
        this.map.layers().add(new LabelLayer(this.map, layer));


        LocationLayer locationLayer = new LocationLayer(this.map);
        locationLayer.setEnabled(true);

        GeoPoint initialGeoPoint = this.map.getMapPosition().getGeoPoint();
        locationLayer.setPosition(initialGeoPoint.getLatitude(), initialGeoPoint.getLongitude(), 1);

        //this.map.layers().add(locationLayer);

        View vLocation = findViewById(R.id.am_location);
        vLocation.setOnClickListener(v ->
                this.map.animator().animateTo(initialGeoPoint));
        vLocation.setVisibility(View.INVISIBLE);

        View vZoomIn = findViewById(R.id.am_zoom_in);
        vZoomIn.setOnClickListener(v ->
                this.map.animator().animateZoom(500, 2, 0, 0));
        vZoomIn.setVisibility(View.INVISIBLE);

        View vZoomOut = findViewById(R.id.am_zoom_out);
        vZoomOut.setOnClickListener(v ->
                this.map.animator().animateZoom(500, 0.5, 0, 0));
        vZoomOut.setVisibility(View.INVISIBLE);

        View vCompass = findViewById(R.id.am_compass);
        vCompass.setVisibility(View.GONE);
        vCompass.setOnClickListener(v -> {

            MapPosition mapPosition = this.map.getMapPosition();
            mapPosition.setBearing(0);
            this.map.animator().animateTo(500, mapPosition);

            vCompass.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    vCompass.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).setDuration(500).rotation(0).start();
        });

        this.map.events.bind((e, mapPosition) -> {
            if (e == Map.ROTATE_EVENT) {
                vCompass.setRotation(mapPosition.getBearing());
                vCompass.setVisibility(View.VISIBLE);
            }
        });

        this.map.layers().add(new MapEventsReceiver(this, this.map, tileSource));
    }

    private File getMapFile(String mapFileName) {
        File worldMapFile = new File(getFilesDir(), mapFileName);
        if (!worldMapFile.exists()) {
            try(InputStream inputStream = getResources().openRawResource(
                    mapFileName.equals("world.map") ? R.raw.world : R.raw.cyprus)) {
                FileHelper.copyFile(inputStream, worldMapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return worldMapFile;
    }
}