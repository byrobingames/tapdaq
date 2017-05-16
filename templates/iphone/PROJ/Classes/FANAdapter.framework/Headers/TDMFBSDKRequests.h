//
//  TDMFBSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 05/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMFBSDKInterstitialRequests.h"
#import "TDMFBSDKVideoRequests.h"
#import "TDAdUnitEnum.h"
#import "TDMBannerSizeEnum.h"
#import "TDMFBSDKConstants.h"

@class TDMediationConfig;
@protocol TDMFANDelegate;

#ifdef FBSDK
@interface TDMFBSDKRequests : NSObject <FBAdViewDelegate, TDMFANInterstitialDelegate, TDMFANVideoDelegate>
#else
@interface TDMFBSDKRequests : NSObject
#endif 

@property (nonatomic, weak) id <TDMFANDelegate> delegate;

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

#pragma mark -
#pragma mark TDMFANDelegate

@protocol TDMFANDelegate <NSObject>

@required

- (void)tapdaqFANDidLoadConfig;

#pragma mark - All ad units

- (void)tapdaqFANDidLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqFANDidFailToLoadAdUnit:(TDAdUnit)adUnit;

- (void)tapdaqFANWillDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqFANDidDisplayAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqFANDidCloseAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

- (void)tapdaqFANDidClickAdUnit:(TDAdUnit)adUnit withPlacementTag:(NSString *)placementTag;

#pragma mark - Banner

- (void)tapdaqFANDidRefreshBanner;

- (void)tapdaqFANDidClickBanner;

@end
