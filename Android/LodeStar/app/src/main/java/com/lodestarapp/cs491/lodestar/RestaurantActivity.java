package com.lodestarapp.cs491.lodestar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.lodestarapp.cs491.lodestar.Adapters.NearMeAdapter;
import com.lodestarapp.cs491.lodestar.Adapters.PlacesToSeeAdapter;
import com.lodestarapp.cs491.lodestar.Controllers.NearMeController;
import com.lodestarapp.cs491.lodestar.Controllers.PlacesToSeeController;
import com.lodestarapp.cs491.lodestar.Interfaces.LodeStarServerCallback;
import com.lodestarapp.cs491.lodestar.Models.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap googleMap;
    String TAG = "restaurants";
    private NearMeController nmc;
    private String[] venueImageURL =  new String[10];
    private List<Places> restList = new ArrayList<>();
    private LinearLayout ll1;
    private RecyclerView recyclerView1;
    private LinearLayoutManager layoutManager;
    private PlacesToSeeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
        recyclerView1 = findViewById(R.id.near_me_recycler1);
        ll1 = findViewById(R.id.ll1);
        recyclerView1.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(layoutManager);
        adapter = new PlacesToSeeAdapter(restList,this.getApplicationContext());
        recyclerView1.setAdapter(adapter);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.addMarker(new MarkerOptions()
        //    .position(new LatLng(0,0))
        //    .title("Marker"));

        boolean locationPermissionGiven = checkForLocationPermission();

        if (locationPermissionGiven) {
            this.googleMap.setMyLocationEnabled(true);
            getLastKnowLocation(false, null);
        } else {
            Log.d(TAG, "ARE YOU HERE");
            requestPermissionForLocation();
            getLastKnowLocation(false, null);
        }
    }

    private void getLastKnowLocation(boolean which, Location location) {
        if (!which) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d(TAG, "not given");
                return;
            }
            Log.d(TAG, "given");
            final Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            final Location[] myLocation = new Location[1];
            final boolean[] done = {false};
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        done[0] = true;
                        myLocation[0] = locationResult.getResult();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                myLocation[0].getLatitude(), myLocation[0].getLongitude()), 14));

                        googleMap.addMarker((new MarkerOptions().
                                position(new LatLng(myLocation[0].getLatitude(), myLocation[0].getLongitude()))
                                .title("You are Here")));

                        nmc = new NearMeController("", true, myLocation[0]);
                        nmc.setLimit(5);
                        nmc.getNearMeInformation(getApplicationContext(),
                                "restaurant", new NearMeController.VolleyCallback5() {
                                        @Override
                                        public void onSuccess(JSONObject result) {
                                            try {
                                                parseTheJSONObject(result);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            }
                                            });
                    }
                    //complete else
                }
            });

        }
        else{
            final Location[] myLocation = new Location[1];
            myLocation[0] = location;

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    myLocation[0].getLatitude(), myLocation[0].getLongitude()), 14));

            googleMap.addMarker((new MarkerOptions().
                    position(new LatLng(myLocation[0].getLatitude(), myLocation[0].getLongitude()))
                    .title("You are Here")));

            nmc = new NearMeController("", true, myLocation[0]);
            nmc.setLimit(5);
            nmc.getNearMeInformation(getApplicationContext(),
                    "restaurant", new NearMeController.VolleyCallback5() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                parseTheJSONObject(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });
        }
    }

    private boolean checkForLocationPermission() {
        int locationPermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        return locationPermissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForLocation() {
        Log.d(TAG, "request");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
    }

    private void parseTheJSONObject(JSONObject jo) throws JSONException {
        JSONArray groups = jo.getJSONArray("groups");
        JSONArray items = groups.getJSONObject(0).getJSONArray("items");
        Log.d("near me", items.toString());

        JSONObject venue; //name
        JSONObject location; //location
        JSONArray categories; // type

        String placeName;
        String placeLocation;
        String placeType;
        String placeRating;
        String placeId;

        LatLng placeLatLng;
        String numberOfReviews;
        String photoURL;
        String iconURL;

        final Bitmap[] imgBitmap = new Bitmap[1];

        for (int i = 0; i < items.length(); i++){
            venue = items.getJSONObject(i).getJSONObject("venue");
            placeName = venue.getString("name");

            numberOfReviews = items.getJSONObject(i).getString("reviewCount");

            location = venue.getJSONObject("location");
            placeId = venue.getString("id");
            //Log.d(TAG, location.toString());

            if(venue.has("rating")){
                placeRating = venue.getDouble("rating") + "";
            }
            else
                placeRating = "0";
            //Log.d("rating", placeRating);


            placeLatLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));

            //Add marker to google maps
            //googleMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));

            try{
                placeLocation = location.getString("address");
            }catch (JSONException jsonException){
                placeLocation = "Address not available";
            }

            categories = venue.getJSONArray("categories");
            placeType = categories.getJSONObject(0).getString("name");

            photoURL = items.getJSONObject(i).getString("venueImage");
            this.venueImageURL[i] = photoURL;
            iconURL = categories.getJSONObject(0).getJSONObject("icon").getString("prefix") + "bg_64" +
                    categories.getJSONObject(0).getJSONObject("icon").getString("suffix");

            //getPlacesIcons(iconURL);
            googleMap.addMarker(new MarkerOptions()
                    .position(placeLatLng)
                    .title(placeName));

            final int finalI = i;
            //Log.d("iconn", iconURL);
            final String finalPlaceName = placeName;
            final String finalPlaceType = placeType;
            final String finalPlaceLocation = placeLocation;
            final String finalNumberOfReviews = numberOfReviews;
            final String finalPlaceRating = placeRating;
            final String fplaceId = placeId;
            final String finalIconURL = iconURL;
            nmc.getPlaceImage(this.venueImageURL[i], getApplicationContext(), new NearMeController.VolleyCallback6() {
                @Override
                public void onSuccess(Bitmap result) {
                    imgBitmap[0] = result;
                    Places pl = new Places(imgBitmap[0], finalPlaceName, finalPlaceLocation, finalPlaceType, finalPlaceRating, null, finalPlaceRating, fplaceId);
                    restList.add(pl);
                    ll1.setVisibility(View.GONE);
                    recyclerView1.setVisibility(View.VISIBLE);
                    adapter.notifyItemInserted(restList.indexOf(pl));
                    getPlacesIcon(restList.indexOf(pl), finalIconURL,"restaurant");                   

                }
            });
        }

    }

    private void getPlacesIcon(final int i,String iconURL, final String criter) {
        nmc.getPlaceImage(iconURL, getApplicationContext(),
                new NearMeController.VolleyCallback6() {
                    @Override
                    public void onSuccess(Bitmap result) {
                            restList.get(i).setPlaceIconImage(result);
                            adapter.notifyItemChanged(i);
                    }
                });
    }




}