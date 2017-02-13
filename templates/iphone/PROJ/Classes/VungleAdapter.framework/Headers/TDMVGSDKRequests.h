//
//  TDMVGSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#define VGSDK 1

#import <VungleSDK/VungleSDK.h>

//Debug flags
#define TDMVGDEBUG 1

#if defined(TDMVGDEBUG)
#define TDMVGLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMVGLog(...)
#endif

@class TDMediationCredentialsConfig;
@protocol TDMVGDelegate;

#ifdef VGSDK

@interface TDMVGSDKRequests : NSObject <VungleSDKDelegate>
#else
@interface TDMVGSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMVGDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationCredentialsConfig *)config;

- (BOOL)isVideoReady;

- (void)showVideo;

- (BOOL)isRewardedVideoReady;

- (void)showRewardedVideo;

@end

#pragma mark - TDMVGDelegate

@protocol TDMVGDelegate <NSObject>

@optional

#pragma mark - Video

- (void)tapdaqVGDidLoadVideo;

- (void)tapdaqVGDidFailToLoadVideo;

- (void)tapdaqVGWillDisplayVideo;

- (void)tapdaqVGDidDisplayVideo;

- (void)tapdaqVGDidCloseVideo;

- (void)tapdaqVGDidClickVideo;

#pragma mark - Rewarded Video

- (void)tapdaqVGDidLoadRewardedVideo;

- (void)tapdaqVGDidFailToLoadRewardedVideo;

- (void)tapdaqVGWillDisplayRewardedVideo;

- (void)tapdaqVGDidDisplayRewardedVideo;

- (void)tapdaqVGDidCloseRewardedVideo;

- (void)tapdaqVGDidClickRewardedVideo;

- (void)tapdaqVGRewardValidationSucceeded:(NSString *)rewardName
                             rewardAmount:(int)rewardAmount;

@end
