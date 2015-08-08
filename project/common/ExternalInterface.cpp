#ifndef STATIC_LINK
#define IMPLEMENT_API
#endif

#if defined(HX_WINDOWS) || defined(HX_MACOS) || defined(HX_LINUX)
#define NEKO_COMPATIBLE
#endif


#include <hx/CFFI.h>
#include "TapdaqEx.h"

using namespace tapdaq;


void tapdaq_init(value app_id, value client_key, value testmode){
	init(val_string(app_id), val_string(client_key), val_string(testmode));

}
DEFINE_PRIM(tapdaq_init, 3);


void tapdaq_interstitial_show(){
	showInterstitial();

}
DEFINE_PRIM(tapdaq_interstitial_show, 0);


//callbacks

static value tapdaq_interstitial_loaded()
{
    if (tapdaq::interstitialLoaded())
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_interstitial_loaded, 0);

static value tapdaq_interstitial_failed()
{
    if (tapdaq::interstitialFailToLoad())
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_interstitial_failed, 0);

static value tapdaq_interstitial_closed()
{
    if (tapdaq::interstitialClosed())
        return val_true;
    return val_false;
}
DEFINE_PRIM(tapdaq_interstitial_closed, 0);


extern "C" void tapdaq_main()
{
}
DEFINE_ENTRY_POINT(tapdaq_main);

extern "C" int tapdaq_register_prims()
{
    return 0;
}

