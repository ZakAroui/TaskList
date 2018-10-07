package com.ikazme.tasklist.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by ikazme on 9/3/18.
 */

public class PermissionsService {

    private static PermissionsService permissionsService;

    private String [] audioRecordPermission = {Manifest.permission.RECORD_AUDIO};


    private PermissionsService() {
    }

    public static PermissionsService getInstance(){
        if (permissionsService == null){
            permissionsService = new PermissionsService();
        }
        return permissionsService;
    }




    public boolean hasOrRequestRecordAudioPerm(AppCompatActivity activity, int reqCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(audioRecordPermission, reqCode);
            return false;
        } else {
            return true;
        }
    }

}
