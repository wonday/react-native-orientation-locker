import * as React from 'react';

import {
  Button,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  View,
} from 'react-native';
import Orientation, {
  useDeviceOrientationChange,
  useLockListener,
  useOrientationChange,
} from 'react-native-orientation-locker';

export default function App() {
  const defaultCallback = (orientation: string) => {
    console.log({ orientation });
  };

  const getInitialOrientation = () => {
    console.log('getInitialOrientation', Orientation.getInitialOrientation());
  };
  const getOrientation = () => {
    Orientation.getOrientation(defaultCallback);
  };

  const getDeviceOrientation = () => {
    Orientation.getDeviceOrientation(defaultCallback);
  };

  const lockToPortrait = () => {
    Orientation.lockToPortrait();
  };

  const lockToPortraitUpsideDown = () => {
    Orientation.lockToPortraitUpsideDown();
  };

  const lockToLandscape = () => {
    Orientation.lockToLandscape();
  };

  const lockToLandscapeLeft = () => {
    Orientation.lockToLandscapeLeft();
  };

  const lockToLandscapeRight = () => {
    Orientation.lockToLandscapeRight();
  };

  const unlockAllOrientations = () => {
    Orientation.unlockAllOrientations();
  };

  const lockToAllOrientationsButUpsideDown = () => {
    Orientation.lockToAllOrientationsButUpsideDown();
  };

  const getAutoRotateState = () => {
    Orientation.getAutoRotateState((state) => {
      console.log('getAutoRotateState', state);
    });
  };

  useDeviceOrientationChange((e) => {
    console.log('useDeviceOrientationChange', e);
  });
  useLockListener((e) => {
    console.log('useLockListener', e);
  });
  useOrientationChange((e) => {
    console.log('useOrientationChange', e);
  });

  React.useEffect(() => {
    Orientation.init();
    Orientation.configure({ disableFaceUpDown: false });
    return () => Orientation.removeInit();
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.container}>
        <ScrollView>
          <View style={styles.boxFunction}>
            <Button
              title="getInitialOrientation"
              onPress={getInitialOrientation}
            />
            <Button title="getOrientation" onPress={getOrientation} />
            <Button
              title="getDeviceOrientation"
              onPress={getDeviceOrientation}
            />
            <Button title="lockToPortrait" onPress={lockToPortrait} />
            <Button
              title="lockToPortraitUpsideDown"
              onPress={lockToPortraitUpsideDown}
            />
            <Button title="lockToLandscape" onPress={lockToLandscape} />
            <Button title="lockToLandscapeLeft" onPress={lockToLandscapeLeft} />
            <Button
              title="lockToLandscapeRight"
              onPress={lockToLandscapeRight}
            />
            <Button
              title="unlockAllOrientations"
              onPress={unlockAllOrientations}
            />
            <Button
              title="lockToAllOrientationsButUpsideDown"
              onPress={lockToAllOrientationsButUpsideDown}
            />
            <Button title="getAutoRotateState" onPress={getAutoRotateState} />
          </View>
        </ScrollView>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  boxFunction: {
    paddingVertical: 12,
    justifyContent: 'center',
    alignItems: 'center',
    rowGap: 4,
  },
});
