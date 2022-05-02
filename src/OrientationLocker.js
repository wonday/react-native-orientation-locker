import { useEffect, useRef } from 'react';
import Orientation from './orientation';

export const UNLOCK = 'UNLOCK';
export const PORTRAIT = 'PORTRAIT';
export const LANDSCAPE = 'LANDSCAPE';
export const LANDSCAPE_LEFT = 'LANDSCAPE_LEFT';
export const LANDSCAPE_RIGHT = 'LANDSCAPE_RIGHT';
export const PORTRAIT_UPSIDE_DOWN = 'PORTRAIT_UPSIDE_DOWN';
export const ALL_ORIENTATIONS_BUT_UPSIDE_DOWN = 'ALL_ORIENTATIONS_BUT_UPSIDE_DOWN';

const stack = [];

let immediateId;

function update() {
  clearImmediate(immediateId);
  immediateId = setImmediate(() => {
    let orientation;
    let length = stack.length;
    while (!orientation && length--) {
      orientation = stack[length].orientation;
    }

    switch (orientation) {
      case UNLOCK:
        Orientation.unlockAllOrientations();
        break;
      case PORTRAIT:
        Orientation.lockToPortrait();
        break;
      case LANDSCAPE:
        Orientation.lockToLandscape();
        break;
      case LANDSCAPE_LEFT:
        Orientation.lockToLandscapeLeft();
        break;
      case LANDSCAPE_RIGHT:
        Orientation.lockToLandscapeRight();
        break;
      case PORTRAIT_UPSIDE_DOWN:
        Orientation.lockToPortraitUpsideDown();
        break;
      case ALL_ORIENTATIONS_BUT_UPSIDE_DOWN:
        Orientation.lockToAllOrientationsButUpsideDown();
        break;
    }
  });
}

export function OrientationLocker({
  orientation,
  onChange,
  onDeviceChange,
}) {
  const stackEntry = useRef({});

  // didMount: add to stack
  useEffect(() => {
    const { current } = stackEntry;
    stack.push(current);

    // willUnmount: remove from stack
    return () => {
      const index = stack.indexOf(current);
      if (index !== -1) {
        stack.splice(index, 1);
      }
      update();
    };
  }, []);

  // props.orientation
  useEffect(() => {
    stackEntry.current.orientation = orientation;
    update();
  }, [orientation]);

  // props.onChange
  useEffect(() => {
    if (onChange) {
      Orientation.addOrientationListener(onChange);
      return () => Orientation.removeOrientationListener(onChange);
    }
  }, [onChange]);

  // props.onDeviceChange
  useEffect(() => {
    if (onDeviceChange) {
      Orientation.addDeviceOrientationListener(onDeviceChange);
      return () => Orientation.removeDeviceOrientationListener(onDeviceChange);
    }
  }, [onDeviceChange]);

  return null;
}
