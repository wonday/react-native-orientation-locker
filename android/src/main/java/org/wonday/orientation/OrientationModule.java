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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;
import android.view.OrientationEventListener;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.hardware.SensorManager;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;


@ReactModule(name = "Orientation")
public class OrientationModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    final BroadcastReceiver uiOrientationChangeRecevier;
    final OrientationEventListener deviceOrientationListener;
    private String lastDeviceOrientationValue = "";
    final ReactApplicationContext ctx;

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        ctx = reactContext;
        setInitialDeviceOrientation(reactContext);

        deviceOrientationListener = new OrientationEventListener(reactContext, SensorManager.SENSOR_DELAY_UI) {

            @Override
            public void onOrientationChanged(int orientation) {
                FLog.d(ReactConstants.TAG,"DeviceOrientation changed to " + orientation);

                String deviceOrientationValue = getDeviceOrientationString(orientation);

                if (!lastDeviceOrientationValue.equals(deviceOrientationValue)) {
                    lastDeviceOrientationValue = deviceOrientationValue;

                    WritableMap params = Arguments.createMap();
                    params.putString("deviceOrientation", deviceOrientationValue);
                    sendEvent("deviceOrientationDidChange", params);
                }
            }

            private String getDeviceOrientationString(int degrees) {
                if (degrees == -1) {
                    return "UNKNOWN";
                } else if (degrees > 355 || degrees < 5) {
                    return "PORTRAIT";
                } else if (degrees > 85 && degrees < 95) {
                    return "LANDSCAPE-RIGHT";
                } else if (degrees > 175 && degrees < 185) {
                    return "PORTRAIT-UPSIDEDOWN";
                } else if (degrees > 265 && degrees < 275) {
                    return "LANDSCAPE-LEFT";
                }
                return lastDeviceOrientationValue;
            }
        };

        if (deviceOrientationListener.canDetectOrientation()) {
           FLog.d(ReactConstants.TAG, "orientation detect enabled.");
           deviceOrientationListener.enable();
        } else {
           FLog.d(ReactConstants.TAG, "orientation detect disabled.");
           deviceOrientationListener.disable();
        }

        uiOrientationChangeRecevier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String orientationValue = getCurrentUIOrientation();

                FLog.d(ReactConstants.TAG,"UI Orientation changed to " + orientationValue);

                WritableMap params = Arguments.createMap();
                params.putString("orientation", orientationValue);
                sendEvent("orientationDidChange", params);
            }
        };

        reactContext.addLifecycleEventListener(this);
    }

    private void setInitialDeviceOrientation(ReactApplicationContext reactContext) {
        int orientation = reactContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lastDeviceOrientationValue = "LANDSCAPE-LEFT";
        } else {
            lastDeviceOrientationValue = "PORTRAIT";
        }
    }

    @Override
    public String getName() {
        return "Orientation";
    }

    private String getCurrentUIOrientation() {
        final Display display = ((WindowManager) getReactApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return "PORTRAIT";
            case Surface.ROTATION_90:
                return "LANDSCAPE-LEFT";
            case Surface.ROTATION_180:
                return "PORTRAIT-UPSIDEDOWN";
            case Surface.ROTATION_270:
                return "LANDSCAPE-RIGHT";
        }
        return "UNKNOWN";
    }

    @ReactMethod
    public void getOrientation(Callback callback) {
        String orientation = getCurrentUIOrientation();
        callback.invoke(orientation);
    }

    @ReactMethod
    public void getDeviceOrientation(Callback callback) {
        callback.invoke(lastDeviceOrientationValue);
    }

    public void lockAndSendEvent(int activityOrientation, String orientation) {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(activityOrientation);

        // send a locked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", orientation);
        sendEvent("lockDidChange", lockParams);
    }

    @ReactMethod
    public void lockToPortrait() {
        lockAndSendEvent(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, "PORTRAIT");
    }

    @ReactMethod
    public void lockToPortraitUpsideDown() {
        lockAndSendEvent(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, "PORTRAIT-UPSIDEDOWN");
    }

    @ReactMethod
    public void lockToLandscape() {
        lockAndSendEvent(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, "LANDSCAPE-LEFT");
    }

    @ReactMethod
    public void lockToLandscapeLeft() {
        lockAndSendEvent(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, "LANDSCAPE-LEFT");
    }

    @ReactMethod
    public void lockToLandscapeRight() {
        lockAndSendEvent(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, "LANDSCAPE-RIGHT");
    }

    @ReactMethod
    public void unlockAllOrientations() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        // send a unlocked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", "UNKNOWN");
        sendEvent("lockDidChange", lockParams);
    }

    @ReactMethod
    public void getAutoRotateState(Callback callback) {
      final ContentResolver resolver = ctx.getContentResolver();
      boolean rotateLock = Settings.System.getInt(resolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
      callback.invoke(rotateLock);
    }

    private void sendEvent(String eventName, WritableMap params) {
        if (ctx.hasActiveCatalystInstance()) {
            ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
        }
    }

    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();

        String orientation = getCurrentUIOrientation();
        constants.put("initialOrientation", orientation);

        return constants;
    }

    @Override
    public void onHostResume() {
        FLog.i(ReactConstants.TAG, "orientation detect enabled.");
        deviceOrientationListener.enable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.registerReceiver(uiOrientationChangeRecevier, new IntentFilter("onConfigurationChanged"));
    }

    @Override
    public void onHostPause() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        deviceOrientationListener.disable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        try {
            activity.unregisterReceiver(uiOrientationChangeRecevier);
        } catch (IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
        }
    }

    @Override
    public void onHostDestroy() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        deviceOrientationListener.disable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        try {
            activity.unregisterReceiver(uiOrientationChangeRecevier);
        } catch (IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
        }
    }
}
