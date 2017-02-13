//
//  TDMAMSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 09/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMBannerSizeEnum.h"
#import "TDMAMSDKInterstitialRequests.h"
#import "TDMAMSDKVideoRequests.h"

#define AMSDK 1

#import <GoogleMobileAds/GoogleMobileAds.h>

//Debug flags
#define TDMAMDEBUG 1

#if defined(TDMAMDEBUG)
#define TDMAMLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMAMLog(...)
#endif

@class TDMediationCredentialsConfig;
@protocol TDMAMDelegate;

#ifdef AMSDK
@interface TDMAMSDKRequests : NSObject <TDMAMInterstitialDelegate, TDMAMVideoDelegate, GADBannerViewDelegate>
#else
@interface TDMAMSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMAMDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationCredentialsConfig *)config;

- (void)addTestDevices:(NSArray *)testDeviceIDs;

// Interstitial

- (void)loadInterstitial;

- (BOOL)isInterstitialReady;

- (void)showInterstitial;

// Video

- (void)loadVideo;

- (BOOL)isVideoReady;

- (void)showVideo;

// Banner

- (void)loadBannerWithSize:(TDMBannerSize)size;

- (BOOL)isBannerReady;

- (UIView *)getBanner;

@end

#pragma mark - TDMAMDelegate

@protocol TDMAMDelegate <NSObject>

@optional

#pragma mark - Banner

- (void)tapdaqAMDidLoadBanner;

- (void)tapdaqAMDidFailToLoadBanner;

- (void)tapdaqAMDidRefreshBanner;

- (void)tapdaqAMDidClickBanner;

#pragma mark - Interstitial

- (void)tapdaqAMDidLoadInterstitial;

- (void)tapdaqAMDidFailToLoadInterstitial;

- (void)tapdaqAMWillDisplayInterstitial;

- (void)tapdaqAMDidDisplayInterstitial;

- (void)tapdaqAMDidCloseInterstitial;

- (void)tapdaqAMDidClickInterstitial;

#pragma mark - Video

- (void)tapdaqAMDidLoadVideo;

- (void)tapdaqAMDidFailToLoadVideo;

- (void)tapdaqAMWillDisplayVideo;

- (void)tapdaqAMDidDisplayVideo;

- (void)tapdaqAMDidCloseVideo;

- (void)tapdaqAMDidClickVideo;

@end
