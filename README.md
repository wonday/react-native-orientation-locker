# react-native-orientation-locker
[![npm](https://img.shields.io/npm/v/react-native-orientation-locker.svg?style=flat-square)](https://www.npmjs.com/package/react-native-orientation-locker)

A react-native module that can listen on orientation changing of device, get current orientation, lock to preferred orientation. (cross-platform support)

### Feature

* lock screen orientation to PORTRAIT|LANDSCAPE-LEFT|PORTRAIT-UPSIDEDOWN|LANDSCAPE-RIGHT.
* listen on orientation changing of device
* get the current orientation of device
 
### Installation
#### Using npm

    npm install react-native-orientation-locker --save
    react-native link react-native-orientation-locker


#### Using CocoaPods (iOS Only)

    pod 'react-native-orientation-locker', :path => '../node_modules/react-native-orientation-locker'

Consult the React Native documentation on how to [install React Native using CocoaPods](https://facebook.github.io/react-native/docs/embedded-app-ios.html#install-react-native-using-cocoapods).

### Configuration

#### iOS

Add the following to your project's `AppDelegate.m`:

```objc
#import "Orientation.h" // <--- import

@implementation AppDelegate

  // ...

  - (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    return [Orientation getOrientation];
  }

@end
```

#### Android

Implement onConfigurationChanged method (in MainActivity.java)

```
    import android.content.Intent; // <--- import
    import android.content.res.Configuration; // <--- import

    public class MainActivity extends ReactActivity {
      ......
      @Override
      public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent("onConfigurationChanged");
        intent.putExtra("newConfig", newConfig);
        this.sendBroadcast(intent);
    }

      ......

    }
```

## Usage

Whenever you want to use it within React Native code now you can:
`import Orientation from 'react-native-orientation-locker';`

```javascript
  _onOrientationDidChange: function(orientation) {
    if (orientation == 'LANDSCAPE-LEFT') {
      //do something with landscape left layout
    } else {
      //do something with portrait layout
    }
  },

  componentWillMount: function() {
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

  componentDidMount: function() {
    Orientation.lockToPortrait(); //this will lock the view to Portrait
    //Orientation.lockToLandscapeLeft(); //this will lock the view to Landscape
    //Orientation.unlockAllOrientations(); //this will unlock the view to all Orientations

    Orientation.addOrientationListener(this._onOrientationDidChange);
  },

  componentWillUnmount: function() {
    Orientation.getOrientation((orientation)=> {
      console.log("Current Device Orientation: ", orientation);
    });
    Orientation.removeOrientationListener(this._onOrientationDidChange);
  }
```

## Events

- `addOrientationListener(function(orientation))`

orientation can return either `PORTRAIT` `LANDSCAPE-LEFT` `LANDSCAPE-RIGHT` `PORTRAIT-UPSIDEDOWN` `UNKNOWN`

- `removeOrientationListener(function(orientation))`


## Functions

- `lockToPortrait()`
- `lockToLandscapeLeft()`
- `lockToLandscapeRight()`
- `unlockAllOrientations()`
- `getOrientation(function(orientation)`

orientation can return either `PORTRAIT` `LANDSCAPE-LEFT` `LANDSCAPE-RIGHT` `PORTRAIT-UPSIDEDOWN` `UNKNOWN`