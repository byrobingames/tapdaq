package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.facebook.ads.*;

import com.tapdaq.sdk.adnetworks.TMServiceQueue;
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

        if (mKeys != null && mCurrentActivity != null) {
            mListener.onInitSuccess(mCurrentActivity, TMMediationNetworks.FACEBOOK);
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
    public boolean isBannerAvailable(TMAdSize size) {
        if (getBannerId(mCurrentActivity) != null) {
            com.facebook.ads.AdSize adSize = new TMFacebookBannerSizes().getSize(size);
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
    public ViewGroup loadAd(Context context, TMAdSize size, TMAdListenerBase listener) {
        com.facebook.ads.AdSize adSize = new TMFacebookBannerSizes().getSize(size);
        if(adSize != null) {
            mAd = new AdView(context, getBannerId(context), adSize);
            mAd.setAdListener(new FBBannerListener(listener));
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
            ad.setAdListener(new FBInterstitialListener(placement, listener));
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
            ad.setAdListener(new FBInterstitialListener(placement, listener));
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

        FBBannerListener(TMAdListenerBase listener) {
            mAdListener = listener;
        }

        @Override
        public void onError(Ad ad, AdError adError) {
            TMListenerHandler.DidFailToLoad(mAdListener, buildError(adError));
            TMServiceQueue.ServiceError(mCurrentActivity, getName(), TMAdType.BANNER);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TMListenerHandler.DidLoad(mAdListener);
        }

        @Override
        public void onAdClicked(Ad ad) {
            TMListenerHandler.DidClick(mAdListener);
        }
    }

    private class FBInterstitialListener implements InterstitialAdListener {
        private final TMAdListenerBase mAdListener;
        private String mPlacement;

        FBInterstitialListener(String placement, TMAdListenerBase listener) {
            mAdListener = listener;
            mPlacement = placement;
        }
        @Override
        public void onInterstitialDisplayed(Ad ad) {
            TMListenerHandler.DidDisplay(mAdListener);
        }

        @Override
        public void onInterstitialDismissed(Ad ad) {
            TMListenerHandler.DidClose(mAdListener);
            ad.destroy();

            if (mInterstitialAd.contains(ad))
                mInterstitialAd.remove(ad);
        }

        @Override
        public void onError(Ad ad, AdError adError) {

            TMAdError error = buildError(adError);
            TMServiceErrorHandler.ServiceError(mCurrentActivity, getName(), TMAdType.STATIC_INTERSTITIAL, mPlacement, error, mAdListener);
            TLog.error(error.getErrorMessage());

            if (mInterstitialAd.contains(ad))
                mInterstitialAd.remove(ad);
        }

        @Override
        public void onAdLoaded(Ad ad) {
            TMListenerHandler.DidLoad(mAdListener);
        }

        @Override
        public void onAdClicked(Ad ad) {
            TMListenerHandler.DidClick(mAdListener);
        }
    }

    private class TMFacebookBannerSizes extends TMBannerAdSizes {
        com.facebook.ads.AdSize getSize(TMAdSize size) {
            if (size == STANDARD)
                return com.facebook.ads.AdSize.BANNER_320_50;
            else if(size == LARGE)
                return com.facebook.ads.AdSize.BANNER_HEIGHT_90;
            else if(size == MEDIUM_RECT)
                return com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250;
            TLog.error(String.format(Locale.getDefault(), "No Facebook Banner Available for size: %s", size.name));
            return null;
        }
    }
}
