//
//  TDMACSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 02/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "TDMediationAdapter.h"

@class TDMediationConfig;

#ifdef ACSDK
@interface TDMACSDKRequests : NSObject <TDMediationAdapter>
#else
@interface TDMACSDKRequests : NSObject <TDMediationAdapter>
#endif

@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@end
