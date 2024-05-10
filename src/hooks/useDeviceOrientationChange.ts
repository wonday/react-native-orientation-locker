import { useRef, useEffect } from 'react';
import Orientation from '../Orientation';

export function useDeviceOrientationChange(
  callback: (orientation: string) => void
) {
  const savedCallback = useRef<(orientation: string) => void>();

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    Orientation.init();
    function listener(orientation: string) {
      savedCallback.current?.(orientation);
    }

    const initial = Orientation.getInitialOrientation();
    listener(initial);
    Orientation.addDeviceOrientationListener(listener);

    return () => {
      Orientation.removeInit();
      Orientation.removeDeviceOrientationListener(listener);
    };
  }, []);
}
