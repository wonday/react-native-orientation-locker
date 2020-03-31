//
//  react-native-orientation-locker
//  Orientation.m
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//
#import "Orientation.h"

@implementation Orientation {
    #if (!TARGET_OS_TV)
        UIInterfaceOrientation _lastOrientation;
        UIInterfaceOrientation _lastDeviceOrientation;
    #endif
    BOOL _isLocking;
}

#if (!TARGET_OS_TV)
static UIInterfaceOrientationMask _orientationMask = UIInterfaceOrientationMaskAll;
NSString *LANDSCAPE_RIGHT = @"LANDSCAPE-RIGHT";
NSString *LANDSCAPE_LEFT = @"LANDSCAPE-LEFT";
NSString *PORTRAIT_UPSIDEDOWN = @"PORTRAIT-UPSIDEDOWN";
NSString *PORTRAIT = @"PORTRAIT";
NSString *FACE_UP = @"FACE-UP";
NSString *FACE_DOWN = @"FACE-DOWN";
NSString *UNKNOWN = @"UNKNOWN";

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
        _lastOrientation = [UIApplication sharedApplication].statusBarOrientation;;
        _lastDeviceOrientation = (UIInterfaceOrientation) [UIDevice currentDevice].orientation;
        _isLocking = NO;
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deviceOrientationDidChange:) name:UIDeviceOrientationDidChangeNotification object:nil];
        [self addListener:@"orientationDidChange"];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self removeListeners:1];
}

- (void)deviceOrientationDidChange:(NSNotification *)notification
{
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    UIInterfaceOrientation deviceOrientation = (UIInterfaceOrientation) [UIDevice currentDevice].orientation;
    
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

- (NSString *)getOrientationStr: (UIInterfaceOrientation)orientation {
    
    NSString *orientationStr;
    switch (orientation) {
        case UIInterfaceOrientationPortrait:
            
            orientationStr = PORTRAIT;
            break;
            
        case UIInterfaceOrientationLandscapeLeft:
            
            orientationStr = LANDSCAPE_RIGHT;
            break;
            
        case UIInterfaceOrientationLandscapeRight:
            
            orientationStr = LANDSCAPE_LEFT;
            break;
            
        case UIInterfaceOrientationPortraitUpsideDown:
            
            orientationStr = PORTRAIT_UPSIDEDOWN;
            break;
        
        case UIDeviceOrientationFaceUp:

            orientationStr = FACE_UP;
            break;

        case UIDeviceOrientationFaceDown:
        
            orientationStr = FACE_DOWN;
            break;

        default:
            orientationStr = UNKNOWN;
            break;
    }
    return orientationStr;
}

- (void)lockToOrientation:(UIInterfaceOrientation) newOrientation usingMask:(UIInterfaceOrientationMask) mask  {
    // set a flag so that no deviceOrientationDidChange events are sent to JS
    _isLocking = YES;
    NSString* orientation = @"orientation";
    
    UIInterfaceOrientation deviceOrientation = _lastDeviceOrientation;
    
    [Orientation setOrientation:mask];
    UIDevice* currentDevice = [UIDevice currentDevice];
    
    [currentDevice setValue:@(UIInterfaceOrientationUnknown) forKey:orientation];
    [currentDevice setValue:@(newOrientation) forKey:orientation];
    
    // restore device orientation
    [currentDevice setValue:@(deviceOrientation) forKey:orientation];
    
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

RCT_EXPORT_METHOD(getOrientation:(RCTPromiseResolveBlock) resolve rejecter:(RCTPromiseRejectBlock)reject) {
    #if (!TARGET_OS_TV)
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
            NSString *orientationStr = [self getOrientationStr:orientation];
            resolve(orientationStr);
        }];
    #endif
}

RCT_EXPORT_METHOD(getDeviceOrientation:(RCTPromiseResolveBlock) resolve rejecter:(RCTPromiseRejectBlock)reject) {
    #if (!TARGET_OS_TV)
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            UIInterfaceOrientation deviceOrientation = (UIInterfaceOrientation) [UIDevice currentDevice].orientation;
            NSString *orientationStr = [self getOrientationStr:deviceOrientation];
            resolve(orientationStr);
        }];
    #endif
}

