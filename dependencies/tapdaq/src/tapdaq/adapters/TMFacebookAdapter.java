package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.facebook.ads.*;

import com.tapdaq.sdk.ads.TapdaqPlacement;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.*;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.listeners.*;
import com.tapdaq.sdk.model.TMAdSize;

import java.util.List;
import java.util.Locale;

/**
 * Created by dominicroberts on 02/09/2016.
 */
public class TMFacebookAdapter extends TMAdapter {

    private AdView mAd;
    private InterstitialAd mInterstitialAd, mVideoInterstitialAd;

    public TMFacebookAdapter(Context context) {
        super(context);
    }

    @Override
    public String getName() { return TMMediationNetworks.FACEBOOK_NAME;}

    @Override
    public int getID() {
        return TMMediationNetworks.FACEBOOK;
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (mKeys != null && activity != null) {
            mServiceListener.onInitSuccess(activity, TMMediationNetworks.FACEBOOK);
        }
    }

    public TMFacebookAdapter setTestDevices(List<String> devices) {
        AdSettings.addTestDevices(devices);
        return this;
    }

    @Override
    public boolean isInitialised(Context context) {
        return getBannerId(context) != null || getInterstitialId(context) != null;
    }

    @Override
    public boolean isBannerAvailable(Context context, TMAdSize size) {
        if (getBannerId(context) != null) {
            com.facebook.ads.AdSize adSize = new TMFacebookBannerSizes().getSize(size);
            if (adSize != null)
                return true;
        }
        return false;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return getInterstitialId(context) != null;
    }

    @Override
    public boolean canDisplayVideo(Context context) {
        return getVideoId(context) != null;
    }

    @Override
    public boolean isStaticInterstitialReady(Activity activity) {
        return mInterstitialAd != null && mInterstitialAd.isAdLoaded();
    }

    @Override
    public boolean isVideoInterstitialReady(Activity activity) {
        return mVideoInterstitialAd != null && mVideoInterstitialAd.isAdLoaded();
    }

    @Override
    public ViewGroup loadAd(Activity activity, String shared_id, TMAdSize size, TMAdListenerBase listener) {
        com.facebook.ads.AdSize adSize = new TMFacebookBannerSizes().getSize(size);
        if(adSize != null) {
            mAd = new AdView(activity, getBannerId(activity), adSize);
            mAd.setAdListener(new FBBannerListener(activity, shared_id, listener));
            mAd.loadAd();
            return mAd;
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook not ready"));
        }
        return null;
    }

    @Override
    public void loadInterstitial(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (activity != null && getInterstitialId(activity) != null) {
            mInterstitialAd = new InterstitialAd(activity, getInterstitialId(activity));
            mInterstitialAd.setAdListener(new FBInterstitialListener(activity, shared_id, placement, TMAdType.STATIC_INTERSTITIAL, listener));
            mInterstitialAd.loadAd();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook not ready"));
        }
    }

