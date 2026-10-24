/**
 * TurboModule codegen spec for react-native-orientation-locker.
 *
 * This file is consumed by the React Native codegen compiler to generate
 * the native scaffolding:
 *   - Android: `org.wonday.orientation.NativeOrientationSpec` (abstract class)
 *   - iOS:     the `NativeOrientationSpec` ObjC++ protocol (pod
 *              `RNOrientationLockerSpec`, umbrella header
 *              <RNOrientationLockerSpec/RNOrientationLockerSpec.h>)
 *
 * It is not imported by the shipped JavaScript; the runtime accessor lives
 * in `src/orientation.js` so that legacy-architecture apps keep working.
 *
 * Requirements for codegen specs: file name must start with `Native`, the
 * exported interface must be named `Spec`, and only codegen-supported types
 * are allowed (promises, plain objects, primitives).
 */

import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  // iOS-only option, flattened to a primitive so codegen produces a plain
  // BOOL (an object parameter would generate a C++ struct that the legacy
  // bridge cannot convert); the public JS API still accepts
  // `{ disableFaceUpDown }` and unwraps it.
  configure(disableFaceUpDown: boolean): void;

  // Getters are promise based; the public JS wrapper preserves the legacy
  // callback based API on top of them.
  getOrientation(): Promise<string>;
  getDeviceOrientation(): Promise<string>;
  getAutoRotateState(): Promise<boolean>;

  lockToPortrait(): void;
  lockToPortraitUpsideDown(): void;
  lockToLandscape(): void;
  lockToLandscapeLeft(): void;
  lockToLandscapeRight(): void;
  lockToAllOrientationsButUpsideDown(): void;
  unlockAllOrientations(): void;

  // Required for RN's NativeEventEmitter to work against this module.
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Orientation');
