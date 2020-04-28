package com.example.intern.fuel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.intern.NewsAndUpdatesACT;
import com.example.intern.R;
import com.example.intern.databinding.ActivityMapsBinding;
import com.example.intern.databinding.ActivityMapsFallbackBinding;
import com.example.intern.mainapp.MainApp;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
	private static final String TAG = MapsActivity.class.getSimpleName();
	private static int chosenPosition = -1;
	private static List<Double> lats = Arrays.asList(23.076927, 22.995767, 23.037741, 23.057581);
	private static List<Double> longs = Arrays.asList(72.524779, 72.536386, 72.558908, 72.556343);
	private static List<String> addresses = Arrays.asList("Near Sola Road, SG Highway, Ahmedabad <b>(BPCL)</b>", "Maktampura, Vasna Road, Ahmedabad <b>(BPCL)</b>", "Swastik Cross Road, CG road, Ahmedabad <b>(IOCL)</b>", "Ankur Road, Naranpura, Ahmedabad <b>(IOCL)</b>");
	SharedPreferences preferences;
	MapFragment mapFragment;
	private Double chosenLat;
	private Double chosenLong;
	private boolean isMapReady;
	private ActivityMapsBinding binding;
	private ActivityMapsFallbackBinding fallbackBinding;
	private GoogleMap mMap;
	private FusedLocationProviderClient locationProviderClient;
	private boolean isOnFallBackMode;
	//For really annoying UIs
	private LocationManager locationManager;
	private LocationRequest locationRequest;
	private LocationCallback locationCallback;
	private boolean hasExplicitLocationPerms;
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			binding = ActivityMapsBinding.inflate(getLayoutInflater());
			setContentView(binding.getRoot());
			isOnFallBackMode = false;
			mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);
			if (mapFragment != null) mapFragment.getMapAsync(this);
		} catch (Exception e) {
			fallbackBinding = ActivityMapsFallbackBinding.inflate(getLayoutInflater());
			setContentView(fallbackBinding.getRoot());
			isOnFallBackMode = true;
		}
		/*locationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
			current_lat = location.getLatitude();
			current_long = location.getLongitude();
			if(chosenLat != null && chosenLong != null){
				float[] distanceRes = new float[1];
				Location.distanceBetween(current_lat, current_long, chosenLat, chosenLong, distanceRes);
				binding.tvDistance.setText("about " + Math.round(distanceRes[0]/1000) + " KMs");
			}
		});*/
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		
		if (isOnFallBackMode) {
			fallbackBinding.back.setOnClickListener(v -> onBackPressed());
			fallbackBinding.home.setOnClickListener(v -> {
				Intent intent = new Intent(MapsActivity.this, MainApp.class);
				startActivity(intent);
				finish();
			});
			fallbackBinding.notifi.setOnClickListener(v -> {
				Intent intent = new Intent(MapsActivity.this, NewsAndUpdatesACT.class);
				startActivity(intent);
			});
		} else {
			binding.back.setOnClickListener(v -> onBackPressed());
			binding.home.setOnClickListener(v -> {
				Intent intent = new Intent(MapsActivity.this, MainApp.class);
				startActivity(intent);
				finish();
			});
			binding.notifi.setOnClickListener(v -> {
				Intent intent = new Intent(MapsActivity.this, NewsAndUpdatesACT.class);
				startActivity(intent);
			});
		}
		preferences = getSharedPreferences("fuelPrefs", Context.MODE_PRIVATE);
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		isMapReady = true;
		checkPerms();
	}
	
	private void checkPerms() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
		}else{
			try {
				//TODO :
				locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
				getLocationCallBack();
			} catch (Exception e) {
				Toast.makeText(this, "Error receiving location!", Toast.LENGTH_SHORT).show();
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
				}else{
					locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
				}
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 101) {
			hasExplicitLocationPerms = true;
			if(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
				try {
					locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
					getLocationCallBack();
				} catch (Exception e) {
					Toast.makeText(this, "Error receiving location!", Toast.LENGTH_SHORT).show();
					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
					}else{
						locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
					}
				}
			}else{
				new AlertDialog.Builder(this).setIcon(R.drawable.pslogotrimmed)
						.setTitle("Need your permission").setMessage("To function properly and to show map on screen!")
						.setPositiveButton("OK", ((dialog, which) -> {
							if (which == AlertDialog.BUTTON_POSITIVE) checkPerms();
						})).setOnDismissListener(dialog -> {
					Toast.makeText(this, "Denied Permissions", Toast.LENGTH_SHORT).show();
				}).show();
			}
		}
	}
	
	@SuppressLint("SetTextI18n")
	private void getLocationCallBack(){
		if(locationProviderClient != null){
			if(!isOnFallBackMode){
				locationRequest = LocationRequest.create();
				locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
				locationRequest.setInterval(60 * 1000);
				locationCallback = new LocationCallback(){
					@Override
					public void onLocationResult(LocationResult locationResult) {
						if(locationResult != null){
							List<Location> locations = locationResult.getLocations();
							if(locations.size() > 0){
								Location lastLocation = locations.get(locations.size()-1);
								locationReadyCall(lastLocation);
							}
						}
					}
				};
				locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
			}
		}else{
			Log.d(TAG, "getLocationCallBack: Failed to load location provider client");
		}
	}
	
	private void locationReadyCall(Location location){
		if(location == null){
			try{
				Toast.makeText(this, "Error receiving location!", Toast.LENGTH_SHORT).show();
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
				}else{
					locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
				}
			}catch (Exception ignored){}
			return;
		}
		//Get current location
		double current_lat = location.getLatitude();
		double current_long = location.getLongitude();
		Log.d(TAG, "locationReadyCall: last location found was" + current_lat + " , " + current_long);
		//Make the custom adapter when got the location
		CustomAdapter adapter = new CustomAdapter(this, addresses, lats, longs, current_lat, current_long);
		binding.spinnerCustom.setAdapter(adapter);
		//Move to last visited pump
		int lastPosition = preferences.getInt("lastPos", -1);
		if(lastPosition != -1) {
			//Found a last visited pump, move to that location
			binding.spinnerCustom.setSelection(lastPosition);
		}
		//Added check for when activity pauses
		if(chosenPosition != -1)binding.spinnerCustom.setSelection(chosenPosition);
		//Set listener to listen to spinner selection events
		binding.spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				chosenLat = lats.get(position);
				chosenLong = longs.get(position);
				chosenPosition = position;
				if(isMapReady){
					LatLng markerLocation = new LatLng(chosenLat, chosenLong);
					mMap.addMarker(new MarkerOptions().position(markerLocation).title("Petrol Pump"));
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 15.0f));
				}else{
					Log.d(TAG, "onItemSelected: Cannnot ready map");
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		//Set button listeners after getting location
		binding.navigateLast.setOnClickListener(v -> {
			Intent stopStaleService = new Intent(MapsActivity.this, BackNavService.class);
			Intent serviceIntent = new Intent(MapsActivity.this, BackNavService.class);
			serviceIntent.putExtra(BackNavService.KEY_LATITUDE_INTENT_EXTRA, chosenLat);
			serviceIntent.putExtra(BackNavService.KEY_LONGITUDE_INTENT_EXTRA, chosenLong);
			//Stop Already running service with stale target
			try{stopService(stopStaleService);}catch (Exception ignored){}
			startService(serviceIntent);
			String base = "https://www.google.com/maps/dir/?api=1&destination="+ chosenLat + "," + chosenLong + "&travelmode=driving";
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(base));
			startActivity(intent);
		});
		binding.continueLast.setOnClickListener(v -> {
			//TODO : Stop the service when user manually clicks I'm here
			Intent stopStaleService = new Intent(MapsActivity.this, BackNavService.class);
			stopService(stopStaleService);
			//Update shared preferences to store the last visited pump
			preferences.edit().putInt("lastPos", chosenPosition).apply();
			Intent intent = new Intent(this, FuelWithUsAct.class);
			startActivity(intent);finish();
		});
	}
	
	private void fallBackLocationReadyCall(Location location) {
		if(location == null){
			try{
				Toast.makeText(this, "Error receiving location!", Toast.LENGTH_SHORT).show();
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
				}else{
					locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
				}
			}catch (Exception ignored){}
			return;
		}
		//Get current location
		double current_lat = location.getLatitude();
		double current_long = location.getLongitude();
		Log.d(TAG, "locationReadyCall: last location found was" + current_lat + " , " + current_long);
		//Make the custom adapter when got the location
		CustomAdapter adapter = new CustomAdapter(this, addresses, lats, longs, current_lat, current_long);
		fallbackBinding.spinnerCustom.setAdapter(adapter);
		//Move to last visited pump
		int lastPosition = preferences.getInt("lastPos", -1);
		if(lastPosition != -1) {
			//Found a last visited pump, move to that location
			fallbackBinding.spinnerCustom.setSelection(lastPosition);
		}
		//Added check for when activity pauses
		if(chosenPosition != -1)fallbackBinding.spinnerCustom.setSelection(chosenPosition);
		//Set listener to listen to spinner selection events
		fallbackBinding.spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				chosenLat = lats.get(position);
				chosenLong = longs.get(position);
				chosenPosition = position;
				if(isMapReady){
					LatLng markerLocation = new LatLng(chosenLat, chosenLong);
					mMap.addMarker(new MarkerOptions().position(markerLocation).title("Petrol Pump"));
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 15.0f));
				}else{
					Log.d(TAG, "onItemSelected: Cannnot ready map");
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		//Set button listeners after getting location
		fallbackBinding.navigateLast.setOnClickListener(v -> {
			Intent stopStaleService = new Intent(MapsActivity.this, BackNavService.class);
			Intent serviceIntent = new Intent(MapsActivity.this, BackNavService.class);
			serviceIntent.putExtra(BackNavService.KEY_LATITUDE_INTENT_EXTRA, chosenLat);
			serviceIntent.putExtra(BackNavService.KEY_LONGITUDE_INTENT_EXTRA, chosenLong);
			//Stop Already running service with stale target
			try{stopService(stopStaleService);}catch (Exception ignored){}
			startService(serviceIntent);
			String base = "https://www.google.com/maps/dir/?api=1&destination="+ chosenLat + "," + chosenLong + "&travelmode=driving";
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(base));
			startActivity(intent);
		});
		fallbackBinding.continueLast.setOnClickListener(v -> {
			//TODO : Stop the service when user manually clicks I'm here
			Intent stopStaleService = new Intent(MapsActivity.this, BackNavService.class);
			stopService(stopStaleService);
			//Update shared preferences to store the last visited pump
			preferences.edit().putInt("lastPos", chosenPosition).apply();
			Intent intent = new Intent(this, FuelWithUsAct.class);
			startActivity(intent);finish();
		});
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(isOnFallBackMode)fallBackLocationReadyCall(location);
		else locationReadyCall(location);
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	
	}
	
	@Override
	public void onProviderEnabled(String provider) {
	
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	
	}
	
	class CustomAdapter extends BaseAdapter{
		private Context context;
		private List<String> addresses;
		private List<Double> latitudes , longitudes;
		private double currentLat, currentLong;
		CustomAdapter(Context context, List<String> addresses, List<Double> latitudes, List<Double> longitudes, double currentLat, double currentLong){
			this.context = context;
			this.addresses = addresses;
			this.latitudes = latitudes;
			this.longitudes = longitudes;
			this.currentLong = currentLong;
			this.currentLat = currentLat;
		}
		
		@Override
		public int getCount() {
			return addresses.size();
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@SuppressLint("SetTextI18n")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.map_custom_spinner_item,null);
				holder = new ViewHolder();
				holder.mAddress = convertView.findViewById(R.id.map_spinner_address);
				holder.mDistance = convertView.findViewById(R.id.map_spinner_distance);
				holder.mPumpLogo = convertView.findViewById(R.id.map_spinner_logo);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			try{
				//TODO : populate the spinner
				String address = addresses.get(position);
				holder.mAddress.setText(Html.fromHtml(address));
				if(addresses.get(position).contains("IOCL")){
					holder.mPumpLogo.setImageDrawable(getResources().getDrawable(R.drawable.iocl_logo));
				}else{
					holder.mPumpLogo.setImageDrawable(getResources().getDrawable(R.drawable.bpcl_logo));
				}
				float[] distRes = new float[1];
				Location.distanceBetween(currentLat, currentLong, latitudes.get(position), longitudes.get(position), distRes);
				holder.mDistance.setText("about " + (int)distRes[0]/1000 + " KMs");
			}catch (Exception ignored){}
			return convertView;
		}
		
		public class ViewHolder{
			TextView mAddress, mDistance;
			ImageView mPumpLogo;
		}
	}
	
	@Override
	protected void onDestroy() {
		locationProviderClient.removeLocationUpdates(locationCallback);
		super.onDestroy();
	}
}
