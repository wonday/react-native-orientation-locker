package org.wonday.orientation;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

public class OrientationActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "OrientationModule";
    private static AtomicInteger activeCount = new AtomicInteger(0);
    private OrientationListeners orientationListeners;

    private static OrientationActivityLifecycle instance;

    public static OrientationActivityLifecycle getInstance() {
        if (instance == null) {
            instance = new OrientationActivityLifecycle();
        }
        return instance;
    }

    private OrientationActivityLifecycle() {}

    public void registerListeners(OrientationListeners listener) {
        this.orientationListeners = listener;
        if (activeCount.get() == 1) {
            // Trigger start event
            orientationListeners.start();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d(TAG, "onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed");
        if (activeCount.incrementAndGet() == 1) {
            if (orientationListeners != null) {
                Log.d(TAG, "Start orientation");
                orientationListeners.start();
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused");
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped");
        if (activeCount.decrementAndGet() == 0) {
            if (orientationListeners != null) {
                orientationListeners.stop();
            }
        }

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed");
        if (activeCount.get() == 0) {
            if (orientationListeners != null) {
                orientationListeners.release();
            }
        }
    }
}
