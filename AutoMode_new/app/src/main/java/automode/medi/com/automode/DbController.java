package automode.medi.com.automode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import automode.medi.com.automode.holder.SavedLocations;

/**
 * Created by ist on 23/4/17.
 */

public class DbController extends SQLiteOpenHelper implements DBConstant {
    private static DbController dbController = null;
    private Context mContext;

    public DbController(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public static DbController getInstance(Context context) {
        if (dbController == null) {
            dbController = new DbController(context);
        }
        return dbController;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_SAVED_LOCATIONS
                + "(uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "address TEXT," +
                "lattitude DOUBLE, " +
                "longitude DOUBLE,"+
        "mode INTEGER)");
        Log.d(TAG, "onCreate(): tables created");
    }

    public Long insertInSavedLocationTable(SavedLocations savedLocations) {
        Log.d(TAG, "insertInSavedLocationTable()");
        ContentValues cv = new ContentValues();
        cv.put("uid", savedLocations.getUid());
        cv.put("address", savedLocations.getAddress());
        cv.put("lattitude", savedLocations.getLattitude());
        cv.put("longitude", savedLocations.getLongitude());
        cv.put("mode", savedLocations.getMode());

        return getWritableDatabase().insert(TABLE_SAVED_LOCATIONS,null,cv);
    }

    public List<SavedLocations> getAllSavedLocations() {
        Log.d(TAG, "getAllSavedLocations()");
        Cursor cursor = selectAll(TABLE_SAVED_LOCATIONS);
        List<SavedLocations> savedLocationsList = new ArrayList<SavedLocations>();
        while (cursor.moveToNext()) {
            try {
                SavedLocations savedLocations = cursorToSavedLocations(cursor);
                savedLocationsList.add(savedLocations);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return savedLocationsList;
    }
    private Cursor selectAll(String tableName) {
        return getReadableDatabase().query(tableName, null, null, null, null, null, null);
    }
    private SavedLocations cursorToSavedLocations(Cursor cursor) {
        SavedLocations savedLocations = new SavedLocations();

        if (checkIfColomnValueIsNull(cursor, "uid")) {
            savedLocations.setUid(null);
        } else {
            savedLocations.setUid(cursor.getInt(cursor.getColumnIndex("uid")));
        }
        if (checkIfColomnValueIsNull(cursor, "address")) {
            savedLocations.setAddress(null);
        } else {
            savedLocations.setAddress(cursor.getString(cursor.getColumnIndex("address")));
        }
        if (checkIfColomnValueIsNull(cursor, "lattitude")) {
            savedLocations.setLattitude(null);
        } else {
            savedLocations.setLattitude(cursor.getDouble(cursor.getColumnIndex("lattitude")));
        }
        if (checkIfColomnValueIsNull(cursor, "longitude")) {
            savedLocations.setLongitude(null);
        } else {
            savedLocations.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
        }
        if (checkIfColomnValueIsNull(cursor, "mode")) {
            savedLocations.setMode(null);
        } else {
            savedLocations.setMode(cursor.getInt(cursor.getColumnIndex("mode")));
        }

        return savedLocations;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private boolean checkIfColomnValueIsNull(Cursor cursor,String colomnName){
        if(cursor.isNull(cursor.getColumnIndex(colomnName))){
            return true;
        }
        return false;
    }

    public long updateMode(Double lat ,Double longi, Integer mode) {
        ContentValues cv = new ContentValues();
        cv.put("mode", mode);
        return update(TABLE_SAVED_LOCATIONS, cv, "lattitude="+lat+" AND longitude="+longi+"",null);
    }
    private long update(String tableName, ContentValues cv, String whereClause, String whereArgs[]) {
        return getWritableDatabase().update(tableName, cv, whereClause, whereArgs);
    }

    public long deleteSavedLocation(Double lat ,Double longi) {
        return  getWritableDatabase().delete(TABLE_SAVED_LOCATIONS, "lattitude="+lat+" AND longitude="+longi+"",null);
    }



}
