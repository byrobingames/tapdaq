//
//  TDMAMSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 09/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMAMSDKInterstitialRequests.h"
#import "TDMAMSDKVideoRequests.h"
#import "TDMAMSDKRewardedRequests.h"

#import "TDMediationAdapter.h"
#import "TDMediationBannerAdapter.h"

#ifdef AMSDK
@interface TDMAMSDKRequests : NSObject <TDMediationAdapter, TDMediationBannerAdapter>
#else
@interface TDMAMSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDAdapterDelegate> delegate;


@end

