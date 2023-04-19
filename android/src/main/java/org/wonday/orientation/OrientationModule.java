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
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
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

  final ReactApplicationContext ctx;

  private @Nullable BroadcastReceiver mReceiver = null;
  private @Nullable OrientationEventListener mOrientationListener  = null;

  private String lastDeviceOrientationValue = "";

  public OrientationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    ctx = reactContext;
  }

  private void createListenersIfNonExists() {
    if (mOrientationListener != null) {
      return;
    }

    FLog.i(ReactConstants.TAG, "creating orientation listeners.");
    mOrientationListener = new OrientationEventListener(this.ctx, SensorManager.SENSOR_DELAY_UI) {

      @Override
      public void onOrientationChanged(int orientation) {

        FLog.d(ReactConstants.TAG, "DeviceOrientation changed to " + orientation);

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

        FLog.d(ReactConstants.TAG, "Orientation changed to " + orientationValue);

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

  @NonNull
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

  private void applyRequestedOrientation(int orientationActivityInfo, String orientationEventValue, String lockEventValue) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      return;
    }

    activity.setRequestedOrientation(orientationActivityInfo);

    if (ctx.hasActiveCatalystInstance()) {
      DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter = ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

      WritableMap orientationEventData = Arguments.createMap();
      orientationEventData.putString("orientation", orientationEventValue);
      eventEmitter.emit("orientationDidChange", orientationEventData);

      WritableMap lockEventData = Arguments.createMap();
      lockEventData.putString("orientation", lockEventValue);
      eventEmitter.emit("lockDidChange", lockEventData);
    }
  }

  private void lockOrientation(int orientationActivityInfo, String orientationValue) {
    applyRequestedOrientation(orientationActivityInfo, orientationValue, orientationValue);
  }

  @ReactMethod
  public void lockToPortrait() {
    lockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, "PORTRAIT");
  }

  @ReactMethod
  public void lockToPortraitUpsideDown() {
    lockOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, "PORTRAIT-UPSIDEDOWN");
  }

  @ReactMethod
  public void lockToLandscape() {
    lockOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, "LANDSCAPE-LEFT");
  }

  @ReactMethod
  public void lockToLandscapeLeft() {
    lockOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, "LANDSCAPE-LEFT");
  }

  @ReactMethod
  public void lockToLandscapeRight() {
    lockOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE, "LANDSCAPE-RIGHT");
  }

  @ReactMethod
  public void unlockAllOrientations() {
    applyRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR, lastDeviceOrientationValue, "UNKNOWN");
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
  public @Nullable
  Map<String, Object> getConstants() {
    HashMap<String, Object> constants = new HashMap<>();

    String orientation = getCurrentOrientation();
    constants.put("initialOrientation", orientation);

    return constants;
  }

  @Override
  public void start() {
    FLog.i(ReactConstants.TAG, "orientation detect enabled.");
    createListenersIfNonExists();
    mOrientationListener.enable();

    final Activity activity = getCurrentActivity();
    if (activity == null) return;
    activity.registerReceiver(mReceiver, new IntentFilter("onConfigurationChanged"));
  }

  @Override
  public void stop() {
    FLog.d(ReactConstants.TAG, "orientation detect disabled.");
    createListenersIfNonExists();
    mOrientationListener.disable();

    final Activity activity = getCurrentActivity();
    if (activity == null) return;
    try {
      activity.unregisterReceiver(mReceiver);
    } catch (java.lang.IllegalArgumentException e) {
      FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
    }
  }

  @Override
  public void release() {
    FLog.d(ReactConstants.TAG, "orientation detect disabled.");
    createListenersIfNonExists();
    mOrientationListener.disable();

    final Activity activity = getCurrentActivity();
    if (activity == null) return;
    try {
      activity.unregisterReceiver(mReceiver);
    } catch (java.lang.IllegalArgumentException e) {
      FLog.w(ReactConstants.TAG, "Receiver already unregistered", e);
    }
  }
}
