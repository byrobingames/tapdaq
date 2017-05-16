//
//  Tapdaq.h
//  Tapdaq
//
//  Created by Tapdaq <support@tapdaq.com>
//  Copyright (c) 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "TDOrientationEnum.h"
#import "TDNativeAdUnitEnum.h"
#import "TDNativeAdSizeEnum.h"

#import "TDAdTypeEnum.h"
#import "TDNativeAdTypeEnum.h"
#import "TDMNetworkEnum.h"
#import "TDMBannerSizeEnum.h"

@protocol TapdaqDelegate;

@class TDAdvert;
@class TDNativeAdvert;
@class TDInterstitialAdvert;
@class TDProperties;
@class TDPlacement;
@class TDMoreAppsConfig;

typedef NSString *const TDPTag;

// Default.
static TDPTag const TDPTagDefault = @"default";
// Bootup - Initial bootup of game.
static TDPTag const TDPTagBootup = @"bootup";
// Home Screen - Home screen the player first sees.
static TDPTag const TDPTagHomeScreen = @"home_screen";
// Main Menu - Menu that provides game options.
static TDPTag const TDPTagMainMenu = @"main_menu";
// Pause - Pause screen.
static TDPTag const TDPTagPause = @"pause";
// Level Start - Start of the level.
static TDPTag const TDPTagLevelStart = @"start";
// Level Complete - Completion of the level.
static TDPTag const TDPTagLevelComplete = @"level_complete";
// Game Center - After a user visits the Game Center.
static TDPTag const TDPTagGameCenter = @"game_center";
// IAP Store - The store where the player pays real money for currency or items.
static TDPTag const TDPTagIAPStore = @"iap_store";
// Item Store - The store where a player buys virtual goods.
static TDPTag const TDPTagItemStore = @"item_store";
// Game Over - The game over screen after a player is finished playing.
static TDPTag const TDPTagGameOver = @"game_over";
// Leaderboard - List of leaders in the game.
static TDPTag const TDPTagLeaderBoard = @"leaderboard";
// Settings - Screen where player can change settings such as sound.
static TDPTag const TDPTagSettings = @"settings";
// Quit - Screen displayed right before the player exits a game.
static TDPTag const TDPTagQuit = @"quit";

@interface Tapdaq : NSObject

@property (nonatomic, weak) id <TapdaqDelegate> delegate;

/**
 The singleton Tapdaq object, use this for all method calls
 
 @return The Tapdaq singleton.
 */
+ (instancetype)sharedSession;


#pragma mark Initializing Tapdaq

/**
 A setter for the Application ID of your app, and the Client Key associated with your Tapdaq account. 
 You can obtain these details when you sign up and add your app to https://tapdaq.com
 You must use this in the application:didFinishLaunchingWithOptions method.
 
 @param applicationId The application ID tied to your app.
 @param clientKey The client key tied to your app.
 @param properties The properties object that overrides the Tapdaq defaults. See TDProperties for info on all configuration options.
 */
- (void)setApplicationId:(NSString *)applicationId
               clientKey:(NSString *)clientKey
              properties:(TDProperties *)properties;

#pragma mark Banner

- (void)loadBanner:(TDMBannerSize)size;

- (BOOL)isBannerReady;

- (UIView *)getBanner;

#pragma mark Interstitial

- (void)loadInterstitialForPlacementTag:(NSString *)placementTag;

- (BOOL)isInterstitialReadyForPlacementTag:(NSString *)placementTag;

- (void)showInterstitialForPlacementTag:(NSString *)placementTag;

- (void)loadInterstitial;

- (BOOL)isInterstitialReady;

- (void)showInterstitial;

#pragma mark Video

- (void)loadVideoForPlacementTag:(NSString *)placementTag;

- (BOOL)isVideoReadyForPlacementTag:(NSString *)placementTag;

- (void)showVideoForPlacementTag:(NSString *)placementTag;

- (void)loadVideo;

- (BOOL)isVideoReady;

- (void)showVideo;

#pragma mark Rewarded Video

- (void)loadRewardedVideoForPlacementTag:(NSString *)placementTag;

- (BOOL)isRewardedVideoReadyForPlacementTag:(NSString *)placementTag;

- (void)showRewardedVideoForPlacementTag:(NSString *)placementTag;

- (void)loadRewardedVideo;

- (BOOL)isRewardedVideoReady;

- (void)showRewardedVideo;

#pragma mark Native adverts

