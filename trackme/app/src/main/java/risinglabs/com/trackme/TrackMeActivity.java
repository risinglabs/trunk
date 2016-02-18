package risinglabs.com.trackme;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackMeActivity extends AppCompatActivity implements OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnMarkerClickListener,
        DialogCallbacks,
        LocationSource {

    private GoogleMap mMap;
    private Button trackButton;
    private Location myLocation;
    private MarkerOptions markerOptions;
    private ShareActionProvider mShareActionProvider;
    private TrackOptionsDialog optionsDialog;
    private GoogleMapInstance instance;
    private OnLocationChangedListener mChangeListner;
    public static String TAG = "TrackMeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_me);

        trackButton = (Button)findViewById(R.id.locatebutton);
        trackButton.setOnClickListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        instance = GoogleMapInstance.getInstance();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Saving Google map Obj in Utility Singleton class
        instance.saveGoogleMapObj(mMap, this);

        //Get my location
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        myLocation = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //mShareActionProvider = (ShareActionProvider) item.getActionProvider();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "This is Settings", Toast.LENGTH_LONG).show();
                return true;

            case R.id.help:
                Toast.makeText(this, "This is Help", Toast.LENGTH_LONG).show();
                return true;

            case R.id.menu_item_share:
                setShareIntent();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onClick(View v) {

        trackButton.setVisibility(View.GONE);

        // Add a marker in Sydney and move the camera
        //TODO: Check for null on myLocation
        LatLng mylatLong = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
//        map.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.house_flag))
//                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
//                .position(new LatLng(41.889, -87.622)));

        // Add a circle in Sydney
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(mylatLong)
                .radius(50)
                .strokeColor(Color.RED)
                .fillColor(Color.TRANSPARENT));

        markerOptions = new MarkerOptions();


        mMap.addMarker(
                markerOptions
                        .position(mylatLong)
                        .title("My Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatLong, 14));

        mMap.setMyLocationEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setLocationSource(this);


    }

    // Call to update the share intent
    private void setShareIntent() {
        if (mShareActionProvider != null) {
            Toast.makeText(this, "This is share", Toast.LENGTH_LONG).show();
            instance.takeSnapshot();
            //mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this, "This is my Location", Toast.LENGTH_LONG).show();
        optionsDialog = new TrackOptionsDialog(this);
        optionsDialog.show(getSupportFragmentManager(), "Options");
        return true;
    }


    @Override
    public void onItemSelected(int which) {
        Log.i(TAG, "onItemSelected "+which);

        //If Track me then start an activity

        GoogleMapInstance.getInstance().takeSnapshot();
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(getApplicationContext(), "Altitute "+location.getAltitude(), Toast.LENGTH_LONG).show();
//    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mChangeListner = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mChangeListner = null;
    }
}
