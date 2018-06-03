package com.qoovers.snapwhyb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Storage
{
    private static final String TAG = Storage.class.getSimpleName();

    private static final String EXTERNAL_STORAGE_DIRECTORY_PHOTO = "/SnapWHYB"; // sub-folder inside "Pictures" folder
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static String generateUniqueFileName() {
        String timestamp = String.valueOf(Time.getUnixTimestamp());
        return timestamp + android.os.Build.SERIAL + JPEG_FILE_SUFFIX;
    }

    private static File getPhotoStorageDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + EXTERNAL_STORAGE_DIRECTORY_PHOTO);
    }

    public static File getPhoto(String fileName) {
        return new File(getPhotoStorageDirectory(), fileName);
    }

    public static void savePhoto(Context context, String fileName, int resultCode) {
        Bitmap bm = Camera.getTmpImageFromResult(context, resultCode);

        File directory = getPhotoStorageDirectory();
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create external storage directory");
            }
        }

        File path = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deletePhoto(String fileName) {
        File file = getPhoto(fileName);
        if(file.exists()) {
            if(file.delete()) {
                return true;
            }
        }

        return false;
    }
}
