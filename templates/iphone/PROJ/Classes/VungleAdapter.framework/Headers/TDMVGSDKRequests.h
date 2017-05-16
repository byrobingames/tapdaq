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
#import "TDAdUnitEnum.h"

//Debug flags
#define TDMVGINFO 1
#define TDMVGDEBUG 0

#if defined(TDMVGINFO)
#define TDMVGInfoLog(fmt, ...) NSLog((@"[Vungle Adapter] " fmt), ##__VA_ARGS__)
#else
#   define TDMVGInfoLog(...)
#endif

#if defined(TDMVGDEBUG)
#define TDMVGDebugLog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMVGDebugLog(...)
#endif

@class TDMediationConfig;
@protocol TDMVGDelegate;

#ifdef VGSDK

@interface TDMVGSDKRequests : NSObject <VungleSDKDelegate>
#else
@interface TDMVGSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMVGDelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationConfig *)config;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

@end

#pragma mark - TDMVGDelegate

@protocol TDMVGDelegate <NSObject>

@required

- (void)tapdaqVGDidLoadConfig;

#pragma mark - Video

- (void)tapdaqVGDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqVGDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqVGWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqVGDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqVGDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqVGDidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Rewarded Video

- (void)tapdaqVGRewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;

@end
