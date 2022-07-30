//
//  react-native-orientation-locker
//
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//

"use strict";

export default class Orientation {
  static configure = options => {}

  static getOrientation = cb => {
    cb("UNKNOWN");
  };

  static getDeviceOrientation = cb => {
    cb("UNKNOWN");
  };

  static isLocked = () => {
    return false;
  };

  static lockToPortrait = () => {};

  static lockToPortraitUpsideDown = () => {};

  static lockToLandscape = () => {};

  static lockToLandscapeRight = () => {};

  static lockToLandscapeLeft = () => {};

  // OrientationMaskAllButUpsideDown
  static lockToAllOrientationsButUpsideDown = () => {};

  static unlockAllOrientations = () => {};

  static addOrientationListener = cb => {};

  static removeOrientationListener = cb => {};

  static addDeviceOrientationListener = cb => {};

  static removeDeviceOrientationListener = cb => {};

  static addLockListener = cb => {};

  static removeLockListener = cb => {};

  static removeAllListeners = () => {};

  static getInitialOrientation = () => {
    return "UNKNOWN";
  };

  static getAutoRotateState = cb => {
    cb(true);
  };
}
