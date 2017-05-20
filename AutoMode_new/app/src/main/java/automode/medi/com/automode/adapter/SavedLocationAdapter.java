package automode.medi.com.automode.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import automode.medi.com.automode.R;
import automode.medi.com.automode.holder.SavedLocations;

/**
 * Created by ist on 30/4/17.
 */

public class SavedLocationAdapter extends ArrayAdapter<SavedLocations> {

    private List<SavedLocations> savedLocationsArrayList;
    public static ArrayList<Long> selectedUsersList;
    private LayoutInflater mInflater;
    private Context context;
    private int layoutId;

    public SavedLocationAdapter(Context context, int textViewResourceId,
                                    List<SavedLocations> objects) {
        super(context, textViewResourceId, objects);
        Log.d("rfActivity", "RFInformationListAdapter");

        savedLocationsArrayList = objects;
        this.context = context;
        layoutId = textViewResourceId;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        View listView = null;
        TextView id,address,lattitude,longitude,mode;
        final SavedLocations savedLocations = savedLocationsArrayList.get(pos);

        if (convertView == null) {
            // inflating the row
            listView = mInflater.inflate(layoutId, null);
        } else {
            listView = convertView;
        }

        id = (TextView) listView.findViewById(R.id.tv_id);
        address = (TextView) listView.findViewById(R.id.tv_address);
        lattitude = (TextView) listView.findViewById(R.id.tv_lat);
        longitude = (TextView) listView.findViewById(R.id.tv_long);
        mode = (TextView) listView.findViewById(R.id.tv_mode);
        if(savedLocations!=null) {
            if (savedLocations.getUid() != null) {
                id.setText("Id : " + savedLocations.getUid());
            }
            if (savedLocations.getAddress() != null) {
                address.setText("Address : " + savedLocations.getAddress());
            }
            if (savedLocations.getLattitude() != null) {
                lattitude.setText("Latitude : " +String.format("%.4f", savedLocations.getLattitude()));
            }
            if (savedLocations.getLongitude() != null) {
                longitude.setText("Longitude : "+String.format("%.4f", savedLocations.getLongitude()));
            }
            if (savedLocations.getMode() != null) {
                if(savedLocations.getMode()==0){
                    mode.setText("Mode : Silent");
                }
                if(savedLocations.getMode()==1){
                    mode.setText("Mode : Vibrate" );
                }
                if(savedLocations.getMode()==2){
                    mode.setText("Mode : Ringer");
                }

            }
        }

        return listView;
    }
}
