//
//  Tapdaq.h
//  Tapdaq
//
//  Created by Tapdaq <support@tapdaq.com>
//  Copyright (c) 2015 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "TDOrientationEnum.h"
#import "TDNativeAdUnitEnum.h"
#import "TDNativeAdSizeEnum.h"

#import "TDAdTypeEnum.h"
#import "TDNativeAdTypeEnum.h"

@protocol TapdaqDelegate;

@class TDAdvert;
@class TDNativeAdvert;
@class TDInterstitialAdvert;
@class TDProperties;

@interface Tapdaq : NSObject

@property (nonatomic, weak) id <TapdaqDelegate> delegate;

/**
 The singleton Tapdaq object, use this for all method calls
 
 @return The Tapdaq singleton.
 */
+ (instancetype)sharedSession;

#pragma mark Initializing Tapdaq

/**
 A setter for the Application ID of your app, and the Client Key associated with your Tapdaq account. You can obtain these details when you sign up and add your app to https://tapdaq.com
 You must use this in the application:didFinishLaunchingWithOptions method. By default, test adverts is not enabled.
 Only intersitials will be fetched, to enable native adverts, use -setApplicationId:clientKey:properties:.
 
 @param applicationId The application ID tied to your app.
 @param clientKey The client key tied to your app.
 */
- (void)setApplicationId:(NSString *)applicationId
               clientKey:(NSString *)clientKey;

/**
 This overloaded method takes in an additional testMode boolean, where you can toggle test adverts.
 Only intersitials will be fetched, to enable native adverts, use -setApplicationId:clientKey:properties:.
 
 @param applicationId The application ID tied to your app.
 @param clientKey The client key tied to your app.
 @param testMode Set to YES if test adverts should be displayed, otherwise NO will display live ads.
 */
- (void)setApplicationId:(NSString *)applicationId
               clientKey:(NSString *)clientKey
                testMode:(BOOL)testMode;

/**
 This overloaded method takes in a TDProperties object, use this method to change the default configuration.
 
 @param applicationId The application ID tied to your app.
 @param clientKey The client key tied to your app.
 @param properties The properties object that overrides the Tapdaq defaults. See TDProperties for info on all configuration options.
 */
- (void)setApplicationId:(NSString *)applicationId
               clientKey:(NSString *)clientKey
              properties:(TDProperties *)properties;

#pragma mark Interstitials

/** 
 Displays an interstitial to the user, if an interstitial is available to be shown. 
 */
- (void)showInterstitial;

/**
 This method gives you greater control over where the interstitial appears in the view heirarchy.
 
 @param view The view which the interstitial view is added to as a subview.
 */
- (void)showInterstitial:(UIView *)view;

#pragma mark Native adverts

/**
 Fetches a TDNativeAdvert which, unlike -showInterstitial, gives you full control over the UI layout. 
 This advert will include the already-fetched creative, icon, and other data such as app name, description, etc.
 You must implement -triggerImpression: and -triggerClick: when using this method.
 
 @param nativeAdType The native advert type to be fetched.
 @return A TDNativeAdvert.
 */
- (TDNativeAdvert *)getNativeAdvertForAdType:(TDNativeAdType)nativeAdType;

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

#pragma mark Interstitials (no UI support)

/**
 Fetches the interstitial advert. 
 Use this method only if you wish to control the UI of the interstitial.
 The interstitial fetched will take into account the current orientation of the device.
 
 @return The fetched interstitial advert.
 */
- (TDInterstitialAdvert *)getInterstitialAdvert;

/**
 Fetches the interstitial advert for a given orientation.
 Use this method only if you wish to control the UI of the interstitial.
 
 @param orientation The orientation of the interstitial to be fetched.
 @return A fetched interstitial advert.
 */
- (TDInterstitialAdvert *)getInterstitialAdvertForOrientation:(TDOrientation)orientation;

#pragma mark Mediation mode

/**
 Used only when mediation mode is enabled, see TDProperties. 
 Loads a native advert, this will fetch the native advert's creative, and call either -didLoadNativeAdvert:forAdType: if the advert is successfully loaded, or -didFailToLoadNativeAdvertForAdType: if it fails to load. 
 We recommend you implement both delegate methods to handle the advert accordingly.
 
 @param nativeAdType The native advert type to be loaded.
 */
- (void)loadNativeAdvertForAdType:(TDNativeAdType)nativeAdType;

/**
 Used only when mediation mode is enabled, see TDProperties.
 Loads an interstitial advert, this will fetch the interstitial's creative, and calls either -didLoadInterstitial:forOrientation: if the advert is successfully loaded, or -didFailToLoadInterstitialForOrientation: if it fails to load.
 We recommend you implement both delegate methods to handle the advert accordingly.
 
 @param orientation The orientation of the interstitial to be loaded.
 */
- (void)loadInterstitialAdvertForOrientation:(TDOrientation)orientation;

#pragma mark Misc

/**
 This method is only used for plugins such as Unity which do not automatically trigger the launch request on application bootup.
 */
- (void)launch;

/******************
 Deprecated methods
 ******************/

- (void)setApplicationId:(NSString *)applicationId
               clientKey:(NSString *)clientKey
                  config:(NSDictionary *)config __deprecated_msg("Use setApplicationId:clientKey:properties:");

- (void)setApplicationId:(NSString *)applicationId
               clientKey:(NSString *)clientKey
             orientation:(TDOrientation)orientation __deprecated_msg("Use setApplicationId:clientKey:properties:");

- (TDNativeAdvert *)getNativeAdvertForAdUnit:(TDNativeAdUnit)adUnit
                                        size:(TDNativeAdSize)adSize __deprecated_msg("Use getNativeAdvertForType:");

