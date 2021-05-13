/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

import React from 'react';

export enum OrientationType {
  "PORTRAIT" = "PORTRAIT",
  "PORTRAIT-UPSIDEDOWN" = "PORTRAIT-UPSIDEDOWN",
  "LANDSCAPE-LEFT" = "LANDSCAPE-LEFT",
  "LANDSCAPE-RIGHT" = "LANDSCAPE-RIGHT",
  "FACE-UP" = "FACE-UP",
  "FACE-DOWN" = "FACE-DOWN",
  "UNKNOWN" = "UNKNOWN",
}

export type UNLOCK = 'UNLOCK';
export type PORTRAIT = 'PORTRAIT';
export type LANDSCAPE = 'LANDSCAPE';
export type LANDSCAPE_LEFT = 'LANDSCAPE_LEFT';
export type LANDSCAPE_RIGHT = 'LANDSCAPE_RIGHT';
export type PORTRAIT_UPSIDE_DOWN = 'PORTRAIT_UPSIDE_DOWN';
export type ALL_ORIENTATIONS_BUT_UPSIDE_DOWN = 'ALL_ORIENTATIONS_BUT_UPSIDE_DOWN';

export interface OrientationLockerProps {
  orientation: UNLOCK | PORTRAIT | LANDSCAPE | LANDSCAPE_LEFT | LANDSCAPE_RIGHT | PORTRAIT_UPSIDE_DOWN | ALL_ORIENTATIONS_BUT_UPSIDE_DOWN;
  onChange?: (orientation: OrientationType) => void;
  onDeviceChange?: (deviceOrientation: OrientationType) => void;
}

export type OrientationLocker = React.ComponentType<OrientationLockerProps>;

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

  static lockToAllOrientationsButUpsideDown(): void;

  static lockToLandscapeRight(): void;

  static lockToPortraitUpsideDown(): void;

  static unlockAllOrientations(): void;

  static getOrientation(callback: (orientation: OrientationType) => void): void;

  static getDeviceOrientation(callback: (orientation: OrientationType) => void): void;

  static getAutoRotateState(callback: (state: boolean) => void): void;
}

export default Orientation;

declare function useOrientationChange(listener: (orientation: OrientationType) => void);

declare function useDeviceOrientationChange(listener: (orientation: OrientationType) => void);

declare function useLockListener(listener: (orientation: OrientationType) => void);

export {
  useOrientationChange,
  useDeviceOrientationChange,
  useLockListener,
}
