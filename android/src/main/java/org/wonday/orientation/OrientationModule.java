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
public class OrientationModule extends ReactContextBaseJavaModule implements LifecycleEventListener{

    final BroadcastReceiver mReceiver;
    final OrientationEventListener mOrientationListener;
    private String lastOrientationValue = "";
    private String lastDeviceOrientationValue = "";
    final ReactApplicationContext ctx;

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        ctx = reactContext;

        mOrientationListener = new OrientationEventListener(reactContext, SensorManager.SENSOR_DELAY_UI) {

            @Override
            public void onOrientationChanged(int orientation) {
                FLog.d(ReactConstants.TAG,"DeviceOrientation changed to " + orientation);

                String deviceOrientationValue = getOrientationString(orientation);

                if (!lastDeviceOrientationValue.equals(deviceOrientationValue)) {

                    lastDeviceOrientationValue = deviceOrientationValue;

                    WritableMap params = Arguments.createMap();
                    params.putString("deviceOrientation", deviceOrientationValue);
                    if (ctx.hasActiveCatalystInstance()) {
                        ctx
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("deviceOrientationDidChange", params);
                    }
                }
            }

            private String getOrientationString(int degrees) {
                String deviceOrientationValue = lastDeviceOrientationValue;

                if (degrees == -1) {
                    deviceOrientationValue = "UNKNOWN";
                } else if (degrees > 355 || degrees < 5) {
                    deviceOrientationValue = "PORTRAIT";
                } else if (degrees > 85 && degrees < 95) {
                    deviceOrientationValue = "LANDSCAPE-RIGHT";
                } else if (degrees > 175 && degrees < 185) {
                    deviceOrientationValue = "PORTRAIT-UPSIDEDOWN";
                } else if (degrees > 265 && degrees < 275) {
                    deviceOrientationValue = "LANDSCAPE-LEFT";
                }
                return deviceOrientationValue;
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
           FLog.d(ReactConstants.TAG, "orientation detect enabled.");
           mOrientationListener.enable();
        } else {
           FLog.d(ReactConstants.TAG, "orientation detect disabled.");
           mOrientationListener.disable();
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String orientationValue = getCurrentOrientation();
                lastOrientationValue = orientationValue;

                FLog.d(ReactConstants.TAG,"Orientation changed to " + orientationValue);

                WritableMap params = Arguments.createMap();
                params.putString("orientation", orientationValue);
                if (ctx.hasActiveCatalystInstance()) {
                    ctx
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("orientationDidChange", params);
                }

            }
        };

        reactContext.addLifecycleEventListener(this);
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
        String orientation = getCurrentOrientation();
        callback.invoke(orientation);
    }

    @ReactMethod
    public void getDeviceOrientation(Callback callback) {
        callback.invoke(lastDeviceOrientationValue);
    }

    @ReactMethod
    public void lockToPortrait() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // force send an UI orientation event
        lastOrientationValue = "PORTRAIT";
        WritableMap params = Arguments.createMap();
        params.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("orientationDidChange", params);
        }

        // send a locked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("lockDidChange", lockParams);
        }
    }

    @ReactMethod
    public void lockToPortraitUpsideDown() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);

        // force send an UI orientation event
        lastOrientationValue = "PORTRAIT-UPSIDEDOWN";
        WritableMap params = Arguments.createMap();
        params.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("orientationDidChange", params);
        }

        // send a locked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("lockDidChange", lockParams);
        }
    }

    @ReactMethod
    public void lockToLandscape() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        // force send an UI orientation event
        lastOrientationValue = "LANDSCAPE-LEFT";
        WritableMap params = Arguments.createMap();
        params.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("orientationDidChange", params);
        }

        // send a locked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("lockDidChange", lockParams);
        }
    }

    @ReactMethod
    public void lockToLandscapeLeft() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // force send an UI orientation event
        lastOrientationValue = "LANDSCAPE-LEFT";
        WritableMap params = Arguments.createMap();
        params.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("orientationDidChange", params);
        }

        // send a locked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("lockDidChange", lockParams);
        }
    }

    @ReactMethod
    public void lockToLandscapeRight() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        // force send an UI orientation event
        lastOrientationValue = "LANDSCAPE-RIGHT";
        WritableMap params = Arguments.createMap();
        params.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("orientationDidChange", params);
        }

        // send a locked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("lockDidChange", lockParams);
        }
    }

    @ReactMethod
    public void unlockAllOrientations() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        //force send an UI orientation event when unlock
        lastOrientationValue = lastDeviceOrientationValue;
        WritableMap params = Arguments.createMap();
        params.putString("orientation", lastOrientationValue);
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("orientationDidChange", params);
        }

        // send a unlocked event
        WritableMap lockParams = Arguments.createMap();
        lockParams.putString("orientation", "UNKNOWN");
        if (ctx.hasActiveCatalystInstance()) {
            ctx
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("lockDidChange", lockParams);
        }
    }

    @ReactMethod
    public void getAutoRotateState(Callback callback) {
      final ContentResolver resolver = ctx.getContentResolver();
      boolean rotateLock = android.provider.Settings.System.getInt(
      resolver,
      android.provider.Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
      callback.invoke(rotateLock);
    }

    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();

        String orientation = getCurrentOrientation();
        constants.put("initialOrientation", orientation);

        return constants;
    }

    @Override
    public void onHostResume() {
        FLog.i(ReactConstants.TAG, "orientation detect enabled.");
        mOrientationListener.enable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.registerReceiver(mReceiver, new IntentFilter("onConfigurationChanged"));
    }

    @Override
    public void onHostPause() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        mOrientationListener.disable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        try
        {
            activity.unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
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
            activity.unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e) {
            FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
        }
    }
}
