package com.orientationlocker

import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableMap

abstract class OrientationLockerSpec internal constructor(context: ReactApplicationContext) :
    ReactContextBaseJavaModule(context) {

  abstract fun getOrientation(callback: Callback)
  abstract fun getDeviceOrientation(callback: Callback)
  abstract fun getInitialOrientation(): String
  abstract fun initial()
  abstract fun destroy()
  abstract fun lockToPortrait()
  abstract fun lockToPortraitUpsideDown()
  abstract fun lockToLandscape()
  abstract fun lockToLandscapeLeft()
  abstract fun lockToLandscapeRight()
  abstract fun unlockAllOrientations()
  abstract fun configure(option: ReadableMap)
  abstract fun lockToAllOrientationsButUpsideDown()
  abstract fun getAutoRotateState(callback: Callback)

  abstract fun addListener(eventName: String)

  abstract fun removeListeners(count: Int)
}
