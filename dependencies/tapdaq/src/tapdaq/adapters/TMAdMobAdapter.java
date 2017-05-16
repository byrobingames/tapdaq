package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.android.gms.ads.*;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.tapdaq.sdk.adnetworks.TMServiceQueue;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.*;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.listeners.*;
import com.tapdaq.sdk.model.TMAdSize;
import com.tapdaq.sdk.storage.Storage;

import java.util.List;
import java.util.Locale;


/**
 * Created by dominicroberts on 01/09/2016.
 */
public class TMAdMobAdapter extends TMAdapter {

    private final static String ERROR_CODE_INTERNAL_ERROR = "Something happened internally; for instance, an invalid response was received from the ad server.";//CODE 0
    private final static String ERROR_CODE_INVALID_REQUEST = "The ad request was invalid; for instance, the ad unit ID was incorrect.";                         //CODE 1
    private final static String ERROR_CODE_NETWORK_ERROR = "The ad request was unsuccessful due to network connectivity.";                                      //CODE 2
    private final static String ERROR_CODE_NO_FILL = "The ad request was successful, but no ad was returned due to lack of ad inventory.";                      //CODE 3
    private final static String ERROR_UNKNOWN = "UNKNOWN ERROR";

    private TMAdMobBannerSizes mBannerSizes = new TMAdMobBannerSizes();

    private InterstitialAd mInterstitialAd;
    private InterstitialAd mVideoInterstitialAd;
    private RewardedVideoAd mRewardVideoAd;

    private boolean mStaticReady, mVideoReady, mRewardVideoReady;

    public TMAdMobAdapter(Context context){
        super(context);
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (activity != null && mKeys != null) {
            MobileAds.initialize(activity);
            mListener.onInitSuccess(activity, TMMediationNetworks.AD_MOB);
        }
    }

    public TMAdMobAdapter setTestDevices(Context context, List<String> devices) {
        Storage storage = new Storage(context);
        String devicesStr = TextUtils.join(", ", devices);
        storage.putString(String.format(Locale.ENGLISH, "%s_TEST_DEVICES", getName()),devicesStr);
        return this;
    }

    @Override
    public boolean isInitialised(Context context) {
        return context != null && (getBannerId(context) != null || getInterstitialId(context) != null || getVideoId(context) != null) ;
    }

    @Override
    public String getName() { return TMMediationNetworks.AD_MOB_NAME; }

    @Override
    public int getID() {
        return TMMediationNetworks.AD_MOB;
    }

    @Override
    public boolean isBannerAvailable(Context context, TMAdSize size) {
        return getBannerId(context) != null && mBannerSizes.getSize(size) != null;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return getInterstitialId(context) != null; //Has keys
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return getVideoId(context) != null; //Has keys
    }

    @Override
    public boolean canDisplayRewardedVideo(Context context) {
        return getRewardedVideoId(context) != null; //Has keys
    }

