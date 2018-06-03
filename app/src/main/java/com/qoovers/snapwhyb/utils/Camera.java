package com.qoovers.snapwhyb.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.qoovers.snapwhyb.BuildConfig;
import com.qoovers.snapwhyb.app.values.CameraImages;

import java.io.File;

public class Camera {
    protected static final String TAG = Camera.class.getSimpleName();

    public static Intent getCameraIntent(Context context) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider", getTmpFile(context)));

        return takePhotoIntent;
    }

    public static Bitmap getTmpImageFromResult(Context context, int resultCode) {
        Bitmap bitmap = null;
        File imageFile = getTmpFile(context);

        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Uri.fromFile(imageFile);
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            int rotation = getRotation(context, uri);
            bitmap = rotate(bitmap, rotation);
        }

        return bitmap;
    }

    public static File getTmpFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), CameraImages.TEMP_IMAGE_NAME);

        imageFile.getParentFile().mkdirs();

        return imageFile;
    }

    private static int getRotation(Context context, Uri imageUri) {
        int rotate = 0;

        try {
            context.getContentResolver().notifyChange(imageUri, null);
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rotate;
    }

    private static Bitmap rotate(Bitmap bitmap, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }
}
