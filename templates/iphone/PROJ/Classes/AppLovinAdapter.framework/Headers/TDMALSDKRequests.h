//
//  TDMALSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 14/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AppLovinSDK/AppLovinSDK.h>
#import "TDMALSDKConstants.h"

#import "TDMediationAdapter.h"

#ifdef ALSDK
@interface TDMALSDKRequests : NSObject <TDMediationAdapter>
#else
@interface TDMALSDKRequests : NSObject
#endif
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;

@end

