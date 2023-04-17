package com.example.adaptive;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

public class ExternalUtils {
    /**
     * 需要申请外部存储权限
     * 返回最后一张图片的地址
     */
    public static String getImagePath(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        Log.d("Daisy", MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        String imagePath = null;
        while (cursor.moveToNext()) {
            imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA));
            Log.d("Daisy", "图片地址：" + imagePath);
        }

        return imagePath;
    }
}
