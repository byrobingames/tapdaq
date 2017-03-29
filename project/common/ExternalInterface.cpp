#ifndef STATIC_LINK
#define IMPLEMENT_API
#endif

#if defined(HX_WINDOWS) || defined(HX_MACOS) || defined(HX_LINUX)
#define NEKO_COMPATIBLE
#endif


#include <hx/CFFI.h>
#include "TapdaqEx.h"
#include <stdio.h>

using namespace tapdaq;

AutoGCRoot* tapdaqEventHandle = 0;

static void tapdaq_set_event_handle(value onEvent)
{
    tapdaqEventHandle = new AutoGCRoot(onEvent);
}
DEFINE_PRIM(tapdaq_set_event_handle, 1);

void tapdaq_init(value app_id, value client_key, value testmode, value tagsJSON){
	init(val_string(app_id), val_string(client_key), val_string(testmode), val_string(tagsJSON));
}
DEFINE_PRIM(tapdaq_init, 4);

void tapdaq_banner_load(value bannerType){
    loadBanner(val_string(bannerType));
}
DEFINE_PRIM(tapdaq_banner_load, 1);

void tapdaq_banner_show(){
    showBanner();
}
DEFINE_PRIM(tapdaq_banner_show, 0);

void tapdaq_banner_hide(){
    hideBanner();
}
DEFINE_PRIM(tapdaq_banner_hide, 0);

void tapdaq_banner_move(value gravity){
    moveBanner(val_string(gravity));
}
DEFINE_PRIM(tapdaq_banner_move, 1);

///interstitial
void tapdaq_interstitial_load(value tag){
    loadInterstitial(val_string(tag));
}
DEFINE_PRIM(tapdaq_interstitial_load, 1);

void tapdaq_interstitial_show(value tag){
	showInterstitial(val_string(tag));
}
DEFINE_PRIM(tapdaq_interstitial_show, 1);

///video
void tapdaq_video_load(value tag){
    loadVideo(val_string(tag));
}
DEFINE_PRIM(tapdaq_video_load, 1);

void tapdaq_video_show(value tag){
    showVideo(val_string(tag));
}
DEFINE_PRIM(tapdaq_video_show, 1);

//rewarded
void tapdaq_rewarded_load(value tag){
    loadRewardedVideo(val_string(tag));
}
DEFINE_PRIM(tapdaq_rewarded_load, 1);

void tapdaq_rewarded_show(value tag){
    showRewardedVideo(val_string(tag));
}
DEFINE_PRIM(tapdaq_rewarded_show, 1);

void tapdaq_mediation_debugger(){
    openMediationDebugger();
}
DEFINE_PRIM(tapdaq_mediation_debugger, 0);

//callbacks

static value tapdaq_banner_isready()
{
    if (tapdaq::bannerIsReady())
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_banner_isready, 0);

static value tapdaq_interstitial_isready(value tag)
{
    if (tapdaq::interstitialIsReady(val_string(tag)))
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_interstitial_isready, 1);

static value tapdaq_video_isready(value tag)
{
    if (tapdaq::videoIsReady(val_string(tag)))
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_video_isready, 1);

static value tapdaq_rewarded_isready(value tag)
{
    if (tapdaq::rewardedIsReady(val_string(tag)))
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_rewarded_isready, 1);

extern "C" void tapdaq_main()
{
    val_int(0); // Fix Neko init
}
DEFINE_ENTRY_POINT(tapdaq_main);

extern "C" int tapdaq_register_prims()
{
    return 0;
}

extern "C" void sendTapdaqEvent(const char* type)
{
    printf("Send Event: %s\n", type);
    value o = alloc_empty_object();
    alloc_field(o,val_id("type"),alloc_string(type));
    val_call1(tapdaqEventHandle->get(), o);
}

