package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdOptions;
import com.adcolony.sdk.AdColonyAdViewActivity;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialActivity;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.adcolony.sdk.AdColonyZone;

import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMAdapter;
import com.tapdaq.sdk.common.TMServiceErrorHandler;
import com.tapdaq.sdk.listeners.TMAdListenerBase;
import com.tapdaq.sdk.listeners.TMListenerHandler;
import com.tapdaq.sdk.listeners.TMRewardAdListenerBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominicroberts on 28/09/2016.
 */

public class TMAdColonyAdapter extends TMAdapter {

    private AdColonyInterstitial mAd, mRewardedAd;

    public TMAdColonyAdapter(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return TMMediationNetworks.ADCOLONY_NAME;
    }

    @Override
    public int getID() {
        return TMMediationNetworks.ADCOLONY;
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (activity != null && mKeys != null) {
            List<String> zones = new ArrayList<String>();

            if (getVideoId(activity) != null)
                zones.add(getVideoId(activity));
            if (getRewardedVideoId(activity) != null)
                zones.add(getRewardedVideoId(activity));

            AdColony.configure(activity, getAppId(activity), zones.toArray(new String[zones.size()]));
            mServiceListener.onInitSuccess(activity, TMMediationNetworks.ADCOLONY);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return context != null
                && isActivityAvailable(context, AdColonyInterstitialActivity.class)
                && isActivityAvailable(context, AdColonyAdViewActivity.class)
                && getAppId(context) != null;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return isInitialised(context) && getVideoId(context) != null;
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return isInitialised(context) && getRewardedVideoId(context) != null;
    }

    public boolean isVideoInterstitialReady(Activity activity) {
        return mAd != null;
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        return mRewardedAd != null;
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, final TMAdListenerBase listener) {
        AdColony.requestInterstitial(getVideoId(activity), new AdColonyListener(activity, shared_id, TMAdType.VIDEO_INTERSTITIAL, placement,listener));
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        AdColonyAdOptions options = new AdColonyAdOptions()
                .enableConfirmationDialog(false)
                .enableResultsDialog(false);

        AdColony.requestInterstitial(getRewardedVideoId(activity), new AdColonyListener(activity, shared_id, TMAdType.REWARD_INTERSTITIAL, placement, listener), options);
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isVideoInterstitialReady(activity)) {
            mAd.setListener(new AdColonyListener(activity, getSharedId(mAd.getZoneID()), TMAdType.VIDEO_INTERSTITIAL, placement, listener));
            mAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (isRewardInterstitialReady(activity)) {
            mRewardedAd.setListener(new AdColonyListener(activity, getSharedId(mRewardedAd.getZoneID()), TMAdType.REWARD_INTERSTITIAL, placement, listener));
            AdColony.setRewardListener(new TMAdColonyRewardListener(listener));
            mRewardedAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }
    }

    private class AdColonyListener extends AdColonyInterstitialListener {

        private TMAdListenerBase mAdListener;
        private int mType;
        private String mPlacement;
        private Activity mActivity;
        private String mShared_id;

        AdColonyListener(Activity activity, String shared_id, int type, String tag, TMAdListenerBase listener) {
            mActivity = activity;
            mType = type;
            mAdListener = listener;
            mPlacement = tag;
            mShared_id = shared_id;
        }

        @Override
        public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
            if (mType == TMAdType.REWARD_INTERSTITIAL) {
                mRewardedAd = adColonyInterstitial;
                setSharedId(mRewardedAd.getZoneID(), mShared_id);
            } else {
                mAd = adColonyInterstitial;
                setSharedId(mAd.getZoneID(), mShared_id);
            }

            TMListenerHandler.DidLoad(mAdListener);
            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mShared_id, true);

            }
            mActivity = null;
        }

        @Override
        public void onRequestNotFilled(AdColonyZone zone) {
            super.onRequestNotFilled(zone);

            TMAdError error = new TMAdError(0, "No Fill");

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mAdListener);
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), "No Fill");
                statsManager.finishAdRequest(mActivity, mShared_id, false);
            }
            mActivity = null;
        }

        @Override
        public void onOpened(AdColonyInterstitial ad) {
            super.onOpened(ad);
            TMListenerHandler.DidDisplay(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
            mActivity = null;
        }

        @Override
        public void onClosed(AdColonyInterstitial ad) {
            super.onClosed(ad);
            TMListenerHandler.DidClose(mAdListener);
            reloadAd(mActivity, mType, mPlacement, mAdListener);
            mActivity = null;

        }

        @Override
        public void onIAPEvent(AdColonyInterstitial ad, String product_id, int engagement_type) {
            super.onIAPEvent(ad, product_id, engagement_type);
        }

        @Override
        public void onExpiring(AdColonyInterstitial ad) {
            super.onExpiring(ad);
        }

        @Override
        public void onLeftApplication(AdColonyInterstitial ad) {
            super.onLeftApplication(ad);
        }

        @Override
        public void onClicked(AdColonyInterstitial ad) {
            super.onClicked(ad);
            TMListenerHandler.DidClick(mAdListener);
        }
    }

    private class TMAdColonyRewardListener implements AdColonyRewardListener {

        private TMRewardAdListenerBase mListener;

        TMAdColonyRewardListener(TMRewardAdListenerBase listener) {
            mListener = listener;
        }

        @Override
        public void onReward(AdColonyReward adColonyReward) {
            TMListenerHandler.DidVerify(mListener, "", adColonyReward.getRewardName(), (double)adColonyReward.getRewardAmount());
        }
    }
}
