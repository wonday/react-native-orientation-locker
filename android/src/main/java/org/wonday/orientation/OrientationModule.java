//
//  react-native-orientation-locker
//  
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//

package org.wonday.orientation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.OrientationEventListener;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class OrientationModule extends ReactContextBaseJavaModule implements LifecycleEventListener{
    
    final BroadcastReceiver receiver;
    final OrientationEventListener mOrientationListener;
    final ReactApplicationContext ctx;
    private boolean isLocked = false;
    private String lastOrientationValue = "";

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.ctx = reactContext;

        mOrientationListener = new OrientationEventListener(reactContext) {

            @Override
            public void onOrientationChanged(int orientation) { 

                FLog.d(ReactConstants.TAG,"Orientation changed to " + orientation);

                String orientationValue = "UNKNOWN";

                if (isLocked == false) {
                    if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                        //on desk, do not known orientation
                        orientationValue = "UNKNOWN";
                    }
                    
                    if (orientation > 315 || orientation < 45) { 
                        orientation = 0;
                        orientationValue = "PORTRAIT";
                    } else if (orientation > 45 && orientation < 135) { 
                        orientation = 90;
                        orientationValue = "LANDSCAPE-LEFT";
                    } else if (orientation > 135 && orientation < 225) { 
                        orientation = 180;
                        orientationValue = "PORTRAIT-UPSIDEDOWN";
                    } else if (orientation > 225 && orientation < 315) { 
                        orientation = 270;
                        orientationValue = "LANDSCAPE-RIGHT";
                    } else {
                        orientationValue = "UNKNOWN";
                    }
                } else {
                    orientationValue = getCurrentOrientation();
                }

                if (!lastOrientationValue.equals(orientationValue)) {
                    lastOrientationValue = orientationValue;

                    WritableMap params = Arguments.createMap();
                    params.putString("orientation", orientationValue);
                    if (ctx.hasActiveCatalystInstance()) {
                        ctx
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("orientationDidChange", params);
                    }

                    FLog.d(ReactConstants.TAG,"Current Orientation:" + orientationValue);
                }

                return;
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
           FLog.d(ReactConstants.TAG, "orientation detect enabled.");
           mOrientationListener.enable();
        } else {
           FLog.d(ReactConstants.TAG, "orientation detect disabled.");
           mOrientationListener.disable();
        }


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Configuration newConfig = intent.getParcelableExtra("newConfig");

                String orientationValue = getCurrentOrientation();

                if (!lastOrientationValue.equals(orientationValue)) {

                    lastOrientationValue = orientationValue;

                    WritableMap params = Arguments.createMap();
                    params.putString("orientation", orientationValue);
                    if (ctx.hasActiveCatalystInstance()) {
                        ctx
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("orientationDidChange", params);
                    }

                    FLog.d(ReactConstants.TAG,"Orientation changed to " + orientationValue);
                }
            }
        };
        ctx.addLifecycleEventListener(this);       
    }

    @Override
    public String getName() {
        return "Orientation";
    }

    private String getCurrentOrientation() {

        final Display display = ((WindowManager) getReactApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return "PORTRAIT";
            case Surface.ROTATION_90:
                return "LANDSCAPE-RIGHT";
            case Surface.ROTATION_180:
                return "PORTRAIT-UPSIDEDOWN";
            case Surface.ROTATION_270:
                return "LANDSCAPE-LEFT";
        }
        return "UNKNOWN";
    }

    @ReactMethod
    public void getOrientation(Callback callback) {

        String orientation = this.getCurrentOrientation();
        callback.invoke(orientation);
    }

    @ReactMethod
    public void lockToPortrait() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.isLocked = true;
    }

    @ReactMethod
    public void lockToPortraitUpsideDown() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        this.isLocked = true;
    }

    @ReactMethod
    public void lockToLandscapeLeft() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.isLocked = true;
    }

    @ReactMethod
    public void lockToLandscapeRight() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        this.isLocked = true;
    }

    @ReactMethod
    public void unlockAllOrientations() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        this.isLocked = false;
    }

    @Override
    public @Nullable Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();

        String orientation = this.getCurrentOrientation();
        constants.put("initialOrientation", orientation);

        return constants;
    }

    @Override
    public void onHostResume() {
        FLog.i(ReactConstants.TAG, "orientation detect enabled.");
        mOrientationListener.enable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.registerReceiver(receiver, new IntentFilter("onConfigurationChanged"));
    }
    @Override
    public void onHostPause() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        mOrientationListener.disable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        try
        {
            activity.unregisterReceiver(receiver);
        }
        catch (java.lang.IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "receiver already unregistered", e);
        }        
    }

    @Override
    public void onHostDestroy() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        mOrientationListener.disable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        try
        {
            activity.unregisterReceiver(receiver);
        }
        catch (java.lang.IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "receiver already unregistered", e);
        }        
    }
}
