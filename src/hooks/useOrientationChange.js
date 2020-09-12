import { useRef, useEffect } from 'react';
import Orientation from '../orientation'

export function useOrientationChange(callback) {
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
      Orientation.addOrientationListener(listener);
  
      return () => {
        Orientation.removeOrientationListener(listener);
      };
    }, []);
  }