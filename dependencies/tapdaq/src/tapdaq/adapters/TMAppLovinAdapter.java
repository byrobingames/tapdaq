package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.applovin.adview.AppLovinConfirmationActivity;
import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.adview.AppLovinInterstitialActivity;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdClickListener;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdService;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMServiceErrorHandler;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdapter;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import com.tapdaq.sdk.listeners.TMListenerHandler;
import com.tapdaq.sdk.listeners.TMRewardAdListenerBase;

import java.util.Map;

/**
 * Created by dominicroberts on 27/09/2016.
 */

public class TMAppLovinAdapter extends TMAdapter {

    private AppLovinSdk mSdkInstance;
    private AppLovinAd mAd, mVideoAd;
    private AppLovinIncentivizedInterstitial mIncentivizedInterstitial;

    private Map mRewardData;

    public TMAppLovinAdapter(Context context) {
        super(context);
    }

    private AppLovinSdk getSdk(Context context) {
        if (mSdkInstance == null && getAppKey(context) != null) {
            mSdkInstance = AppLovinSdk.getInstance(getAppKey(context), new AppLovinSdkSettings(), context);
        }
        return mSdkInstance;
    }

    @Override
    public String getName() { return TMMediationNetworks.APPLOVIN_NAME; }

    @Override
    public int getID() {
        return TMMediationNetworks.APPLOVIN;
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (activity != null && getSdk(activity) != null) {
            mServiceListener.onInitSuccess(activity, TMMediationNetworks.APPLOVIN);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return context != null && getAppKey(context) != null && getSdk(context) != null && isActivityAvailable(context, AppLovinInterstitialActivity.class) && isActivityAvailable(context, AppLovinConfirmationActivity.class);
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return isInitialised(context);
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context);
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context);
    }

    @Override
    public boolean isStaticInterstitialReady(Activity activity) {
        return mAd != null;
    }

    @Override
    public boolean isVideoInterstitialReady(Activity activity) {
        return mVideoAd != null;
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        return mIncentivizedInterstitial != null && mIncentivizedInterstitial.isAdReadyToDisplay();
    }

    @Override
    public void loadInterstitial(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (isInitialised(activity)) {
            AppLovinAdService adService = getSdk(activity).getAdService();
            adService.loadNextAd(AppLovinAdSize.INTERSTITIAL, new AdLoadListener(activity, shared_id, TMAdType.STATIC_INTERSTITIAL, placement, listener));
        } else {
            new TMStatsManager(activity).finishAdRequest(activity, shared_id, false);
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.STATIC_INTERSTITIAL, placement, new TMAdError(0, "Applovin not initialised"), listener);
        }
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (isInitialised(activity)) {
            AppLovinAdService adService = getSdk(activity).getAdService();
            adService.loadNextAd(AppLovinAdSize.INTERSTITIAL, new AdLoadListener(activity, shared_id, TMAdType.VIDEO_INTERSTITIAL, placement, listener));
        } else {
            new TMStatsManager(activity).finishAdRequest(activity, shared_id, false);
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.VIDEO_INTERSTITIAL, placement, new TMAdError(0, "Applovin not initialised"), listener);
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (isInitialised(activity)) {
            if (mIncentivizedInterstitial == null || !mIncentivizedInterstitial.isAdReadyToDisplay()) {
                mIncentivizedInterstitial = AppLovinIncentivizedInterstitial.create(getSdk(activity));
                mIncentivizedInterstitial.preload(new AdLoadListener(activity, shared_id, TMAdType.REWARD_INTERSTITIAL, placement, listener));
            } else {
                new TMStatsManager(activity).finishAdRequest(activity, shared_id, true);
                TMListenerHandler.DidLoad(listener);
            }
        } else {
            new TMStatsManager(activity).finishAdRequest(activity, shared_id, false);
            TMServiceErrorHandler.ServiceError(activity, shared_id, getName(), TMAdType.REWARD_INTERSTITIAL, placement, new TMAdError(0, "Applovin not initialised"), listener);
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (isStaticInterstitialReady(activity)) {
            AppLovinInterstitialAdDialog dialog = AppLovinInterstitialAd.create(getSdk(activity), activity);
            dialog.setAdDisplayListener(new AdDisplayListener(activity, getSharedId(Long.toString(mAd.getAdIdNumber())), TMAdType.STATIC_INTERSTITIAL, placement, listener));
            dialog.setAdClickListener(new AdClickListener(listener));
            dialog.showAndRender(mAd);
        } else {
            TLog.error("Applovin cannot display ad");
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isVideoInterstitialReady(activity)) {
            AppLovinInterstitialAdDialog dialog = AppLovinInterstitialAd.create(getSdk(activity), activity);
            dialog.setAdDisplayListener(new AdDisplayListener(activity, getSharedId(Long.toString(mVideoAd.getAdIdNumber())), TMAdType.VIDEO_INTERSTITIAL, placement, listener));
            dialog.setAdClickListener(new AdClickListener(listener));
            dialog.showAndRender(mVideoAd);
        } else {
            TLog.error("Applovin cannot display ad");
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (isRewardInterstitialReady(activity)) {
            if (mIncentivizedInterstitial.isAdReadyToDisplay()) {
                mIncentivizedInterstitial.show(activity, new AdRewardListener(listener), new AdPlaybackListener(listener), new AdDisplayListener(activity, getSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, placement)), TMAdType.REWARD_INTERSTITIAL, placement, listener), new AdClickListener(listener));
                mIncentivizedInterstitial = null;
            } else {
                TLog.error("Applovin cannot display incentivized ad");
            }
        }
    }

    private class AdLoadListener implements AppLovinAdLoadListener {
        private final TMAdListenerBase mListener;
        private Activity mActivity;
        private int mType;
        private String mPlacement;
        private String mShared_id;

        private AdLoadListener(Activity activity, String shared_id, int type, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mType = type;
            mPlacement = placement;
            mListener = listener;
            mShared_id = shared_id;
        }
        @Override
        public void adReceived(AppLovinAd appLovinAd) {
            TLog.debug("Applovin adReceived " + TMAdType.getString(mType));
            switch (mType) {
                case TMAdType.STATIC_INTERSTITIAL:
                    mAd = appLovinAd;
                    setSharedId(Long.toString(mAd.getAdIdNumber()), mShared_id);
                    break;
                case TMAdType.VIDEO_INTERSTITIAL:
                    mVideoAd = appLovinAd;
                    setSharedId(Long.toString(mVideoAd.getAdIdNumber()), mShared_id);
                    break;
                case TMAdType.REWARD_INTERSTITIAL:
                    setSharedId(getSharedKey(TMAdType.REWARD_INTERSTITIAL, mPlacement), mShared_id);
                    break;
                default:
                    break;
            }

            if (appLovinAd != null) {
                TMListenerHandler.DidLoad(mListener);
                if (mActivity != null) {
                    TMStatsManager statsManager = new TMStatsManager(mActivity);
                    statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                    statsManager.finishAdRequest(mActivity, mShared_id, true);

                }
            } else {
                if (mActivity != null) {
                    TMAdError error = new TMAdError(0, "No Fill");
                    TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mListener);
                    TMStatsManager statsManager = new TMStatsManager(mActivity);
                    statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                    statsManager.finishAdRequest(mActivity, mShared_id, false);
                }
            }
            mActivity = null;
        }

        @Override
        public void failedToReceiveAd(int i) {
            TLog.debug("failedToReceiveAd " + TMAdType.getString(mType));

            switch (mType) {
                case TMAdType.STATIC_INTERSTITIAL:
                    mAd = null;
                    break;
                case TMAdType.VIDEO_INTERSTITIAL:
                    mVideoAd = null;
                    break;
                case TMAdType.REWARD_INTERSTITIAL:
                    mIncentivizedInterstitial = null;
                    break;
                default:
                    break;
            }

            TMAdError error = new TMAdError(i, getError(i));

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mListener);
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_id, false);
            }
            mActivity = null;
        }
    }

    private  class AdPlaybackListener implements AppLovinAdVideoPlaybackListener {
        private TMRewardAdListenerBase mListener;

        AdPlaybackListener(TMRewardAdListenerBase listener) {
            mListener = listener;
        }

        @Override
        public void videoPlaybackBegan(AppLovinAd appLovinAd) {
            TLog.debug("Applovin videoPlaybackBegan");
        }

        @Override
        public void videoPlaybackEnded(AppLovinAd appLovinAd, double percentageViewed, boolean wasFullyWatched) {
            TLog.debug("Applovin videoPlaybackEnded");
            if (mRewardData != null) {
                if (wasFullyWatched) {
                    TMRewardAdListenerBase rewardListener = (TMRewardAdListenerBase) mListener;
                    try {
                        String currency = (String) mRewardData.get("currency");
                        double amount = Double.parseDouble((String) mRewardData.get("amount"));
                        TMListenerHandler.DidVerify(rewardListener, "", currency, amount);
                    } catch (Exception e) {
                        TLog.error(e);
                    }
                }
                mRewardData = null;
            }
        }
    }

    private class AdDisplayListener implements AppLovinAdDisplayListener {

        private final TMAdListenerBase mListener;
        private Activity mActivity;
        private int mType;
        private String mPlacement;
        private String mShared_id;

        private AdDisplayListener(Activity activity, String shared_id, int type, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mType = type;
            mPlacement = placement;
            mListener = listener;
            mShared_id = shared_id;
        }

        @Override
        public void adDisplayed(AppLovinAd appLovinAd) {
            TMListenerHandler.DidDisplay(mListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void adHidden(AppLovinAd appLovinAd) {
            TMListenerHandler.DidClose(mListener);

            switch (mType) {
                case TMAdType.STATIC_INTERSTITIAL:
                    mAd = null;
                    break;
                case TMAdType.VIDEO_INTERSTITIAL:
                    mVideoAd = null;
                    break;
                case TMAdType.REWARD_INTERSTITIAL:
                    mIncentivizedInterstitial = null;
                    break;
                default:
                    break;
            }

            reloadAd(mActivity, mType, mPlacement, mListener);
            mActivity = null;
        }
    }

    private class AdClickListener implements AppLovinAdClickListener {

        private final TMAdListenerBase mListener;

        private AdClickListener(TMAdListenerBase listener) {
            mListener = listener;
        }

        @Override
        public void adClicked(AppLovinAd appLovinAd) {
            TMListenerHandler.DidClick(mListener);
        }
    }

    private class AdRewardListener implements AppLovinAdRewardListener
    {
        private final TMRewardAdListenerBase mListener;

        private AdRewardListener(TMRewardAdListenerBase listener) {
            mListener = listener;
        }

        @Override
        public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
            mRewardData = map;
        }

        @Override
        public void userOverQuota(AppLovinAd appLovinAd, Map map) {

        }

        @Override
        public void userRewardRejected(AppLovinAd appLovinAd, Map map) {

        }

        @Override
        public void validationRequestFailed(AppLovinAd appLovinAd, int i) {
//            mServiceListener.didFail(new TMAdError(i, ""));
        }

        @Override
        public void userDeclinedToViewAd(AppLovinAd appLovinAd) {
            TMListenerHandler.OnUserDeclined(mListener);
        }
    }

    private String getError(int code) {
        switch (code) {
            case AppLovinErrorCodes.FETCH_AD_TIMEOUT:
                return "FETCH_AD_TIMEOUT";
            case AppLovinErrorCodes.INCENTIVIZED_NO_AD_PRELOADED:
                return "INCENTIVIZED_NO_AD_PRELOADED";
            case AppLovinErrorCodes.INCENTIVIZED_SERVER_TIMEOUT:
                return "INCENTIVIZED_SERVER_TIMEOUT";
            case AppLovinErrorCodes.INCENTIVIZED_UNKNOWN_SERVER_ERROR:
                return "INCENTIVIZED_UNKNOWN_SERVER_ERROR";
            case AppLovinErrorCodes.INCENTIVIZED_USER_CLOSED_VIDEO:
                return "INCENTIVIZED_USER_CLOSED_VIDEO";
            case AppLovinErrorCodes.INVALID_URL:
                return "INVALID_URL";
            case AppLovinErrorCodes.NO_FILL:
                return "NO_FILL";
            case AppLovinErrorCodes.NO_NETWORK:
                return "NO_NETWORK";
            case AppLovinErrorCodes.UNABLE_TO_PRECACHE_IMAGE_RESOURCES:
                return "UNABLE_TO_PRECACHE_IMAGE_RESOURCES";
            case AppLovinErrorCodes.UNABLE_TO_PRECACHE_RESOURCES:
                return "UNABLE_TO_PRECACHE_RESOURCES";
            case AppLovinErrorCodes.UNABLE_TO_PRECACHE_VIDEO_RESOURCES:
                return "UNABLE_TO_PRECACHE_VIDEO_RESOURCES";
            case AppLovinErrorCodes.UNABLE_TO_PREPARE_NATIVE_AD:
                return "UNABLE_TO_PREPARE_NATIVE_AD";
            case AppLovinErrorCodes.UNABLE_TO_RENDER_AD:
                return "UNABLE_TO_RENDER_AD";
            case AppLovinErrorCodes.UNSPECIFIED_ERROR:
                return "UNSPECIFIED_ERROR";
            default:
                return "Unknown Applovin Error";
        }
    }
}
