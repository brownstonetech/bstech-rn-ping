//
//  BSTEnvDetect.m
//
//  Created by Miles Huang on 2018-11-29.
//  Copyright © 2018,2019 Brownstonetech Consulting Inc. All rights reserved.
//

#import "BSTEnvDetect.h"

@implementation BSTEnvDetect

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(getEnvAsync, 
    matchingFingerprint:(NSString *)matchingFingerprint
    resolver: (RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject)
{
    NSURL *receiptURL = [[NSBundle mainBundle] appStoreReceiptURL];
    NSString *receiptURLString = [receiptURL path];
    BOOL isRunningTestFlightBeta =  ([receiptURLString rangeOfString:@"sandboxReceipt"].location != NSNotFound);
    if(isRunningTestFlightBeta) {
        NSString *thingToReturn = @"TESTING";
        resolve(thingToReturn);
    } else {
        NSString *thingToReturn = @"PRODUCTION";
        resolve(thingToReturn);
    }
}

@end
