//
//  react-native-orientation-locker
//
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//
//  TurboModule implementation: extends the codegen-generated
//  `NativeOrientationSpec` (generated from src/NativeOrientation.ts) and is
//  registered through `OrientationPackage` (a TurboReactPackage), so the
//  same code serves both the legacy bridge and the New Architecture.
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
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

@ReactModule(name = OrientationModule.NAME)
public class OrientationModule extends NativeOrientationSpec
        implements LifecycleEventListener {

    public static final String NAME = "Orientation";

    private static final String TAG = "OrientationModule";

    private static final String ON_CONFIGURATION_CHANGED = "onConfigurationChanged";

    private final ReactApplicationContext ctx;
    private final BroadcastReceiver mReceiver;
    private final OrientationEventListener mOrientationListener;

    private boolean isLocked = false;
    private boolean isReceiverRegistered = false;
    private String lastOrientationValue = "";
    private String lastDeviceOrientationValue = "";

    public OrientationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        ctx = reactContext;

        mOrientationListener = new OrientationEventListener(reactContext, SensorManager.SENSOR_DELAY_UI) {

            @Override
            public void onOrientationChanged(int orientation) {

                Log.d(TAG, "DeviceOrientation changed to " + orientation);

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
                    sendEvent("deviceOrientationDidChange", "deviceOrientation", deviceOrientationValue);
                }

                String orientationValue = getCurrentOrientation();
                if (!lastOrientationValue.equals(orientationValue)) {
                    lastOrientationValue = orientationValue;

                    Log.d(TAG, "Orientation changed to " + orientationValue);
                    sendEvent("orientationDidChange", "orientation", orientationValue);
                }
            }
        };

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String orientationValue = getCurrentOrientation();
                lastOrientationValue = orientationValue;

                Log.d(TAG, "Orientation changed to " + orientationValue);
                sendEvent("orientationDidChange", "orientation", orientationValue);
            }
        };

        // Self-managed lifecycle: no manual registration in
        // MainApplication/MainActivity is needed anymore. Users who keep the
        // legacy `OrientationActivityLifecycle` registration are unaffected
        // (it becomes a no-op).
        ctx.addLifecycleEventListener(this);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    private String getCurrentOrientation() {
        final Display display =
                ((WindowManager) getReactApplicationContext().getSystemService(Context.WINDOW_SERVICE))
                        .getDefaultDisplay();

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

    private void sendEvent(String eventName, String key, String value) {
        WritableMap params = Arguments.createMap();
        params.putString(key, value);
        try {
            ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } catch (Exception e) {
            // The JS runtime may already be torn down (e.g. during reload).
            Log.d(TAG, "Not emitting " + eventName + ": " + e.getMessage());
        }
    }

    @Override
    public void configure(boolean disableFaceUpDown) {
        // iOS only (`disableFaceUpDown`); nothing to configure on Android.
    }

    @ReactMethod
    public void getOrientation(Promise promise) {
        promise.resolve(getCurrentOrientation());
    }

    @ReactMethod
    public void getDeviceOrientation(Promise promise) {
        promise.resolve(lastDeviceOrientationValue);
    }

    @ReactMethod
    public void lockToPortrait() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isLocked = true;
        sendLockedEvent("PORTRAIT");
    }

    @ReactMethod
    public void lockToPortraitUpsideDown() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        isLocked = true;
        sendLockedEvent("PORTRAIT-UPSIDEDOWN");
    }

    @ReactMethod
    public void lockToLandscape() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        isLocked = true;

        // SENSOR_LANDSCAPE keeps the current landscape side when already in
        // landscape; only claim LANDSCAPE-LEFT when rotating away from
        // portrait, otherwise the forced event reports the wrong direction.
        String current = getCurrentOrientation();
        String lockedTo = "LANDSCAPE-LEFT";
        if ("LANDSCAPE-LEFT".equals(current) || "LANDSCAPE-RIGHT".equals(current)) {
            lockedTo = current;
        }
        sendLockedEvent(lockedTo);
    }

    @ReactMethod
    public void lockToLandscapeLeft() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        isLocked = true;
        sendLockedEvent("LANDSCAPE-LEFT");
    }

    @ReactMethod
    public void lockToLandscapeRight() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        isLocked = true;
        sendLockedEvent("LANDSCAPE-RIGHT");
    }

    @ReactMethod
    public void lockToAllOrientationsButUpsideDown() {
        // Android has no direct equivalent of iOS'
        // UIInterfaceOrientationMaskAllButUpsideDown; most devices already
        // suppress the 180 degree rotation, so this is intentionally a no-op
        // (matches the previous JS-side behaviour).
    }

    @ReactMethod
    public void unlockAllOrientations() {
        final Activity activity = getCurrentActivity();
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        isLocked = false;

        // force send an UI orientation event when unlock
        String orientationValue = getCurrentOrientation();
        lastOrientationValue = orientationValue;
        sendEvent("orientationDidChange", "orientation", orientationValue);

        // send a unlocked event
        sendEvent("lockDidChange", "orientation", "UNKNOWN");
    }

    @ReactMethod
    public void getAutoRotateState(Promise promise) {
        final ContentResolver resolver = ctx.getContentResolver();
        boolean autoRotate = android.provider.Settings.System.getInt(
                resolver,
                android.provider.Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
        promise.resolve(autoRotate);
    }

    private void sendLockedEvent(String orientation) {
        // force send an UI orientation event
        lastOrientationValue = orientation;
        sendEvent("orientationDidChange", "orientation", orientation);

        // send a locked event
        sendEvent("lockDidChange", "orientation", orientation);
    }

    @Override
    public @Nullable Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        constants.put("initialOrientation", getCurrentOrientation());
        return constants;
    }

    // ---------------------------------------------------------------------
    // Lifecycle (replaces the manual OrientationActivityLifecycle setup)
    // ---------------------------------------------------------------------

    @Override
    public void onHostResume() {
        start();
    }

    @Override
    public void onHostPause() {
        stop();
    }

    @Override
    public void onHostDestroy() {
        release();
    }

    private void start() {
        Log.i(TAG, "orientation detect enabled.");
        mOrientationListener.enable();

        IntentFilter filter = new IntentFilter();
        // System configuration change: no MainActivity broadcast needed.
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        // Legacy action, still sent by apps following the old README setup.
        filter.addAction(ON_CONFIGURATION_CHANGED);
        compatRegisterReceiver(ctx, mReceiver, filter, false);
        isReceiverRegistered = true;
    }

    private void stop() {
        Log.d(TAG, "orientation detect disabled.");
        mOrientationListener.disable();
        unregisterReceiverQuietly();
    }

    private void release() {
        Log.d(TAG, "orientation detect released.");
        mOrientationListener.disable();
        unregisterReceiverQuietly();
    }

    // The receiver is registered against the react context, so it must be
    // unregistered from the same context (the old release() unregistered
    // from the activity and skipped it entirely when no activity existed).
    private void unregisterReceiverQuietly() {
        if (!isReceiverRegistered) {
            return;
        }
        try {
            ctx.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.w(TAG, "Receiver already unregistered", e);
        }
        isReceiverRegistered = false;
    }

    // Not annotated with @Override: invalidate() only exists on
    // react-native >= 0.74, onCatalystInstanceDestroy() was removed in 0.74.
    public void invalidate() {
        release();
        ctx.removeLifecycleEventListener(this);
    }

    public void onCatalystInstanceDestroy() {
        invalidate();
    }

    // ---------------------------------------------------------------------
    // Event emitter stubs (required by RN's NativeEventEmitter)
    // ---------------------------------------------------------------------

    @ReactMethod
    public void addListener(String eventName) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    public void removeListeners(double count) {
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
