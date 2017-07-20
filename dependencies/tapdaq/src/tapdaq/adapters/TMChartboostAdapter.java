package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMServiceErrorHandler;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import com.tapdaq.sdk.listeners.TMListenerHandler;
import com.tapdaq.sdk.listeners.TMRewardAdListenerBase;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdapter;

/**
 * Created by dominicroberts on 22/09/2016.
 */
public class TMChartboostAdapter extends TMAdapter {

    public TMChartboostAdapter(Context context){
        super(context);
    }

    @Override
    public String getName() { return TMMediationNetworks.CHARTBOOST_NAME; }

    @Override
    public int getID() {
        return TMMediationNetworks.CHARTBOOST;
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (activity != null && mKeys != null) {
            Chartboost.startWithAppId(activity, getAppId(activity), getAppKey(activity));
            Chartboost.setActivityCallbacks(false);
            Chartboost.onCreate(activity);
            Chartboost.setAutoCacheAds(false);
            mServiceListener.onInitSuccess(activity, getID());
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return context != null && getAppId(context) != null && getAppKey(context) != null;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return (getAppId(context) != null && getAppKey(context) != null);
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return (getAppId(context) != null && getAppKey(context) != null);
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return (getAppId(context) != null && getAppKey(context) != null);
    }

    public boolean isStaticInterstitialReady(Activity activity) {
        onStart(activity);
        return Chartboost.hasInterstitial(CBLocation.LOCATION_MAIN_MENU);
    }

    @Override
    public boolean isVideoInterstitialReady(Activity activity) {
        onStart(activity);
        return Chartboost.hasInterstitial(CBLocation.LOCATION_SETTINGS);
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        onStart(activity);
        return Chartboost.hasRewardedVideo(CBLocation.LOCATION_GAMEOVER);
    }


    @Override
    public void loadInterstitial(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        onStart(activity);
        if(Chartboost.isWebViewEnabled()) {
            if (Chartboost.hasInterstitial(CBLocation.LOCATION_MAIN_MENU)) {
                TMListenerHandler.DidLoad(listener);
            } else {
                Chartboost.setDelegate(new ChartboostListener(activity, TMAdType.STATIC_INTERSTITIAL, placement, shared_id, listener));
                Chartboost.cacheInterstitial(CBLocation.LOCATION_MAIN_MENU);
            }
        } else {
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.STATIC_INTERSTITIAL, placement, new TMAdError(0, "CB Webview unavailable"), listener);
        }
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        onStart(activity);
        if (Chartboost.isWebViewEnabled()) {
            if (Chartboost.hasInterstitial(CBLocation.LOCATION_SETTINGS)) {
                TMListenerHandler.DidLoad(listener);
            } else {
                Chartboost.setDelegate(new ChartboostListener(activity, TMAdType.VIDEO_INTERSTITIAL, placement, shared_id, listener));
                Chartboost.cacheInterstitial(CBLocation.LOCATION_SETTINGS);
            }
        } else {
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.VIDEO_INTERSTITIAL, placement, new TMAdError(0, "CB Webview unavailable"), listener);
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        onStart(activity);
        if (Chartboost.isWebViewEnabled()) {
            if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_GAMEOVER)) {
                TMListenerHandler.DidLoad(listener);
            } else {
                Chartboost.setDelegate(new ChartboostListener(activity, TMAdType.REWARD_INTERSTITIAL, placement, shared_id, listener));
                Chartboost.cacheRewardedVideo(CBLocation.LOCATION_GAMEOVER);
            }
        } else {
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.REWARD_INTERSTITIAL, placement, new TMAdError(0, "CB Webview unavailable"), listener);
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        onStart(activity);
        if (Chartboost.hasInterstitial(CBLocation.LOCATION_MAIN_MENU)) {
            Chartboost.setDelegate(new ChartboostListener(activity, TMAdType.STATIC_INTERSTITIAL, placement, getSharedId(CBLocation.LOCATION_MAIN_MENU), listener));
            Chartboost.showInterstitial(CBLocation.LOCATION_MAIN_MENU);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        onStart(activity);
        if (Chartboost.hasInterstitial(CBLocation.LOCATION_SETTINGS)) {
            Chartboost.setDelegate(new ChartboostListener(activity, TMAdType.VIDEO_INTERSTITIAL, placement, getSharedId(CBLocation.LOCATION_SETTINGS), listener));
            Chartboost.showInterstitial(CBLocation.LOCATION_SETTINGS);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        onStart(activity);
        if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_GAMEOVER)) {
            Chartboost.setDelegate(new ChartboostListener(activity, TMAdType.REWARD_INTERSTITIAL, placement, getSharedId(CBLocation.LOCATION_GAMEOVER), listener));
            Chartboost.showRewardedVideo(CBLocation.LOCATION_GAMEOVER);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }
    }

    private TMAdError buildError(CBError.CBImpressionError error) {
        String message = "";
        switch (error) {
            case INTERNAL:
                message = "INTERNAL";
                break;
            case INTERNET_UNAVAILABLE:
                message = "INTERNET_UNAVAILABLE";
                break;
            case TOO_MANY_CONNECTIONS:
                message = "TOO_MANY_CONNECTIONS";
                break;
            case WRONG_ORIENTATION:
                message = "WRONG_ORIENTATION";
                break;
            case FIRST_SESSION_INTERSTITIALS_DISABLED:
                message = "FIRST_SESSION_INTERSTITIALS_DISABLED";
                break;
            case NETWORK_FAILURE:
                message = "NETWORK_FAILURE";
                break;
            case NO_AD_FOUND:
                message = "NO_AD_FOUND";
                break;
            case SESSION_NOT_STARTED:
                message = "SESSION_NOT_STARTED";
                break;
            case IMPRESSION_ALREADY_VISIBLE:
                message = "IMPRESSION_ALREADY_VISIBLE";
                break;
            case NO_HOST_ACTIVITY:
                message = "NO_HOST_ACTIVITY";
                break;
            case USER_CANCELLATION:
                message = "USER_CANCELLATION";
                break;
            case INVALID_LOCATION:
                message = "INVALID_LOCATION";
                break;
            case VIDEO_UNAVAILABLE:
                message = "VIDEO_UNAVAILABLE";
                break;
            case VIDEO_ID_MISSING:
                message = "VIDEO_ID_MISSING";
                break;
            case ERROR_PLAYING_VIDEO:
                message = "ERROR_PLAYING_VIDEO";
                break;
            case INVALID_RESPONSE:
                message = "INVALID_RESPONSE";
                break;
            case ASSETS_DOWNLOAD_FAILURE:
                message = "ASSETS_DOWNLOAD_FAILURE";
                break;
            case ERROR_CREATING_VIEW:
                message = "ERROR_CREATING_VIEW";
                break;
            case ERROR_DISPLAYING_VIEW:
                message = "ERROR_DISPLAYING_VIEW";
                break;
            case INCOMPATIBLE_API_VERSION:
                message = "INCOMPATIBLE_API_VERSION";
                break;
            case ERROR_LOADING_WEB_VIEW:
                message = "ERROR_LOADING_WEB_VIEW";
                break;
            case ASSET_PREFETCH_IN_PROGRESS:
                message = "ASSET_PREFETCH_IN_PROGRESS";
                break;
            case EMPTY_LOCAL_AD_LIST:
                message = "EMPTY_LOCAL_AD_LIST";
                break;
            case ACTIVITY_MISSING_IN_MANIFEST:
                message = "ACTIVITY_MISSING_IN_MANIFEST";
                break;
            case EMPTY_LOCAL_VIDEO_LIST:
                message = "EMPTY_LOCAL_VIDEO_LIST";
                break;
            case END_POINT_DISABLED:
                message = "END_POINT_DISABLED";
                break;
            case HARDWARE_ACCELERATION_DISABLED:
                message = "HARDWARE_ACCELERATION_DISABLED";
                break;
            case PENDING_IMPRESSION_ERROR:
                message = "PENDING_IMPRESSION_ERROR";
                break;
            default:
                break;

        }
        return new TMAdError(0, message);
    }

    private class ChartboostListener extends ChartboostDelegate {
        private TMAdListenerBase mListener;
        private Activity mActivity;
        private boolean mDidDisplay = false;
        private int mType;
        private String mPlacement;
        private String mSharedId;

        ChartboostListener(Activity activity, int type, String placement, String shared_id, TMAdListenerBase listenerBase) {
            mActivity = activity;
            mType = type;
            mPlacement = placement;
            mSharedId = shared_id;
            mListener = listenerBase;
        }

        @Override
        public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
            super.didFailToLoadInterstitial(location, error);
            TMAdError tapdaqError =  buildError(error);

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mSharedId, getName(), mType, mPlacement, tapdaqError, mListener);
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), "No Fill");
                statsManager.finishAdRequest(mActivity, mSharedId, false);
            }
            onStop(mActivity);
            mActivity = null;
        }

        @Override
        public void didDismissInterstitial(String location) {
            super.didDismissInterstitial(location);
        }

        @Override
        public void didCacheInterstitial(String location) {
            super.didCacheInterstitial(location);
            TMListenerHandler.DidLoad(mListener);
            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mSharedId, true);
            }
            setSharedId(location, mSharedId);
        }

        @Override
        public void didCloseInterstitial(String location) {
            super.didCloseInterstitial(location);
            TMListenerHandler.DidClose(mListener);
            reloadAd(mActivity, mType, mPlacement, mListener);
            onStop(mActivity);
            mActivity = null;
        }

        @Override
        public void didClickInterstitial(String location) {
            super.didClickInterstitial(location);
            TMListenerHandler.DidClick(mListener);
        }

        @Override
        public void didDisplayInterstitial(String location) {
            super.didDisplayInterstitial(location);
            TMListenerHandler.DidDisplay(mListener);

            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mSharedId, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));

        }

        @Override
        public boolean shouldDisplayRewardedVideo(String location) {
            return super.shouldDisplayRewardedVideo(location);
        }

        @Override
        public void didCacheRewardedVideo(String location) {
            super.didCacheRewardedVideo(location);
            TMListenerHandler.DidLoad(mListener);

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mSharedId, true);
            }
            setSharedId(location, mSharedId);
        }

        @Override
        public void didFailToLoadRewardedVideo(String location, CBError.CBImpressionError error) {
            super.didFailToLoadRewardedVideo(location, error);
            TMAdError tapdaqError =  buildError(error);

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mSharedId, getName(), mType, mPlacement, tapdaqError, mListener);
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), "No Fill");
                statsManager.finishAdRequest(mActivity, mSharedId, false);
            }
            onStop(mActivity);
            mActivity = null;
        }

        @Override
        public void didDismissRewardedVideo(String location) {
            super.didDismissRewardedVideo(location);

            if (!mDidDisplay && mListener instanceof TMRewardAdListenerBase) {
                TMListenerHandler.OnUserDeclined((TMRewardAdListenerBase) mListener);
            }
        }

        @Override
        public void didCloseRewardedVideo(String location) {
            super.didCloseRewardedVideo(location);
            TMListenerHandler.DidClose(mListener);
            reloadAd(mActivity, mType, mPlacement, mListener);
            onStop(mActivity);
            mActivity = null;
        }

        @Override
        public void didClickRewardedVideo(String location) {
            super.didClickRewardedVideo(location);
            TMListenerHandler.DidClick(mListener);
        }

        @Override
        public void didCompleteRewardedVideo(String location, int reward) {
            super.didCompleteRewardedVideo(location, reward);
            if (mListener != null && mListener instanceof TMRewardAdListenerBase)
                TMListenerHandler.DidVerify((TMRewardAdListenerBase) mListener, "", "Reward", (double)reward);
        }

        @Override
        public void didDisplayRewardedVideo(String location) {
            super.didDisplayRewardedVideo(location);
            TMListenerHandler.DidDisplay(mListener);
            mDidDisplay = true;

            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mSharedId, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));

        }

        @Override
        public void willDisplayVideo(String location) {
            super.willDisplayVideo(location);
        }
    }

    private void onStart(Activity activity) {
        Chartboost.onStart(activity);
        Chartboost.onResume(activity);
    }

    private void onStop(Activity activity) {
        Chartboost.onPause(activity);
        Chartboost.onStop(activity);
        Chartboost.onDestroy(activity);
    }
}
