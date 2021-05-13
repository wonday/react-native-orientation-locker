import { useRef, useEffect } from 'react';
import Orientation from '../orientation'

export function useLockListener(callback) {
    const savedCallback = useRef();
  
    useEffect(() => {
      savedCallback.current = callback;
    }, [callback]);
  
    useEffect(() => {
      function listener(ori) {
        savedCallback.current(ori);
      }
      Orientation.addLockListener(listener);
  
      return () => {
        Orientation.removeLockListener(listener);
      };
    }, []);
  }