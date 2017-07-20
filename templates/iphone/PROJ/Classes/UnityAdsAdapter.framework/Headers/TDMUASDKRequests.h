//
//  TDMUASDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "TDMediationAdapter.h"

#define UASDK 1

#import <UnityAds/UnityAds.h>

//Debug flags
#define TDMUADEBUG 1

#if defined(TDMUADEBUG)
#define TDMUALog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMUALog(...)
#endif

@class TDMediationConfig;
@protocol TDMUADelegate;

#ifdef UASDK

@interface TDMUASDKRequests : NSObject <TDMediationAdapter>
#else
@interface TDMUASDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDAdapterDelegate> delegate;

@end

