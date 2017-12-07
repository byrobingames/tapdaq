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

@interface TDMTJSDKRequests : NSObject <TDMediationAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end

