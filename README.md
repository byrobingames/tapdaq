## Stencyl Tapdaq Advertising Extension (Openfl)

For Stencyl 3.4 and above

Stencyl extension for "Tapdaq" (http://www.tapdaq.com) for iOS and Android. This extension allows you to easily integrate Tapdaq on your Stencyl game / application. (http://www.stencyl.com)

### Important!!

This Extension Required the Toolset Extension Manager [https://byrobingames.github.io](https://byrobingames.github.io)

![tapdaqtoolset](http://byrobin.nl/store/wp-content/uploads/sites/4/2016/03/tapdaqtoolset-1.png)

### Main Features
* Cross Promotion
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game#interstitial-ads">Interstitial Ad</a>
* Mediation
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game#banner-ads">Banner Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game#interstitial-ads">Interstitial Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game#video-ads">Video Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game#rewarded-video-ads">Rewarded Video Ad</a>
    - <a href="https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game#more-apps">More Apps</a>
    
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
* Tapjoy
* IronSource

## How to Install

To install this Engine Extension, go to the toolset (byRobin Extension Mananger) in the Extension menu of your game inside Stencyl.<br/>
![toolsetextensionlocation](https://byrobingames.github.io/img/toolset/toolsetextensionlocation.png)<br/>
Select the Extension from the menu and click on "Download"

If you not have byRobin Extension Mananger installed, install this first.<br/>
Go to: [https://byrobingames.github.io](https://byrobingames.github.io)


## Documentation and Blocks
See wiki page:[https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game](https://github.com/byrobingames/tapdaq/wiki/Implement-Tapdaq-Advertising-in-your-Stencyl-game).

### iOS Error on Compiling
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
### Solution:<br/>
1) Go to "YOURDOCUMENTFOLDER"/stencylworks/engine-extensions/tapdaq-master/frameworks"<br/>
2) Delete the Tapdaq.framework in this folder<br/>
3) Download the Tapdaq.framework from here: https://www.dropbox.com/s/bsjbklze05v0cfx/tapdaq-ios-sdk-master.zip?dl=0<br/>
4) Unzip it and copy the Tapdaq.framework to "YOURDOCUMENTFOLDER"/stencylworks/engine-extensions/tapdaq-master/frameworks"<br/>
5) Run clean project and try to compile<br/>

## iOS Make an ipa with Xcode when ready for publish

When you publish with Stencyl you will get error ITMS 90179 when upload ipa with Application Loader.
To Fix this you have to make an ipa with Xcode..

See this wiki page how to do this:</br>
https://github.com/byrobingames/tapdaq/wiki/iOS:-Publish-ipa-with-Xcode

## Version History

- 2015-08-08 (1.0) First release
- 2016-03-28 (1.4) Add Android support
- 2016-10-01 (1.5) Update iOS SDK to 1.1.1 and Android SDK to 2.5.5
- 2016-10-01 (1.6) Fix: error on compile Android
- 2017-02-13 (1.7) Major update iOS SDK to 4.9.0 and Android SDK to 4.2.0 (Mediation Support for banner, inititial, video and rewarded ads)
- 2017-02-15 (1.8) Update iOS SDK to 4.10.0 and Android SDK to 4.3.0
- 2017-02-16 (1.9) Update Android SDK to 4.4.0
- 2017-03-19 (2.0) Update iOS SDK to 4.11.0 and Android SDK to 4.10.0, Added Android Gradle support for openfl4
- 2017-03-21 (2.1) Added placementTag support
- 2017-03-29(2.2) Fix: Register Placement Tags (Register tags inside the Initialize block). Updated iOS SDK to 4.13
- 2017-05-16(2.2.1) Update SDK to iOS: 5.0.1 Android: 5.1.0, Tested for Stencyl 3.5, Required byRobin Toolset Extension Manager
- 2017-07-16(2.2.2) Update SDK to iOS:5.3.0 Android: 5.3.0. Required byRobin Toolset Extension Manager(v3)
- 2017-12-02(2.2.3) Update SDK to iOS:5.9.1 Android: 5.9.0. Required byRobin Toolset Extension Manager(v4) and byRobin Engine manager 0.1.5 For android required Minimum version 16.
- 2017-12-06(2.2.4) Fix and Update SDK to iOS:5.9.3 Android: 5.9.1. Required byRobin Toolset Extension Manager(v4) and byRobin Engine manager 0.1.6 For android required Minimum version 16.
- 2017-12-06(2.2.5) Fix: adapters not being found and Update SDK to iOS:5.9.4  Required byRobin Toolset Extension Manager(v4) and byRobin Engine manager 0.1.6
- 2017-12-11(2.2.6) Fix iOS: rewardValidationSucceeded callback.

## Submitting a Pull Request

This software is opensource.<br/>
If you want to contribute you can make a pull request

Repository: [https://github.com/byrobingames/tapdaq](https://github.com/byrobingames/tapdaq)

Need help with a pull request?<br/>
[https://help.github.com/articles/creating-a-pull-request/](https://help.github.com/articles/creating-a-pull-request/)

### ANY ISSUES?

Add the issue on GitHub<br/>
Repository: [https://github.com/byrobingames/tapdaq/issues](https://github.com/byrobingames/tapdaq/issues)

Need help with creating a issue?<br/>
[https://help.github.com/articles/creating-an-issue/](https://help.github.com/articles/creating-an-issue/)

## Donate

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HKLGFCAGKBMFL)<br />

## License

Author: Robin Schaafsma

The MIT License (MIT)

Copyright (c) 2014 byRobinGames [http://www.byrobin.nl](http://www.byrobin.nl)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
