package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kt.geom.model.Coord;
import com.kt.geom.model.UTMKBounds;
import com.kt.maps.GMap;
import com.kt.maps.GMapFragment;
import com.kt.maps.GMapResultCode;
import com.kt.maps.OnMapReadyListener;
import com.kt.maps.ViewpointChange;
import com.kt.maps.overlay.Marker;
import com.kt.maps.overlay.MarkerOptions;
import com.kt.maps.util.GMapKeyManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyListener, GMap.OnMapTapListener {
    final String TAG = this.getClass().getSimpleName();
    final String gisKey = "";
    private GMap mapObject;

    private final List<Marker> addedMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /// register map key
        GMapKeyManager.getInstance().init(getApplicationContext(), gisKey);

        setContentView(R.layout.activity_main);

        initMapFragment();

        initComponent();
    }

    private void initComponent () {
        Button showMarker = (Button) findViewById(R.id.btnShowAllMarker);
        showMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllMarkers();
            }
        });

        Button clearMarker = (Button) findViewById(R.id.btnClearAllMarker);
        clearMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarkers();
            }
        });
    }

    /**
     * register map ready listener
     * Rendering ready callback
     */
    private void initMapFragment () {
        GMapFragment fragment =
                (GMapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        fragment.setOnMapReadyListener(this);
    }


    /// ========================
    /// OnMapReadyListener implement
    /// ========================
    @Override
    public void onMapReady(GMap gMap) {
        this.mapObject = gMap;
        this.mapObject.setOnMapTapListener(this);
    }

    @Override
    public void onFailReadyMap(GMapResultCode gMapResultCode) {
        Toast.makeText(getApplicationContext(), "Error  :" + gMapResultCode, Toast.LENGTH_SHORT).show();
    }

    /// ========================
    /// GMap$OnMapTapListener implement
    /// ========================

    /**
     * 지도의 tap event callback
     * @param gMap  tap event를 받은 map객체
     * @param coord 지도상의 좌표
     */
    @Override
    public void onMapTap(GMap gMap, Coord coord) {
        addMarker(coord);
    }

    /**
     * 지도에 마커를 추가.
     * @param coord 좌표
     */
    private void addMarker(Coord coord) {
        if(!isMapReady()) return;

        Marker marker = new Marker(
                new MarkerOptions()
                        .position(coord)
        );

        if(mapObject.addOverlay(marker)) {
            addedMarkers.add(marker);
        }
    }

    ///======================
    ///  private function
    ///======================
    private void showAllMarkers() {
        if(!isMapReady()) return;

        if(addedMarkers.size() > 1) {
            List<Coord> pointList = new ArrayList<>();
            for (Marker marker : addedMarkers) {
                pointList.add(marker.getPosition());
            }
            UTMKBounds markerBounds = UTMKBounds.fromCoords(pointList);
            ViewpointChange viewpointChange = ViewpointChange.fitBounds(markerBounds);
            mapObject.animate(viewpointChange);
        } else if(addedMarkers.size() == 1) {
            ViewpointChange viewpointChange = ViewpointChange.panTo(addedMarkers.get(0).getPosition());
            mapObject.animate(viewpointChange);
        } else {
            Log.d(TAG,"added marker size == 0");
        }
    }

    private void clearMarkers () {
        if(!isMapReady()) return;
        for(Marker marker : addedMarkers) {
            mapObject.removeOverlay(marker);
        }

        addedMarkers.clear();
    }

    private boolean isMapReady () {
        return mapObject != null;
    }
}