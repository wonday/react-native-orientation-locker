import { useRef, useEffect } from 'react';
import Orientation from '../orientation'

export function useDeviceOrientationChange(callback) {
  const savedCallback = useRef();

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    function listener(ori) {
      savedCallback.current(ori);
    }
    const initial = Orientation.getInitialOrientation();
    listener(initial);
    Orientation.addDeviceOrientationListener(listener);

    return () => {
      Orientation.removeDeviceOrientationListener(listener);
    };
  }, []);
}