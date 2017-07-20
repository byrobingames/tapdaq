//
//  TDMCBSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 15/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#define CBSDK 1

//Debug flags
#define TDMCBDEBUG 1

#if defined(TDMCBDEBUG)
#define TDMCBLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMCBLog(...)
#endif

#ifdef CBSDK

#import "TDMediationAdapter.h"

@interface TDMCBSDKRequests : NSObject <TDMediationAdapter>
#else
@interface TDMCBSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDAdapterDelegate> delegate;

@end
