import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { Int32 } from 'react-native/Libraries/Types/CodegenTypes';

export interface Spec extends TurboModule {
  initial(): void;
  destroy(): void;
  configure(options: Object): void;
  getOrientation(callback: (orientation: string) => void): void;
  getDeviceOrientation(callback: (orientation: string) => void): void;
  lockToPortrait(): void;
  lockToPortraitUpsideDown(): void;
  lockToLandscape(): void;
  lockToLandscapeLeft(): void;
  lockToLandscapeRight(): void;
  unlockAllOrientations(): void;
  lockToAllOrientationsButUpsideDown(): void;
  getInitialOrientation(): string;
  getAutoRotateState(callback: (rotateLock: boolean) => void): void;
  addListener(eventName: string): void;
  removeListeners(count: Int32): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('OrientationLocker');
