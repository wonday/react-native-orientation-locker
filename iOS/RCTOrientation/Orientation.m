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
    }
    return self;
    
}

- (void)dealloc
{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}

- (void)deviceOrientationDidChange:(NSNotification *)notification
{
    
    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    
    [self sendEventWithName:@"orientationDidChange" body:@{@"orientation": [self getOrientationStr:orientation]}];
    
}

- (NSString *)getOrientationStr: (UIDeviceOrientation)orientation {
    
    NSString *orientationStr;
    switch (orientation) {
        case UIDeviceOrientationPortrait:

            orientationStr = @"PORTRAIT";
            break;
        
        case UIDeviceOrientationLandscapeLeft:
            
            orientationStr = @"LANDSCAPE-LEFT";
            break;
            
        case UIDeviceOrientationLandscapeRight:
            
            orientationStr = @"LANDSCAPE-RIGHT";
            break;
            
        case UIDeviceOrientationPortraitUpsideDown:

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
    
    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    NSString *orientationStr = [self getOrientationStr:orientation];
    callback(@[orientationStr]);
    
}

RCT_EXPORT_METHOD(lockToPortrait)
{
    
#if DEBUG
    NSLog(@"Locked to Portrait");
#endif
    [Orientation setOrientation:UIInterfaceOrientationMaskPortrait];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationPortrait] forKey:@"orientation"];
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscapeRight)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape Right");
#endif
    [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeLeft];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeLeft] forKey:@"orientation"];
    }];
    
}

RCT_EXPORT_METHOD(lockToLandscapeLeft)
{
    
#if DEBUG
    NSLog(@"Locked to Landscape Left");
#endif
    [Orientation setOrientation:UIInterfaceOrientationMaskLandscapeRight];
    [[NSOperationQueue mainQueue] addOperationWithBlock:^ {
        // this seems counter intuitive
        [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger: UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
    }];
    
}

RCT_EXPORT_METHOD(unlockAllOrientations)
{
    
#if DEBUG
    NSLog(@"Unlock All Orientations");
#endif
    [Orientation setOrientation:UIInterfaceOrientationMaskAll];
    
}

- (NSDictionary *)constantsToExport
{
    
    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    NSString *orientationStr = [self getOrientationStr:orientation];
    
    return @{@"initialOrientation": orientationStr};
    
}

@end
