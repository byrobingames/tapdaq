//
//  TDMUASDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#define UASDK 1

#import <UnityAds/UnityAds.h>

//Debug flags
#define TDMUADEBUG 1

#if defined(TDMUADEBUG)
#define TDMUALog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMUALog(...)
#endif

@class TDMediationCredentialsConfig;
@protocol TDMUADelegate;

#ifdef UASDK

@interface TDMUASDKRequests : NSObject <UnityAdsDelegate>
#else
@interface TDMUASDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMUADelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationCredentialsConfig *)config;

- (BOOL)isVideoReady;

- (void)showVideo;

- (BOOL)isRewardedVideoReady;

- (void)showRewardedVideo;

@end


#pragma mark -
#pragma mark TDMUADelegate

@protocol TDMUADelegate <NSObject>

@optional

#pragma mark - Video

- (void)tapdaqUADidLoadVideo;

- (void)tapdaqUADidFailToLoadVideo;

- (void)tapdaqUAWillDisplayVideo;

- (void)tapdaqUADidDisplayVideo;

- (void)tapdaqUADidCloseVideo;

#pragma mark - Rewarded Video

- (void)tapdaqUADidLoadRewardedVideo;

- (void)tapdaqUADidFailToLoadRewardedVideo;

- (void)tapdaqUAWillDisplayRewardedVideo;

- (void)tapdaqUADidDisplayRewardedVideo;

- (void)tapdaqUADidCloseRewardedVideo;

- (void)tapdaqUARewardValidationSucceeded:(NSString *)rewardName
                             rewardAmount:(int)rewardAmount;

- (void)tapdaqUARewardValidationErrored;

@end
