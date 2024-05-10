import { NativeModules, Platform, NativeEventEmitter } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-orientation-locker' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const OrientationLockerModule = isTurboModuleEnabled
  ? require('./NativeOrientationLocker').default
  : NativeModules.OrientationLocker;

const OrientationLocker = OrientationLockerModule
  ? OrientationLockerModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

let listeners = {} as Record<string, { remove: () => void }>;
let id = 0;
let META = '__listener_id';

let LocalEventEmitter: NativeEventEmitter;

let locked = false;

function getKey(listener: (orientation: string) => void) {
  if (!listener.hasOwnProperty(META)) {
    if (!Object.isExtensible(listener)) {
      return 'F';
    }
    Object.defineProperty(listener, META, {
      value: 'L' + ++id,
    });
  }
  //@ts-ignore
  return listener[META];
}

export default class Orientation1 {
  private static getLocalEventEmitter = () => {
    return LocalEventEmitter ?? new NativeEventEmitter(OrientationLocker);
  };

  static init = () => {
    OrientationLocker.initial();
  };
  static removeInit = () => {
    OrientationLocker.destroy();
  };

  static configure = (options: { disableFaceUpDown: boolean }) => {
    OrientationLocker.configure(options);
  };

  static getOrientation = (cb: (orientation: string) => void) => {
    OrientationLocker.getOrientation(cb);
  };

  static getDeviceOrientation = (cb: (orientation: string) => void) => {
    OrientationLocker.getDeviceOrientation(cb);
  };

  static isLocked = () => {
    return locked;
  };

  static lockToPortrait = () => {
    locked = true;
    OrientationLocker.lockToPortrait();
  };

  static lockToPortraitUpsideDown = () => {
    locked = true;
    OrientationLocker.lockToPortraitUpsideDown();
  };

  static lockToLandscape = () => {
    locked = true;
    OrientationLocker.lockToLandscape();
  };

  static lockToLandscapeRight = () => {
    locked = true;
    OrientationLocker.lockToLandscapeRight();
  };

  static lockToLandscapeLeft = () => {
    locked = true;
    OrientationLocker.lockToLandscapeLeft();
  };

  static lockToAllOrientationsButUpsideDown = () => {
    locked = true;
    OrientationLocker.lockToAllOrientationsButUpsideDown();
  };

  static unlockAllOrientations = () => {
    locked = false;
    OrientationLocker.unlockAllOrientations();
  };

  static addOrientationListener = (cb: (orientation: string) => void) => {
    var key = getKey(cb);
    listeners[key] = this.getLocalEventEmitter().addListener(
      'orientationDidChange',
      (body) => {
        cb(body.orientation);
      }
    );
  };

  static removeOrientationListener = (cb: (orientation: string) => void) => {
    var key = getKey(cb);
    if (!listeners[key]) {
      return;
    }
    //@ts-ignore
    listeners[key].remove();
    //@ts-ignore
    listeners[key] = null;
  };

  static addDeviceOrientationListener = (cb: (orientation: string) => void) => {
    var key = getKey(cb);
    listeners[key] = this.getLocalEventEmitter().addListener(
      'deviceOrientationDidChange',
      (body) => {
        cb(body.deviceOrientation);
      }
    );
  };

  static removeDeviceOrientationListener = (
    cb: (orientation: string) => void
  ) => {
    var key = getKey(cb);
    if (!listeners[key]) {
      return;
    }
    //@ts-ignore
    listeners[key].remove();
    //@ts-ignore
    listeners[key] = null;
  };

  static addLockListener = (cb: (orientation: string) => void) => {
    var key = getKey(cb);
    listeners[key] = this.getLocalEventEmitter().addListener(
      'lockDidChange',
      (body) => {
        cb(body.orientation);
      }
    );
  };

  static removeLockListener = (cb: (orientation: string) => void) => {
    var key = getKey(cb);
    if (!listeners[key]) {
      return;
    }
    //@ts-ignore
    listeners[key].remove();
    //@ts-ignore
    listeners[key] = null;
  };

  static removeAllListeners = () => {
    for (let key in listeners) {
      if (!listeners[key]) {
        continue;
      }
      //@ts-ignore
      listeners[key].remove();
      //@ts-ignore
      listeners[key] = null;
    }
  };

  static getInitialOrientation = () => {
    return OrientationLocker.getInitialOrientation();
  };

  static getAutoRotateState = (cb: (rotateLock: boolean) => void) => {
    OrientationLocker.getAutoRotateState(cb);
  };
}
