package com.example.mapapp;

import com.example.common.model.YoulaGeoLocation;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.GL;
import org.oscim.backend.canvas.Color;
import org.oscim.core.Box;
import org.oscim.core.MercatorProjection;
import org.oscim.core.Tile;
import org.oscim.layers.Layer;
import org.oscim.map.Map;
import org.oscim.renderer.GLShader;
import org.oscim.renderer.GLState;
import org.oscim.renderer.GLUtils;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;
import org.oscim.renderer.MapRenderer;
import org.oscim.utils.FastMath;

import static org.oscim.backend.GLAdapter.gl;

public class CustomLocationLayer extends Layer {

    final LocationRenderer locationRenderer;

    CustomLocationLayer(Map map) {
        this(map, CanvasAdapter.getScale());
    }

    CustomLocationLayer(Map map, float scale) {
        super(map);

        mRenderer = locationRenderer = new LocationRenderer(mMap, this, scale);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled())
            return;

        super.setEnabled(enabled);
    }

    void setPosition(YoulaGeoLocation location) {
        double x = MercatorProjection.longitudeToX(location.getLon());
        double y = MercatorProjection.latitudeToY(location.getLat());
        double radius = location.getAccuracy() / MercatorProjection.groundResolutionWithScale(location.getLat(), 1);
        locationRenderer.setLocation(x, y, radius, location.getBearing());
    }

    public class LocationRenderer extends LayerRenderer {

        private static final float CIRCLE_SIZE = 20;
        private static final int COLOR = 0xff3333cc;
        private static final int SHOW_ACCURACY_ZOOM = 16;

        private final Map mMap;
        private final Layer mLayer;
        private final float mScale;

        private int mShaderProgram;
        private int hVertexPosition;
        private int hMatrixPosition;
        private int hScale;
        private int hPhase;
        private int hDirection;
        private int uColor;
        private int uMode;

        private final org.oscim.core.Point mIndicatorPosition = new org.oscim.core.Point();

        //private final org.oscim.core.Point mScreenPoint = new org.oscim.core.Point();
        private final Box mBBox = new Box();

        private boolean mInitialized;

        private boolean mLocationIsVisible;

        private final float[] mColors = new float[4];
        private final org.oscim.core.Point mLocation = new org.oscim.core.Point(Double.NaN, Double.NaN);
        private Float mBearing = 90.0f;
        private double mRadius;
        private int mShowAccuracyZoom = SHOW_ACCURACY_ZOOM;

        LocationRenderer(Map map, Layer layer, float scale) {
            mMap = map;
            mLayer = layer;
            mScale = scale;

            float a = Color.aToFloat(COLOR);
            mColors[0] = a * Color.rToFloat(COLOR);
            mColors[1] = a * Color.gToFloat(COLOR);
            mColors[2] = a * Color.bToFloat(COLOR);
            mColors[3] = a;
        }

        public void setColor(int color) {
            float a = Color.aToFloat(color);
            mColors[0] = a * Color.rToFloat(color);
            mColors[1] = a * Color.gToFloat(color);
            mColors[2] = a * Color.bToFloat(color);
            mColors[3] = a;
        }

        void setLocation(double x, double y, double radius, Float bearing) {
            mLocation.x = x;
            mLocation.y = y;
            mRadius = radius;
            mBearing = bearing;
        }

        @Override
        public void update(GLViewport v) {

            if (!mInitialized) {
                init();
                mInitialized = true;
            }

            if (!mLayer.isEnabled()) {
                setReady(false);
                return;
            }

            setReady(true);

            float width = mMap.getWidth();
            float height = mMap.getHeight();

            // clamp location to a position that can be
            // savely translated to screen coordinates
            v.getBBox(mBBox, 0);

            double x = mLocation.x;
            double y = mLocation.y;

            if (!mBBox.contains(mLocation)) {
                x = FastMath.clamp(x, mBBox.xmin, mBBox.xmax);
                y = FastMath.clamp(y, mBBox.ymin, mBBox.ymax);
            }

            org.oscim.core.Point mScreenPoint = new org.oscim.core.Point();
            // get position of Location in pixel relative to
            // screen center
            v.toScreenPoint(x, y, mScreenPoint);

            x = mScreenPoint.x + width / 2;
            y = mScreenPoint.y + height / 2;

            // clip position to screen boundaries
            int visible = 0;

            if (x > width - 5)
                x = width;
            else if (x < 5)
                x = 0;
            else
                visible++;

            if (y > height - 5)
                y = height;
            else if (y < 5)
                y = 0;
            else
                visible++;

            mLocationIsVisible = (visible == 2);

            // set location indicator position
            v.fromScreenPoint(x, y, mIndicatorPosition);
        }

        @Override
        public void render(GLViewport v) {

            GLState.useProgram(mShaderProgram);
            GLState.blend(true);
            GLState.test(false, false);

            GLState.enableVertexArrays(hVertexPosition, -1);
            MapRenderer.bindQuadVertexVBO(hVertexPosition/*, true*/);

            float radius = CIRCLE_SIZE * mScale;

            boolean viewShed = false;
            if (mLocationIsVisible) {
                if (v.pos.zoomLevel >= mShowAccuracyZoom)
                    radius = (float) (mRadius * v.pos.scale);
                radius = Math.max(CIRCLE_SIZE * mScale, radius);

                viewShed = true;
            }
            gl.uniform1f(hScale, radius);

            double x = mIndicatorPosition.x - v.pos.x;
            double y = mIndicatorPosition.y - v.pos.y;
            double tileScale = Tile.SIZE * v.pos.scale;

            v.mvp.setTransScale((float) (x * tileScale), (float) (y * tileScale), 1);
            v.mvp.multiplyMM(v.viewproj, v.mvp);
            v.mvp.setAsUniform(hMatrixPosition);

            gl.uniform1f(hPhase, 1);

            if (viewShed && mLocationIsVisible) {
                if (mBearing != null) {
                    float rotation = mBearing;
                    rotation -= 90;
                    gl.uniform2f(hDirection,
                            (float) Math.cos(Math.toRadians(rotation)),
                            (float) Math.sin(Math.toRadians(rotation)));
                    gl.uniform1i(uMode, 1); // With bearing*/
                }
                else {
                    gl.uniform2f(hDirection, 0, 0);
                    gl.uniform1i(uMode, 0); // Without bearing
                }
            } else
                gl.uniform1i(uMode, -1); // Outside screen

            GLUtils.glUniform4fv(uColor, 1, mColors);

            gl.drawArrays(GL.TRIANGLE_STRIP, 0, 4);
        }

        private void init() {
            int program = GLShader.loadShader("location_1");
            if (program == 0)
                return;

            mShaderProgram = program;
            hVertexPosition = gl.getAttribLocation(program, "a_pos");
            hMatrixPosition = gl.getUniformLocation(program, "u_mvp");
            hPhase = gl.getUniformLocation(program, "u_phase");
            hScale = gl.getUniformLocation(program, "u_scale");
            hDirection = gl.getUniformLocation(program, "u_dir");
            uColor = gl.getUniformLocation(program, "u_color");
            uMode = gl.getUniformLocation(program, "u_mode");

        }
    }
}
