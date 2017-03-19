package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.facebook.ads.*;

import com.tapdaq.sdk.adnetworks.TMServiceQueue;
import com.tapdaq.sdk.analytics.TMStatsManager;
import com.tapdaq.sdk.common.*;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.listeners.*;
import com.tapdaq.sdk.model.TMAdSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dominicroberts on 02/09/2016.
 */
public class TMFacebookAdapter extends TMAdapter {

    private AdView mAd;
    private List<InterstitialAd> mInterstitialAd = new ArrayList<InterstitialAd>();

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
            mListener.onInitSuccess(activity, TMMediationNetworks.FACEBOOK);
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
            AdSize adSize = new TMFacebookBannerSizes().getSize(size);
            if (adSize != null)
                return true;
        }
        return false;
    }

    @Override
    public boolean canDisplayInterstitial(Context context) {
        return getInterstitialId(context) != null && isActivityAvailable(context, InterstitialAdActivity.class);
    }

    @Override
    public ViewGroup loadAd(Activity activity, TMAdSize size, TMAdListenerBase listener) {
        AdSize adSize = new TMFacebookBannerSizes().getSize(size);
        if(adSize != null) {
            mAd = new AdView(activity, getBannerId(activity), adSize);
            mAd.setAdListener(new FBBannerListener(activity, listener));
            mAd.loadAd();
            return mAd;
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook not ready"));
        }
        return null;
    }

    @Override
    public void loadInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (activity != null && getInterstitialId(activity) != null) {
            InterstitialAd ad = new InterstitialAd(activity, getInterstitialId(activity));
            ad.setAdListener(new FBInterstitialListener(activity, placement, listener));
            mInterstitialAd.add(ad);
            ad.loadAd();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Facebook not ready"));
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        InterstitialAd ad = (mInterstitialAd.isEmpty() ? null : mInterstitialAd.get(0));
        if (ad != null && ad.isAdLoaded()) {
            ad.setAdListener(new FBInterstitialListener(activity, placement, listener));
            ad.show();
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
        private Activity mActivity;

        FBBannerListener(Activity activity, TMAdListenerBase listener) {
            mActivity = activity;
            mAdListener = listener;
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            TMAdError error = buildError(adError);
            TMListenerHandler.DidFailToLoad(mAdListener, error);

            if (mActivity != null) {
                TMServiceQueue.ServiceError(mActivity, getName(), TMAdType.BANNER);
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), TMAdType.getString(TMAdType.BANNER), null, getVersionID(mActivity), error.getErrorMessage());
            }
            mActivity = null;
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TMListenerHandler.DidLoad(mAdListener);

            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), TMAdType.getString(TMAdType.BANNER), null, getVersionID(mActivity));
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

        FBInterstitialListener(Activity activity, String placement, TMAdListenerBase listener) {
            mActivity = activity;
            mAdListener = listener;
            mPlacement = placement;
        }
        @Override
        public void onInterstitialDisplayed(Ad ad) {
            TMListenerHandler.DidDisplay(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendImpression(mActivity, getName(), TMAdType.getString(TMAdType.STATIC_INTERSTITIAL), mPlacement, getVersionID(mActivity));
        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            TMListenerHandler.DidClose(mAdListener);
            ad.destroy();

            if (mInterstitialAd.contains(ad))
                mInterstitialAd.remove(ad);

            mActivity = null;
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            TMAdError error = buildError(adError);
            TMServiceErrorHandler.ServiceError(mActivity, getName(), TMAdType.STATIC_INTERSTITIAL, mPlacement, error, mAdListener);
            TLog.error(error.getErrorMessage());

            if (mInterstitialAd.contains(ad))
                mInterstitialAd.remove(ad);

            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidFailToLoad(mActivity, getName(), TMAdType.getString(TMAdType.STATIC_INTERSTITIAL), mPlacement, getVersionID(mActivity), error.getErrorMessage());
            mActivity = null;
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TMListenerHandler.DidLoad(mAdListener);
            if (mActivity != null)
                new TMStatsManager(mActivity).sendDidLoad(mActivity, getName(), TMAdType.getString(TMAdType.STATIC_INTERSTITIAL), mPlacement, getVersionID(mActivity));
            mActivity = null;
        }

        @Override
        public void onAdClicked(Ad ad) {
            TMListenerHandler.DidClick(mAdListener);
        }
    }

    private class TMFacebookBannerSizes extends TMBannerAdSizes {
        AdSize getSize(TMAdSize size) {
            if (size == STANDARD)
                return AdSize.BANNER_320_50;
            else if(size == LARGE)
                return AdSize.BANNER_HEIGHT_90;
            else if(size == MEDIUM_RECT)
                return AdSize.RECTANGLE_HEIGHT_250;
            TLog.error(String.format(Locale.getDefault(), "No Facebook Banner Available for size: %s", size.name));
            return null;
        }
    }
}
