# react-native-orientation-locker
[![npm](https://img.shields.io/npm/v/react-native-orientation-locker.svg?style=flat-square)](https://www.npmjs.com/package/react-native-orientation-locker)

A react-native module that can listen on orientation changing of device, get current orientation, lock to preferred orientation. (cross-platform support)

### Feature

* lock screen orientation to PORTRAIT|LANDSCAPE-LEFT|PORTRAIT-UPSIDEDOWN|LANDSCAPE-RIGHT.
* listen on orientation changing of device
* get the current orientation of device

### ChangeLog
<details>
  <summary>ChangeLog details</summary>


v1.3.1 
1. Fix require cycle problem

v1.3.0 **BREAKING CHANGES**
1. Add support for Window
2. Modify sample project
3. Updates Tyescript define

v1.2.0 **BREAKING CHANGES**
1. Add support for lockAllOrientationsButUpsideDown
2. Ignore on web and desktop (#115)
3. Fix for not finding @ReactModule (#125)
4. Fix unlockAllOrientations on Android (#133)
5. Implement ActivityLifecycleCallbacks on Android (#131)

Please be sure to add to `onCreate` of your `MainApplication`
```
import org.wonday.orientation.OrientationActivityLifecycle;

  @Override
  public void onCreate() {
    ...
+    registerActivityLifecycleCallbacks(OrientationActivityLifecycle.getInstance());
  }

```
v1.1.8
1. Support FACE-UP and FACE-DOWN on iOS

v1.1.7
1. Add lockToPortraitUpsideDown() to iOS
2. Minor case corrections

v1.1.6
1. Catch unknown device orientation value
2. When calling unlockAllOrientations(), forcibly unlock whether locked or not

v1.1.5
1. Add Orientation.isLocked() and Orientation.removeAllListeners()

v1.1.4
1. Fix TypeScript declarations

v1.1.3
1. Add `addLockListener` and `removeLockListener`
2. Improve Android orientation changed event sending condition

v1.1.2
 1. Improve Android orientation changed event timing

v1.1.1
 1. Fix show "supported event type for deviceOrientationDidChange..." error in debug
 2. Fix getAutoRotateState() code error


v1.1.0 **BREAKING CHANGES**  
 1. Split ```addOrientationListener(function(orientation, deviceOrientation))``` to ```addOrientationListener(function(orientation))``` and ```addDeviceOrientationListener(function(deviceOrientation))```
 2. Make sure when lockToXXX and unlockAllOrientations resend UI orientation event
 3. remove setTimout from orientation listener
 4. Add getAutoRotateState() for Android
 5. Add TypeScript definitions

[[more]](https://github.com/wonday/react-native-orientation-locker/releases)
</details>

### Notice

1. RN 0.58 + Android target SDK 27 maybe cause 
```Issue: java.lang.IllegalStateException: Only fullscreen activities can request orientation``` problem, 
see [[#55]](https://github.com/wonday/react-native-orientation-locker/issues/55) for a solution.

2. orientationDidChange will be delayed on iPads if we set upside down to true.
Simply disable upside down for iPad and everything works like a charm ([[#78]](https://github.com/wonday/react-native-orientation-locker/issues/78) Thanks [truongluong1314520](https://github.com/truongluong1314520))

3. If you get the following build error on iOS: 
```ld: library not found for -lRCTOrientation-tvOS```
Just remove it from linked libraries and frameworks

4. For Windows, locking to an orientation will only work on devices in tablet mode.

5. For Windows, getting information on device orientation and tracking its changes will only be possible on devices with an orientation sensor. If the device running your application does not have the appropriate hardware to support tracking device orientation, `getDeviceOrientation()` will return UNKNOWN.

#### To run example on Windows Tablet (This will allow one to view modules full functionality)
1. Open example/windows/example.sln in Visual Studio. 
2. Go to Project > Publish > Create App Packages.
3. Certificate password is "password".
4. Select "Sideloading" and hit "Next".
5. Hit "Yes, use the current certificate" and hit "Next".
6. Choose desired output location and hit "Create".
7. Got to output location and copy example_<version>_Test directory over to tablet device.
8. On tablet device open APPXBUNDLE File and hit "Install" (make sure tablet is in developer mode). App should launch after install is complete.
9. Turn on tablet mode on device to see locking to a UI orientation functionality. 

### Installation
#### Using yarn (RN 0.60 and and above)

```
    yarn add react-native-orientation-locker
```


#### Using yarn (RN 0.59 and and below)

```
    yarn add react-native-orientation-locker
    react-native link react-native-orientation-locker
```
#### Manual linking
For Windows, if you are using RNW v0.63.0 or higher, autolinking should link the module for you. Otherwise, you must follow the steps outlined [here](https://microsoft.github.io/react-native-windows/docs/native-modules-using) for linking module.

Add following to MainApplication.java
(This will be added automatically by auto link. If not, please manually add the following )

```diff
//...
+import org.wonday.orientation.OrientationPackage;
    @Override
    protected List<ReactPackage> getPackages() {
      @SuppressWarnings("UnnecessaryLocalVariable")
      List<ReactPackage> packages = new PackageList(this).getPackages();
      // Packages that cannot be autolinked yet can be added manually here, for example:
      // packages.add(new MyReactNativePackage());
+      packages.add(new OrientationPackage());
      return packages;
    }
//...
```

#### Using CocoaPods (iOS Only)

Run ```pod install``` in the ios directory. Linking is not required in React Native 0.60 and above.

### Configuration

#### iOS

Add the following to your project's `AppDelegate.m`:

```diff
+#import "Orientation.h"

@implementation AppDelegate

// ...

+- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
+  return [Orientation getOrientation];
+}

@end
```

#### Android

Add following to android/app/src/main/AndroidManifest.xml

```diff
      <activity
        ....
+       android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
        android:windowSoftInputMode="adjustResize">

          ....

      </activity>

```

Implement onConfigurationChanged method (in `MainActivity.java`)

```diff
// ...

+import android.content.Intent;
+import android.content.res.Configuration;

public class MainActivity extends ReactActivity {

+   @Override
+   public void onConfigurationChanged(Configuration newConfig) {
+       super.onConfigurationChanged(newConfig);
+       Intent intent = new Intent("onConfigurationChanged");
+       intent.putExtra("newConfig", newConfig);
+       this.sendBroadcast(intent);
+   }

    // ......
}
```

Add following to MainApplication.java

```diff
+import org.wonday.orientation.OrientationActivityLifecycle;
  @Override
  public void onCreate() {
+    registerActivityLifecycleCallbacks(OrientationActivityLifecycle.getInstance());
  }
```

## Usage

### Imperative API

Whenever you want to use it within React Native code now you can:
`import Orientation from 'react-native-orientation-locker';`

```js

import Orientation from 'react-native-orientation-locker';


  _onOrientationDidChange = (orientation) => {
    if (orientation == 'LANDSCAPE-LEFT') {
      //do something with landscape left layout
    } else {
      //do something with portrait layout
    }
  };

  componentWillMount() {
    //The getOrientation method is async. It happens sometimes that
    //you need the orientation at the moment the js starts running on device.
    //getInitialOrientation returns directly because its a constant set at the
    //beginning of the js code.
    var initial = Orientation.getInitialOrientation();
    if (initial === 'PORTRAIT') {
      //do stuff
    } else {
      //do other stuff
    }
  },

  componentDidMount() {

    Orientation.getAutoRotateState((rotationLock) => this.setState({rotationLock}));
    //this allows to check if the system autolock is enabled or not.

    Orientation.lockToPortrait(); //this will lock the view to Portrait
    //Orientation.lockToLandscapeLeft(); //this will lock the view to Landscape
    //Orientation.unlockAllOrientations(); //this will unlock the view to all Orientations

    //get current UI orientation
    /*
    Orientation.getOrientation((orientation)=> {
      console.log("Current UI Orientation: ", orientation);
    });

    //get current device orientation
    Orientation.getDeviceOrientation((deviceOrientation)=> {
      console.log("Current Device Orientation: ", deviceOrientation);
    });
    */

    Orientation.addOrientationListener(this._onOrientationDidChange);
  },

  componentWillUnmount: function() {
    Orientation.removeOrientationListener(this._onOrientationDidChange);
  }
```

### Reactive component `<OrientationLocker>`

It is possible to have multiple `OrientationLocker` components mounted at the same time. The props will be merged in the order the `OrientationLocker` components were mounted. This follows the same usability of [\<StatusBar\>](https://reactnative.dev/docs/statusbar).

```js
import React, { useState } from "react";
import { Text, View } from "react-native";
import { OrientationLocker, PORTRAIT, LANDSCAPE } from "react-native-orientation-locker";

export default function App() {
  const [showVideo, setShowVideo] = useState(true);
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <OrientationLocker
        orientation={PORTRAIT}
        onChange={orientation => console.log('onChange', orientation)}
        onDeviceChange={orientation => console.log('onDeviceChange', orientation)}
      />
      <Button title="Toggle Video" onPress={() => setShowVideo(!showVideo)} />
      {showVideo && (
        <View>
          <OrientationLocker orientation={LANDSCAPE} />
          <View style={{ width: 320, height: 180, backgroundColor: '#ccc' }}>
            <Text>Landscape video goes here</Text>
          </View>
        </View>
      )}
    </View>
  );
};
```

## Hooks
- `useOrientationChange`: hook for `addOrientationListener` event
- `useDeviceOrientationChange`: hook for `addDeviceOrientationListener` event

```
function SomeComponent() {
  useOrientationChange((o) => {
    // Handle orientation change
  });

  useDeviceOrientationChange((o) => {
    // Handle device orientation change
  });
}
```

## Events

- `addOrientationListener(function(orientation))`

When UI orientation changed, callback function will be called.
But if lockToXXX is called , callback function will be not called untill unlockAllOrientations.
It can return either `PORTRAIT` `LANDSCAPE-LEFT` `LANDSCAPE-RIGHT` `PORTRAIT-UPSIDEDOWN` `UNKNOWN`
When lockToXXX/unlockAllOrientations, it will force resend UI orientation changed event.

- `removeOrientationListener(function(orientation))`

- `addDeviceOrientationListener(function(deviceOrientation))`

When device orientation changed, callback function will be called.
When lockToXXX is called, callback function also can be called.
It can return either `PORTRAIT` `LANDSCAPE-LEFT` `LANDSCAPE-RIGHT` `PORTRAIT-UPSIDEDOWN` `UNKNOWN`

- `removeDeviceOrientationListener(function(deviceOrientation))`

- `addLockListener(function(orientation))`

When call lockToXXX/unlockAllOrientations, callback function will be called.
It can return either `PORTRAIT` `LANDSCAPE-LEFT` `LANDSCAPE-RIGHT` `UNKNOWN`
`UNKNOWN` means not be locked.

- `removeLockListener(function(orientation))`

- `removeAllListeners()`

## Functions

- `lockToPortrait()`
- `lockToLandscape()`
- `lockToLandscapeLeft()`  this will lock to camera left home button right
- `lockToLandscapeRight()` this will lock to camera right home button left
- `lockToPortraitUpsideDown` only support android and Windows
- `lockToAllOrientationsButUpsideDown` only ios
- `unlockAllOrientations()`
- `getOrientation(function(orientation))`
- `getDeviceOrientation(function(deviceOrientation))`
- `getAutoRotateState(function(state))` (android only)
- `isLocked()` (lock status by this library)

orientation can return one of:

- `PORTRAIT`
- `LANDSCAPE-LEFT` camera left home button right
- `LANDSCAPE-RIGHT` camera right home button left
- `PORTRAIT-UPSIDEDOWN`
- `FACE-UP`
- `FACE-DOWN`
- `UNKNOWN`

Notice: PORTRAIT-UPSIDEDOWN is currently not supported on iOS at the moment. FACE-UP and FACE-DOWN are only supported on iOS and Windows.
