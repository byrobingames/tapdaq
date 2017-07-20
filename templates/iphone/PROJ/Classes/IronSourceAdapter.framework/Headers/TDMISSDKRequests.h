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

#ifdef ISSDK
@interface TDMISSDKRequests : NSObject <TDMediationAdapter>
#else
@interface TDMISSDKRequests : NSObject
#endif

@property (nonatomic, weak) id <TDAdapterDelegate> delegate;

@end
