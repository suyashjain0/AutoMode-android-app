package automode.medi.com.automode.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import automode.medi.com.automode.DbController;
import automode.medi.com.automode.holder.SavedLocations;
import automode.medi.com.automode.recievers.ScreenService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by ist on 23/4/17.
 */

public class AppUtils {
    private static AppUtils _instance;
    private final static String TAG = AppUtils.class.getSimpleName();
    Context mContext;


    public static AppUtils getInstance(Context pContext) {
        if (_instance == null) {
            _instance = new AppUtils(pContext);
        }
        return _instance;
    }
    public AppUtils(Context context) {
        this.mContext = context;
    }

    public  List<Address> getLocationByAddress(String address, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            address = address.replaceAll(" ", "%20");
            HttpsURLConnection connection = null;
            String url = "https://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false";
            URL u = new URL(url);
            connection = (HttpsURLConnection) u.openConnection();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            stringBuilder = new StringBuilder();
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

        List<Address> res = new ArrayList<Address>();
        res = getAddrByWeb(jsonObject);
        return res;
    }
    @SuppressLint("UseValueOf")
    private  List<Address> getAddrByWeb(JSONObject jsonObject) {
        List<Address> res = new ArrayList<Address>();
        try {
            JSONArray array = (JSONArray) jsonObject.get("results");
            for (int i = 0; i < array.length(); i++) {
                Double lon = new Double(0);
                Double lat = new Double(0);
                String name = "";
                try {
                    lon = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    lat = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    name = array.getJSONObject(i).getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setLatitude(lat);
                    addr.setLongitude(lon);
                    addr.setAddressLine(0, name != null ? name : "");
                    res.add(addr);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return res;
    }

    public  String getAddressFromLocation(Context context, Location location) {
        String adrs = "";
        String address[] = getAddressByLatLong(context, location.getLatitude(), location.getLongitude()).trim().split(",");
        if (address != null) {
            if (address.length >= 5) {
                adrs = address[2].replaceAll("[0-9]", "") + "," + address[3].replaceAll("[0-9]", "") + "," + address[4].replaceAll("[0-9]", "");
            } else if (address.length >= 4) {
                adrs = address[2].replaceAll("[0-9]", "") + "," + address[3].replaceAll("[0-9]", "");
            } else if (address.length >= 3) {
                adrs = address[2].replaceAll("[0-9]", "");
            }

            adrs = formatLocation(adrs);

        }
        if (adrs.startsWith(",")) {
            adrs = adrs.substring(1, adrs.length());
        }

        return adrs.trim();
    }


    public static String getAddressByLatLong(Context mContext, Double latitute, Double longitute) {
        HttpsURLConnection connection = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL u = new URL("https://maps.google.com/maps/api/geocode/json?latlng=" + latitute + "," + longitute + "&sensor=true");
            connection = (HttpsURLConnection) u.openConnection();
            InputStream stream = new BufferedInputStream(connection.getInputStream());
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (Exception e) {
            Log.d("findLocation", "getLocationAddress():Exeception: " + e.getMessage());
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getaddressFromJSONObject(jsonObject);
    }
    private static String formatLocation(String address) {

        if (address != null && address.trim().length() > 0) {

            if (address.startsWith(","))
                return formatLocation(address.substring(1));
        }

        return address;

    }
    private static String getaddressFromJSONObject(JSONObject object) {
        JSONObject location;
        String location_string = "";
        try {
            location = object.getJSONArray("results").getJSONObject(0);
            location_string = location.getString("formatted_address");
        } catch (Exception e1) {
            e1.printStackTrace();

        }
        return location_string;

    }

    public String sendRegistrationCallDataToServer(String url,String user, String pass,String cpass,String email) throws IOException {
        Log.d(TAG, "getJson()");
        Integer statusCode = null;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .build();
            Request request = null;
            request = new Request.Builder()
                    .url(url)
                    .addHeader("userId",user)
                    .addHeader("email",email)
                    .addHeader("password",pass)
                    .addHeader("confirmPassword",cpass)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string().toString();
            statusCode = response.code();

            Log.d("uploadFile", "result=" + result);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static ComponentName isServiceRunning(Context context, String serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.equals(service.service.getClassName())) {
                return service.service;
            }
        }
        return null;
    }
    public void connectToScreenService(){
        ComponentName serviceRunning = isServiceRunning(mContext, ScreenService.class.getName());
        if (serviceRunning == null) {
            Log.d(TAG, "Service created " );
            Intent intent = new Intent(mContext,ScreenService.class);
            mContext.startService(intent);
        }

    }
    public List<SavedLocations> getDataofSavedLocation(){
        List<SavedLocations> savedLocationsArrayList= DbController.getInstance(mContext).getAllSavedLocations();
        if (savedLocationsArrayList != null) {
            return  savedLocationsArrayList;
        }
        return  null;
    }

    public Boolean getEventToChangeTheModeOfPhone(Double latitude,Double longitude) {
        LocationManager lManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location_gps = null;
        Location location_network = null;
        if (lManager != null) {
            location_gps = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            location_network = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location_gps != null && latitude != null && longitude != null) {
            Location temp = new Location("");
            temp.setLatitude(latitude);
            temp.setLongitude(longitude);
            float distance = location_gps.distanceTo(temp);
            if (distance != 0 && distance < 1000) {
                return true;
            }
        } else if (location_network != null && latitude != null && longitude != null) {
            Location temp = new Location("");
            temp.setLatitude(latitude);
            temp.setLongitude(longitude);
            float distance = location_network.distanceTo(temp);
            if (distance != 0 && distance < 1000) {
                return true;
            }
        }
        return false;
    }

    public void setModeAccordingToSavedLocation(Integer mode) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        if (mode != null && mode == 0) {
            Toast.makeText(mContext, "Silent Mode Enabled", Toast.LENGTH_LONG).show();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if (mode != null && mode == 1) {
            Toast.makeText(mContext, "Vibrate Mode Enabled", Toast.LENGTH_LONG).show();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if (mode != null && mode == 2) {
            Toast.makeText(mContext, "Normal Mode Enabled", Toast.LENGTH_LONG).show();
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }

    }

    public String getTimeTableUrlFormServer(String postUrl,String dayName){
        Log.d("uploadUrl", "url=" + postUrl);

        String sBuilder = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .build();
            Request request;
            request = new Request.Builder()
                    .url(postUrl).addHeader("Content-Type", "text/plain")
                    .addHeader("dayName", dayName)
                    .get().build();
            Response response = client.newCall(request).execute();
            sBuilder = response.body().string().toString();

            Log.d("data", "upload response: " + sBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("data", "response()" + postUrl + " " + e.getMessage());
        }

        return sBuilder;
    }
    public String getEventUrlFormServer(String postUrl,String eventName){
        Log.d("uploadUrl", "url=" + postUrl);

        String sBuilder = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .build();
            Request request;
            request = new Request.Builder()
                    .url(postUrl).addHeader("Content-Type", "text/plain")
                    .addHeader("eventNo", eventName)
                    .get().build();
            Response response = client.newCall(request).execute();
            sBuilder = response.body().string().toString();

            Log.d("data", "upload response: " + sBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("data", "response()" + postUrl + " " + e.getMessage());
        }

        return sBuilder;
    }



}
