//
//  react-native-orientation-locker
//  Orientation.mm
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//
//  TurboModule implementation: implements the codegen-generated
//  `NativeOrientationSpec` protocol (from src/NativeOrientation.ts) and
//  binds it to JSI through `getTurboModule:` under the New Architecture,
//  while remaining a classic RCTEventEmitter module on the legacy bridge.
//


#import "Orientation.h"

#import <React/RCTBridgeModule.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTUtils.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <ReactCommon/RCTTurboModuleManager.h>
#endif

@implementation Orientation
{
#if (!TARGET_OS_TV)
    UIInterfaceOrientation _lastOrientation;
    UIInterfaceOrientation _lastDeviceOrientation;
    BOOL _disableFaceUpDown;
#endif
    BOOL _isLocking;
}

#if (!TARGET_OS_TV)
static UIInterfaceOrientationMask _orientationMask = UIInterfaceOrientationMaskAll;

+ (void)setOrientation: (UIInterfaceOrientationMask)orientationMask {
    _orientationMask = orientationMask;
}

+ (UIInterfaceOrientationMask)getOrientation {
    return _orientationMask;
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"orientationDidChange",@"deviceOrientationDidChange",@"lockDidChange"];
}

- (instancetype)init
{
    if ((self = [super init])) {
        _lastOrientation = [self getInterfaceOrientation];
        _lastDeviceOrientation = [self getDeviceOrientation];
        _isLocking = NO;
        _disableFaceUpDown = NO;

        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deviceOrientationDidChange:) name:UIDeviceOrientationDidChangeNotification object:nil];
        [self addListener:@"orientationDidChange"];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    // Do not call `[self removeListeners:1]` here. The bridge tears down
    // JS-side subscriptions before module dealloc, decrementing
    // RCTEventEmitter's listener counter to 0. An additional removeListeners:1
    // here drives the counter negative on every reload, producing
    // "Attempted to remove more Orientation listeners than added" and (under
    // the New Architecture) destabilizing the Fabric scheduler teardown.
}

- (UIInterfaceOrientation)getInterfaceOrientation
{
    if(@available(iOS 13, *)) {
        UIInterfaceOrientation orientation = UIInterfaceOrientationUnknown;
        UIWindowScene *activeScene = [self getWindowScene];
        if (activeScene != nil && [activeScene isKindOfClass:[UIWindowScene class]]) {
            UIWindowScene *windowScene = (UIWindowScene *)activeScene;
            orientation = windowScene.interfaceOrientation;
        }

#if DEBUG
        if(orientation == UIInterfaceOrientationUnknown) {
            NSLog(@"Device orientation is unknown.");
        }
#endif

        return orientation;
    } else {
        return [UIApplication sharedApplication].statusBarOrientation;
    }
}

- (UIInterfaceOrientation)getDeviceOrientation {
    UIInterfaceOrientation deviceOrientation = (UIInterfaceOrientation) [UIDevice currentDevice].orientation;

    BOOL isFaceUpDown = deviceOrientation == UIDeviceOrientationFaceUp || deviceOrientation == UIDeviceOrientationFaceDown;
    if (_disableFaceUpDown && isFaceUpDown) {
        return [self getInterfaceOrientation];
    }

    return deviceOrientation;
}

- (void)deviceOrientationDidChange:(NSNotification *)notification
{
    UIInterfaceOrientation orientation = [self getInterfaceOrientation];
    UIInterfaceOrientation deviceOrientation = [self getDeviceOrientation];

    // do not send Unknown Orientation
    if (deviceOrientation==UIInterfaceOrientationUnknown) {
        return;
    }

    if (orientation!=UIInterfaceOrientationUnknown && orientation!=_lastOrientation) {
        [self sendEventWithName:@"orientationDidChange" body:@{@"orientation": [self getOrientationStr:orientation]}];
        _lastOrientation = orientation;
    }

    // when call lockToXXX, not sent deviceOrientationDidChange
    if (!_isLocking && deviceOrientation!=_lastDeviceOrientation) {
        [self sendEventWithName:@"deviceOrientationDidChange" body:@{@"deviceOrientation":[self getOrientationStr:deviceOrientation]}];
        _lastDeviceOrientation = deviceOrientation;
    }
}

