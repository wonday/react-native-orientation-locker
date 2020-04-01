//
//  react-native-orientation-locker
//
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//

'use strict';

const resolveUnkown = () => Promise.resolve(Orientation.UNKNOWN);
const doNothing = () => null;

export default {
  getOrientation: resolveUnkown,
  getDeviceOrientation: resolveUnkown,
  isLocked: () => Promise.resolve(false),
  lockToPortrait: doNothing,
  lockToPortraitUpsideDown: doNothing,
  lockToLandscape: doNothing,
  lockToLandscapeRight: doNothing,
  lockToLandscapeLeft: doNothing,
  // OrientationMaskAllButUpsideDown
  lockToAllOrientationsButUpsideDown: doNothing,
  unlockAllOrientations: doNothing,
  addOrientationListener: doNothing,
  addDeviceOrientationListener: doNothing,
  addLockListener: doNothing,
  getInitialOrientation: resolveUnkown,
  getAutoRotateState: () => Promise.resolve(true),
};
