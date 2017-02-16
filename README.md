## Stencyl Tapdaq Advertising Extension (Openfl)

For Stencyl 3.4 and above

Stencyl extension for "Tapdaq" (http://www.tapdaq.com) for iOS and Android. This extension allows you to easily integrate Tapdaq on your Stencyl game / application. (http://www.stencyl.com)

### Important!!

To get it work on IOS you have to Download, Install and Enable “<a href="http://byrobin.nl/store/product/byrobin-extension-manager-stencylopenfl/">byRobin Extension Manager</a>”

### Main Features
* Cross Promotion
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game.#interstitial-ads">Interstitial Ad</a>
* Mediation
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game.#banner-ads">Banner Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game.#interstitial-ads">Interstitial Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game.#video-ads">Video Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game.#rewarded-video-ads">Rewarded Video Ad</a>
    
### Supported Networks
This extension supports the following Networks.<br/>
**The networks SDK's are included in this extension, you don't need to enable the seperate extenions for those networks **<br/>
* Adcolony
* Admob
* AppLovin
* Chartboost
* Facebook Audience Network
* UnityAds
* Vungle

## How to Install

http://byrobin.nl/store Add the extension to the cart en proceed with checkout. After you successfully placed the order, you will get an e-mail with the download link or go to “<strong>My Account</strong>” section where you can also find the download link.

or use "Download Zip" on this page.

Install the zip file: Go to : <a href="http://community.stencyl.com/index.php/topic,30432.0.html" target="_blank">http://community.stencyl.com/index.php/topic,30432.0.html</a>

## Documentation and Blocks
See wiki page:
https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game.

## iOS Error on Compiling
If you get this error on compiling: iOS (only)

    [haxelib] ** BUILD FAILED **
    [haxelib] clang: error: linker command failed with exit code 1 (use -v to see invocation)
    [haxelib] ld: symbol(s) not found for architecture arm64
    [haxelib]       objc-class-ref in libtapdaq.a(cd332943_TapdaqEx.o)
    [haxelib]   "_OBJC_CLASS_$_TDProperties", referenced from:
    [haxelib]       objc-class-ref in libtapdaq.a(cd332943_TapdaqEx.o)
    [haxelib]   "_OBJC_CLASS_$_TDTestDevices", referenced from:
    [haxelib]      (maybe you meant: _OBJC_CLASS_$_TapdaqController)
    [haxelib]       objc-class-ref in libtapdaq.a(cd332943_TapdaqEx.o)
    [haxelib]   "_OBJC_CLASS_$_Tapdaq", referenced from:
    
This means that the iOS Framework are not linked anymore in some how after you donwload the extension.
### Solution:
1) Go to "YOURDOCUMENTFOLDER"/stencylworks/engine-extensions/tapdaq-master/frameworks"
2) Delete the Tapdaq.framework in this folder
3) Download the Tapdaq.framework from here: https://www.dropbox.com/s/vqeg64i8f8736mq/tapdaq-ios-sdk-master.zip?dl=0
4) Unzip it and copy the Tapdaq.framework to "YOURDOCUMENTFOLDER"/stencylworks/engine-extensions/tapdaq-master/frameworks"
5) Run clean project and try to compile

## Donate

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HKLGFCAGKBMFL)<br />


## License

The MIT License (MIT) - LICENSE.md

Copyright © 2014 byRobinGames (http://www.byrobingames.com)

Author: Robin Schaafsma
