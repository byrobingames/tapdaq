//
//  TDMAMSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 09/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TDMediationAdapter.h"
#import "TDMediationBannerAdapter.h"

@interface TDMAMSDKRequests : NSObject <TDMediationAdapter, TDMediationBannerAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end

