//
//  TDMCBSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 15/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>


//Debug flags
#define TDMCBDEBUG 1

#if defined(TDMCBDEBUG)
#define TDMCBLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMCBLog(...)
#endif

#import "TDMediationAdapter.h"

@interface TDMCBSDKRequests : NSObject <TDMediationAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end
