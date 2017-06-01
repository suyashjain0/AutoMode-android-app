package automode.medi.com.automode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import automode.medi.com.automode.adapter.SavedLocationAdapter;
import automode.medi.com.automode.holder.SavedLocations;

/**
 * Created by ist on 30/4/17.
 */

public class SavedLocationHistoryActivity extends Activity {
    private Context context;
    private LinearLayout layoutActionBar;
    private LinearLayout layoutHistoryList;
    private List<SavedLocations> listSavedHistory;
    private List<SavedLocations> checkedListSavedHistory = new ArrayList<SavedLocations>();
    public SavedLocationAdapter savedLocationAdapter;
    private ListView lvSavedInfo;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_savedhistory);
        context = SavedLocationHistoryActivity.this;
        initWidgets();
        listSavedHistory = DbController.getInstance(context).getAllSavedLocations();
        for (SavedLocations savedLocations : listSavedHistory) {
            if (savedLocations != null) {
                checkedListSavedHistory.add(savedLocations);
            }
        }

        if (checkedListSavedHistory ==null || checkedListSavedHistory.size()==0) {
            layoutHistoryList.setVisibility(View.GONE);
            findViewById(R.id.layout_no_history).setVisibility(View.VISIBLE);
            return;
        }

        if(checkedListSavedHistory!=null) {
            savedLocationAdapter = new SavedLocationAdapter(context, R.layout.layout_saved_item, checkedListSavedHistory);
        }
//        updateAdapter();
        try {
            if (null!=savedLocationAdapter && lvSavedInfo != null) {
                lvSavedInfo.setAdapter(savedLocationAdapter);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }


    }
    private void initWidgets() {
        layoutHistoryList = (LinearLayout) findViewById(R.id.layout_history_list);

        lvSavedInfo = (ListView) findViewById(R.id.list_savedInformation);

        layoutActionBar = (LinearLayout) findViewById(R.id.layout_action_bar_history);
       lvSavedInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               createPopupMenu(context,view,position);
               return false;
           }
       });

    }

    public void createPopupMenu(final Context mContext,View view,final Integer id) {
        try {
            PopupMenu popupMenu;
            final Double lat,longi;

            lat=checkedListSavedHistory.get(id).getLattitude();
            longi=checkedListSavedHistory.get(id).getLongitude();

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
                        case R.id.menu_normal:
                           long updatedRow= DbController.getInstance(context).updateMode(lat,longi,2);
                            updateAdapter();
                            return true;
                        case R.id.menu_silent:
                            long updatedRow1= DbController.getInstance(context).updateMode(lat,longi,0);
                            updateAdapter();
                            return true;
                        case R.id.menu_vibrate:
                            long updatedRow2=  DbController.getInstance(context).updateMode(lat,longi,1);
                            updateAdapter();
                            return true;
                        case R.id.menu_delete:
                            long deletedRow=  DbController.getInstance(context).deleteSavedLocation(lat,longi);
                            updateAdapter();
                            return true;
                    }
                    return false;
                }
            });
            popupMenu.inflate(R.menu.more_menu);

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
    private void updateAdapter(){
        try {
                if ( checkedListSavedHistory!= null ) {
                    checkedListSavedHistory.clear();
                }
                if(listSavedHistory!=null){
                    listSavedHistory.clear();
                }
                if(savedLocationAdapter!=null){
                    savedLocationAdapter.clear();
                }

            listSavedHistory = DbController.getInstance(context).getAllSavedLocations();
            for (SavedLocations savedLocations : listSavedHistory) {
                if (savedLocations != null) {
                    checkedListSavedHistory.add(savedLocations);
                }
            }

                //cellInfoNeighbourList = getNeighbourCellInfo();

                if (checkedListSavedHistory != null && checkedListSavedHistory.size() > 0) {
                    if (savedLocationAdapter != null) {
                        Log.d("rfActivity", "updateAdapter");
                        if (null!=savedLocationAdapter && lvSavedInfo != null) {
                            lvSavedInfo.setAdapter(savedLocationAdapter);
                        }
                    } else {
                        Log.d("rfActivity", "updateAdapter else");
                        savedLocationAdapter = new SavedLocationAdapter(context, R.layout.layout_saved_item, checkedListSavedHistory);
                        if (null!=savedLocationAdapter && lvSavedInfo != null) {
                            lvSavedInfo.setAdapter(savedLocationAdapter);
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
