//
//  TDMACSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 02/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#define ACSDK 1

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdocumentation"

#import <AdColony/AdColony.h>

#pragma clang pop

//Debug flags
#define TDMACDEBUG 1

#if defined(TDMACDEBUG)
#define TDMACLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMACLog(...)
#endif

@class TDMediationCredentialsConfig;

@protocol TDMACDelegate;

#ifdef ACSDK
@interface TDMACSDKRequests : NSObject <AdColonyDelegate, AdColonyAdDelegate>
#else
@interface TDMACSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMACDelegate> delegate;

+ (instancetype)sharedInstance;

/**
 * Configures the SDK, prefetches ads.
 */
- (void)configure:(TDMediationCredentialsConfig *)config;

- (BOOL)isVideoReady;

- (void)showVideo;

- (BOOL)isRewardedVideoReady;

- (void)showRewardedVideo;

@end

#pragma mark -
#pragma mark TDMACDelegate

@protocol TDMACDelegate <NSObject>

@optional

#pragma mark - Video

- (void)tapdaqACDidLoadVideo;

- (void)tapdaqACDidFailToLoadVideo;

- (void)tapdaqACWillDisplayVideo;

- (void)tapdaqACDidDisplayVideo;

- (void)tapdaqACDidCloseVideo;

#pragma mark - Rewarded Video

- (void)tapdaqACDidLoadRewardedVideo;

- (void)tapdaqACDidFailToLoadRewardedVideo;

- (void)tapdaqACWillDisplayRewardedVideo;

- (void)tapdaqACDidDisplayRewardedVideo;

- (void)tapdaqACDidCloseRewardedVideo;

- (void)tapdaqACRewardValidationSucceeded:(NSString *)rewardName
                             rewardAmount:(int)rewardAmount;

- (void)tapdaqACRewardValidationErrored;

@end
