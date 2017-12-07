//
//  TDMVGSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TDMediationAdapter.h"
#define VGSDK 1


//Debug flags
#define TDMVGINFO 1
#define TDMVGDEBUG 0

#if defined(TDMVGINFO)
#define TDMVGInfoLog(fmt, ...) NSLog((@"[Vungle Adapter] " fmt), ##__VA_ARGS__)
#else
#   define TDMVGInfoLog(...)
#endif

#if defined(TDMVGDEBUG)
#define TDMVGDebugLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMVGDebugLog(...)
#endif

@interface TDMVGSDKRequests : NSObject <TDMediationAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end

