package automode.medi.com.automode;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import automode.medi.com.automode.holder.SavedLocations;
import automode.medi.com.automode.utils.AppUtils;

/**
 * Created by ist on 22/4/17.
 */

public class InputModeActivity extends Activity implements View.OnClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private MapFragment mapFrag;
    Context mContext;
    public String TOAST_UNABLE_TO_INITIALISE_MAP_FRAG = "Unable to initialize Google mapFragment";
    public String TOAST_UNABLE_TO_INITIALISE_MAP = "Unable to initialize Google map";
    public static EditText tvCurrentLocation;
    private ImageView imgSpeaker,savedButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Marker userSelectionMarker;
    SavedLocations savedLocationsHolder=null;
    List<Address> addressListMain;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inputmode);
        mContext=InputModeActivity.this;
        initWidgets();
        loadMap();
        setKeyboardListenerInSearchBox();

    }
    private void initWidgets() {
        tvCurrentLocation = (EditText) findViewById(R.id.tv_current_location);
        imgSpeaker = (ImageView) findViewById(R.id.img_speaker);
        savedButton=(ImageView)findViewById(R.id.tv_input_mode_save);
        savedButton.setOnClickListener(this);
        ((LinearLayout) (imgSpeaker.getParent())).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                convertSpeechToText();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CurrentMode", "Entered " + requestCode + " ," + resultCode);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    Log.d("CurrentMode", "Entered ");
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvCurrentLocation.setText(result.get(0));
                    new FindLatLongFromAddress().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, result.get(0));
                }
                break;
            default:
                Log.d("CurrentMode", "Entered(Default) ");

        }
    }


    private void loadMap() {
        mapFrag = getMapFragment();
        if (mapFrag != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mapFrag.getView().getLayoutParams();
            int topMargin = ((screenHeight(mContext)) / 2) - ((screenHeight(mContext)) / 2);
            params.topMargin = -topMargin;
            mapFrag.getView().setLayoutParams(params);
        }
        if (mapFrag != null) {
            map = mapFrag.getMap();
        } else {
            Toast.makeText(mContext, TOAST_UNABLE_TO_INITIALISE_MAP_FRAG, Toast.LENGTH_LONG).show();
            return;
        }
        if (map == null) {
            Toast.makeText(mContext, TOAST_UNABLE_TO_INITIALISE_MAP, Toast.LENGTH_LONG).show();
            return;
        }

        LocationManager lManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location_gps=null;
        Location location_network=null;
        if(lManager!=null) {
             location_gps = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             location_network = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        map.clear();
        map.setOnMarkerClickListener(this);
        // map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        if(location_gps!=null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location_gps.getLatitude(), location_gps.getLongitude()), 15));
           LatLng mLatLng = new LatLng(location_gps.getLatitude(), location_gps.getLongitude());
            if(userSelectionMarker!=null) {
                userSelectionMarker.remove();
            }
            userSelectionMarker = map.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green_drivetest)).anchor(0.55f, 0.55f));
//              tvCurrentLocation.setText(AppUtils.getInstance(mContext).getAddressFromLocation(mContext,location_gps));
        }
        else{
            if(location_network!=null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location_network.getLatitude(), location_network.getLongitude()), 15));
                LatLng mLatLng = new LatLng(location_network.getLatitude(), location_network.getLongitude());
                if(userSelectionMarker!=null) {
                    userSelectionMarker.remove();
                }
                userSelectionMarker = map.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green_drivetest)).anchor(0.55f, 0.55f));
//                tvCurrentLocation.setText(AppUtils.getInstance(mContext).getAddressFromLocation(mContext,location_network));
            }
        }
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location loc) {

            }
        });
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                sharingResult(mContext, mInflater, false);
            }
        });
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg) {
                // LatLng l = arg.target;
            }
        });

    }

    private MapFragment getMapFragment() {
        FragmentManager fm = null;
        Log.d("CurrentMode", "sdk: " + Build.VERSION.SDK_INT);
        Log.d("CurrentMode", "release: " + Build.VERSION.RELEASE);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            Log.d("CurrentMode", "using getFragmentManager");
            fm = getFragmentManager();
        } else {
            Log.d("CurrentMode", "using getChildFragmentManager");
            fm = getFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.map_fragment_second);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_input_mode_save: {
                if(addressListMain!=null && addressListMain.size()>0) {
                    setDataOfSavedLocationInDb(addressListMain);
                    finish();
                }
                else{
                    Toast.makeText(mContext,"Please enter Address",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    @SuppressWarnings("deprecation")
    public int screenHeight(Context context) {
        int screenHeight;
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        return screenHeight;

    }

    public  int screenWidth(Context context) {
        int screenWidth;
        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        return screenWidth;

    }
    private void setKeyboardListenerInSearchBox() {
        // set listener to search box
        tvCurrentLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d("CurrentMode", "actionId: " + actionId + " SearchCode: " + EditorInfo.IME_ACTION_SEARCH);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.d("CurrentMode", "action search");
                    String searchedLocation = tvCurrentLocation.getText().toString();
                    Log.d("CurrentMode", "locationInEditText: " + searchedLocation);
                    hideSoftKeyboard(tvCurrentLocation.getWindowToken());
                    boolean isLocationEmpty = searchedLocation.isEmpty();
                    Log.d("CurrentMode", "isLocationEmpty: " + isLocationEmpty);
                    if (!isLocationEmpty) {
                        new FindLatLongFromAddress().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchedLocation);
                    } else {
                        Toast.makeText(mContext, "Location cannot be empty", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }
    public void hideSoftKeyboard(IBinder iBinder) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
    }
    private class FindLatLongFromAddress extends AsyncTask<String, String, String> {

        List<Address> addressList;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Searching location...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("CurrentMode", "FindLatLongFromAddress: typed address: " + params[0]);
            addressList = AppUtils.getInstance(mContext).getLocationByAddress(params[0], mContext);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("CurrentMode", "FindLatLongFromAddress: onPostExecute()");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (addressList != null && addressList.size() > 0) {
                map.clear();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude()), 15);
                map.animateCamera(cameraUpdate);
                if(userSelectionMarker!=null) {
                    userSelectionMarker.remove();
                }
                LatLng mLatLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                userSelectionMarker = map.addMarker(new MarkerOptions().position(mLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green_drivetest)).anchor(0.55f, 0.55f));
                addressListMain=addressList;
            } else {
                Toast.makeText(mContext, "Location not found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void convertSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "speech_not_supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDataOfSavedLocationInDb(List<Address> addressList){
       savedLocationsHolder=new SavedLocations();
        savedLocationsHolder.setAddress(addressList.get(0).getAddressLine(0));
        savedLocationsHolder.setLattitude(addressList.get(0).getLatitude());
        savedLocationsHolder.setLongitude(addressList.get(0).getLongitude());
        savedLocationsHolder.setMode(0);
        DbController.getInstance(mContext).insertInSavedLocationTable(savedLocationsHolder);
    }

}
