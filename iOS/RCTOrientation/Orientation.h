//
//  react-native-orientation-locker
//  Orientation.h
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <RNOrientationLockerSpec/RNOrientationLockerSpec.h>
#else
#import <React/RCTBridgeModule.h>
#endif
#import <React/RCTEventEmitter.h>

@interface Orientation : RCTEventEmitter
#ifdef RCT_NEW_ARCH_ENABLED
    <NativeOrientationSpec>
#else
    <RCTBridgeModule>
#endif
#if (!TARGET_OS_TV)
+ (void)setOrientation: (UIInterfaceOrientationMask)orientation;
+ (UIInterfaceOrientationMask)getOrientation;
#endif
@end
