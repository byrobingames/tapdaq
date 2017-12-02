//
//  TDMoPubSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Dmitry Dovgoshliubnyi on 19/07/2017.
//  Copyright © 2017 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMediationAdapter.h"
#import "TDMediationBannerAdapter.h"

@interface TDMoPubSDKRequests : NSObject <TDMediationAdapter, TDMediationBannerAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end
