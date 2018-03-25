package com.framgia.music.utils.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.framgia.music.utils.Constant;

/**
 * Created by Admin on 3/24/2018.
 */

public final class PermissionUtils {

    private PermissionUtils() {
        //No-op
    }

    public static boolean requestPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, Constant.REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        Constant.REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        } else {
            return true;
        }
        return false;
    }
}
