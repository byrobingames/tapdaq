//
//  TDMALSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AppLovinSDK/AppLovinSDK.h>
#import "TDMALSDKConstants.h"
#import "TDAdUnitEnum.h"
#import "TDMALSDKInterstitialRequests.h"
#import "TDMALSDKVideoRequests.h"
#import "TDMALSDKRewardedRequests.h"

@class TDMediationConfig;

@protocol TDMALDelegate;

#ifdef ALSDK
@interface TDMALSDKRequests : NSObject <TDMALInterstitialDelegate, TDMALVideoDelegate, TDMALRewardedDelegate>
#else
@interface TDMALSDKRequests : NSObject
#endif
@property (nonatomic, weak) id <TDMALDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationConfig *)config;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

@end

#pragma mark -
#pragma mark TDMALDelegate

@protocol TDMALDelegate <NSObject>

@required

- (void)tapdaqALDidLoadConfig;

#pragma mark - All ad units

- (void)tapdaqALDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqALDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqALWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqALDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqALDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqALDidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Rewarded Video

- (void)tapdaqALRewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;

- (void)tapdaqALRewardValidationErroredWithPlacementTag:(NSString *)placementTag;

@end
