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
#import "TDAdUnitEnum.h"

//Debug flags
#define TDMUADEBUG 1

#if defined(TDMUADEBUG)
#define TDMUALog(fmt, ...) NSLog((@"[%@] " fmt), [self class], ##__VA_ARGS__)
#else
#   define TDMUALog(...)
#endif

@class TDMediationConfig;
@protocol TDMUADelegate;

#ifdef UASDK

@interface TDMUASDKRequests : NSObject
#else
@interface TDMUASDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDMUADelegate> delegate;

+ (instancetype)sharedInstance;

- (void)configure:(TDMediationConfig *)config;

- (void)loadForAdUnit:(TDAdUnit)adUnit;

- (BOOL)isReadyForAdUnit:(TDAdUnit)adUnit;

- (void)showForAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

@end


#pragma mark -
#pragma mark TDMUADelegate

@protocol TDMUADelegate <NSObject>

@required

- (void)tapdaqUADidLoadConfig;

#pragma mark - All ad units

- (void)tapdaqUADidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqUADidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqUAWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqUADidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqUADidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqUADidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Rewarded Video

- (void)tapdaqUARewardValidationSucceededWithPlacementTag:(NSString *)placementTag
                                               rewardName:(NSString *)rewardName
                                             rewardAmount:(int)rewardAmount;

- (void)tapdaqUARewardValidationErroredWithPlacementTag:(NSString *)placementTag;

@end
