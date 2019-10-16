package com.example.mapapp;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mapapp.controls.FloorPickerControl;
import com.example.mapapp.map.FloorsManager;
import com.example.mapapp.map.MapEventsReceiver;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.android.MapView;
import org.oscim.core.BoundingBox;
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
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.FloorData;

public class MainActivity extends AppCompatActivity {

    private Map map;
    private FloorData floorData;
    private FloorPickerControl floorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        initMap();
        initLocationLayer();
        initMapActionButtons();

        initFloors();
    }

    private void initMap() {
        MapView mapView = findViewById(R.id.map_view);
        this.map = mapView.map();

        MultiMapFileTileSource mmtilesource = new MultiMapFileTileSource();

        File baseMapFile = getMapFile("cyprus.map");
        MapFileTileSource tileSource = new MapFileTileSource();
        tileSource.setMapFile(baseMapFile.getAbsolutePath());
        mmtilesource.add(tileSource);

        MapFileTileSource worldTileSource = new MapFileTileSource();

        File worldMapFile = getMapFile("world.map");
        worldTileSource.setMapFile(worldMapFile.getAbsolutePath());
        mmtilesource.add(worldTileSource);

        VectorTileLayer layer = this.map.setBaseMap(mmtilesource);
        MapInfo info = tileSource.getMapInfo();
        if (info != null) {
            MapPosition pos = new MapPosition();
            pos.setByBoundingBox(info.boundingBox, Tile.SIZE * 4, Tile.SIZE * 4);
            this.map.setMapPosition(pos);
        }

        this.map.setTheme(VtmThemes.DEFAULT);

        this.map.layers().add(new BuildingLayer(this.map, layer));
        this.map.layers().add(new LabelLayer(this.map, layer));
        this.map.layers().add(new MapEventsReceiver(this, this.map, tileSource));
    }

    private void initLocationLayer() {
        LocationLayer locationLayer = new LocationLayer(this.map);
        locationLayer.setEnabled(true);

        GeoPoint initialGeoPoint = this.map.getMapPosition().getGeoPoint();
        locationLayer.setPosition(initialGeoPoint.getLatitude(), initialGeoPoint.getLongitude(), 1);

        this.map.layers().add(locationLayer);

        View vLocation = findViewById(R.id.am_location);
        vLocation.setOnClickListener(v ->
                this.map.animator().animateTo(initialGeoPoint));
    }

    private void initMapActionButtons() {
        View vZoomIn = findViewById(R.id.am_zoom_in);
        vZoomIn.setOnClickListener(v ->
                this.map.animator().animateZoom(500, 2, 0, 0));

        View vZoomOut = findViewById(R.id.am_zoom_out);
        vZoomOut.setOnClickListener(v ->
                this.map.animator().animateZoom(500, 0.5, 0, 0));

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
    }

    private void initFloors() {
        floorData = new FloorData();
        FloorsManager floorsManager = new FloorsManager(floorData);
        floorsManager.init(this.map);
        floorsManager.drawFloor(1);

        floorPicker = findViewById(R.id.am_floors_picker);
        floorPicker.setOnSelectionChanged(floor -> floorsManager.drawFloor(floor.getId()));
        floorPicker.setItems(floorData.getFloors());
        floorPicker.setSelectedIndex(0);
        floorPicker.setVisibility(View.INVISIBLE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_floors) {
            Coordinate[] coords = floorData.getFloorBuilding().getCoordinates();
            List<GeoPoint> points = new ArrayList<>(coords.length);
            for (Coordinate c : coords) {
                points.add(new GeoPoint(c.y, c.x));
            }

            this.map.animator().animateTo(new BoundingBox(points));

            floorPicker.setVisibility(View.VISIBLE);
            floorPicker.requestLayout();
        }

        return true;
    }
}