//
//  react-native-orientation-locker
//  Orientation.m
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//


#import "Orientation.h"


@implementation Orientation

static UIInterfaceOrientationMask _orientation = UIInterfaceOrientationMaskAll;

+ (void)setOrientation: (UIInterfaceOrientationMask)orientation {
    
    _orientation = orientation;
    
}

+ (UIInterfaceOrientationMask)getOrientation {
    
    return _orientation;
    
}

- (NSArray<NSString *> *)supportedEvents
{
    
    return @[@"orientationDidChange"];
    
}

- (instancetype)init
{
    
    if ((self = [super init])) {
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
    
    [self sendEventWithName:@"orientationDidChange" body:@{@"orientation": [self getOrientationStr:orientation], @"deviceOrientation":[self getOrientationStr:deviceOrientation]}];
    
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
    
#if DEBUG
    NSLog(@"Locked to Portrait");
#endif

    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [Orientation setOrientation:UIInterfaceOrientationMaskPortrait];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationPortrait] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscape)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape");
#endif

    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
        NSString *orientationStr = [self getOrientationStr:orientation];
        if ([orientationStr isEqualToString:@"LANDSCAPE-RIGHT"]) {
            [Orientation setOrientation:UIInterfaceOrientationMaskLandscape];
            [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
        } else {
            [Orientation setOrientation:UIInterfaceOrientationMaskLandscape];
            [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
        }
        
        [UIViewController attemptRotationToDeviceOrientation];
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscapeRight)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape Right");
#endif
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeLeft];
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscapeLeft)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape Left");
#endif
    
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeRight];
    
        // this seems counter intuitive
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
        
        [UIViewController attemptRotationToDeviceOrientation];
    }];
    
}

RCT_EXPORT_METHOD(unlockAllOrientations)
{
    
#if DEBUG
    NSLog(@"Unlock All Orientations");
#endif
    [Orientation setOrientation:UIInterfaceOrientationMaskAll];
    
    [UIViewController attemptRotationToDeviceOrientation];
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
