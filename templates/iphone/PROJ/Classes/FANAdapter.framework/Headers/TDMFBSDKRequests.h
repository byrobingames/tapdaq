//
//  TDMFBSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 05/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMBannerSizeEnum.h"

#define FBSDK 1

#import <FBAudienceNetwork/FBAudienceNetwork.h>

//Debug flags
#define TDMFBDEBUG 1

#if defined(TDMFBDEBUG)
#define TDMFBLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMFBLog(...)
#endif


@class TDMediationCredentialsConfig;
@protocol TDMFANDelegate;

#ifdef FBSDK
@interface TDMFBSDKRequests : NSObject <FBInterstitialAdDelegate, FBAdViewDelegate>
#else
@interface TDMFBSDKRequests : NSObject
#endif 

@property (nonatomic, weak) id <TDMFANDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationCredentialsConfig *)config;

- (void)addTestDevices:(NSArray *)testDeviceIDs;

// Interstitial

- (void)loadInterstitial;

- (BOOL)isInterstitialReady;

- (void)showInterstitial;

// Banner

- (void)loadBannerWithSize:(TDMBannerSize)size;

- (BOOL)isBannerReady;

- (UIView *)getBanner;

@end

#pragma mark -
#pragma mark TDMFANDelegate

@protocol TDMFANDelegate <NSObject>

@optional

#pragma mark - Banner

- (void)tapdaqFANDidLoadBanner;

- (void)tapdaqFANDidFailToLoadBanner;

- (void)tapdaqFANDidRefreshBanner;

- (void)tapdaqFANDidClickBanner;

#pragma mark - Interstitial

- (void)tapdaqFANDidLoadInterstitial;

- (void)tapdaqFANDidFailToLoadInterstitial;

- (void)tapdaqFANWillDisplayInterstitial;

- (void)tapdaqFANDidDisplayInterstitial;

- (void)tapdaqFANDidCloseInterstitial;

- (void)tapdaqFANDidClickInterstitial;

@end
