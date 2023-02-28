package kkkb1114.sampleproject.bodytemperature.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionManager {

    private final int PERMISSION_REQUEST_CODE_S = 101;
    private final int PERMISSION_REQUEST_CODE = 100;

    /** 권한 확인 **/
    // 기기 SDK 확인을 여기서 안하기 때문에 사용하기전에 SDK 확인 필요하다.
    public boolean permissionCheck(Context context, String strPermission){
        // 권한이 허용되어있다면 true를 반환, 거부되어있으면 false 반환
        try {
            return ActivityCompat.checkSelfPermission(context, strPermission) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** 권한 요청 **/
    // 기기 SDK 확인을 여기서 안하기 때문에 사용하기전에 SDK 확인 필요하다.
    public void requestPermission(Context context, String[] strPermissions){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                ActivityCompat.requestPermissions((Activity) context, strPermissions, PERMISSION_REQUEST_CODE_S);
            }else {
                ActivityCompat.requestPermissions((Activity) context, strPermissions, PERMISSION_REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
