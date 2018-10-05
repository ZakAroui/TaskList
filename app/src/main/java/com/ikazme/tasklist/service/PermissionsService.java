package com.ikazme.tasklist.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by ikazme on 9/3/18.
 */

public class PermissionsService {

    private static PermissionsService permissionsService;

    private boolean permissionToRecordAccepted = false;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionToRecordAccepted) {
            activity.requestPermissions(audioRecordPermission, reqCode);
            return false;
        } else {
            return true;
        }
    }

    public boolean hasRecordAudioPerm(AppCompatActivity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }


    public void setPermissionToRecordAccepted(boolean permissionToRecordAccepted) {
        this.permissionToRecordAccepted = permissionToRecordAccepted;
    }

}
