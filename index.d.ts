/**
 * Copyright (c) 2017-present, Wonday (@wonday.org)
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

declare module 'react-native-orientation-locker' {
  namespace Orientation {
    type Orientation = "PORTRAIT" | "PORTRAIT-UPSIDEDOWN" | "LANDSCAPE-LEFT" | "LANDSCAPE-RIGHT" | "UNKNOWN";
  
    export function addOrientationListener(callback: (orientation: Orientation, deviceOrientation: Orientation) => void): void;
    export function removeOrientationListener(callback: (orientation: Orientation, deviceOrientation: Orientation) => void): void;
  
    export function getInitialOrientation(): Orientation;
    export function lockToPortrait(): void;
    export function lockToLandscape(): void;
    export function lockToLandscapeLeft(): void;
    export function lockToLandscapeRight(): void;
    export function unlockAllOrientations(): void;
    export function getOrientation(callback: (orientation: Orientation) => void): void;
    export function getDeviceOrientation(callback: (orientation: Orientation) => void): void;
    export function getAutoRotateState(callback: (state: boolean) => void): void;
  }
  
  export = Orientation;
}