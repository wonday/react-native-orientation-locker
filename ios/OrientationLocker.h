#import <React/RCTEventEmitter.h>
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNOrientationLockerSpec.h"

@interface OrientationLocker : RCTEventEmitter <NativeOrientationLockerSpec>
#else
#import <React/RCTBridgeModule.h>

@interface OrientationLocker : RCTEventEmitter <RCTBridgeModule>
#endif

@end
