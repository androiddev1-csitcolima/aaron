package com.banregio.mapa;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;

import com.banregio.mapa.utils.DrawMapItem;
import com.banregio.mapa.utils.JsonParser;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainMap extends MapActivity {
	
	// ************* <Members> ************* 
	
	private AssetManager assetManager;
	private MapView mapa;
	private ImageButton btnSucursal, btnCajero;
	
	private boolean SHOW_SUCURSAL = false;
	private boolean SHOW_CAJERO = false;
	
	private List<Overlay> mapItems;
	private JSONArray jsonSucursal;
	private DrawMapItem myLocOverlay;
	private DrawMapItem sucursalOverlay;
	private DrawMapItem cajeroOverlay;
	private Location myLocation;
	private LocationManager locManager;
	private LocationListener listener = new LocationListener(){
		public void onLocationChanged(Location location) {
		    JsonParser.getCurrentGeoPoint(location);
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	// ************* </Members> *************
	
	/* ******************************************************************* */

	// ************* <Local Methods> *************
	
	public void drawAllIcons(){
		
		for (int i = 0; i < jsonSucursal.length(); i++){
			JSONObject jsonObject = null;
			OverlayItem overlayitem = null;
			try {
				jsonObject = jsonSucursal.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				if (jsonObject.getString("tipo").equals("S")){
					GeoPoint p = JsonParser.getSucursalLatLong(jsonSucursal, i);
					overlayitem = new OverlayItem(p, 
							jsonObject.getString("NOMBRE").toString(), 
							jsonObject.getString("DOMICILIO").toString());
					sucursalOverlay.addOverlay(overlayitem);
				}
				else{
					GeoPoint p = JsonParser.getSucursalLatLong(jsonSucursal, i);
					overlayitem = new OverlayItem(p, 
							jsonObject.getString("NOMBRE").toString(), 
							jsonObject.getString("DOMICILIO").toString());
					cajeroOverlay.addOverlay(overlayitem);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			
		}
		mapItems.add(sucursalOverlay);
		mapItems.add(cajeroOverlay);
	}
	
	public void click_btnSucursal(View v){
		btnCajero.setImageResource(R.drawable.bank3);
		if (SHOW_SUCURSAL == false){
			mapItems.clear();
			mapItems.add(sucursalOverlay);
			mapItems.add(myLocOverlay);
			mapa.postInvalidate();
			SHOW_SUCURSAL = true;
			SHOW_CAJERO = false;
			btnSucursal.setImageResource(R.drawable.congress2);
		}
		else {
			mapItems.clear();
			mapItems.add(cajeroOverlay);
			mapItems.add(sucursalOverlay);
			mapItems.add(myLocOverlay);
			mapa.postInvalidate();
			SHOW_SUCURSAL = false;
			SHOW_CAJERO = false;
			btnSucursal.setImageResource(R.drawable.congress3);
		}
	}
	
	public void click_btnCajero(View v){
		btnSucursal.setImageResource(R.drawable.congress3);
		if (SHOW_CAJERO == false){
			mapItems.clear();
			mapItems.add(cajeroOverlay);
			mapItems.add(myLocOverlay);
			mapa.postInvalidate();
			SHOW_CAJERO = true;
			SHOW_SUCURSAL = false;
			btnCajero.setImageResource(R.drawable.bank2);
		}
		else {
			mapItems.clear();
			mapItems.add(cajeroOverlay);
			mapItems.add(sucursalOverlay);
			mapItems.add(myLocOverlay);
			mapa.postInvalidate();
			SHOW_CAJERO = false;
			SHOW_SUCURSAL = false;
			btnCajero.setImageResource(R.drawable.bank3);
		}
	}
	
	
	// ************* </Local Methods> *************
	
	/* ******************************************************************* */
	
	// ************* <Life Cycle> *************
	@Override
	protected boolean isRouteDisplayed() {
		return false; 
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.map_view);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mapa = (MapView)findViewById(R.id.main_map);
		mapa.setBuiltInZoomControls(true);
		
		Drawable sucursalIcon = this.getResources().getDrawable(R.drawable.congress);
		Drawable cajeroIcon = this.getResources().getDrawable(R.drawable.bank);
		Drawable myLocIcon = this.getResources().getDrawable(R.drawable.dir_icon);
		
		btnSucursal = (ImageButton) findViewById(R.id.btnSucursal);
		btnCajero = (ImageButton) findViewById(R.id.btnCajero);
        
		locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        assetManager = getAssets();
        jsonSucursal = JsonParser.getSucursalInfoFromAssets(assetManager);
		
        myLocOverlay= new DrawMapItem(myLocIcon, this);
		sucursalOverlay = new DrawMapItem(sucursalIcon, this);
		cajeroOverlay = new DrawMapItem(cajeroIcon, this);
		
		mapItems = mapa.getOverlays();
		GeoPoint mCurrentGeoPoint = JsonParser.getCurrentGeoPoint(myLocation);		
		
		OverlayItem overlayitem = new OverlayItem(mCurrentGeoPoint, "Banregio Movil", "¡Usted estás aqui!");
		myLocOverlay.addOverlay(overlayitem);
		mapItems.add(myLocOverlay);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		drawAllIcons();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		locManager.removeUpdates(listener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		locManager.removeUpdates(listener);
	}
}
// ************* </Life Cycle> *************