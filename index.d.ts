/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

export type OrientationType = "PORTRAIT" | "PORTRAIT-UPSIDEDOWN" | "LANDSCAPE-LEFT" | "LANDSCAPE-RIGHT" | "FACE-UP" | "FACE-DOWN" | "UNKNOWN";

declare class Orientation {
  static addOrientationListener(callback: (orientation: OrientationType) => void): void;
  static removeOrientationListener(callback: (orientation: OrientationType) => void): void;
  static addDeviceOrientationListener(callback: (deviceOrientation: OrientationType) => void): void;
  static removeDeviceOrientationListener(callback: (deviceOrientation: OrientationType) => void): void;
  static addLockListener(callback: (orientation: OrientationType) => void): void;
  static removeLockListener(callback: (orientation: OrientationType) => void): void;
  static removeAllListeners(): void;
  static getInitialOrientation(): OrientationType;
  static isLocked(): boolean;
  static lockToPortrait(): void;
  static lockToLandscape(): void;
  static lockToLandscapeLeft(): void;
  static lockToLandscapeRight(): void;
  static lockToPortraitUpsideDown(): void;
  static unlockAllOrientations(): void;
  static getOrientation(callback: (orientation: OrientationType) => void): void;
  static getDeviceOrientation(callback: (orientation: OrientationType) => void): void;
  static getAutoRotateState(callback: (state: boolean) => void): void;
}
export default Orientation;