- (UIWindowScene *)getWindowScene {
    NSArray *array = [[[UIApplication sharedApplication] connectedScenes] allObjects];

    // Prefer a foreground scene: with multiple scenes connected (e.g. iPad
    // multitasking) the first entry is not necessarily the visible one.
    UIWindowScene *foregroundScene = nil;
    for (id connectedScene in array) {
        if ([connectedScene isKindOfClass:[UIWindowScene class]] &&
            ((UIWindowScene *)connectedScene).activationState == UISceneActivationStateForegroundActive) {
            foregroundScene = connectedScene;
            break;
        }
    }
    if (foregroundScene != nil) {
        return foregroundScene;
    }
    for (id connectedScene in array) {
        if ([connectedScene isKindOfClass:[UIWindowScene class]]) {
            return connectedScene;
        }
    }
    return nil;
}

- (NSString *)getOrientationStr: (UIInterfaceOrientation)orientation {

    NSString *orientationStr;
    switch (orientation) {
        case UIInterfaceOrientationPortrait:
            orientationStr = @"PORTRAIT";
            break;

        case UIInterfaceOrientationLandscapeLeft:
            orientationStr = @"LANDSCAPE-RIGHT";
            break;

        case UIInterfaceOrientationLandscapeRight:
            orientationStr = @"LANDSCAPE-LEFT";
            break;

        case UIInterfaceOrientationPortraitUpsideDown:
            orientationStr = @"PORTRAIT-UPSIDEDOWN";
            break;

        case UIDeviceOrientationFaceUp:
            orientationStr = @"FACE-UP";
            break;

        case UIDeviceOrientationFaceDown:
            orientationStr = @"FACE-DOWN";
            break;

        default:
            orientationStr = @"UNKNOWN";
            break;
    }
    return orientationStr;
}

- (void)lockToOrientation:(UIInterfaceOrientation) newOrientation usingMask:(UIInterfaceOrientationMask) mask  {
    // set a flag so that no deviceOrientationDidChange events are sent to JS
    _isLocking = YES;
    NSString* orientation = @"orientation";

    [Orientation setOrientation:mask];

    if (@available(iOS 16.0, *)) {
        UIWindowScene *windowScene = [self getWindowScene];
        if (windowScene != nil) {
            UIWindowSceneGeometryPreferencesIOS *geometryPreferences = [[UIWindowSceneGeometryPreferencesIOS alloc] initWithInterfaceOrientations:mask];
            [windowScene requestGeometryUpdateWithPreferences:geometryPreferences errorHandler:^(NSError * _Nonnull error) {
#if DEBUG
                if (error) {
                    NSLog(@"Failed to update geometry with UIInterfaceOrientationMask: %@", error);
                }
#endif
            }];
        }
    } else {
        UIDevice* currentDevice = [UIDevice currentDevice];

        [currentDevice setValue:@(UIInterfaceOrientationUnknown) forKey:orientation];
        [currentDevice setValue:@(newOrientation) forKey:orientation];
    }

    [UIViewController attemptRotationToDeviceOrientation];

    [self sendEventWithName:@"lockDidChange" body:@{orientation: [self getOrientationStr:newOrientation]}];

    _isLocking = NO;
}

#else

- (NSArray<NSString *> *)supportedEvents
{
    return @[];
}

#endif

RCT_EXPORT_MODULE();

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeOrientationSpecJSI>(params);
}
#endif

