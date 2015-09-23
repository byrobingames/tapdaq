## Stencyl Tapdaq Advertising Extension (Openfl)

For Stencyl 3.3 and above

Stencyl extension for "Tapdaq" (http://www.tapdaq.com) for iOS. This extension allows you to easily integrate Tapdaq on your Stencyl game / application. (http://www.stencyl.com)

### Important!!
1) If you use Xcode7, make sure you have selected "IOS Device version" to 5.1.1 in your mobile settings  

2) This extension is using a custum "PROJ.xcodeproj" file, to load the .bundle file, that Tapdaq used, automatically in your IOS game. It wil no harm for your game.

## Main Features

  * Interstitial Support.
  * Allows you to specify min amount of time between interstitial displays (to avoid annoying your users).
  * Allows you to specify min amount of calls to interstitial before it actually gets displayed (to avoid annoying your users).

**TO DO:**
- Android support.

## How to Install
Download zip file on the right of the screen. ![download](http://www.byrobingames.com/stencyl/heyzap/download.png) on this page https://github.com/byrobingames/tapdaq<br />

Install the zip file, see this post how to install: http://community.stencyl.com/index.php/topic,30432.0.html

## Documentation and Blocks

If you don't have an account, create one on http://www.tapdaq.com and get your appID and clientKey.

**Blocks**

1) Put the Start Tapdaq session block in when created event of your first scene (for example in your loading scene). <br/>
**You only have to use this block once.!!**<br/>
![starttapdaq](http://www.byrobingames.com/stencyl/tapdaq/starttapdaq.png)

2) Put the Show Interstitial block under a button or when a scene is created  where you want to show the ad.<br/>
![showinterstitialtapdaq](http://www.byrobingames.com/stencyl/tapdaq/showinterstitialtapdaq.png)<br/>
From the dropdown you can select:
- Release mode
- Test mode<br/>

**Don't forget to set the block on Release mode when your publish your game, or else it will show test ads to your players.**

3) If you want to know if there ads available or when player closed the ad, use the boolean block to get a callback.<br/>
![callbackstapdaq](http://www.byrobingames.com/stencyl/tapdaq/callbackstapdaq.png)<br/>
From the dropdown you can select:
- did successfully load
- did fail to load
- is Closed

## Donate

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=HKLGFCAGKBMFL)<br />


## License

The MIT License (MIT) - LICENSE.md

Copyright © 2014 byRobinGames (http://www.byrobingames.com)

Author: Robin Schaafsma
