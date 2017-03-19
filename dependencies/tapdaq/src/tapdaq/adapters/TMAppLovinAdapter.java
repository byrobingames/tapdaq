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
import com.applovin.sdk.AppLovinErrorCodes;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdType;
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
    private AppLovinIncentivizedInterstitial mIncentivizedInterstitial;

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
            mListener.onInitSuccess(activity, TMMediationNetworks.APPLOVIN);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return context != null && getAppKey(context) != null && getSdk(context) != null && isActivityAvailable(context, AppLovinInterstitialActivity.class) && isActivityAvailable(context, AppLovinConfirmationActivity.class);
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
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isInitialised(activity)) {
            AppLovinAdService adService = getSdk(activity).getAdService();
            adService.loadNextAd(AppLovinAdSize.INTERSTITIAL, new AdLoadListener(activity, TMAdType.VIDEO_INTERSTITIAL, placement, listener));
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Applovin not initialised"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isInitialised(activity)) {
            if (mIncentivizedInterstitial == null || !mIncentivizedInterstitial.isAdReadyToDisplay()) {
                mIncentivizedInterstitial = AppLovinIncentivizedInterstitial.create(getSdk(activity));
                mIncentivizedInterstitial.preload(new AdLoadListener(activity, TMAdType.REWARD_INTERSTITIAL, placement, listener));
            } else {
                TMListenerHandler.DidLoad(listener);
            }
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Applovin not initialised"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isInitialised(activity)) {
            if (getSdk(activity).getAdService().hasPreloadedAd(AppLovinAdSize.INTERSTITIAL)) {
                AppLovinInterstitialAdDialog ad = AppLovinInterstitialAd.create(getSdk(activity), activity);
                ad.setAdDisplayListener(new AdDisplayListener(activity, TMAdType.REWARD_INTERSTITIAL, placement, listener));
                ad.setAdClickListener(new AdClickListener(listener));

                ad.show();
            } else {
                TLog.error("Applovin cannot display interstitial ad");
            }
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (isInitialised(activity)) {
            if (mIncentivizedInterstitial.isAdReadyToDisplay()) {
                mIncentivizedInterstitial.show(activity, new AdRewardListener(listener), null, new AdDisplayListener(activity, TMAdType.REWARD_INTERSTITIAL, placement, listener), new AdClickListener(listener));
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

        private AdLoadListener(Activity activity, int type, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mType = type;
            mPlacement = placement;
            mListener = listener;
        }
        @Override
        public void adReceived(AppLovinAd appLovinAd) {
            TMListenerHandler.DidLoad(mListener);

            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), TMAdType.getString(mType), null, getVersionID(mActivity));
            mActivity = null;
        }

        @Override
        public void failedToReceiveAd(int i) {
            TMAdError error = new TMAdError(i, getError(i));
            TMListenerHandler.DidFailToLoad(mListener, error);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
            mActivity = null;
        }
    }

    private class AdDisplayListener implements AppLovinAdDisplayListener {

        private final TMAdListenerBase mListener;
        private Activity mActivity;
        private int mType;
        private String mPlacement;

        private AdDisplayListener(Activity activity, int type, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mType = type;
            mPlacement = placement;
            mListener = listener;
        }

        @Override
        public void adDisplayed(AppLovinAd appLovinAd) {
            TMListenerHandler.DidDisplay(mListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, getName(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
            mActivity = null;
        }

        @Override
        public void adHidden(AppLovinAd appLovinAd) {
            TMListenerHandler.DidClose(mListener);
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
            try {
                String currency = (String) map.get("currency");
                double amount = Double.parseDouble((String) map.get("amount"));
                TMListenerHandler.DidVerify(mListener, "", currency, amount);
            } catch (Exception e) {
                TLog.error(e);
                TMListenerHandler.DidVerify(mListener, "", "", 0);
            }
        }

        @Override
        public void userOverQuota(AppLovinAd appLovinAd, Map map) {

        }

        @Override
        public void userRewardRejected(AppLovinAd appLovinAd, Map map) {

        }

        @Override
        public void validationRequestFailed(AppLovinAd appLovinAd, int i) {
//            mListener.didFail(new TMAdError(i, ""));
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
