# react-native-orientation-locker example

Example app for `react-native-orientation-locker`, running on the
**React Native New Architecture** (TurboModule).

- React Native `0.76.1`, New Architecture enabled by default
  (`android/gradle.properties` → `newArchEnabled=true`, iOS pod install with
  `RCT_NEW_ARCH_ENABLED=1`).
- The library is referenced via `"file:../"`; `.npmrc` sets
  `install-links=true` so npm **copies** the library into `node_modules`
  (a symlink would be skipped by Metro, since the target lies outside the
  project root).
- iOS: `AppDelegate.mm` returns `Orientation.getOrientation()` from
  `supportedInterfaceOrientationsForWindow` (required for locking).

## Run

```sh
npm install

# iOS
cd ios && RCT_NEW_ARCH_ENABLED=1 pod install && cd ..
npm run ios

# Android
npm run android
```

## After editing the library source

The library in `node_modules` is a copy, so re-sync it (and rebuild the
native side) after changes:

```sh
npm run lib:sync
```

(iOS: rebuild from Xcode or `npm run ios`; Android is picked up by gradle
automatically.)
