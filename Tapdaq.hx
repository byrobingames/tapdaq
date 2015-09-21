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
	
		#if ios
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
		#end
		
		#if android
		if(initialized) return;
		initialized = true;
		try{
			// JNI METHOD LINKING
			__init = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "init", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
			__showInterstitial = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "showInterstitial", "()V");

			__init(appId, clientKey, testmode);
		}catch(e:Dynamic){
			trace("Android INIT Exception: "+e);
		}
		#end
			
	}
	
	public static function getInterstitialInfo(info:Int):Bool
	{
        if (info == 0)
        {
			#if ios
           	return __interstitialLoaded();
			#end
			
			#if android
			if (__interstitialLoaded == null)
            	{
                	__interstitialLoaded = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "interstitialIsLoaded", "()Z", true);
            	}
            	return __interstitialLoaded();
			#end
        }
        else if (info == 1)
        {
           	#if ios
			return __interstitialFailedToLoad();
			#end
			
			#if android
			if (__interstitialFailedToLoad == null)
            	{
                	__interstitialFailedToLoad = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "interstitialFailedToLoad", "()Z", true);
            	}
            	return __interstitialFailedToLoad();
			#end
        }
        else
        {	
           	#if ios
			return __interstitialClosed();
			#end
			
			#if android
			if (__interstitialClosed == null)
            	{
                	__interstitialClosed = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "interstitialClosed", "()Z", true);
            	}
            	return __interstitialClosed();
			#end
		}

        return false;
    }
}