package com.example.fa_saikiranreddy_c0836167_android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private Location location = null;
    private TextView txtLength;
    private Button btnOk;
    private String address = "";
    private double lat = 0, lng = 0, currentLat = 0, currentLng = 0;
    private String action;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 103;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private static final int LOCATION_MIN_UPDATE_TIME = 10;
    private static final int LOCATION_MIN_UPDATE_DISTANCE = 1000;
    private FavoritePlace place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        txtLength = findViewById(R.id.txtLength);
        btnOk = findViewById(R.id.btnOk);
        initMap();
        getIntentData();
        btnOk.setOnClickListener(view -> {
            if (lat != 0 && lng != 0) {
                if (action.equalsIgnoreCase("add")) {
                    SavePlace sp = new SavePlace();
                    sp.execute();
                } else if (action.equalsIgnoreCase("edit")) {
                    UpdatePlace up = new UpdatePlace();
                    up.execute();
                }
            }
        });
    }

    private void getIntentData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle data = getIntent().getExtras();

            if (data.containsKey("ClickedPlace")){
                place = (FavoritePlace) data.getSerializable("ClickedPlace");
                address = place.getAddress();
                lat = place.getLatitude();
                lng = place.getLongitude();
            }
            action = data.getString("action");
            btnOk.setText(action.equalsIgnoreCase("edit") ? "Update" : "Save");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        if (lat != 0 && lng != 0) {
            LatLng location = new LatLng(lat, lng);
            setMarker(location, false, address);
        }
    }

    private void setMarker(LatLng location, boolean isCurrentLocation, String address) {
        MarkerOptions markerOptions = new MarkerOptions().position(location);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(isCurrentLocation ? R.drawable.location : R.drawable.custom_marker));
        if (!isCurrentLocation)
            markerOptions.draggable(true);
        mMap.addMarker(markerOptions).setTitle(address);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 3.5f));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.action_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.action_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.action_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionDialog();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            if (mMap == null)
                initMap();
            else if (mMap != null) {
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            }
            return true;
        }
    }

    private void showPermissionDialog() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage("Please Turn On Your Location")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (action.equalsIgnoreCase("add")) {
            GetPlaceAddress placeAddress = new GetPlaceAddress(latLng);
            placeAddress.execute();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            setMarker(new LatLng(location.getLatitude(), location.getLongitude()), true, "");
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(getApplicationContext(), "GPS and Network are not enabled", Toast.LENGTH_LONG).show();
            } else {
                location = null;
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_MIN_UPDATE_TIME, LOCATION_MIN_UPDATE_DISTANCE, locationListener);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    setMarker(new LatLng(location.getLatitude(), location.getLongitude()), true, "");
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 12:
            case 13:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
      /*  if (action.equalsIgnoreCase("edit")) {
            GetPlaceAddress placeAddress = new GetPlaceAddress(marker.getPosition());
            placeAddress.execute();
        }*/
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (action.equalsIgnoreCase("edit")) {
            GetPlaceAddress placeAddress = new GetPlaceAddress(marker.getPosition());
            placeAddress.execute();
        }

    }

    class SavePlace extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FavoritePlace place = new FavoritePlace(address, getCurrentDate(), "UnVisited", lat, lng);

            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .placeDao()
                    .insert(place);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    class UpdatePlace extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            place.setAddress(address);
            place.setLatitude(lat);
            place.setLongitude(lng);
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .placeDao()
                    .update(place);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void showMarkersAndDistance(String address, double latitude, double longitude) {
        this.address = address;
        lat = latitude;
        lng = longitude;
        mMap.clear();
        setMarker(new LatLng(lat, lng), false, this.address.isEmpty() ? getCurrentDate() : this.address);
        setMarker(new LatLng(currentLat, currentLng), true, "");

        txtLength.setText("Distance between two points " + CalculationByDistance(new LatLng(lat, lng), new LatLng(currentLat, currentLng)) + " KMS");
    }

    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        String date = spf.format(Calendar.getInstance().getTime());
        return date;
    }

    public String getAddress(Context ctx, double latitude, double longitude) throws IOException {
        StringBuilder result = new StringBuilder();
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);

                String locality = address.getLocality();
                String city = address.getCountryName();
                String region_code = address.getCountryCode();
                String zipcode = address.getPostalCode();

                result.append(locality != null && !locality.isEmpty() ? locality + " " : "");
                result.append(city != null && !city.isEmpty() ? city + " " : region_code + " ");
                result.append(zipcode != null && !zipcode.isEmpty() ? zipcode + " " : "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private class GetPlaceAddress extends AsyncTask<String, Void, String> {

        String placeAddress = "";
        double latitude;
        double longitude;

        public GetPlaceAddress(LatLng latLng) {
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }

        @Override
        protected String doInBackground(String... urls) {

            try {
                placeAddress = getAddress(MapsActivity.this, latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(String resultString) {
            showMarkersAndDistance(placeAddress, latitude, longitude);
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        return Radius * c;
    }
}