    @Override
    public boolean isStaticInterstitialReady(Activity activity) {
        //If using Android UI Thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                return mInterstitialAd != null && mInterstitialAd.isLoaded();
            } catch (Exception e) {
                TLog.error(e);
            }
        }
        return mInterstitialAd != null && mStaticReady;
    }

    @Override
    public boolean isVideoInterstitialReady(Activity activity) {
        //If using Android UI Thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                return mVideoInterstitialAd != null && mVideoInterstitialAd.isLoaded();
            } catch (Exception e) {
                TLog.error(e);
            }
        }
        return mVideoInterstitialAd != null && mVideoReady;
    }

    @Override
    public boolean isRewardInterstitialReady(Activity activity) {
        //If using Android UI Thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                return mRewardVideoAd != null && mRewardVideoAd.isLoaded();
            } catch (Exception e) {
                TLog.error(e);
            }
        }
        return mRewardVideoAd != null && mRewardVideoReady;
    }

    @Override
    public ViewGroup loadAd(Activity activity, TMAdSize size, TMAdListenerBase listener) {
        com.google.android.gms.ads.AdSize adSize = mBannerSizes.getSize(size);
        if(adSize != null) {
            AdView view = new AdView(activity);
            view.setAdUnitId(getBannerId(activity));
            view.setAdSize(adSize);
            view.setAdListener(new AdMobAdListener(activity, listener));
            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(activity);
            if (devices != null) {
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            view.loadAd(builder.build());
            return view;
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not ready"));
        }
        return null;
    }

    @Override
    public void loadInterstitial(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (activity != null && getInterstitialId(activity) != null) {
            mInterstitialAd = new InterstitialAd(activity);
            mInterstitialAd.setAdUnitId(getInterstitialId(activity));
            mInterstitialAd.setAdListener(new AdMobInterstitialAdListener(activity, mInterstitialAd, shared_id, listener, TMAdType.STATIC_INTERSTITIAL, placement));

            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(activity);
            if (devices != null) {
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            mInterstitialAd.loadAd(builder.build());
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not ready"));
        }
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (activity != null && getVideoId(activity) != null) {
            mVideoInterstitialAd = new InterstitialAd(activity);
            mVideoInterstitialAd.setAdUnitId(getVideoId(activity));
            mVideoInterstitialAd.setAdListener(new AdMobInterstitialAdListener(activity, mVideoInterstitialAd, shared_id, listener, TMAdType.VIDEO_INTERSTITIAL, placement));

            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(activity);
            if (devices != null) {
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            mVideoInterstitialAd.loadAd(builder.build());
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not ready"));
        }
    }

    @Override
    public void loadRewardedVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (activity != null && getRewardedVideoId(activity) != null) {
            mRewardVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
            mRewardVideoAd.setRewardedVideoAdListener(new AdMobRewardListener(activity, mRewardVideoAd, shared_id, listener, TMAdType.REWARD_INTERSTITIAL, placement));

            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(activity);
            if (devices != null) {
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            mRewardVideoAd.loadAd(getRewardedVideoId(activity), builder.build());

        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not ready"));
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (isStaticInterstitialReady(activity)) {
            if (listener != null)
                mInterstitialAd.setAdListener(new AdMobInterstitialAdListener(activity, mInterstitialAd, getSharedId(mInterstitialAd.getAdUnitId()), listener, TMAdType.STATIC_INTERSTITIAL, placement));
            mInterstitialAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not loaded ad"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isVideoInterstitialReady(activity)) {
            if (listener != null)
                mVideoInterstitialAd.setAdListener(new AdMobInterstitialAdListener(activity, mVideoInterstitialAd, getSharedId(mVideoInterstitialAd.getAdUnitId()), listener, TMAdType.VIDEO_INTERSTITIAL, placement));
            mVideoInterstitialAd.show();
        }  else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not loaded ad"));
        }
    }

    @Override
    public void showRewardedVideo(Activity activity, String placement, TMRewardAdListenerBase listener) {
        if (isRewardInterstitialReady(activity)) {
            mRewardVideoAd.setRewardedVideoAdListener(new AdMobRewardListener(activity, mRewardVideoAd, getSharedId("ADMOB_REWARDED_VIDEO"), listener, TMAdType.REWARD_INTERSTITIAL, placement));
            mRewardVideoAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not loaded ad"));
        }
    }

    private TMAdError buildError(int code) {
        switch (code) {
            case 0:
                return new TMAdError(code, ERROR_CODE_INTERNAL_ERROR);
            case 1:
                return new TMAdError(code, ERROR_CODE_INVALID_REQUEST);
            case 2:
                return new TMAdError(code, ERROR_CODE_NETWORK_ERROR);
            case 3:
                return new TMAdError(code, ERROR_CODE_NO_FILL);
            default:
                return new TMAdError(code, ERROR_UNKNOWN);
        }
    }

    private class AdMobAdListener extends AdListener
    {
        private Activity mActivity;
        private final TMAdListenerBase mAdListener;

        AdMobAdListener(Activity activity, TMAdListenerBase listener) {
            mActivity = activity;
            mAdListener = listener;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            TMListenerHandler.DidClose(mAdListener);
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            TMAdError error = buildError(i);
            TMListenerHandler.DidFailToLoad(mAdListener, error);

            if (mActivity != null) {
                TMServiceQueue.ServiceError(mActivity, getName(), TMAdType.BANNER);
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(TMAdType.BANNER), "", getVersionID(mActivity), error.getErrorMessage());
            }
            mActivity = null;
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            TMListenerHandler.DidClick(mAdListener);
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            TMListenerHandler.DidDisplay(mAdListener);
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            TMListenerHandler.DidLoad(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), false, TMAdType.getString(TMAdType.BANNER), null, getVersionID(mActivity));
            mActivity = null;
        }
    }

    private class AdMobInterstitialAdListener extends AdListener
    {
        private Activity mActivity;
        private TMAdListenerBase mAdListener;
        private InterstitialAd mAd;
        private int mType;
        private String mPlacement;
        private String mShared_id;

        AdMobInterstitialAdListener(Activity activity, InterstitialAd ad, String shared_id, TMAdListenerBase listener, int type, String placement) {
            mActivity = activity;
            mAd = ad;
            mAdListener = listener;
            mType = type;
            mPlacement = placement;
            mShared_id = shared_id;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();

            TMListenerHandler.DidClose(mAdListener);

            if (mInterstitialAd == mAd) {
                mInterstitialAd = null;
                mStaticReady = false;
            }
            if (mVideoInterstitialAd == mAd) {
                mVideoInterstitialAd = null;
                mVideoReady = false;
            }

            mAd = null;
            mAdListener = null;
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            TMAdError error = buildError(i);

            TLog.error(error.getErrorMessage());

            if (mInterstitialAd == mAd) {
                mInterstitialAd = null;
                mStaticReady = false;
            }
            if (mVideoInterstitialAd == mAd) {
                mVideoInterstitialAd = null;
                mVideoReady = false;
            }

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mAdListener);

                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_id, false);
            }
            mActivity = null;
        }

        @Override
        public void onAdLeftApplication() {
            super.onAdLeftApplication();
            TMListenerHandler.DidClick(mAdListener);
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            TMListenerHandler.DidDisplay(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();

            if (mAd == mInterstitialAd)
                mStaticReady = true;
            if (mAd == mVideoInterstitialAd)
                mVideoReady = true;

            TMListenerHandler.DidLoad(mAdListener);
            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mShared_id, true);

                setSharedId(mAd.getAdUnitId(), mShared_id);
            }
            mActivity = null;
        }
    }

    private class AdMobRewardListener implements RewardedVideoAdListener {
        private Activity mActivity;
        private final TMAdListenerBase mAdListener;
        private final RewardedVideoAd mAd;
        private final int mType;
        private final String mPlacement;
        private final String mShared_id;

        AdMobRewardListener(Activity activity, RewardedVideoAd ad, String shared_id, TMAdListenerBase listener, int type, String placement) {
            mActivity = activity;
            mAd = ad;
            mShared_id = shared_id;
            mAdListener = listener;
            mType = type;
            mPlacement = placement;
        }

        @Override
        public void onRewardedVideoAdLoaded() {
            TLog.debug("onRewardedVideoAdLoaded");

            TMListenerHandler.DidLoad(mAdListener);
            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mShared_id, true);

                setSharedId("ADMOB_REWARDED_VIDEO", mShared_id);
            }
            mActivity = null;
        }

        @Override
        public void onRewardedVideoAdOpened() {
            TLog.debug("onRewardedVideoAdOpened");

            TMListenerHandler.DidDisplay(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity));

        }

        @Override
        public void onRewardedVideoStarted() {
            TLog.debug("onRewardedVideoStarted");
        }

        @Override
        public void onRewardedVideoAdClosed() {
            TLog.debug("onRewardedVideoAdClosed");
            TMListenerHandler.DidClose(mAdListener);
            mActivity = null;
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            TLog.debug("onRewarded " + rewardItem.getType() + " " + rewardItem.getAmount());

            if (mAdListener instanceof  TMRewardAdListenerBase) {
                TMListenerHandler.DidVerify((TMRewardAdListenerBase) mAdListener, mPlacement, rewardItem.getType(), rewardItem.getAmount());
            }
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
            TLog.debug("onRewardedVideoAdLeftApplication");
            TMListenerHandler.DidClick(mAdListener);
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            TLog.debug("onRewardedVideoAdFailedToLoad");

            TMAdError error = buildError(i);

            TLog.error(error.getErrorMessage());

            if (mRewardVideoAd == mAd) {
                mRewardVideoAd = null;
                mRewardVideoReady = false;
            }

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mAdListener);

                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), isPublisherKeys(), TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_id, false);
            }
            mActivity = null;
        }
    }

    private class TMAdMobBannerSizes extends TMBannerAdSizes {
        com.google.android.gms.ads.AdSize getSize(TMAdSize size) {
            if (size == STANDARD)
                return com.google.android.gms.ads.AdSize.BANNER;
            else if(size == LARGE)
                return com.google.android.gms.ads.AdSize.LARGE_BANNER;
            else if(size == MEDIUM_RECT)
                return com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE;
            else if(size == FULL)
                return com.google.android.gms.ads.AdSize.FULL_BANNER;
            else if(size == LEADERBOARD)
                return com.google.android.gms.ads.AdSize.LEADERBOARD;
            else if(size == SMART)
                return com.google.android.gms.ads.AdSize.SMART_BANNER;
            TLog.error(String.format(Locale.getDefault(), "No Ad Mob Banner Available for size: %s", size.name));
            return null;
        }
    }
}
