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
#import "TDMAMSDKRewardedRequests.h"
#import <GoogleMobileAds/GoogleMobileAds.h>
#import "TDMAMSDKConstants.h"
#import "TDAdUnitEnum.h"

@class TDMediationConfig;
@protocol TDMAMDelegate;

#ifdef AMSDK
@interface TDMAMSDKRequests : NSObject <TDMAMInterstitialDelegate, TDMAMVideoDelegate, TDMAMRewardedDelegate, GADBannerViewDelegate>
#else
@interface TDMAMSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMAMDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationConfig *)config;

- (void)addTestDevices:(NSArray *)testDeviceIDs;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

// Banner

- (void)loadBannerWithSize:(TDMBannerSize)size;

- (UIView *)getBanner;

@end

#pragma mark - TDMAMDelegate

@protocol TDMAMDelegate <NSObject>

@required

- (void)tapdaqAMDidLoadConfig;

#pragma mark - All ad units

- (void)tapdaqAMDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqAMDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqAMWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqAMDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqAMDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqAMDidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Banner

- (void)tapdaqAMDidRefreshBanner;

- (void)tapdaqAMDidClickBanner;

#pragma mark - Rewarded Video

- (void)tapdaqAMRewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;


@end
