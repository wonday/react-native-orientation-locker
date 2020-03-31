/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect} from 'react';
import {
  Button,
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
} from 'react-native';

import {Header, Colors} from 'react-native/Libraries/NewAppScreen';

import Orientation from 'react-native-orientation-locker';

const App = () => {
  useEffect(() => {
    console.log('mount');
    const initial = Orientation.getInitialOrientation();
    console.log(initial);
    Orientation.getAutoRotateState().then(rotationLock =>
      console.log(rotationLock),
    );

    Orientation.getOrientation().then(orientation => console.log(orientation));

    Orientation.getDeviceOrientation().then(orientation =>
      console.log(orientation),
    );

    Orientation.isLocked().then(islocked => console.log(islocked));

    Orientation.lockToPortrait();

    const unsuscribe = Orientation.addOrientationListener(a =>
      console.log('here', a),
    );
    return unsuscribe;
  }, []);

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          {global.HermesInternal == null ? null : (
            <View style={styles.engine}>
              <Text style={styles.footer}>Engine: Hermes</Text>
            </View>
          )}
          <View style={{margin: 16}}>
            <View style={{marginBottom: 16}}>
              <Button
                title="to landscape"
                onPress={() => Orientation.lockToLandscape()}
              />
            </View>
            <View style={{marginBottom: 16}}>
              <Button
                title="to landscape right"
                onPress={() => Orientation.lockToLandscapeRight()}
              />
            </View>
            <View style={{marginBottom: 16}}>
              <Button
                title="to landscape left"
                onPress={() => Orientation.lockToLandscapeLeft()}
              />
            </View>
            <View style={{marginBottom: 16}}>
              <Button
                title="to portrait"
                onPress={() => Orientation.lockToPortrait()}
              />
            </View>
            <View style={{marginBottom: 16}}>
              <Button
                title="unlock"
                onPress={() => Orientation.unlockAllOrientations()}
              />
            </View>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
});

export default App;
