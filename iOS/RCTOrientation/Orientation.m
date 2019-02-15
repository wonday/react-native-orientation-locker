//
//  react-native-orientation-locker
//  Orientation.m
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//


#import "Orientation.h"


@implementation Orientation
{
    UIInterfaceOrientation _lastOrientation;
    UIInterfaceOrientation _lastDeviceOrientation;
    BOOL _isLocking;
}

static UIInterfaceOrientationMask _orientation = UIInterfaceOrientationMaskAll;

+ (void)setOrientation: (UIInterfaceOrientationMask)orientation {
    
    _orientation = orientation;
    
}

+ (UIInterfaceOrientationMask)getOrientation {
    
    return _orientation;
    
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
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(deviceOrientationDidChange:) name:@"UIDeviceOrientationDidChangeNotification" object:nil];
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

    // do not send UnKnow Orientation
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
            
        default:
            orientationStr = @"UNKNOWN";
            break;
    }
    return orientationStr;
    
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(getOrientation:(RCTResponseSenderBlock)callback)
{
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
        NSString *orientationStr = [self getOrientationStr:orientation];
        callback(@[orientationStr]);
    }];
    
}

RCT_EXPORT_METHOD(getDeviceOrientation:(RCTResponseSenderBlock)callback)
{
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        UIInterfaceOrientation deviceOrientation = (UIInterfaceOrientation) [UIDevice currentDevice].orientation;
        NSString *orientationStr = [self getOrientationStr:deviceOrientation];
        callback(@[orientationStr]);
    }];
    
}

RCT_EXPORT_METHOD(lockToPortrait)
{

    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        
        // set a flag so that no deviceOrientationDidChange events are sent to JS
        _isLocking = YES;
        
        UIInterfaceOrientation deviceOrientation = _lastDeviceOrientation;
        
        // lock to Portrait
        [Orientation setOrientation:UIInterfaceOrientationMaskPortrait];

        // when call lockXXX, make sure to sent orientationDidChange event to JS
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationUnknown] forKey:@"orientation"];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationPortrait] forKey:@"orientation"];
        
        // restore device orientation
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: deviceOrientation] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];

        // send a lock event
        [self sendEventWithName:@"lockDidChange" body:@{@"orientation":@"PORTRAIT"}];

        _isLocking = NO;
        
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscape)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape");
#endif

    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        
        // set a flag so that no deviceOrientationDidChange events are sent to JS
        _isLocking = YES;
        
        UIInterfaceOrientation deviceOrientation = _lastDeviceOrientation;

        UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
        NSString *orientationStr = [self getOrientationStr:orientation];

        // when call lockXXX, make sure to sent orientationDidChange event to JS
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationUnknown] forKey:@"orientation"];
        
        if ([orientationStr isEqualToString:@"LANDSCAPE-RIGHT"]) {
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
        [self sendEventWithName:@"lockDidChange" body:@{@"orientation":@"LANDSCAPE-LEFT"}];

        _isLocking = NO;
        
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscapeRight)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape Right");
#endif
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        
        // set a flag so that no deviceOrientationDidChange events are sent to JS
        _isLocking = YES;
        
        UIInterfaceOrientation deviceOrientation = _lastDeviceOrientation;
        
        // lock to LandscapeLeft
        [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeLeft];
        
        // when call lockXXX, make sure to sent orientationDidChange event to JS
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationUnknown] forKey:@"orientation"];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
        
        // restore device orientation
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: deviceOrientation] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];

        // send a lock event
        [self sendEventWithName:@"lockDidChange" body:@{@"orientation":@"PORTRAIT"}];

        _isLocking = NO;
        
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscapeLeft)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape Left");
#endif
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {

        // set a flag so that no deviceOrientationDidChange events are sent to JS
        _isLocking = YES;
        
        UIInterfaceOrientation deviceOrientation = _lastDeviceOrientation;
        
        // lock to LandscapeRight
        [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeRight];
        
        // when call lockXXX, make sure to sent orientationDidChange event to JS
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationUnknown] forKey:@"orientation"];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
        
        // restore device orientation
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: deviceOrientation] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];

        // send a lock event
        [self sendEventWithName:@"lockDidChange" body:@{@"orientation":@"LANDSCAPE-LEFT"}];

        _isLocking = NO;
        
    }];
    
}

RCT_EXPORT_METHOD(unlockAllOrientations)
{
    
#if DEBUG
    NSLog(@"Unlock All Orientations");
#endif
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        
        // set a flag so that no deviceOrientationDidChange events are sent to JS
        _isLocking = YES;
        
        // unlock all
        [Orientation setOrientation:UIInterfaceOrientationMaskAll];
        
        // restore to device orientation and make sure to sent orientationDidChange event to JS
        UIInterfaceOrientation lastDeviceOrientation = _lastDeviceOrientation;
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationUnknown] forKey:@"orientation"];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: lastDeviceOrientation] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];

        // send a lock event
        [self sendEventWithName:@"lockDidChange" body:@{@"orientation":@"UNKNOWN"}];

        _isLocking = NO;

    }];

}

- (NSDictionary *)constantsToExport
{
    
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    NSString *orientationStr = [self getOrientationStr:orientation];
    
    return @{@"initialOrientation": orientationStr};
    
}

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

@end
