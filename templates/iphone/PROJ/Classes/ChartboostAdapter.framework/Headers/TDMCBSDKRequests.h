//
//  TDMCBSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 15/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#define CBSDK 1

#import <Chartboost/Chartboost.h>

//Debug flags
#define TDMCBDEBUG 1

#if defined(TDMCBDEBUG)
#define TDMCBLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMCBLog(...)
#endif

@class TDMediationCredentialsConfig;
@protocol TDMCBDelegate;

#ifdef CBSDK

@interface TDMCBSDKRequests : NSObject <ChartboostDelegate>
#else
@interface TDMCBSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMCBDelegate> delegate;

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

#pragma mark TDMCBDelegate

@protocol TDMCBDelegate <NSObject>

@optional

#pragma mark - Interstitial

- (void)tapdaqCBDidLoadInterstitial;

- (void)tapdaqCBDidFailToLoadInterstitial;

- (void)tapdaqCBWillDisplayInterstitial;

- (void)tapdaqCBDidDisplayInterstitial;

- (void)tapdaqCBDidCloseInterstitial;

- (void)tapdaqCBDidClickInterstitial;

#pragma mark - Video

- (void)tapdaqCBDidLoadVideo;

- (void)tapdaqCBDidFailToLoadVideo;

- (void)tapdaqCBWillDisplayVideo;

- (void)tapdaqCBDidDisplayVideo;

- (void)tapdaqCBDidCloseVideo;

- (void)tapdaqCBDidClickVideo;

#pragma mark - Rewarded Video

- (void)tapdaqCBDidLoadRewardedVideo;

- (void)tapdaqCBDidFailToLoadRewardedVideo;

- (void)tapdaqCBWillDisplayRewardedVideo;

- (void)tapdaqCBDidDisplayRewardedVideo;

- (void)tapdaqCBDidCloseRewardedVideo;

- (void)tapdaqCBDidClickRewardedVideo;

- (void)tapdaqCBRewardValidationSucceeded:(NSString *)rewardName
                             rewardAmount:(int)rewardAmount;

@end
