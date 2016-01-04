package com.mtk.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.location.core.CoordinateConvert;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.gomtel.util.LogUtil;
import com.gomtel.util.PositionInfo;
import com.mtk.btnotification.R;

import java.util.ArrayList;

/**
 * Created by lixiang on 15-12-1.
 */
public class LocationPage extends Activity{
    private MapView mapView;
    private ArrayList<PositionInfo> list = null;
    private AMap mAmap;
    private Polyline polyline;
    private Marker mMoveMarker;
    private static final double DISTANCE = 0.0001;

    public LocationPage() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_page);
        list = (ArrayList<PositionInfo>)getIntent().getSerializableExtra(DetailInfo.POSITION_LIST);
        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mAmap = mapView.getMap();
        initRoadData();
    }

    private void initRoadData() {
        PolylineOptions polylineOptions = new PolylineOptions();
        if(list != null) {
            for (PositionInfo positionInfo:list) {
                GeoPoint pos = CoordinateConvert.fromGpsToAMap(positionInfo.getLat(), positionInfo.getLon());
                LogUtil.e("gomtel","positionInfo.getLat()= "+positionInfo.getLat()+"  "+positionInfo.getLon());
                polylineOptions.add(new LatLng(pos.getLatitudeE6() * 1.E-6, pos.getLongitudeE6() * 1.E-6));
            }
            polylineOptions.width(10);
            polylineOptions.color(Color.RED);
            polyline = mAmap.addPolyline(polylineOptions);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
//        markerOptions.anchor(0.5f, 0.5f);
//        markerOptions.icon(BitmapDescriptorFactory
//                .fromResource(R.drawable.marker));
            if(list.size() != 0) {
                markerOptions.position(polylineOptions.getPoints().get(0));
                mAmap.addMarker(markerOptions);
                mAmap.moveCamera(CameraUpdateFactory.changeLatLng(polylineOptions.getPoints().get(0)));
                mAmap.moveCamera(CameraUpdateFactory.zoomTo(17));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
