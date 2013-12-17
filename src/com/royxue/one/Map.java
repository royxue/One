package com.royxue.one;

import java.util.ArrayList;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;


public class Map extends Activity {

	private GoogleMap map;
	private ArrayList<LatLng> trace;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		 map =((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
//Map Markers		
		LatLng sydney = new LatLng(-33.867,151.206);
		//map.setMyLocationEnabled(true);是否可以获取地理位置信息
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
		map.addMarker(new MarkerOptions().title("I am Here ").snippet("Start Now!!!").position(sydney));
//Map Polyline
		//ArrayList<LatLng> trace = new ArrayList<LatLng>();
		//trace.add(sydney);
		/*Polyline line = map.addPolyline(new PolylineOptions().add(new LatLng(51.5,-0.1),new LatLng(40.7,-74.0))
		.width(10)
		.color(Color.RED));*/
		trackMe(-33.867,151.206);
		trackMe(51.5,-0.1);
		trackMe(40.7,-74.0);
		
	}
	
	private void trackMe(double lat,double lng)
	{
		if (trace == null)
		{
			trace = new ArrayList<LatLng>();
		}
		trace.add(new LatLng(lat,lng));
		PolylineOptions polylineOp = new PolylineOptions();
		for (LatLng latlng:trace){
			polylineOp.add(latlng);
		}
		polylineOp.color(Color.RED);
		Polyline line = map.addPolyline(polylineOp);
		line.setWidth(10);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

}

