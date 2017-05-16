package;

import openfl.Lib;
import scripts.ByRobinAssets;

class Tapdaq {

	private static var initialized:Bool=false;
	private static var testmode:String;
	
	private static var _bannerdidload:Bool=false;
	private static var _bannerfailtoload:Bool=false;
	private static var _bannerdidclick:Bool=false;
	private static var _bannerdidrefresh:Bool=false;
	private static var _interstitialwilldisplay:Bool=false;
	private static var _interstitialdiddisplay:Bool=false;
	private static var _interstitialdidclose:Bool=false;
	private static var _interstitialdidclick:Bool=false;
	private static var _videowilldisplay:Bool=false;
	private static var _videodiddisplay:Bool=false;
	private static var _videodidclose:Bool=false;
	private static var _videodidclick:Bool=false;
	private static var _rewardedwilldisplay:Bool=false;
	private static var _rewardeddiddisplay:Bool=false;
	private static var _rewardeddidclose:Bool=false;
	private static var _rewardeddidclick:Bool=false;
	private static var _rewardedsucceeded:Bool=false;

	////////////////////////////////////////////////////////////////////////////
	#if ios
	private static var __init:String->String->String->String->Void = function(appId:String,clientKey:String,testmode:String,tagsString:String){};
	private static var __tapdaq_set_event_handle = cpp.Lib.load("tapdaq","tapdaq_set_event_handle", 1);
	#end
	#if android
	private static var __init:Dynamic;
	#end
	private static var __loadInterstitial:String->Void = function(tag:String){};
	private static var __showInterstitial:String->Void = function(tag:String){};
	private static var __loadVideo:String->Void = function(tag:String){};
	private static var __showVideo:String->Void = function(tag:String){};
	private static var __loadRewarded:String->Void = function(tag:String){};
	private static var __showRewarded:String->Void = function(tag:String){};
	private static var __loadBanner:String->Void = function(bannerType:String){};
	private static var __showBanner:Void->Void = function(){};
	private static var __hideBanner:Void->Void = function(){};
	private static var __moveBanner:String->Void = function(gravity:String){};
	private static var __openMediationDebugger:Void->Void = function(){};
	
	private static var __bannerIsReady:Dynamic;
	private static var __interstitialIsReady:Dynamic;
	private static var __videoIsReady:Dynamic;
	private static var __rewardedIsReady:Dynamic;
	////////////////////////////////////////////////////////////////////////////
	public static var _tagsArray:Array<Dynamic>;
	////////////////////////////////////////////////////////////////////////////
	
	private function new() {
		_tagsArray=[];
	}
	
