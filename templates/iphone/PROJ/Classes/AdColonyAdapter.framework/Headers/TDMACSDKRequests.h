//
//  TDMACSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 02/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "TDMACSDKConstants.h"
#import "TDMACSDKVideoRequests.h"
#import "TDMACSDKRewardedRequests.h"
#import "TDAdUnitEnum.h"

@class TDMediationConfig;

@protocol TDMACDelegate;

#ifdef ACSDK
@interface TDMACSDKRequests : NSObject <TDMACVideoDelegate, TDMACRewardedDelegate>
#else
@interface TDMACSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMACDelegate> delegate;

+ (instancetype)sharedInstance;

/**
 * Configures the SDK, prefetches ads.
 */
- (void)configure:(TDMediationConfig *)config;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

@end

#pragma mark - TDMACDelegate

@protocol TDMACDelegate <NSObject>

@required

#pragma mark - Config

- (void)tapdaqACDidLoadConfig;

- (void)tapdaqACDidFailToLoadConfig;

#pragma mark - All Ads

- (void)tapdaqACDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqACDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqACWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqACDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqACDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqACDidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Rewarded Video

- (void)tapdaqACRewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;

- (void)tapdaqACRewardValidationErroredWithPlacementTag:(NSString *)placementTag;

@end
