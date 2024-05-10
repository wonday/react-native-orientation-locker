import { useRef, useEffect } from 'react';
import Orientation from '../Orientation';

export function useLockListener(callback: (orientation: string) => void) {
  const savedCallback = useRef<(orientation: string) => void>();

  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  useEffect(() => {
    Orientation.init();
    function listener(orientation: string) {
      savedCallback.current?.(orientation);
    }
    Orientation.addLockListener(listener);

    return () => {
      Orientation.removeInit();
      Orientation.removeLockListener(listener);
    };
  }, []);
}
