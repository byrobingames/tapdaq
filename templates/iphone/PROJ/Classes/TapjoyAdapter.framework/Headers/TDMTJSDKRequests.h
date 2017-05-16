//
//  TDMTJSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Nick Reffitt on 18/03/2017.
//  Copyright © 2017 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMTJSDKConstants.h"
#import "TDMTJSDKInterstitialRequests.h"
#import "TDMTJSDKVideoRequests.h"
#import "TDMTJSDKRewardedVideoRequests.h"
#import "TDAdUnitEnum.h"

@class TDMediationConfig;
@protocol TDMTJDelegate;

#ifdef TJSDK
@interface TDMTJSDKRequests : NSObject <TDMTJInterstitialDelegate, TDMTJVideoDelegate, TDMTJRewardedVideoDelegate>
#else
@interface TDMTJSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMTJDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationConfig *)config;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

@end

@protocol TDMTJDelegate <NSObject>

@required

- (void)tapdaqTJDidLoadConfig;

- (void)tapdaqTJDidFailToLoadConfig;

#pragma mark - All Ads

- (void)tapdaqTJDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqTJDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqTJWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqTJDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqTJDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Rewarded Video

- (void)tapdaqTJRewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;

@end
