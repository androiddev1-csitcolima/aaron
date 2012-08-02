package com.banregio.mapa;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

@SuppressLint("HandlerLeak")
public class MainActivity extends FragmentActivity {
	
	@SuppressLint("ValidFragment")
	private class EnableGpsDialogFragment extends DialogFragment {
 	
    	@Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.enable_gps_dialog)
                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            enableLocationSettings();
                        }
                    })
                    .create();
        }
    }
	
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            Location loc = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
                Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
                Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
            }
			return null;
		}
     }
	
	private class LocListener implements LocationListener{
		public void onLocationChanged(Location loc){
			updateUILocation(loc);
		}
		public void onProviderDisabled(String provider){
			
		}
		public void onProviderEnabled(String provider){
			
		}
		public void onStatusChanged(String provider, int status, Bundle extras){}
	}
	
	private TextView mLatLng;
    private TextView mAddress;
    @SuppressWarnings("unused")
	private Button mFineProviderButton;
    @SuppressWarnings("unused")
	private Button mGoToMap;
    private LocationManager locManager;
    private LocListener locListener;
    private Handler mHandler;
	
	private boolean mUseFine;
    private boolean mUseBoth;
    private boolean mGeocoderAvailable;
    
    private static final int UPDATE_ADDRESS = 1;
    private static final int UPDATE_LATLNG = 2;
    
    private static final String KEY_FINE = "use_fine";
    private static final String KEY_BOTH = "use_both";
    
    private static final int TEN_SECONDS = 10000;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
	
	public void Provider_Click(View v) {
		useFineProvider(v);
	}
	
	public void Go_to_Map(View v) {
		Intent i = new Intent(this, MainMap.class);
		startActivity(i);
	}
	
	public String getCriteriaProvider(){                
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);        
        return locManager.getBestProvider(criteria, true);
	}
	
	private void doReverseGeocoding(Location location) {
        (new ReverseGeocodingTask(this)).execute(new Location[] {location});
    }
	
	private void updateUILocation(Location location) {
       Message.obtain(mHandler,
                UPDATE_LATLNG,
                location.getLatitude() + ", " + location.getLongitude()).sendToTarget();

        if (mGeocoderAvailable) doReverseGeocoding(location);
    }

	private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
	        Location location = null;
	        if (locManager.isProviderEnabled(provider)) {
	            locManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, locListener);
	            location = locManager.getLastKnownLocation(provider);
	        } else {
	            Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
	        }
	        return location;
	    }
	
	private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
	
	protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return newLocation;
        }

        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return newLocation;
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
    }
		
	@SuppressWarnings("static-access")
	private void setup() {
    	
        Location gpsLocation = null;
        Location networkLocation = null;
        
        locManager.removeUpdates(locListener);
        mLatLng.setText(R.string.unknown);
        mAddress.setText(R.string.unknown);
        
        if (mUseFine) {
            gpsLocation = requestUpdatesFromProvider(
                    locManager.GPS_PROVIDER, R.string.not_support_gps);
            if (gpsLocation != null) updateUILocation(gpsLocation);
            
        } else if (mUseBoth) {
            gpsLocation = requestUpdatesFromProvider(
                    LocationManager.GPS_PROVIDER, R.string.not_support_gps);
            networkLocation = requestUpdatesFromProvider(
                    LocationManager.NETWORK_PROVIDER, R.string.not_support_network);
            
            if (gpsLocation != null && networkLocation != null) {
                updateUILocation(getBetterLocation(gpsLocation, networkLocation));
            } else if (gpsLocation != null) {
                updateUILocation(gpsLocation);
            } else if (networkLocation != null) {
                updateUILocation(networkLocation);
            }
        }
    }
   
    public void useFineProvider(View v) {
        mUseFine = true;
        mUseBoth = false;
        setup();
    }
    
    public void useCoarseProvider(View v) {
        mUseFine = false;
        mUseBoth = true;
        setup();
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState != null) {
            mUseFine = savedInstanceState.getBoolean(KEY_FINE);
            mUseBoth = savedInstanceState.getBoolean(KEY_BOTH);
        } else {
            mUseFine = false;
            mUseBoth = false;
        }
        
        mLatLng = (TextView) findViewById(R.id.lat);
        mAddress = (TextView) findViewById(R.id.address);
        
        mFineProviderButton = (Button) findViewById(R.id.provider_fine);
        mGoToMap = (Button)findViewById(R.id.go_to_map);
        
        mGeocoderAvailable =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();
                
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locManager.getProvider(LocationManager.GPS_PROVIDER);
        locListener = new LocListener ();
                
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_ADDRESS:
                        mAddress.setText((String) msg.obj);
                        break;
                    case UPDATE_LATLNG:
                        mLatLng.setText((String) msg.obj);
                        break;
                }
            }
        };
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean(KEY_FINE, mUseFine);
        outState.putBoolean(KEY_BOTH, mUseBoth);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
	    final boolean gpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

	    if (!gpsEnabled) {
	    	new EnableGpsDialogFragment().show(getSupportFragmentManager(), "enableGpsDialog");
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setup();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		locManager.removeUpdates(locListener);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}