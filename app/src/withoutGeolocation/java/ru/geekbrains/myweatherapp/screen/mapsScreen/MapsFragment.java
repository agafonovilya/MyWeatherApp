package ru.geekbrains.myweatherapp.screen.maps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.myweatherapp.R;
import ru.geekbrains.myweatherapp.StartFragment;
import ru.geekbrains.myweatherapp.screen.main.MainScreenFragment;

public class MapsFragment extends Fragment {
    private static final String TAG = "WEATHER";
    private static final int PERMISSION_REQUEST_CODE = 10;


    private AutocompleteSupportFragment autocompleteSupportFragment;
    private Geocoder geocoder;
    String cityName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       String apiKey = "AIzaSyC9KGqgZtgWC_4ldt4GERHgY_cNAf8K6d8";
        if (!Places.isInitialized()) {
            Log.d(TAG, "!Places.isInitialized() ");
            Places.initialize(requireContext().getApplicationContext(), apiKey);
        }

        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        initAutocompleteSupportFragment();

    }



    private void initAutocompleteSupportFragment() {
        autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.mapsScreen_autocompleteFragment);
        autocompleteSupportFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.d(TAG, "onPlaceSelected: ");

                cityName = place.getName();

                if (cityName != null) {
                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    Log.d(TAG, "onPlaceSelected: lat:" + latitude + ", lon: " + longitude);
                    Bundle result = new Bundle();
                    result.putString("lat", String.valueOf(latitude));
                    result.putString("lon", String.valueOf(longitude));
                    result.putString("cityName", cityName);
                    getParentFragmentManager().setFragmentResult("coord", result);
                    ((StartFragment) requireActivity()).startFragment(new MainScreenFragment());
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "onError: " + status);
            }
        });
    }














    private void setCityNameFromCoordinates(double lat, double lng) {
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            cityName = addressList.get(0).getLocality();
            Log.d(TAG, "onLocationChanged: " + cityName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}