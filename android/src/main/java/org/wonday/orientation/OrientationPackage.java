//
//  react-native-orientation-locker
//
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//
//  TurboReactPackage registers the module for both the legacy bridge
//  (createNativeModules) and the New Architecture (getModule +
//  getReactModuleInfoProvider with isTurboModule = true).
//

package org.wonday.orientation;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.TurboReactPackage;

import java.util.HashMap;
import java.util.Map;

// BuildConfig here is this library's own (org.wonday.orientation),
// IS_NEW_ARCHITECTURE_ENABLED is defined in android/build.gradle.

public class OrientationPackage extends TurboReactPackage {

    @Nullable
    @Override
    public NativeModule getModule(String name, ReactApplicationContext reactContext) {
        if (name.equals(OrientationModule.NAME)) {
            return new OrientationModule(reactContext);
        } else {
            return null;
        }
    }

    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return () -> {
            final Map<String, ReactModuleInfo> moduleInfos = new HashMap<>();
            boolean isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
            moduleInfos.put(
                    OrientationModule.NAME,
                    new ReactModuleInfo(
                            OrientationModule.NAME,
                            OrientationModule.NAME,
                            false, // canOverrideExistingModule
                            false, // needsEagerInit
                            true,  // hasConstants
                            false, // isCxxModule
                            isTurboModule));
            return moduleInfos;
        };
    }
}
