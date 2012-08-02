package com.banregio.mapa.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.location.Location;

import com.google.android.maps.GeoPoint;

public class JsonParser {

	public static GeoPoint getCurrentGeoPoint(Location loc){
		GeoPoint p = new GeoPoint ((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6));
		return p;
	}
	
	public static JSONObject getLocationInfo(String address) {
	    StringBuilder stringBuilder = new StringBuilder();
	    try {

	    address = address.replaceAll(" ","%20");    

	    HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    stringBuilder = new StringBuilder();

	        response = client.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }

	    return jsonObject;
	}
	
	@SuppressLint({ "UseValueOf", "UseValueOf" })
	public static GeoPoint getLocationLatLong(JSONObject jsonObject) {

        Double lon = new Double(0);
        Double lat = new Double(0);

        try {

            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
    }
	
	public static JSONArray getSucursalInfoFromAssets(AssetManager assetManager) {
		InputStream input;
		JSONArray jsonArray = new JSONArray();
		try {  
            input = assetManager.open("ubicanos_30072012.json");          
            int size = input.available();  
            byte[] buffer = new byte[size];  
            input.read(buffer);  
            input.close();  

            String text = new String(buffer);
            try {        
            	jsonArray = new JSONArray(text);	    	
     	    } catch (JSONException e) {	    	
     	        e.printStackTrace();
     	    }
            
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		return jsonArray; 
	}
	
	public static JSONArray getSucursalInfoFromNet() {
	    StringBuilder stringBuilder = new StringBuilder();
	    try {

	    HttpPost httppost = new HttpPost("http://banregio.cloudsourceithosting.com/CloudWS/BanregioWS_Get_Ubicanos.php");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    stringBuilder = new StringBuilder();

	        response = client.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	        
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }

	    JSONArray jsonArray = new JSONArray();    
	    try {        
	    	jsonArray = new JSONArray(stringBuilder.toString());	    	
	    } catch (JSONException e) {	    	
	        e.printStackTrace();
	    }
	    
	    return jsonArray;
	}
	
	@SuppressLint({ "UseValueOf", "UseValueOf" })
	public static GeoPoint getSucursalLatLong(JSONArray jsonArray, int i){
		
		Double lon = new Double(0);
	    Double lat = new Double(0);
	    
	    try {
			
	    	JSONObject jsonObject = jsonArray.getJSONObject(i);
			lon = jsonObject.getDouble("Longitud");
			lat = jsonObject.getDouble("Latitud");
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
		
	}
	
	public static JSONObject getSucursalObject(JSONArray jsonArray, int i){
		JSONObject jsonObject = null;
		try {
			jsonObject = jsonArray.getJSONObject(i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
