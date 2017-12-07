//
//  TDMISSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Nick Reffitt on 31/05/2017.
//  Copyright © 2017 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TDMISSDKConstants.h"
#import "TDMediationAdapter.h"

@interface TDMISSDKRequests : NSObject <TDMediationAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end
