package;

import openfl.Lib;


class Tapdaq {

	private static var initialized:Bool=false;
	private static var testmode:String;

	////////////////////////////////////////////////////////////////////////////
	
	private static var __init:String->String->String->Void = function(appId:String,clientKey:String,testmode:String){};
	private static var __showInterstitial:Void->Void = function(){};
	
	private static var __interstitialLoaded:Dynamic;
	private static var __interstitialFailedToLoad:Dynamic;
	private static var __interstitialClosed:Dynamic;


	////////////////////////////////////////////////////////////////////////////

	private static var lastTimeInterstitial:Int = -60*1000;
	private static var displayCallsCounter:Int = 0;
	
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////

	public static function showInterstitial(minInterval:Int=60, minCallsBeforeDisplay:Int=0) {
		displayCallsCounter++;
		if( (Lib.getTimer()-lastTimeInterstitial)<(minInterval*1000) ) return;
		if( minCallsBeforeDisplay > displayCallsCounter ) return;
		displayCallsCounter = 0;
		lastTimeInterstitial = Lib.getTimer();
		try{
			__showInterstitial();
		}catch(e:Dynamic){
			trace("ShowInterstitial Exception: "+e);
		}
	}
	
	public static function init(appId:String, clientKey:String,mode:Int){
	
		if(mode == 1)
		{
			testmode = "YES";
		}else
		{
			testmode = "NO";
		}
	
		
		if(initialized) return;
		initialized = true;
		try{
			// CPP METHOD LINKING
			__init = cpp.Lib.load("tapdaq","tapdaq_init",3);
			__showInterstitial = cpp.Lib.load("tapdaq","tapdaq_interstitial_show",0);
			__interstitialLoaded = cpp.Lib.load("tapdaq","tapdaq_interstitial_loaded",0);
			__interstitialFailedToLoad = cpp.Lib.load("tapdaq","tapdaq_interstitial_failed",0);
			__interstitialClosed = cpp.Lib.load("tapdaq","tapdaq_interstitial_closed",0);

			__init(appId, clientKey, testmode);
		}catch(e:Dynamic){
			trace("iOS INIT Exception: "+e);
		}
			
	}
	
	public static function getInterstitialInfo(info:Int):Bool
	{
        if (info == 0)
        {
           	 return __interstitialLoaded();
        }
        else if (info == 1)
        {
           		return __interstitialFailedToLoad();
        }
        else
        {	
           		return __interstitialClosed();
		}

        return false;
    }
}