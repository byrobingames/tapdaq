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
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdapter;
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

        if (mCurrentActivity != null && mKeys != null) {
            List<String> zones = new ArrayList<String>();

            if (getVideoId(mCurrentActivity) != null)
                zones.add(getVideoId(mCurrentActivity));
            if (getRewardedVideoId(mCurrentActivity) != null)
                zones.add(getRewardedVideoId(mCurrentActivity));

            AdColony.configure(mCurrentActivity, getAppId(mCurrentActivity), zones.toArray(new String[zones.size()]));
            mListener.onInitSuccess(activity, TMMediationNetworks.ADCOLONY);
        }
    }

    @Override
    public boolean isInitialised(Context context) {
        return mCurrentActivity != null
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

    @Override
    public void loadVideo(Activity activity, String placement, final TMAdListenerBase listener) {
        AdColony.requestInterstitial(getVideoId(activity), new AdColonyListener(false, listener));
    }

    @Override
    public void loadRewardedVideo(Activity activity, String placement, TMAdListenerBase listener) {
        AdColonyAdOptions options = new AdColonyAdOptions()
                .enableConfirmationDialog(false)
                .enableResultsDialog(false);

        AdColony.requestInterstitial(getRewardedVideoId(activity), new AdColonyListener(true, listener), options);
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (mAd != null) {
            mAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (mRewardedAd != null) {
            AdColony.setRewardListener(new TMAdColonyRewardListener(listener));
            mRewardedAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "No ad available"));
        }

    }

    private class AdColonyListener extends AdColonyInterstitialListener {

        private TMAdListenerBase mAdListener;
        private boolean mRewarded;

        AdColonyListener(boolean rewarded, TMAdListenerBase listener) {
            mRewarded = rewarded;
            mAdListener = listener;
        }

        @Override
        public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
            if (mRewarded)
                mRewardedAd = adColonyInterstitial;
            else
                mAd = adColonyInterstitial;

            TMListenerHandler.DidLoad(mAdListener);
        }

        @Override
        public void onRequestNotFilled(AdColonyZone zone) {
            super.onRequestNotFilled(zone);

            TMListenerHandler.DidFailToLoad(mAdListener, new TMAdError(0, "AdColony no ad available"));

        }

        @Override
        public void onOpened(AdColonyInterstitial ad) {
            super.onOpened(ad);
            TMListenerHandler.DidDisplay(mAdListener);
        }

        @Override
        public void onClosed(AdColonyInterstitial ad) {
            super.onClosed(ad);
            TMListenerHandler.DidClose(mAdListener);
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
