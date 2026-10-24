/**
 * @format
 */

import React from 'react';
import renderer, {act} from 'react-test-renderer';

jest.mock('react-native-orientation-locker', () => ({
  __esModule: true,
  default: {
    isLocked: jest.fn(() => false),
    lockToPortrait: jest.fn(),
    lockToPortraitUpsideDown: jest.fn(),
    lockToLandscape: jest.fn(),
    lockToLandscapeLeft: jest.fn(),
    lockToLandscapeRight: jest.fn(),
    unlockAllOrientations: jest.fn(),
  },
  useOrientationChange: jest.fn(),
  useDeviceOrientationChange: jest.fn(),
  useLockListener: jest.fn(),
}));

import App from '../App';

it('renders without crashing', () => {
  let tree;
  act(() => {
    tree = renderer.create(<App />);
  });
  act(() => {
    tree.unmount();
  });
});
