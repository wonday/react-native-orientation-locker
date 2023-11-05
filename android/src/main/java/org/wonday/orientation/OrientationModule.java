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
import android.hardware.SensorManager;
import android.os.Build;
import android.view.Display;
import android.view.OrientationEventListener;
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

public class OrientationModule extends ReactContextBaseJavaModule implements OrientationListeners {

    final BroadcastReceiver mReceiver;
    final OrientationEventListener mOrientationListener;
    final ReactApplicationContext ctx;
    private boolean isLocked = false;
    private boolean isConfigurationChangeReceiverRegistered = false;
    private String lastOrientationValue = "";
    private String lastDeviceOrientationValue = "";

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        ctx = reactContext;

        mOrientationListener = new OrientationEventListener(reactContext, SensorManager.SENSOR_DELAY_UI) {

            @Override
            public void onOrientationChanged(int orientation) {

                FLog.d(ReactConstants.TAG,"DeviceOrientation changed to " + orientation);

                String deviceOrientationValue = lastDeviceOrientationValue;


                if (orientation == -1) {
                    deviceOrientationValue = "UNKNOWN";
                } else if (orientation > 355 || orientation < 5) {
                    deviceOrientationValue = "PORTRAIT";
                } else if (orientation > 85 && orientation < 95) {
                    deviceOrientationValue = "LANDSCAPE-RIGHT";
                } else if (orientation > 175 && orientation < 185) {
                    deviceOrientationValue = "PORTRAIT-UPSIDEDOWN";
                } else if (orientation > 265 && orientation < 275) {
                    deviceOrientationValue = "LANDSCAPE-LEFT";
                }

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

                String orientationValue = getCurrentOrientation();
                if (!lastOrientationValue.equals(orientationValue)) {
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
        OrientationActivityLifecycle.getInstance().registerListeners(this);
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
        isLocked = true;

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
        isLocked = true;

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
        isLocked = true;

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
        isLocked = true;

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
        isLocked = true;

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
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        isLocked = false;

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
    public @Nullable Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();

        String orientation = getCurrentOrientation();
        constants.put("initialOrientation", orientation);

        return constants;
    }

    @Override
    public void start() {
        FLog.i(ReactConstants.TAG, "orientation detect enabled.");
        mOrientationListener.enable();
        compatRegisterReceiver(ctx, mReceiver, new IntentFilter("onConfigurationChanged"), false);
        isConfigurationChangeReceiverRegistered = true;
    }

    @Override
    public void stop() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        mOrientationListener.disable();
        try {
            if (isConfigurationChangeReceiverRegistered) {
                ctx.unregisterReceiver(mReceiver);
                isConfigurationChangeReceiverRegistered = false;
            }
        } catch (Exception e) {
           FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
        }
    }

    @Override
    public void release() {
        FLog.d(ReactConstants.TAG, "orientation detect disabled.");
        mOrientationListener.disable();

        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        try
        {
            if (isConfigurationChangeReceiverRegistered) {
                activity.unregisterReceiver(mReceiver);
                isConfigurationChangeReceiverRegistered = false;
            }
        }
        catch (Exception e) {
            FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
        }
    }

    @ReactMethod
    public void addListener(String eventName) {
     // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(Integer count) {
     // Keep: Required for RN built in Event Emitter Calls.
    }

    /**
   * Starting with Android 14, apps and services that target Android 14 and use context-registered
   * receivers are required to specify a flag to indicate whether or not the receiver should be
   * exported to all other apps on the device: either RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED
   *
   * <p>https://developer.android.com/about/versions/14/behavior-changes-14#runtime-receivers-exported
   */
    private void compatRegisterReceiver(
        Context context, BroadcastReceiver receiver, IntentFilter filter, boolean exported) {
        if (Build.VERSION.SDK_INT >= 34 && context.getApplicationInfo().targetSdkVersion >= 34) {
        context.registerReceiver(
            receiver, filter, exported ? Context.RECEIVER_EXPORTED : Context.RECEIVER_NOT_EXPORTED);
        } else {
        context.registerReceiver(receiver, filter);
        }
    }
}

  