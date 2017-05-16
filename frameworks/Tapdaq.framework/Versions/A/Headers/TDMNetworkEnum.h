//
//  TDMNetworkEnum.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 13/10/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, TDMNetwork) {
    TDMUnknown,
    TDMTapdaq,
    TDMAdColony,
    TDMAdMob,
    TDMApplovin,
    TDMChartboost,
    TDMInMobi,
    TDMFacebookAudienceNetwork,
    TDMVungle,
    TDMUnityAds,
    TDMTapjoy
};

static NSString *const kNetworkUnknown = @"unknown";
static NSString *const kNetworkTapdaq = @"tapdaq";
static NSString *const kNetworkAdColony = @"adcolony";
static NSString *const kNetworkAdMob = @"admob";
static NSString *const kNetworkApplovin = @"applovin";
static NSString *const kNetworkChartboost = @"chartboost";
static NSString *const kNetworkInMobi = @"inmobi";
static NSString *const kNetworkFacebookAudienceNetwork = @"facebook";
static NSString *const kNetworkVungle = @"vungle";
static NSString *const kNetworkUnityads = @"unityads";
static NSString *const kNetworkTapjoy = @"tapjoy";
