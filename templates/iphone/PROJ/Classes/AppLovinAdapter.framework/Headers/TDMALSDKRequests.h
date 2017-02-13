//
//  TDMALSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#define ALSDK 1

//Debug flags
#define TDMALDEBUG 1

#if defined(TDMALDEBUG)
#define TDMALLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMALLog(...)
#endif

@class TDMediationCredentialsConfig;

@protocol TDMALDelegate;

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdocumentation"

#import "ALSdk.h"
#import "TDMALSDKInterstitialRequests.h"
#import "TDMALSDKVideoRequests.h"
#import "TDMALSDKRewardedRequests.h"

#pragma clang pop

#ifdef ALSDK
@interface TDMALSDKRequests : NSObject <TDMALInterstitialDelegate, TDMALVideoDelegate, TDMALRewardedDelegate>
#else
@interface TDMALSDKRequests : NSObject
#endif
@property (nonatomic, weak) id <TDMALDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationCredentialsConfig *)config;

// Interstitial

- (void)loadInterstitial;

- (BOOL)isInterstitialReady;

- (void)showInterstitial;

// Video

- (void)loadVideo;

- (BOOL)isVideoReady;

- (void)showVideo;

// Rewarded Video

- (void)loadRewardedVideo;

- (BOOL)isRewardedVideoReady;

- (void)showRewardedVideo;

@end

#pragma mark -
#pragma mark TDMALDelegate

@protocol TDMALDelegate <NSObject>

@optional

#pragma mark - Interstitial

- (void)tapdaqALDidLoadInterstitial;

- (void)tapdaqALDidFailToLoadInterstitial;

- (void)tapdaqALWillDisplayInterstitial;

- (void)tapdaqALDidDisplayInterstitial;

- (void)tapdaqALDidCloseInterstitial;

- (void)tapdaqALDidClickInterstitial;

#pragma mark - Video

- (void)tapdaqALDidLoadVideo;

- (void)tapdaqALDidFailToLoadVideo;

- (void)tapdaqALWillDisplayVideo;

- (void)tapdaqALDidDisplayVideo;

- (void)tapdaqALDidCloseVideo;

- (void)tapdaqALDidClickVideo;

#pragma mark - Rewarded Video

- (void)tapdaqALDidLoadRewardedVideo;

- (void)tapdaqALDidFailToLoadRewardedVideo;

- (void)tapdaqALWillDisplayRewardedVideo;

- (void)tapdaqALDidDisplayRewardedVideo;

- (void)tapdaqALDidCloseRewardedVideo;

- (void)tapdaqALDidClickRewardedVideo;

- (void)tapdaqALRewardValidationSucceeded:(NSString *)rewardName
                             rewardAmount:(int)rewardAmount;

- (void)tapdaqALRewardValidationErrored;

@end