RCT_EXPORT_METHOD(configure:(BOOL)disableFaceUpDown)
{
#if DEBUG
    NSLog(@"Configure called with disableFaceUpDown: %d", disableFaceUpDown);
#endif

#if (!TARGET_OS_TV)
    _disableFaceUpDown = disableFaceUpDown;
#endif
}

RCT_EXPORT_METHOD(getOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        UIInterfaceOrientation orientation = [self getInterfaceOrientation];
        NSString *orientationStr = [self getOrientationStr:orientation];
        resolve(orientationStr);
    });
#endif
}

RCT_EXPORT_METHOD(getDeviceOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        UIInterfaceOrientation deviceOrientation = [self getDeviceOrientation];
        NSString *orientationStr = [self getOrientationStr:deviceOrientation];
        resolve(orientationStr);
    });
#endif
}

RCT_EXPORT_METHOD(getAutoRotateState:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    // No public iOS API exposes the system rotation-lock setting.
    resolve(@YES);
}

RCT_EXPORT_METHOD(lockToPortrait)
{
#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self lockToOrientation:UIInterfaceOrientationPortrait usingMask:UIInterfaceOrientationMaskPortrait];
    });
#endif
}

RCT_EXPORT_METHOD(lockToPortraitUpsideDown)
{
#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self lockToOrientation:UIInterfaceOrientationPortraitUpsideDown usingMask:UIInterfaceOrientationMaskPortraitUpsideDown];
    });
#endif
}

RCT_EXPORT_METHOD(lockToLandscape)
{
#if DEBUG
    NSLog(@"Locking to Landscape");
#endif

#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        // Lock to sensor landscape, keeping the current landscape side when
        // already in landscape (mirrors Android's SENSOR_LANDSCAPE). The
        // lock event reports the direction actually kept, not a hardcoded
        // "LANDSCAPE-LEFT".
        UIInterfaceOrientation current = [self getInterfaceOrientation];
        UIInterfaceOrientation target =
            (current == UIInterfaceOrientationLandscapeLeft || current == UIInterfaceOrientationLandscapeRight)
                ? current
                : UIInterfaceOrientationLandscapeRight; // reports as "LANDSCAPE-LEFT"
        [self lockToOrientation:target usingMask:UIInterfaceOrientationMaskLandscape];
    });
#endif
}

RCT_EXPORT_METHOD(lockToLandscapeRight)
{
#if DEBUG
    NSLog(@"Locking to Landscape Right");
#endif

#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self lockToOrientation:UIInterfaceOrientationLandscapeLeft usingMask:UIInterfaceOrientationMaskLandscapeLeft];
    });
#endif
}

RCT_EXPORT_METHOD(lockToLandscapeLeft)
{
#if DEBUG
    NSLog(@"Locking to Landscape Left");
#endif
#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self lockToOrientation:UIInterfaceOrientationLandscapeRight usingMask:UIInterfaceOrientationMaskLandscapeRight];
    });
#endif
}

RCT_EXPORT_METHOD(lockToAllOrientationsButUpsideDown)
{
#if DEBUG
    NSLog(@"Locking to all except upside down");
#endif
#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self lockToOrientation:UIInterfaceOrientationPortrait usingMask:UIInterfaceOrientationMaskAllButUpsideDown];
    });
#endif
}

RCT_EXPORT_METHOD(unlockAllOrientations)
{
#if DEBUG
    NSLog(@"Unlocking All Orientations");
#endif

#if (!TARGET_OS_TV)
    dispatch_async(dispatch_get_main_queue(), ^{
        [self lockToOrientation:UIInterfaceOrientationUnknown usingMask:UIInterfaceOrientationMaskAll];
    });
#endif
}

- (NSDictionary *)constantsToExport
{
#if (!TARGET_OS_TV)
    UIInterfaceOrientation orientation = [self getInterfaceOrientation];
    NSString *orientationStr = [self getOrientationStr:orientation];

    return @{@"initialOrientation": orientationStr};
#endif
    return nil;
}

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

@end