	public static function init(){
		var clientKey:String = ByRobinAssets.TDClientKey;
		#if ios
		var appId:String = ByRobinAssets.TDIosAppId;
		#elseif android
		var appId:String = ByRobinAssets.TDAndroidAppId;
		#end
		
		var interTags:String = ByRobinAssets.TDAdTypeInterstitial;
		var vidTags:String = ByRobinAssets.TDAdTypeVideo;
		var rewarTags:String = ByRobinAssets.TDAdTypeRewardedVideo;
		
		if (interTags == ""){
			interTags = "default";
		}
		if (vidTags == ""){
			vidTags = "default";
		}
		if (rewarTags == ""){
			rewarTags = "default";
		}
		///////////
		if(ByRobinAssets.TDTestADS)
		{
			testmode = "YES";
		}else
		{
			testmode = "NO";
		}
		
		var newInterString:String = getJSONString(interTags);
		var newVideoString:String = getJSONString(vidTags);
		var newRewardString:String = getJSONString(rewarTags);
		
		trace("newInterString" + newInterString);
		trace("newVideoString" + newVideoString);
		trace("newRewardString" + newRewardString);
		
		var tagsString:String = '{"TDAdTypeInterstitial": [$newInterString],"TDAdTypeVideo": [$newVideoString],"TDAdTypeRewardedVideo": [$newRewardString]}';
		trace("tags string : " + tagsString);
	
		#if ios
		if(initialized) return;
		initialized = true;
		try{
			// CPP METHOD LINKING
			__init = cpp.Lib.load("tapdaq","tapdaq_init",4);
			__loadInterstitial = cpp.Lib.load("tapdaq","tapdaq_interstitial_load",1);
			__showInterstitial = cpp.Lib.load("tapdaq","tapdaq_interstitial_show",1);
			__loadVideo = cpp.Lib.load("tapdaq","tapdaq_video_load",1);
			__showVideo = cpp.Lib.load("tapdaq","tapdaq_video_show",1);
			__loadRewarded = cpp.Lib.load("tapdaq","tapdaq_rewarded_load",1);
			__showRewarded = cpp.Lib.load("tapdaq","tapdaq_rewarded_show",1);
			__loadBanner = cpp.Lib.load("tapdaq","tapdaq_banner_load",1);
			__showBanner = cpp.Lib.load("tapdaq","tapdaq_banner_show",0);
	 		__hideBanner = cpp.Lib.load("tapdaq","tapdaq_banner_hide",0);
			__moveBanner = cpp.Lib.load("tapdaq","tapdaq_banner_move",1);
			__openMediationDebugger = cpp.Lib.load("tapdaq","tapdaq_mediation_debugger",0);
			
			__bannerIsReady = cpp.Lib.load("tapdaq","tapdaq_banner_isready",0);
			__interstitialIsReady = cpp.Lib.load("tapdaq","tapdaq_interstitial_isready",1);
			__videoIsReady = cpp.Lib.load("tapdaq","tapdaq_video_isready",1);
			__rewardedIsReady = cpp.Lib.load("tapdaq","tapdaq_rewarded_isready",1);

			__init(appId, clientKey, testmode,tagsString);
			__tapdaq_set_event_handle(notifyListeners);
		}catch(e:Dynamic){
			trace("iOS INIT Exception: "+e);
		}
		#end
		
		#if android
		if(initialized) return;
		initialized = true;
		try{
			// JNI METHOD LINKING
			__loadInterstitial = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "loadInterstitial", "(Ljava/lang/String;)V");
			__showInterstitial = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "showInterstitial", "(Ljava/lang/String;)V");
			__loadVideo = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "loadVideo", "(Ljava/lang/String;)V");
			__showVideo = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "showVideo", "(Ljava/lang/String;)V");
			__loadRewarded = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "loadRewarded", "(Ljava/lang/String;)V");
			__showRewarded = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "showRewarded", "(Ljava/lang/String;)V");
			__loadBanner = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "loadBanner", "(Ljava/lang/String;)V");
			__showBanner = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "showBanner", "()V");
	 		__hideBanner = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "hideBanner", "()V");
			__moveBanner = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "setBannerPosition", "(Ljava/lang/String;)V");	
			__openMediationDebugger = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "openDebugger", "()V");
			
			__interstitialIsReady = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "isInterstitialReady", "(Ljava/lang/String;)Z");
			__videoIsReady = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "isVideoReady", "(Ljava/lang/String;)Z");
			__rewardedIsReady = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "isRewardedReady", "(Ljava/lang/String;)Z");
		
			if(__init == null)
			{
				__init = openfl.utils.JNI.createStaticMethod("com/byrobin/tapdaq/TapdaqEx", "init", "(Lorg/haxe/lime/HaxeObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", true);
			}
	
			var args = new Array<Dynamic>();
			args.push(new Tapdaq());
			args.push(appId);
			args.push(clientKey);
			args.push(testmode);
			args.push(tagsString);
			__init(args);
		}catch(e:Dynamic){
			trace("Android INIT Exception: "+e);
		}
		#end
	}
	
	public static function openMediationDebugger() {
		try{
			__openMediationDebugger();
		} catch(e:Dynamic) {
			trace("Debugger Exception: "+e);
		}
		
	}
	
	public static function loadBanner(type:String) {
		try{
			__loadBanner(type);
		} catch(e:Dynamic) {
			trace("Loadbanner Exception: "+e);
		}
		
	}
	public static function showBanner() {
		try{
			__showBanner();
		} catch(e:Dynamic) {
			trace("ShowBanner Exception: "+e);
		}
	}
	public static function hideBanner() {
		try{
			__hideBanner();
		} catch(e:Dynamic) {
			trace("HideBanner Exception: "+e);
		}
	}
	public static function moveBanner(gravity:String) {
		try{
			__moveBanner(gravity);
		} catch(e:Dynamic) {
			trace("MoveBanner Exception: "+e);
		}
	}
	
	public static function loadInterstitial(tag:String) {
		try{
			__loadInterstitial(tag);
		} catch(e:Dynamic) {
			trace("LoadInterstitial Exception: "+e);
		}
	}
	public static function showInterstitial(tag:String) {
		try{
			__showInterstitial(tag);
		} catch(e:Dynamic) {
			trace("ShowInterstitial Exception: "+e);
		}
	}
	
	public static function loadVideo(tag:String) {
		try{
			__loadVideo(tag);
		} catch(e:Dynamic) {
			trace("LoadVideo Exception: "+e);
		}
	}
	public static function showVideo(tag:String) {
		try{
			__showVideo(tag);
		} catch(e:Dynamic) {
			trace("ShowVideo Exception: "+e);
		}
	}
	
	public static function loadRewarded(tag:String) {
		try{
			__loadRewarded(tag);
		} catch(e:Dynamic) {
			trace("LoadRewarded Exception: "+e);
		}
	}
	public static function showRewarded(tag:String) {
		try{
			__showRewarded(tag);
		} catch(e:Dynamic) {
			trace("ShowRewarded Exception: "+e);
		}
	}
	
	///////Are ads ready///////////////
	public static function bannerIsReady():Bool{
		return __bannerIsReady();
	}
	public static function interstitialIsReady(tag:String):Bool{
		return __interstitialIsReady(tag);
	}
	public static function videoIsReady(tag:String):Bool{
		return __videoIsReady(tag);
	}
	public static function rewardedIsReady(tag:String):Bool{
		return __rewardedIsReady(tag);
	}
	////////////Banner////////////////
	public static function bannerDidLoad():Bool{
		
		if(_bannerdidload){
			_bannerdidload = false;
			return true;
		}
		
		return false;
	}
	public static function bannerFailToLoad():Bool{
		
		if(_bannerfailtoload){
			_bannerfailtoload = false;
			return true;
		}
		
		return false;
	}
	public static function bannerDidClick():Bool{
		
		if(_bannerdidclick){
			_bannerdidclick = false;
			return true;
		}
		
		return false;
	}
	public static function bannerDidRefresh():Bool{
		
		if(_bannerdidrefresh){
			_bannerdidrefresh = false;
			return true;
		}
		
		return false;
	}
	////////////Interstitial////////////////
	public static function interstitialWillDisplay():Bool{
		
		if(_interstitialwilldisplay){
			_interstitialwilldisplay = false;
			return true;
		}
		
		return false;
	}
	public static function interstitialDidDisplay():Bool{
		
		if(_interstitialdiddisplay){
			_interstitialdiddisplay = false;
			return true;
		}
		
		return false;
	}
	public static function interstitialDidClose():Bool{
		
		if(_interstitialdidclose){
			_interstitialdidclose = false;
			return true;
		}
		
		return false;
	}
	public static function interstitialDidClick():Bool{
		
		if(_interstitialdidclick){
			_interstitialdidclick = false;
			return true;
		}
		
		return false;
	}
	////////////Video////////////////
	public static function videoWillDisplay():Bool{
		
		if(_videowilldisplay){
			_videowilldisplay = false;
			return true;
		}
		
		return false;
	}
	public static function videoDidDisplay():Bool{
		
		if(_videodiddisplay){
			_videodiddisplay = false;
			return true;
		}
		
		return false;
	}
	public static function videoDidClose():Bool{
		
		if(_videodidclose){
			_videodidclose = false;
			return true;
		}
		
		return false;
	}
	public static function videoDidClick():Bool{
		
		if(_videodidclick){
			_videodidclick = false;
			return true;
		}
		
		return false;
	}
	////////////Rewarded////////////////
	public static function rewardedWillDisplay():Bool{
		
		if(_rewardedwilldisplay){
			_rewardedwilldisplay = false;
			return true;
		}
		
		return false;
	}
	public static function rewardedDidDisplay():Bool{
		
		if(_rewardeddiddisplay){
			_rewardeddiddisplay = false;
			return true;
		}
		
		return false;
	}
	public static function rewardedDidClose():Bool{
		
		if(_rewardeddidclose){
			_rewardeddidclose = false;
			return true;
		}
		
		return false;
	}
	public static function rewardedDidClick():Bool{
		
		if(_rewardeddidclick){
			_rewardeddidclick = false;
			return true;
		}
		
		return false;
	}
	public static function rewardedSucceeded():Bool{
		
		if(_rewardedsucceeded){
			_rewardedsucceeded = false;
			return true;
		}
		
		return false;
	}
	///////Events Callbacks/////////////
	
	#if ios
	//Ads Events only happen on iOS.
	private static function notifyListeners(inEvent:Dynamic)
	{
		var event:String = Std.string(Reflect.field(inEvent, "type"));
		
		if(event == "bannerdidload")
		{
			_bannerdidload = true;
		}
		if(event == "bannerfailtoload")
		{
			_bannerfailtoload = true;
		}
		if(event == "bannerdidclick")
		{
			_bannerdidclick = true;
		}
		if(event == "bannerdidrefresh")
		{
			_bannerdidrefresh = true;
		}
		if(event == "interstitialwilldisplay")
		{
			_interstitialwilldisplay = true;
		}
		if(event == "interstitialdiddisplay")
		{
			_interstitialdiddisplay = true;
		}
		if(event == "interstitialdidclose")
		{
			_interstitialdidclose = true;
		}
		if(event == "interstitialdidclick")
		{
			_interstitialdidclick = true;
		}
		if(event == "videowilldisplay")
		{
			_videowilldisplay = true;
		}
		if(event == "videodiddisplay")
		{
			_videodiddisplay = true;
		}
		if(event == "videodidclose")
		{
			_videodidclose = true;
		}
		if(event == "videodidclick")
		{
			_videodidclick = true;
		}
		if(event == "rewardedwilldisplay")
		{
			_rewardedwilldisplay = true;
		}
		if(event == "rewardeddiddisplay")
		{
			_rewardeddiddisplay = true;
		}
		if(event == "rewardeddidclose")
		{
			_rewardeddidclose = true;
		}
		if(event == "rewardeddidclick")
		{
			_rewardeddidclick = true;
		}
		if(event == "rewardedsucceeded")
		{
			_rewardedsucceeded = true;
		}
	}
	#end
	
	#if android
	
	public function onBannerDidLoad() 
	{
		_bannerdidload = true;
	}
	public function onBannerFailToLoad() 
	{
		_bannerfailtoload = true;
	}
	public function onBannerDidClick() 
	{
		_bannerdidclick = true;
	}
	public function onInterstitialWillDisplay() 
	{
		_interstitialwilldisplay = true;
	}
	public function onInterstitialDidDisplay() 
	{
		_interstitialdiddisplay = true;
	}
	public function onInterstitialDidClose() 
	{
		_interstitialdidclose = true;
	}
	public function onInterstitialDidClick()
	{
		_interstitialdidclick = true;
	}
	public function onVideoWillDisplay() 
	{
		_videowilldisplay = true;
	}
	public function onVideoDidDisplay() 
	{
		_videodiddisplay = true;
	}
	public function onVideoDidClose() 
	{
		_videodidclose = true;
	}
	public function onVideoDidClick()
	{
		_videodidclick = true;
	}
	public function onRewardedWillDisplay() 
	{
		_rewardedwilldisplay = true;
	}
	public function onRewardedDidDisplay() 
	{
		_rewardeddiddisplay = true;
	}
	public function onRewardedDidClose() 
	{
		_rewardeddidclose = true;
	}
	public function onRewardedDidClick()
	{
		_rewardeddidclick = true;
	}
	public function onRewardedSucceeded()
	{
		_rewardedsucceeded = true;
	}
	#end
	
	private static function getJSONString(tags:String):String
	{
		var _splitTags = tags.split(",");
		_tagsArray = _splitTags;
		var newString:String = "";
		var i = 1;
		for(item in cast(_tagsArray, Array<Dynamic>))
		{
			var newTags:String = "\""+item+"\"";
			if(i >= _tagsArray.length){
			    newString = newString + newTags;
			}else{
				newString = newString + newTags + ",";
			}
			i++;
			//trace("_tagsArray " + _tagsArray.length + " i: " +i); 
		}
		//trace("newString: " + newString); 
		
		return newString;
	}
	
}