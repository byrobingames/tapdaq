//
//  TDCreative.h
//  Tapdaq
//
//  Created by Tapdaq <support@tapdaq.com>
//  Copyright (c) 2016 Tapdaq. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

#import "TDOrientationEnum.h"
#import "TDResolutionEnum.h"

@class TDAspectRatio;

@interface TDCreative : NSObject

@property (nonatomic, strong) NSString *identifier;
@property (nonatomic, strong) NSURL *url;
@property (nonatomic, strong) UIImage *image;

- (id)initWithIdentifier:(NSString *)identifier
                imageUrl:(NSString *)url;

@end
