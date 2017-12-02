//
//  TDMACSDKRequests.h
//  Tapdaq iOS SDK
//
//  Created by Mukund Agarwal on 02/09/2016.
//  Copyright © 2016 Tapdaq. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "TDMediationAdapter.h"

@interface TDMACSDKRequests : NSObject <TDMediationAdapter>
@property (nonatomic, weak) id <TDAdapterDelegate> delegate;
@property (nonatomic, weak) id <TDAdapterConfigDelegate> configDelegate;
@end
