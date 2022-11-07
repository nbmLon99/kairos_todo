package com.nbmlon.managemodule;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class PermissionManager {

    public static final int ALBUM_PERMISSION_REQUEST =  200;

    /** 권한 확인하기 (없을 시 요청) **/
    public static boolean GetPermission(Activity activity, String permissionCode, int REQUEST_CODE){
        if(! CheckPermission(activity.getApplicationContext(), permissionCode)){
            //권한 요청
            ActivityCompat.requestPermissions(activity, new String[] {permissionCode}, REQUEST_CODE);
        }

        return CheckPermission(activity.getApplicationContext(), permissionCode );
    }

    public static boolean CheckPermission(Context context, String permissionCode) {
        return (ActivityCompat.checkSelfPermission(context, permissionCode) == PackageManager.PERMISSION_GRANTED);
    }
}
