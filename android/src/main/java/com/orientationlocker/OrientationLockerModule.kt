package com.orientationlocker

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.OrientationEventListener
import android.view.Surface
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.ReactConstants
import com.facebook.react.modules.core.DeviceEventManagerModule

class OrientationLockerModule internal constructor(context: ReactApplicationContext) :
  OrientationLockerSpec(context), OrientationListeners {

  private var listenerCount = 0

  private var mReceiver: BroadcastReceiver? = null
  private var mOrientationListener: OrientationEventListener? = null
  private val reactContext: ReactApplicationContext = context
  private var isLocked = false
  private var isConfigurationChangeReceiverRegistered = false
  private var lastOrientationValue = ""
  private var lastDeviceOrientationValue = ""

  init {
    mReceiver =
      (object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
          val orientationValue = getCurrentOrientation()
          lastOrientationValue = orientationValue
          FLog.d(ReactConstants.TAG, "Orientation changed to $orientationValue")

          val params = Arguments.createMap()
          params.putString(ORIENTATION_PARAM, orientationValue)

          sendEvent(reactContext, OrientationConstant.orientationDidChange, params)
        }
      })
    OrientationActivityLifecycle.getInstance().registerListeners(this)
  }

  override fun getName(): String = NAME

  @ReactMethod
  override fun getOrientation(callback: Callback) {
    val orientation = getCurrentOrientation()
    callback.invoke(orientation)
  }

  @ReactMethod
  override fun getDeviceOrientation(callback: Callback) {
    callback.invoke(lastDeviceOrientationValue)
  }

  @ReactMethod
  override fun initial() {
    if (mOrientationListener != null) {
      return
    }
    Log.e("OrientationModule", "init()")
    mOrientationListener =
      (object : OrientationEventListener(reactContext, SensorManager.SENSOR_DELAY_UI) {
        override fun onOrientationChanged(orientation: Int) {
          FLog.d(ReactConstants.TAG, "DeviceOrientation changed to $orientation")
          var deviceOrientationValue = lastDeviceOrientationValue

          if (orientation == -1) {
            deviceOrientationValue = OrientationDisplay.UNKNOWN.type
          } else if (orientation > 355 || orientation < 5) {
            deviceOrientationValue = OrientationDisplay.PORTRAIT.type
          } else if (orientation in 86..94) {
            deviceOrientationValue = OrientationDisplay.LANDSCAPE_RIGHT.type
          } else if (orientation in 176..184) {
            deviceOrientationValue = OrientationDisplay.PORTRAIT_UPSIDEDOWN.type
          } else if (orientation in 266..274) {
            deviceOrientationValue = OrientationDisplay.LANDSCAPE_LEFT.type
          }

          if (lastDeviceOrientationValue != deviceOrientationValue) {
            lastDeviceOrientationValue = deviceOrientationValue
            val params = Arguments.createMap()
            params.putString(DEVICE_ORIENTATION_PARAM, deviceOrientationValue)
            sendEvent(reactContext, OrientationConstant.deviceOrientationDidChange, params)
          }

          val currentOrientationDisplay = getCurrentOrientation()
          if (lastDeviceOrientationValue != currentOrientationDisplay) {
            lastOrientationValue = currentOrientationDisplay
            FLog.d(ReactConstants.TAG, "Orientation changed to $currentOrientationDisplay")
            val params = Arguments.createMap()
            params.putString(ORIENTATION_PARAM, currentOrientationDisplay)
            sendEvent(reactContext, OrientationConstant.orientationDidChange, params)
            return
          }
        }
      })

    if (mOrientationListener?.canDetectOrientation() == true) {
      Log.d(ReactConstants.TAG, "orientation detect enabled.")
      mOrientationListener!!.enable()
    } else {
      Log.d(ReactConstants.TAG, "orientation detect disabled.")
      mOrientationListener!!.disable()
    }
  }

  @ReactMethod
  override fun destroy() {
    if (mOrientationListener != null) {
      mOrientationListener!!.disable()
      mOrientationListener = null
    }
  }

  @SuppressLint("SourceLockedOrientationActivity")
  @ReactMethod
  override fun lockToPortrait() {
    if (currentActivity != null) {
      currentActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
      isLocked = true

      lastOrientationValue = OrientationDisplay.PORTRAIT.type
      val params = Arguments.createMap()
      params.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.orientationDidChange, params)

      // send a locked event
      val lockParams = Arguments.createMap()
      lockParams.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.lockDidChange, lockParams)
    }
  }

  @SuppressLint("SourceLockedOrientationActivity")
  @ReactMethod
  override fun lockToPortraitUpsideDown() {
    if (currentActivity != null) {
      currentActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
      isLocked = true

      lastOrientationValue = OrientationDisplay.PORTRAIT_UPSIDEDOWN.type
      val params = Arguments.createMap()
      params.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.orientationDidChange, params)

      // send a locked event
      val lockParams = Arguments.createMap()
      lockParams.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.lockDidChange, lockParams)
    }
  }


  @ReactMethod
  override fun lockToLandscape() {
    if (currentActivity != null) {
      currentActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      isLocked = true

      lastOrientationValue = OrientationDisplay.LANDSCAPE_LEFT.type
      val params = Arguments.createMap()
      params.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.orientationDidChange, params)

      // send a locked event
      val lockParams = Arguments.createMap()
      lockParams.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.lockDidChange, lockParams)
    }
  }

  @ReactMethod
  override fun lockToLandscapeLeft() {
    if (currentActivity != null) {
      currentActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      isLocked = true

      lastOrientationValue = OrientationDisplay.LANDSCAPE_LEFT.type
      val params = Arguments.createMap()
      params.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.orientationDidChange, params)

      // send a locked event
      val lockParams = Arguments.createMap()
      lockParams.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.lockDidChange, lockParams)
    }
  }

  @ReactMethod
  override fun lockToLandscapeRight() {
    if (currentActivity != null) {
      currentActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
      isLocked = true

      lastOrientationValue = OrientationDisplay.LANDSCAPE_RIGHT.type
      val params = Arguments.createMap()
      params.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.orientationDidChange, params)

      // send a locked event
      val lockParams = Arguments.createMap()
      lockParams.putString(ORIENTATION_PARAM, lastOrientationValue)

      sendEvent(reactContext, OrientationConstant.lockDidChange, lockParams)
    }
  }

  @ReactMethod
  override fun unlockAllOrientations() {
    if (currentActivity != null) {
      currentActivity!!.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
      isLocked = false
      lastOrientationValue = getCurrentOrientation()

      val params = Arguments.createMap()
      params.putString(ORIENTATION_PARAM, lastOrientationValue)
      sendEvent(reactContext, OrientationConstant.orientationDidChange, params)

      val lockParams = Arguments.createMap()
      lockParams.putString(ORIENTATION_PARAM, OrientationDisplay.UNKNOWN.type)
      sendEvent(reactContext, OrientationConstant.lockDidChange, lockParams)
    }
  }

  @ReactMethod
  override fun configure(option: ReadableMap) {
    // IOS only, skip android
  }

  @ReactMethod
  override fun lockToAllOrientationsButUpsideDown() {
    // IOS only, skip android
  }

  @ReactMethod

  override fun getAutoRotateState(callback: Callback) {
    val resolver: ContentResolver = reactContext.contentResolver
    val rotateLock =
      Settings.System.getInt(resolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
    callback.invoke(rotateLock)
  }

  @ReactMethod
  override fun addListener(eventName: String) {
    listenerCount += 1
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  override fun removeListeners(count: Int) {
    listenerCount -= count
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun getInitialOrientation(): String {
    return getCurrentOrientation()
  }

  override fun getConstants(): MutableMap<String, Any>? {
    return hashMapOf("initialOrientation" to "orientation")
  }

  override fun start() {
    FLog.i(ReactConstants.TAG, "orientation detect enabled.")
    mOrientationListener?.enable()
    mReceiver?.let {
      compatRegisterReceiver(reactContext, it, IntentFilter("onConfigurationChanged"), false)
      isConfigurationChangeReceiverRegistered = true
    }
  }

  override fun stop() {
    FLog.d(ReactConstants.TAG, "orientation detect disabled.")
    mOrientationListener?.disable()
    try {
      if (isConfigurationChangeReceiverRegistered) {
        reactContext.unregisterReceiver(mReceiver)
        isConfigurationChangeReceiverRegistered = false
      }
    } catch (e: Exception) {
      FLog.w(ReactConstants.TAG, "Receiver already unregistered", e)
    }
  }

  override fun release() {
    FLog.d(ReactConstants.TAG, "orientation detect disabled.")
    mOrientationListener?.disable()
    if (currentActivity != null) {
      try {
        if (isConfigurationChangeReceiverRegistered) {
          currentActivity!!.unregisterReceiver(mReceiver)
          isConfigurationChangeReceiverRegistered = false
        }
      } catch (e: java.lang.Exception) {
        FLog.w(ReactConstants.TAG, "Receiver already unregistered", e)
      }
    }
  }

  // ======== Private Method ======== \\

  private fun sendEvent(reactContext: ReactContext, eventName: String, params: WritableMap?) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  private fun compatRegisterReceiver(
    context: Context,
    receiver: BroadcastReceiver,
    filter: IntentFilter,
    exported: Boolean
  ) {
    if (Build.VERSION.SDK_INT >= 34 && context.applicationInfo.targetSdkVersion >= 34) {
      context.registerReceiver(
        receiver,
        filter,
        if (exported) Context.RECEIVER_EXPORTED else Context.RECEIVER_NOT_EXPORTED
      )
    } else {
      context.registerReceiver(receiver, filter)
    }
  }

  private fun getCurrentOrientation(): String {
    val display: Display =
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        reactApplicationContext.currentActivity!!.display as Display
      } else {
        @Suppress("Deprecated")
        reactApplicationContext.currentActivity!!.windowManager.defaultDisplay
      }

    return when (display.rotation) {
      Surface.ROTATION_0 -> OrientationDisplay.PORTRAIT.type
      Surface.ROTATION_90 -> OrientationDisplay.LANDSCAPE_LEFT.type
      Surface.ROTATION_180 -> OrientationDisplay.PORTRAIT_UPSIDEDOWN.type
      Surface.ROTATION_270 -> OrientationDisplay.LANDSCAPE_RIGHT.type
      else -> OrientationDisplay.UNKNOWN.type
    }
  }

  companion object {
    const val NAME = "OrientationLocker"
    const val ORIENTATION_PARAM = "orientation"
    const val DEVICE_ORIENTATION_PARAM = "deviceOrientation"
  }
}
