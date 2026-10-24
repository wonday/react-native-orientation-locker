//
//  react-native-orientation-locker
//
//  Unified implementation for Android and iOS.
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//
//  The native module is a TurboModule under the New Architecture and falls
//  back to `NativeModules` on the legacy architecture, with an identical
//  promise-based method surface on both paths.
//

"use strict";

const { NativeModules, NativeEventEmitter, TurboModuleRegistry } = require("react-native");

// TurboModule under the New Architecture, classic module otherwise.
// `TurboModuleRegistry` is missing on very old RN versions; guard for it.
const OrientationNative =
  (typeof TurboModuleRegistry !== "undefined" &&
    TurboModuleRegistry.get("Orientation")) ||
  NativeModules.Orientation;

let LocalEventEmitter;

// Callback -> subscription map. Using the callback itself as the key avoids
// mutating user objects and the id collisions the old `getKey()` had for
// non-extensible listeners.
const listeners = new Map();

let locked = false;

function getEventEmitter() {
  if (!LocalEventEmitter) {
    LocalEventEmitter = new NativeEventEmitter(OrientationNative);
  }
  return LocalEventEmitter;
}

export default class Orientation {
  static configure = (options) => {
    OrientationNative.configure(!!(options && options.disableFaceUpDown));
  };

  static getOrientation = (cb) => {
    OrientationNative.getOrientation().then(cb);
  };

  static getDeviceOrientation = (cb) => {
    OrientationNative.getDeviceOrientation().then(cb);
  };

  static isLocked = () => {
    return locked;
  };

  static lockToPortrait = () => {
    locked = true;
    OrientationNative.lockToPortrait();
  };

  static lockToPortraitUpsideDown = () => {
    locked = true;
    OrientationNative.lockToPortraitUpsideDown();
  };

  static lockToLandscape = () => {
    locked = true;
    OrientationNative.lockToLandscape();
  };

  static lockToLandscapeRight = () => {
    locked = true;
    OrientationNative.lockToLandscapeRight();
  };

  static lockToLandscapeLeft = () => {
    locked = true;
    OrientationNative.lockToLandscapeLeft();
  };

  // OrientationMaskAllButUpsideDown
  static lockToAllOrientationsButUpsideDown = () => {
    locked = true;
    OrientationNative.lockToAllOrientationsButUpsideDown();
  };

  static unlockAllOrientations = () => {
    locked = false;
    OrientationNative.unlockAllOrientations();
  };

  static addOrientationListener = (cb) => {
    listeners.set(
      cb,
      getEventEmitter().addListener("orientationDidChange", (body) => {
        cb(body.orientation);
      })
    );
  };

  static removeOrientationListener = (cb) => {
    const subscription = listeners.get(cb);
    if (!subscription) {
      return;
    }
    subscription.remove();
    listeners.delete(cb);
  };

  static addDeviceOrientationListener = (cb) => {
    listeners.set(
      cb,
      getEventEmitter().addListener("deviceOrientationDidChange", (body) => {
        cb(body.deviceOrientation);
      })
    );
  };

  static removeDeviceOrientationListener = (cb) => {
    const subscription = listeners.get(cb);
    if (!subscription) {
      return;
    }
    subscription.remove();
    listeners.delete(cb);
  };

  static addLockListener = (cb) => {
    listeners.set(
      cb,
      getEventEmitter().addListener("lockDidChange", (body) => {
        cb(body.orientation);
      })
    );
  };

  static removeLockListener = (cb) => {
    const subscription = listeners.get(cb);
    if (!subscription) {
      return;
    }
    subscription.remove();
    listeners.delete(cb);
  };

  static removeAllListeners = () => {
    listeners.forEach((subscription) => {
      subscription.remove();
    });
    listeners.clear();
  };

  static getInitialOrientation = () => {
    // Constant exported by the native module; "UNKNOWN" until it is
    // available (e.g. before the first event arrives under edge cases).
    return OrientationNative.initialOrientation ?? "UNKNOWN";
  };

  static getAutoRotateState = (cb) => {
    OrientationNative.getAutoRotateState().then(cb);
  };
}