/**
 Loads a native advert for a particular placement tag, this will fetch the native advert's creative, and call either -didLoadNativeAdvert:forPlacementTag:adType: if the advert is successfully loaded, or -didFailToLoadNativeAdvertForPlacementTag:adType: if it fails to load.
 We recommend you implement both delegate methods to handle the advert accordingly.
 
 @param placementTag The placement tag of the advert to be loaded.
 @param nativeAdType The native ad type of the advert to be loaded.
 */
- (void)loadNativeAdvertForPlacementTag:(NSString *)placementTag adType:(TDNativeAdType)nativeAdType;

/**
 Fetches a TDNativeAdvert for a particular placement tag.
 You must register the tag in TDProperties otherwise adverts will not display.
 
 @param placementTag The placement tag
 @param nativeAdType The native advert type to be fetched.
 */
- (TDNativeAdvert *)getNativeAdvertForPlacementTag:(NSString *)placementTag adType:(TDNativeAdType)nativeAdType;

/**
 This method must be called when the advert is displayed to the user. You do not need to call this method when using -showInterstitial. 
 This should only be used when either a TDInterstitialAdvert or TDNativeAdvert has been fetched.
 
 @param advert The TDAdvert that has been displayed to the user, this can be a TDInterstitialAdvert or TDNativeAdvert.
 */
- (void)triggerImpression:(TDAdvert *)advert;

/**
 This method must be called when a user taps on the advert, you do not need to call this method when using -showInterstitial. 
 This should only be used when either TDInterstitialAdvert or TDNativeAdvert has been fetched.
 Unlike -triggerImpression:, this method will also direct users to the the App Store, or to a custom URL, depending on the adverts configuration.
 
 @param advert The TDAdvert that has been displayed to the user, this can be a TDInterstitialAdvert or TDNativeAdvert.
 */
- (void)triggerClick:(TDAdvert *)advert;

/**
 Loads a native advert, this will fetch the native advert's creative, and call either -didLoadNativeAdvert:forAdType: if the advert is successfully loaded, or -didFailToLoadNativeAdvertForAdType: if it fails to load.
 We recommend you implement both delegate methods to handle the advert accordingly.
 
 @param nativeAdType The native advert type to be loaded.
 */
- (void)loadNativeAdvertForAdType:(TDNativeAdType)nativeAdType;

/**
 Fetches a TDNativeAdvert which, unlike -showInterstitial, gives you full control over the UI layout.
 This advert will include the already-fetched creative, icon, and other data such as app name, description, etc.
 You must implement -triggerImpression: and -triggerClick: when using this method.
 
 @param nativeAdType The native advert type to be fetched.
 @return A TDNativeAdvert.
 */
- (TDNativeAdvert *)getNativeAdvertForAdType:(TDNativeAdType)nativeAdType;

#pragma mark - More apps

- (void)loadMoreApps;

- (void)loadMoreAppsWithConfig:(TDMoreAppsConfig *)moreAppsConfig;

- (BOOL)isMoreAppsReady;

- (void)showMoreApps;

#pragma mark Misc

/**
 This method is only used for plugins such as Unity which do not automatically trigger the launch request on application bootup.
 */
- (void)launch;

#pragma mark Mediation debug view

/**
 Used to launch the debugger view to test if ads can be shown
 
 @param vc The view controller which will display the launch debugger view.
 
 */
- (void)launchMediationDebugger:(UIViewController *)vc;

@end

#pragma mark -
#pragma mark TapdaqDelegate

@protocol TapdaqDelegate <NSObject>

@optional

- (void)didLoadConfig;

- (void)didFailToLoadConfig;

#pragma mark Banner delegate methods

/**
 Called immediately after the banner is loaded.
 This method should be used in conjunction with -getBanner:.
 */
- (void)didLoadBanner;

/**
 Called when, for whatever reason, the banner was not able to be loaded.
 Tapdaq will automatically attempt to load a banner again with a 1 second delay.
 */
- (void)didFailToLoadBanner;

/**
 Called when the user clicks the banner.
 */
- (void)didClickBanner;

/**
 Called when the ad within the banner view loads another ad.
 */
- (void)didRefreshBanner;

#pragma mark Interstitial delegate methods

/**
 Called immediately after an interstitial is available to the user for a specific placement tag.
 This method should be used in conjunction with -showInterstitialForPlacementTag:.
 @param placementTag A placement tag.
 */
- (void)didLoadInterstitialForPlacementTag:(NSString *)placementTag;

/**
 Called when the interstitial was not able to be loaded for a specific placement tag.
 Tapdaq will automatically attempt to load an interstitial again with a 1 second delay.
 @param placementTag A placement tag.
 */
- (void)didFailToLoadInterstitialForPlacementTag:(NSString *)placementTag;

/**
 Called immediately before the interstitial is to be displayed to the user.
 */
- (void)willDisplayInterstitial;