    @Override
    public void loadVideo(Activity activity, String shared_id, String placement, TMAdListenerBase listener) {
        if (activity != null && getVideoId(activity) != null) {
            mVideoInterstitialAd = new InterstitialAd(activity, getInterstitialId(activity));
            mVideoInterstitialAd.setAdListener(new FBInterstitialListener(activity, shared_id, placement, TMAdType.VIDEO_INTERSTITIAL, listener));
            mVideoInterstitialAd.loadAd();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook not ready"));
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (isStaticInterstitialReady(activity)) {
            mInterstitialAd.setAdListener(new FBInterstitialListener(activity, getSharedId(mInterstitialAd.getPlacementId()), placement, TMAdType.STATIC_INTERSTITIAL, listener));
            mInterstitialAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook ad not loaded"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (isVideoInterstitialReady(activity)) {
            mVideoInterstitialAd.setAdListener(new FBInterstitialListener(activity, getSharedId(mVideoInterstitialAd.getPlacementId()), placement, TMAdType.VIDEO_INTERSTITIAL, listener));
            mVideoInterstitialAd.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook ad not loaded"));
        }
    }

    @Override
    public void destroy() {
        if(mAd != null)
            mAd.destroy();
    }

    private TMAdError buildError(AdError error) {
        return new TMAdError(error.getErrorCode(), error.getErrorMessage());
    }

    private class FBBannerListener implements AdListener
    {
        private final TMAdListenerBase mAdListener;
        private String mShared_Id;
        private Activity mActivity;

        FBBannerListener(Activity activity, String shared_id, TMAdListenerBase listener) {
            mActivity = activity;
            mShared_Id = shared_id;
            mAdListener = listener;
        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }

        @Override
        public void onError(Ad ad, AdError adError) {
            TMAdError error = buildError(adError);

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mShared_Id, getName(), TMAdType.BANNER, TapdaqPlacement.TDPTagDefault, error, mAdListener);

                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), false, TMAdType.getString(TMAdType.BANNER), null, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_Id, false);

            }
            mActivity = null;
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TMListenerHandler.DidLoad(mAdListener);

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), false, TMAdType.getString(TMAdType.BANNER), null, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mShared_Id, true);
            }
            mActivity = null;
        }

        @Override
        public void onAdClicked(Ad ad) {
            TMListenerHandler.DidClick(mAdListener);
        }
    }

    private class FBInterstitialListener implements InterstitialAdListener {
        private final TMAdListenerBase mAdListener;
        private String mPlacement;
        private Activity mActivity;
        private String mShared_id;
        private int mType;

        FBInterstitialListener(Activity activity, String shared_id, String placement, int type, TMAdListenerBase listener) {
            mActivity = activity;
            mShared_id = shared_id;
            mAdListener = listener;
            mPlacement = placement;
            mType = type;
        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }

        @Override
        public void onInterstitialDisplayed(Ad ad) {
            TMListenerHandler.DidDisplay(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, mShared_id, getName(), false, TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            TMListenerHandler.DidClose(mAdListener);
            ad.destroy();

            if (ad == mInterstitialAd)
                mInterstitialAd = null;
            else if (ad == mVideoInterstitialAd)
                mVideoInterstitialAd = null;

            reloadAd(mActivity, mType, mPlacement, mAdListener);

            mActivity = null;
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            TMAdError error = buildError(adError);
            TLog.error(error.getErrorMessage());

            if (mActivity != null) {
                TMServiceErrorHandler.ServiceError(mActivity, mShared_id, getName(), mType, mPlacement, error, mAdListener);
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidFailToLoad(mActivity, getName(), false, TMAdType.getString(mType), mPlacement, getVersionID(mActivity), error.getErrorMessage());
                statsManager.finishAdRequest(mActivity, mShared_id, false);
            }


            if (ad == mInterstitialAd)
                mInterstitialAd = null;
            else if (ad == mVideoInterstitialAd)
                mVideoInterstitialAd = null;
            mActivity = null;
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TMListenerHandler.DidLoad(mAdListener);
            setSharedId(ad.getPlacementId(), mShared_id);

            if (mActivity != null) {
                TMStatsManager statsManager = new TMStatsManager(mActivity);
                statsManager.sendDidLoad(mActivity, getName(), false, TMAdType.getString(mType), mPlacement, getVersionID(mActivity));
                statsManager.finishAdRequest(mActivity, mShared_id, true);
            }
            mActivity = null;
        }

        @Override
        public void onAdClicked(Ad ad) {
            TMListenerHandler.DidClick(mAdListener);
        }
    }

    private class TMFacebookBannerSizes extends TMBannerAdSizes {
        com.facebook.ads.AdSize getSize(TMAdSize size) {
            if (size == STANDARD)
                return AdSize.BANNER_HEIGHT_50;
            else if(size == LARGE)
                return AdSize.BANNER_HEIGHT_90;
            else if(size == MEDIUM_RECT)
                return AdSize.RECTANGLE_HEIGHT_250;
            TLog.error(String.format(Locale.getDefault(), "No Facebook Banner Available for size: %s", size.name));
            return null;
        }
    }
}