RCT_EXPORT_METHOD(lockToPortrait)
{
#if (!TARGET_OS_TV)
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [self lockToOrientation:UIInterfaceOrientationPortrait usingMask:UIInterfaceOrientationMaskPortrait];
    }];
#endif
}

RCT_EXPORT_METHOD(lockToPortraitUpsideDown)
{
#if (!TARGET_OS_TV)
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [self lockToOrientation:UIInterfaceOrientationPortraitUpsideDown usingMask:UIInterfaceOrientationMaskPortraitUpsideDown];
    }];
#endif
}

RCT_EXPORT_METHOD(lockToLandscape)
{
#if DEBUG
    NSLog(@"Locking to Landscape");
#endif
    
#if (!TARGET_OS_TV)
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        
        // set a flag so that no deviceOrientationDidChange events are sent to JS
        _isLocking = YES;
        
        UIInterfaceOrientation deviceOrientation = _lastDeviceOrientation;
        
        UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
        NSString *orientationStr = [self getOrientationStr:orientation];
        
        // when call lockXXX, make sure to sent orientationDidChange event to JS
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationUnknown] forKey:@"orientation"];
        
        if ([orientationStr isEqualToString:LANDSCAPE_RIGHT]) {
            [Orientation setOrientation:UIInterfaceOrientationMaskLandscape];
            [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
        } else {
            [Orientation setOrientation:UIInterfaceOrientationMaskLandscape];
            [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
        }
        
        // restore device orientation
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: deviceOrientation] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];
        
        // send a lock event
        [self sendEventWithName:@"lockDidChange" body:@{@"orientation":LANDSCAPE_LEFT}];
        
        _isLocking = NO;
        
    }];
#endif
}

RCT_EXPORT_METHOD(lockToLandscapeRight) {
    #if DEBUG
        NSLog(@"Locking to Landscape Right");
    #endif
        
    #if (!TARGET_OS_TV)
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            [self lockToOrientation:UIInterfaceOrientationLandscapeLeft usingMask:UIInterfaceOrientationMaskLandscapeLeft];
        }];
    #endif
}

RCT_EXPORT_METHOD(lockToLandscapeLeft) {
    #if DEBUG
        NSLog(@"Locking to Landscape Left");
    #endif
    #if (!TARGET_OS_TV)
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            [self lockToOrientation:UIInterfaceOrientationLandscapeRight usingMask:UIInterfaceOrientationMaskLandscapeRight];
        }];
    #endif
}

RCT_EXPORT_METHOD(lockToAllOrientationsButUpsideDown) {
    #if DEBUG
        NSLog(@"Locking to all except upside down");
    #endif
    #if (!TARGET_OS_TV)
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            [self lockToOrientation:UIInterfaceOrientationPortrait usingMask:UIInterfaceOrientationMaskAllButUpsideDown];
        }];
    #endif
}

RCT_EXPORT_METHOD(unlockAllOrientations) {
    #if DEBUG
        NSLog(@"Unlocking All Orientations");
    #endif
        
    #if (!TARGET_OS_TV)
        [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
            [self lockToOrientation:UIInterfaceOrientationUnknown usingMask:UIInterfaceOrientationMaskAll];
        }];
    #endif
}

RCT_EXPORT_METHOD(isLocked:(RCTPromiseResolveBlock) resolve rejecter:(RCTPromiseRejectBlock)reject) {
    resolve(@(_isLocking));
}

RCT_EXPORT_METHOD(getAutoRotateState:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    reject(@"Error", @"Not implemented on iOS", nil);
}

- (NSDictionary *)constantsToExport {
    #if (!TARGET_OS_TV)
        UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
        NSString *orientationStr = [self getOrientationStr:orientation];
        
        return @{
            @"initialOrientation": orientationStr,
            @"PORTRAIT": PORTRAIT,
            @"PORTRAIT_UPSIDEDOWN": PORTRAIT_UPSIDEDOWN,
            @"LANDSCAPE_RIGHT": LANDSCAPE_RIGHT,
            @"LANDSCAPE_LEFT": LANDSCAPE_LEFT,
            @"FACE_UP": FACE_UP,
            @"FACE_DOWN": FACE_DOWN,
            @"UNKNOWN": UNKNOWN
        };
    #endif
    return nil;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

@end
