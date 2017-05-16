//
//  TDProperties.h
//  Tapdaq
//
//  Created by Tapdaq <support@tapdaq.com>
//  Copyright (c) 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "TDOrientationEnum.h"
#import "TDAdTypeEnum.h"
#import "TDPlacement.h"
#import "TDTestDevices.h"

@interface TDProperties : NSObject

@property (nonatomic, strong) NSString *rewardName;

@property (nonatomic, strong) NSNumber *rewardValue;

@property (nonatomic) BOOL isDebugEnabled;

/**
 Note: For plugin developers only.
 */
@property (nonatomic) NSString *pluginVersion;

/**
 To use placement tags, you must create a TDPlacement object and register it.
 If you do not register a placement tag but attempt to use a custom one elsewhere, adverts will not display.
 
 @param placement The TDPlacement object to be registered
 */
- (BOOL)registerPlacement:(TDPlacement *)placement;
- (BOOL)registerPlacements:(NSArray *)placements;

- (BOOL)registerTestDevices:(TDTestDevices *)testDevices;

- (NSArray *)registeredPlacements;

- (NSArray *)registeredTestDevices;

@end
