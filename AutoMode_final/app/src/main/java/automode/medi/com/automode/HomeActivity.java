package automode.medi.com.automode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.lang.reflect.Field;

import automode.medi.com.automode.adapter.SavedLocationAdapter;
import automode.medi.com.automode.utils.AppUtils;


public class HomeActivity extends FragmentActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    ViewPager viewPager;
    LinearLayout layoutLocation, layoutPlaces;
    View locationHighlight, placesHighlight;
    private ImageView tvPlaces, tvLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    ImageButton homeButton,searchButton,savedlocationButton,aboutButton;
    ImageView tvSetting;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_actiivity);
        mContext=HomeActivity.this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        initView();
        AppUtils.getInstance(getApplicationContext()).connectToScreenService();
        resetImagesFormOnCreate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(HomeActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(HomeActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.fragment_container_location_places);
        layoutLocation = (LinearLayout) findViewById(R.id.layout_location);
        layoutLocation.setOnClickListener(this);

        layoutPlaces = (LinearLayout) findViewById(R.id.layout_places);
        layoutPlaces.setOnClickListener(this);

        locationHighlight = (View) findViewById(R.id.location_highlight);
        placesHighlight = (View) findViewById(R.id.places_highlight);
        tvLocation = (ImageView) findViewById(R.id.tv_location);
        tvSetting=(ImageView)findViewById(R.id.tv_setting);
        tvPlaces = (ImageView) findViewById(R.id.tv_places);
        homeButton=(ImageButton)findViewById(R.id.imageButton_home);
        searchButton=(ImageButton)findViewById(R.id.imageButton_search);
        savedlocationButton=(ImageButton)findViewById(R.id.imageButton_fav);
        aboutButton=(ImageButton)findViewById(R.id.imageButton_about);

        homeButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        savedlocationButton.setOnClickListener(this);
        aboutButton.setOnClickListener(this);
        tvSetting.setOnClickListener(this);

        PageListener pageListener = new PageListener();
        viewPager.setOnPageChangeListener(pageListener);
        HomeLocationAdapter adapter = new HomeLocationAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);//to show to do at current position
        changeTabHighlight("location");
        viewPager.setCurrentItem(0);//1st time
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting:
                createPopupMenu(getApplicationContext(),tvSetting);
                break;

            case R.id.layout_location:
                changeTabHighlight("location");
                viewPager.setCurrentItem(0);
                break;
            case R.id.layout_places:
                changeTabHighlight("places");
                viewPager.setCurrentItem(1);
                break;
            case R.id.imageButton_about:
                resetImages();
                Intent intent3 = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent3);
                break;
            case R.id.imageButton_fav:
                Intent intent = new Intent(getApplicationContext(), SavedLocationHistoryActivity.class);
                startActivity(intent);

                break;
            case R.id.imageButton_home:
                resetImages();
                homeButton.setBackgroundResource(R.drawable.home_icon_enable);
                Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.imageButton_search:
                resetImages();
                searchButton.setBackgroundResource(R.drawable.search_icon_enable);
                Intent intent1 = new Intent(getApplicationContext(), InputModeActivity.class);
                startActivity(intent1);
                break;

        }


    }

    public void changeTabHighlight(String tabPosition) {
            if (tabPosition!=null && tabPosition.equalsIgnoreCase("location")) {
                locationHighlight.setBackgroundColor(getResources().getColor(R.color.view_selected_color));
                placesHighlight.setBackgroundColor(getResources().getColor(R.color.Transparent));
            } else if(tabPosition!=null && tabPosition.equalsIgnoreCase("places")) {
                placesHighlight.setBackgroundColor(getResources().getColor(R.color.view_selected_color));
                locationHighlight.setBackgroundColor(getResources().getColor(R.color.Transparent));
            }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    HomeActivity.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Log.d("get", "page selected " + position);
            if (position == 0) {
                changeTabHighlight("location");
            }
            if (position == 1) {
                changeTabHighlight("places");
            }
        }
    }

    private void resetImagesFormOnCreate(){
        homeButton.setBackgroundResource(R.drawable.home_icon_enable);
        aboutButton.setBackgroundResource(R.drawable.about_icon);
        searchButton.setBackgroundResource(R.drawable.search_icon);
        savedlocationButton.setBackgroundResource(R.drawable.favorite_icon);

    }
    private void resetImages(){
        homeButton.setBackgroundResource(R.drawable.home_icon);
        aboutButton.setBackgroundResource(R.drawable.about_icon);
        searchButton.setBackgroundResource(R.drawable.search_icon);
        savedlocationButton.setBackgroundResource(R.drawable.favorite_icon);

    }
    public void createPopupMenu(final Context mContext, View view) {
        try {
            PopupMenu popupMenu;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                popupMenu = new PopupMenu(mContext, view, Gravity.END);
            } else {
                popupMenu = new PopupMenu(mContext, view);
            }
            setTheme(R.style.MenuTheme);
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                }
            });
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // TODO Auto-generated method stub
                    switch (item.getItemId()) {
                        case R.id.menu_ip:
                            Intent intentIpIntent = new Intent(getApplicationContext(), IpPortActivity.class);
                            startActivity(intentIpIntent);
                            return true;
                        case R.id.log_out:
                            Intent intentlogout = new Intent(getApplicationContext(), AccountActivity.class);
                            startActivity(intentlogout);
                            break;

                    }
                    return false;
                }
            });
            popupMenu.inflate(R.menu.setting_menu);

            Object menuHelper;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                popupMenu.show();
                return;
            }
            popupMenu.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(mContext==HomeActivity.this){
            //THIS BLOCK WILL NOT DO ANYTHING AND WOULD DISABLE BACK BUTTON

        }else{
            super.onBackPressed();
//THIS BLOCK WILL BE CALLED IF ABOVE COND IS FALSE, AND WOULD ENABLE BACK BUTTON
        }

    }


}


