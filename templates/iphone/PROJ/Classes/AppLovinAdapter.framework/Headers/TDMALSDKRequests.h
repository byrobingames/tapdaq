//
//  TDMALSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMALSDKConstants.h"
#import "TDMediationAdapter.h"
#import "TDMediationBannerAdapter.h"

@interface TDMALSDKRequests : NSObject <TDMediationAdapter, TDMediationBannerAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end

