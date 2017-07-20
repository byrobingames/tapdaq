//
//  TDMTJSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Nick Reffitt on 18/03/2017.
//  Copyright © 2017 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMTJSDKConstants.h"
#import "TDMediationAdapter.h"

#ifdef TJSDK
@interface TDMTJSDKRequests : NSObject <TDMediationAdapter>
#else
@interface TDMTJSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDAdapterDelegate> delegate;

@end

