# react-native-orientation-locker
[![npm](https://img.shields.io/npm/v/react-native-orientation-locker.svg?style=flat-square)](https://www.npmjs.com/package/react-native-orientation-locker)

A react-native module that can listen on orientation changing of device, get current orientation, lock to preferred orientation. (cross-platform support)

### Feature

* lock screen orientation to PORTRAIT|LANDSCAPE-LEFT|PORTRAIT-UPSIDEDOWN|LANDSCAPE-RIGHT.
* listen on orientation changing of device
* get the current orientation of device

 ### ChangeLog

v1.1.4
1. Fix typescript declarations

v1.1.3
1. add addLockListener/removeLockListener
2. improve android orientation changed event sending condition

v1.1.2
 1. improve android orientation changed event timing

v1.1.1
 1. fix show "supported event type for deviceOrientationDidChange..." error in debug
 2. fix getAutoRotateState() code error


v1.1.0 **BREAK CHANGE**  
 1. split ```addOrientationListener(function(orientation, deviceOrientation))``` to ```addOrientationListener(function(orientation))``` and ```addDeviceOrientationListener(function(deviceOrientation))```
 2. make sure when lockToXXX and unlockAllOrientations resend UI orientation event
 3. remove setTimout from orientation listener
 4. add getAutoRotateState() for android
 5. add TypeScript define file

v1.0.22  
 1. add getAutoRotateState() (android only)

v1.0.21
1. add getDeviceOrientation()
2. orientationDidChange return DeviceOrientation


[[more]](https://github.com/wonday/react-native-orientation-locker/releases)

### Notice

RN 0.58 + Android target SDK 27 maybe cause 
```Issue: java.lang.IllegalStateException: Only fullscreen activities can request orientation``` problem, 
see [[#55]](https://github.com/wonday/react-native-orientation-locker/issues/55) for a solution.


### Installation
#### Using npm

    npm install react-native-orientation-locker --save
    react-native link react-native-orientation-locker


#### Using CocoaPods (iOS Only)

    pod 'react-native-orientation-locker', :path => '../node_modules/react-native-orientation-locker/react-native-orientation-locker.podspec'

Consult the React Native documentation on how to [install React Native using CocoaPods](https://facebook.github.io/react-native/docs/embedded-app-ios.html#install-react-native-using-cocoapods).

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

## Usage

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


## Functions

- `lockToPortrait()`
- `lockToLandscape()`
- `lockToLandscapeLeft()`  this will lock to camera left home button right
- `lockToLandscapeRight()` this will lock to camera right home button left
- `unlockAllOrientations()`
- `getOrientation(function(orientation))`
- `getDeviceOrientation(function(deviceOrientation))`
- `getAutoRotateState(function(state))` (android only)

orientation can return one of:

- `PORTRAIT`
- `LANDSCAPE-LEFT` camera left home button right
- `LANDSCAPE-RIGHT` camera right home button left
- `PORTRAIT-UPSIDEDOWN`
- `UNKNOWN`

Notice: PORTRAIT-UPSIDEDOWN not support at iOS now