- (void)willDisplayInterstitialForPlacementTag:(NSString *)placementTag;

/**
 Called immediately after the interstitial is displayed to the user.
 */
- (void)didDisplayInterstitial;

- (void)didDisplayInterstitialForPlacementTag:(NSString *)placementTag;

/**
 Called when the user closes interstitial, either by tapping the close button, or the background surrounding the interstitial.
 */
- (void)didCloseInterstitial;

- (void)didCloseInterstitialForPlacementTag:(NSString *)placementTag;

/**
 Called when the user clicks the interstitial.
 */
- (void)didClickInterstitial;

- (void)didClickInterstitialForPlacementTag:(NSString *)placementTag;

#pragma mark Video delegate methods

/**
 Called immediately after a video is available to the user for a specific placement tag.
 This method should be used in conjunction with -showVideoForPlacementTag:.
 @param placementTag A placement tag.
 */
- (void)didLoadVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when, for whatever reason, the video was not able to be loaded.
 Tapdaq will automatically attempt to load a video again with a 1 second delay.
 @param placementTag A placement tag.
 */
- (void)didFailToLoadVideoForPlacementTag:(NSString *)placementTag;

/**
 Called immediately before the video is to be displayed to the user.
 */
- (void)willDisplayVideo;

- (void)willDisplayVideoForPlacementTag:(NSString *)placementTag;

/**
 Called immediately after the video is displayed to the user.
 */
- (void)didDisplayVideo;

- (void)didDisplayVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when the user closes the video.
 */
- (void)didCloseVideo;

- (void)didCloseVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when the user clicks the video ad.
 */
- (void)didClickVideo;

- (void)didClickVideoForPlacementTag:(NSString *)placementTag;


#pragma mark Rewarded Video delegate methods

/**
 Called immediately after a rewarded video is available to the user for a specific placement tag.
 This method should be used in conjunction with -showRewardedVideoForPlacementTag:.
 @param placementTag A placement tag.
 */
- (void)didLoadRewardedVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when, for whatever reason, the rewarded video was not able to be loaded.
 Tapdaq will automatically attempt to load a rewarded video again with a 1 second delay.
 @param placementTag A placement tag.
 */
- (void)didFailToLoadRewardedVideoForPlacementTag:(NSString *)placementTag;

/**
 Called immediately before the rewarded video is to be displayed to the user.
 */
- (void)willDisplayRewardedVideo;

- (void)willDisplayRewardedVideoForPlacementTag:(NSString *)placementTag;

/**
 Called immediately after the rewarded video is displayed to the user.
 */
- (void)didDisplayRewardedVideo;

- (void)didDisplayRewardedVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when the user closes the rewarded video.
 */
- (void)didCloseRewardedVideo;

- (void)didCloseRewardedVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when the user clicks the rewarded video ad.
 */
- (void)didClickRewardedVideo;

- (void)didClickRewardedVideoForPlacementTag:(NSString *)placementTag;

/**
 Called when a reward is ready for the user.
 @param rewardName The name of the reward.
 @param rewardAmount The value of the reward.
 */
- (void)rewardValidationSucceededForRewardName:(NSString *)rewardName
                                  rewardAmount:(int)rewardAmount;

- (void)rewardValidationSucceededForPlacementTag:(NSString *)placementTag
                                      rewardName:(NSString *)rewardName
                                    rewardAmount:(int)rewardAmount;

/**
 Called if an error occurred when rewarding the user.
 */
- (void)rewardValidationErrored;

- (void)rewardValidationErroredForPlacementTag:(NSString *)placementTag;

#pragma mark Native advert delegate methods

/**
 Called when a native advert is successfully loaded, used in conjunction with -loadNativeAdvertForPlacementTag:adType:.
 
 @param tag The placement tag of the native advert that loaded.
 @param nativeAdType The ad type of the native advert that loaded.
 */
- (void)didLoadNativeAdvertForPlacementTag:(NSString *)placementTag
                                    adType:(TDNativeAdType)nativeAdType;

/**
 Called when the native ad failed to load, used in conjunction with -loadNativeAdvertForPlacementTag:adType:.
 
 @param tag The placement tag that failed to load the native ad.
 @param nativeAdType The ad type of the native advert that failed to load.
 */
- (void)didFailToLoadNativeAdvertForPlacementTag:(NSString *)placementTag
                                          adType:(TDNativeAdType)nativeAdType;

#pragma mark More apps delegate methods

- (void)didLoadMoreApps;

- (void)didFailToLoadMoreApps;

- (void)willDisplayMoreApps;

- (void)didDisplayMoreApps;

- (void)didCloseMoreApps;

@end
