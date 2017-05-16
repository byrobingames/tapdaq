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
#import "TDAdUnitEnum.h"

//Debug flags
#define TDMCBDEBUG 1

#if defined(TDMCBDEBUG)
#define TDMCBLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMCBLog(...)
#endif

@class TDMediationConfig;
@protocol TDMCBDelegate;

#ifdef CBSDK

@interface TDMCBSDKRequests : NSObject <ChartboostDelegate>
#else
@interface TDMCBSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMCBDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationConfig *)config;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

@end

#pragma mark TDMCBDelegate

@protocol TDMCBDelegate <NSObject>

@required

- (void)tapdaqCBDidLoadConfig;

- (void)tapdaqCBDidFailToLoadConfig;

#pragma mark - All ad units

- (void)tapdaqCBDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqCBDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqCBWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqCBDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqCBDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqCBDidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Rewarded Video

- (void)tapdaqCBRewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;

- (void)tapdaqCBRewardValidationErroredWithPlacementTag:(NSString *)placementTag;

@end