- (TDNativeAdvert *)getNativeAdvertForAdUnit:(TDNativeAdUnit)adUnit
                                        size:(TDNativeAdSize)adSize
                                 orientation:(TDOrientation)orientation __deprecated_msg("Use getNativeAdvertForType:");

- (void)loadNativeAdvertForAdUnit:(TDNativeAdUnit)adUnit
                             size:(TDNativeAdSize)adSize
                      orientation:(TDOrientation)orientation __deprecated_msg("Use loadNativeAdvertForAdType:");

- (void)sendImpression:(TDAdvert *)advert __deprecated_msg("Use triggerImpression:");

- (void)sendClick:(TDAdvert *)advert __deprecated_msg("Use triggerClick:");

@end

#pragma mark -
#pragma mark TapdaqDelegate

@protocol TapdaqDelegate <NSObject>

@optional

#pragma mark Interstitial delegate methods

/**
 Called immediately before the interstitial is to be displayed to the user.
 This method is only used in conjunction with -showInterstitial.
 */
- (void)willDisplayInterstitial;

/**
 Called immediately after the interstitial is displayed to the user.
 This method is only used in conjunction with -showInterstitial.
 */
- (void)didDisplayInterstitial;

/**
 Called when, for whatever reason, the interstitial was not able to be displayed.
 This method is only used in conjunction with -showInterstitial.
 */
- (void)didFailToDisplayInterstitial;

/**
 Called when the user closes interstitial, either by tapping the close button, or the background surrounding the interstitial.
 This method is only used in conjunction with -showInterstitial.
 */
- (void)didCloseInterstitial;

/**
 Called when the user clicks the interstitial.
 This method is only used in conjunction with -showInterstitial.
 */
- (void)didClickInterstitial;

/**
 Called with an error occurs when requesting interstitials from the Tapdaq servers.
 */
- (void)didFailToFetchInterstitialsFromServer;

/**
 Called when the request to obtain interstitials from the Tapdaq servers was successful, but no interstitials were found.
 */
- (void)hasNoInterstitialsAvailable;

/**
 Called each time an interstitial is ready to be displayed. 
 By default this method may be called multiple times on application launch, for each supported orientation.
 
 @param orientation The orientation of the interstitial that is ready to be displayed.
 */
- (void)hasInterstitialsAvailableForOrientation:(TDOrientation)orientation;

#pragma mark Native advert delegate methods

/**
 Called when an error occurs when requesting native adverts from the Tapdaq servers.
 */
- (void)didFailToFetchNativeAdvertsFromServer;

/**
 Called when the request to obtain native adverts from the Tapdaq servers was successful, but no native adverts were found.
 */
- (void)hasNoNativeAdvertsAvailable;

/**
 Called each time a native advert is ready to be fetched.
 By default this method may be called multiple times on application launch, for each supported native ad type.
 
 @param nativeAdType The advert type that is ready to be fetched.
 */
- (void)hasNativeAdvertsAvailableForAdType:(TDNativeAdType)nativeAdType;

#pragma mark Mediation mode delegate methods

/**
 Used only when mediation mode is enabled, see TDProperties.
 Called when an interstitial is successfully loaded, used in conjunction with -loadInterstitialAdvertForOrientation:.
 
 @param advert The loaded interstitial advert.
 @param orientation The orientation of the interstitial advert.
 */
- (void)didLoadInterstitial:(TDInterstitialAdvert *)advert forOrientation:(TDOrientation)orientation;

/**
 Used only when mediation mode is enabled, see TDProperties.
 Called when the interstitial failed to load, used in conjunction with -loadInterstitialAdvertForOrientation:.
 
 @param orientation The orientation of the interstitial that failed to load.
 */
- (void)didFailToLoadInterstitialForOrientation:(TDOrientation)orientation;

/**
 Used only when mediation mode is enabled, see TDProperties.
 Called when a native adverts is successfully loaded, used in conjunction with -loadNativeAdvertForAdType:.
 
 @param advert The loaded native advert.
 @param nativeAdType The ad type.
 */
- (void)didLoadNativeAdvert:(TDNativeAdvert *)advert
                  forAdType:(TDNativeAdType)nativeAdType;

/**
 Used only when mediation mode is enabled, see TDProperties.
 Called when the native ad failed to load, used in conjunction with -loadNativeAdvertForAdType:.
 
 @param nativeAdType The ad type of the native advert that failed to load.
 */
- (void)didFailToLoadNativeAdvertForAdType:(TDNativeAdType)nativeAdType;

/***************************
 Deprecated delegate methods
 ***************************/

- (void)hasNativeAdvertsAvailableForAdUnit:(TDNativeAdUnit)adUnit
                                      size:(TDNativeAdSize)adSize
                               orientation:(TDOrientation)orientation __deprecated_msg("Use hasNativeAdvertsAvailableForAdType:");


- (void)didLoadNativeAdvert:(TDNativeAdvert *)advert
                  forAdUnit:(TDNativeAdUnit)adUnit
                       size:(TDNativeAdSize)adSize
                orientation:(TDOrientation)orientation __deprecated_msg("Use didLoadNativeAdvert:forAdType:");

- (void)didFailToLoadNativeAdvertForAdUnit:(TDNativeAdUnit)adUnit
                                      size:(TDNativeAdSize)adSize
                               orientation:(TDOrientation)orientation __deprecated_msg("Use didFailToLoadNativeAdvertForAdType:");

- (void)didFailToLoadInterstitial __deprecated_msg("Use didFailToFetchInterstitialsFromServer");

- (void)didFailToShowInterstitial __deprecated_msg("Use didFailToDisplayInterstitial");

- (void)didFailToLoadNativeAdverts __deprecated_msg("Use didFailtoFetchNativeAdvertsFromServer");

@end