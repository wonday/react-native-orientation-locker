/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, {Component, useEffect, useState} from 'react';
import {
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import Orientation, {
  useOrientationChange,
//  useDeviceOrientationChange,
//  useLockListener,
} from 'react-native-orientation-locker';
//import {useLockListener} from '../src/hooks';

export default function App() {
  const [isLocked, setLocked] = useState();
  const [orientation, setOrientation] = useState();
  const [deviceOrientation, setDeviceOrientation] = useState();
  const [lock, setLock] = useState();

  // eslint-disable-next-line react-hooks/exhaustive-deps
/*  useEffect(() => {
    checkLocked();
  });*/

  useOrientationChange((o) => {
    setOrientation(o);
  });
/*
  useDeviceOrientationChange((o) => {
    setDeviceOrientation(o);
  });

  useLockListener((o) => {
    setLocked(o);
  });

  function checkLocked() {
    const locked = Orientation.isLocked();
    if (locked !== isLocked) {
      setLocked(locked);
    }
  }*/

  return (
    <View style={styles.container}>
      <Text style={styles.welcome}>☆OrientationLocker example☆</Text>
      <View style={styles.row}>
        <Text style={{flex: 1}}>isLocked</Text>
        <Text style={styles.value}>{isLocked ? 'TRUE' : 'FALSE'}</Text>
      </View>
      <View style={styles.row}>
        <Text style={{flex: 1}}>addOrientationListener</Text>
        <Text style={styles.value}>{orientation}</Text>
      </View>
      <View style={styles.row}>
        <Text style={{flex: 1}}>addDeviceOrientationListener</Text>
        <Text style={styles.value}>{deviceOrientation}</Text>
      </View>
      <ScrollView
        contentContainerStyle={{
          alignItems: 'center',
          marginTop: 20,
          paddingVertical: 20,
        }}>
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Orientation.lockToPortrait();
            //checkLocked();
          }}
          style={styles.button}>
          <Text>Lock me to PORTRAIT</Text>
        </TouchableOpacity>
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Orientation.lockToPortraitUpsideDown();
            //checkLocked();
          }}
          style={styles.button}>
          <Text>Lock me to PORTRAIT UPSIDE DOWN</Text>
        </TouchableOpacity>
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Orientation.lockToLandscape();
            //checkLocked();
          }}
          style={styles.button}>
          <Text>Lock me to LANDSCAPE</Text>
        </TouchableOpacity>
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Orientation.lockToLandscapeLeft();
            //checkLocked();
          }}
          style={styles.button}>
          <Text>Lock me to LANDSCAPE LEFT</Text>
        </TouchableOpacity>
        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Orientation.lockToLandscapeRight();
            //checkLocked();
          }}
          style={styles.button}>
          <Text>Lock me to LANDSCAPE RIGHT</Text>
        </TouchableOpacity>

        <TouchableOpacity
          activeOpacity={0.9}
          onPress={() => {
            Orientation.unlockAllOrientations();
            //checkLocked();
          }}
          style={styles.button}>
          <Text>Unlock all orientations</Text>
        </TouchableOpacity>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
    marginTop: 50,
  },
  row: {
    flexDirection: 'row',
    marginTop: 10,
    paddingHorizontal: 15,
    alignItems: 'center',
  },
  value: {
    backgroundColor: 'green',
    color: 'white',
    paddingHorizontal: 10,
    paddingVertical: 5,
  },
  button: {
    backgroundColor: 'orange',
    padding: 10,
    borderRadius: 10,
    marginTop: 10,
  },
});