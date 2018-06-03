package com.qoovers.snapwhyb.app.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.qoovers.snapwhyb.app.models.Photo;
import com.qoovers.snapwhyb.utils.Time;

import java.util.ArrayList;
import java.util.List;

public class PhotosDatabaseHelper extends SQLiteOpenHelper
{
    protected static final String TAG = PhotosDatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "snapwhyb";

    private static final String TABLE_PHOTOS = "photos";

    private static final String KEY_ID = "id";
    private static final String KEY_LOCATION_MODE = "location_mode";
    private static final String KEY_PHOTO_FILE_NAME = "photo_file_name";
    private static final String KEY_PLACE = "place";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " +
                TABLE_PHOTOS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_LOCATION_MODE + " INTEGER," +
                KEY_PHOTO_FILE_NAME + " TEXT," +
                KEY_PLACE + " TEXT," +
                KEY_ADDRESS + " TEXT," +
                KEY_COUNTRY + " TEXT," +
                KEY_DESCRIPTION + " TEXT," +
                KEY_LATITUDE + " TEXT," +
                KEY_LONGITUDE + " TEXT," +
                KEY_CREATED_AT + " INTEGER," +
                KEY_UPDATED_AT + " INTEGER" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    public PhotosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     * Resource
     *
     */

    public List<Photo> index() {
        List<Photo> photos = new ArrayList<Photo>();

        String query = "SELECT * FROM " + TABLE_PHOTOS + " ORDER BY " + KEY_CREATED_AT + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                photo.setLocationMode((c.getColumnIndex(KEY_LOCATION_MODE)));
                photo.setPhotoFileName(c.getString((c.getColumnIndex(KEY_PHOTO_FILE_NAME))));
                photo.setPlace(c.getString((c.getColumnIndex(KEY_PLACE))));
                photo.setAddress(c.getString((c.getColumnIndex(KEY_ADDRESS))));
                photo.setCountry(c.getString((c.getColumnIndex(KEY_COUNTRY))));
                photo.setLatitude(c.getString((c.getColumnIndex(KEY_LATITUDE))));
                photo.setLongitude(c.getString((c.getColumnIndex(KEY_LONGITUDE))));
                photo.setDescription(c.getString((c.getColumnIndex(KEY_DESCRIPTION))));
                photo.setCreatedAt(c.getInt((c.getColumnIndex(KEY_CREATED_AT))));
                photo.setUpdatedAt(c.getInt((c.getColumnIndex(KEY_UPDATED_AT))));
                photos.add(photo);
                // Write to log cat
                StringBuilder sb = new StringBuilder();
                int columnsQty = c.getColumnCount();
                for (int idx=0; idx<columnsQty; ++idx) {
                    sb.append(c.getString(idx));
                    if (idx < columnsQty - 1)
                        sb.append("; ");
                }
                Log.i(TAG, String.format("Row: %d, Values: %s", c.getPosition(), sb.toString()));
            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return photos;
    }

    public long store(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        int unixTimestamp = (int) Time.getUnixTimestamp();

        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_MODE, photo.getLocationMode());
        values.put(KEY_PHOTO_FILE_NAME, photo.getPhotoFileName());
        values.put(KEY_PLACE, photo.getPlace());
        values.put(KEY_ADDRESS, photo.getAddress());
        values.put(KEY_COUNTRY, photo.getCountry());
        values.put(KEY_LATITUDE, photo.getLatitude());
        values.put(KEY_LONGITUDE, photo.getLongitude());
        values.put(KEY_DESCRIPTION, photo.getDescription());
        values.put(KEY_CREATED_AT, unixTimestamp);
        values.put(KEY_UPDATED_AT, unixTimestamp);

        long id = db.insert(TABLE_PHOTOS, null, values);
        db.close();

        return id;
    }

    public List<Photo> show(int id) {
        List<Photo> photos = new ArrayList<Photo>();

        String query = "SELECT * FROM " + TABLE_PHOTOS + " WHERE " + KEY_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                photo.setLocationMode((c.getColumnIndex(KEY_LOCATION_MODE)));
                photo.setPhotoFileName(c.getString((c.getColumnIndex(KEY_PHOTO_FILE_NAME))));
                photo.setPlace(c.getString((c.getColumnIndex(KEY_PLACE))));
                photo.setAddress(c.getString((c.getColumnIndex(KEY_ADDRESS))));
                photo.setCountry(c.getString((c.getColumnIndex(KEY_COUNTRY))));
                photo.setLatitude(c.getString((c.getColumnIndex(KEY_LATITUDE))));
                photo.setLongitude(c.getString((c.getColumnIndex(KEY_LONGITUDE))));
                photo.setDescription(c.getString((c.getColumnIndex(KEY_DESCRIPTION))));
                photo.setCreatedAt(c.getInt((c.getColumnIndex(KEY_CREATED_AT))));
                photo.setUpdatedAt(c.getInt((c.getColumnIndex(KEY_UPDATED_AT))));
                photos.add(photo);

            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return photos;
    }

    public int update(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE, photo.getPlace());
        values.put(KEY_ADDRESS, photo.getAddress());
        values.put(KEY_COUNTRY, photo.getCountry());
        values.put(KEY_DESCRIPTION, photo.getDescription());

        int update = db.update(TABLE_PHOTOS, values, KEY_ID + " = ?", new String[] { String.valueOf(photo.getId()) });

        db.close();

        return update;
    }

    public int destroy(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int delete = db.delete(TABLE_PHOTOS, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();

        return delete;
    }
}
