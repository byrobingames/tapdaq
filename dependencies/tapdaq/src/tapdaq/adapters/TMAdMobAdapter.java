package tapdaq.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.android.gms.ads.*;

import com.tapdaq.sdk.adnetworks.TMServiceQueue;
import com.tapdaq.sdk.common.*;
import com.tapdaq.sdk.helpers.TLog;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.listeners.*;
import com.tapdaq.sdk.model.TMAdSize;
import com.tapdaq.sdk.storage.Storage;

import java.util.ArrayList;
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

    private List<InterstitialAd> mInterstitialAd = new ArrayList<InterstitialAd>();
    private List<InterstitialAd> mVideoInterstitialAd = new ArrayList<InterstitialAd>();

    public TMAdMobAdapter(Context context){
        super(context);
    }

    @Override
    public void initialise(Activity activity) {
        super.initialise(activity);

        if (mCurrentActivity != null && mKeys != null) {
            MobileAds.initialize(mCurrentActivity);
            mListener.onInitSuccess(mCurrentActivity, TMMediationNetworks.AD_MOB);
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
        return mCurrentActivity != null && (getBannerId(context) != null || getInterstitialId(context) != null || getVideoId(context) != null) ;
    }

    @Override
    public String getName() { return TMMediationNetworks.AD_MOB_NAME; }

    @Override
    public int getID() {
        return TMMediationNetworks.AD_MOB;
    }

    @Override
    public boolean isBannerAvailable(TMAdSize size) {
        return getBannerId(mCurrentActivity) != null && mBannerSizes.getSize(size) != null;
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
    public ViewGroup loadAd(Context context, TMAdSize size, TMAdListenerBase listener) {
        com.google.android.gms.ads.AdSize adSize = mBannerSizes.getSize(size);
        if(adSize != null) {
            AdView view = new AdView(context);
            view.setAdUnitId(getBannerId(context));
            view.setAdSize(adSize);
            view.setAdListener(new AdMobAdListener(listener));
            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(context);
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
    public void loadInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        if (activity != null && getInterstitialId(activity) != null) {
            InterstitialAd ad = new InterstitialAd(activity);
            ad.setAdUnitId(getInterstitialId(activity));
            ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.STATIC_INTERSTITIAL, placement));

            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(activity);
            if (devices != null) {
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            ad.loadAd(builder.build());
            mInterstitialAd.add(ad);
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not ready"));
        }
    }

    @Override
    public void loadVideo(Activity activity, String placement, TMAdListenerBase listener) {
        if (activity != null && getVideoId(activity) != null) {
            InterstitialAd ad = new InterstitialAd(activity);
            ad.setAdUnitId(getVideoId(activity));
            ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.VIDEO_INTERSTITIAL, placement));
            mVideoInterstitialAd.add(ad);

            AdRequest.Builder builder = new AdRequest.Builder();

            String[] devices = getTestDevices(activity);
            if (devices != null) {
                for (String d : devices) {
                    builder.addTestDevice(d);
                }
            }

            ad.loadAd(builder.build());
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not ready"));
        }
    }

    @Override
    public void showInterstitial(Activity activity, String placement, TMAdListenerBase listener) {
        InterstitialAd ad = (mInterstitialAd.isEmpty() ? null : mInterstitialAd.get(0));
        if (ad != null && ad.isLoaded()) {
            if (listener != null)
                ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.STATIC_INTERSTITIAL, placement));
            ad.show();
        } else {
            TMListenerHandler.DidFailToLoad(listener, new TMAdError(0, "Ad Mob not loaded ad"));
        }
    }

    @Override
    public void showVideo(Activity activity, String placement, TMAdListenerBase listener) {
        InterstitialAd ad = (mVideoInterstitialAd.isEmpty() ? null : mVideoInterstitialAd.get(0));
        if (ad != null && ad.isLoaded()) {
            if (listener != null)
                ad.setAdListener(new AdMobInterstitialAdListener(ad, listener, TMAdType.VIDEO_INTERSTITIAL, placement));
            ad.show();
        }  else {
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
        private final TMAdListenerBase mAdListener;

        AdMobAdListener(TMAdListenerBase listener) {
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
            TMListenerHandler.DidFailToLoad(mAdListener, buildError(i));
            TMServiceQueue.ServiceError(mCurrentActivity, getName(), TMAdType.BANNER);
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
        }
    }

    private class AdMobInterstitialAdListener extends AdListener
    {
        private TMAdListenerBase mAdListener;
        private InterstitialAd mAd;
        private int mType;
        private String mPlacement;

        AdMobInterstitialAdListener(InterstitialAd ad, TMAdListenerBase listener, int type, String placement) {
            mAd = ad;
            mAdListener = listener;
            mType = type;
            mPlacement = placement;
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();

            TMListenerHandler.DidClose(mAdListener);

            if (!mInterstitialAd.isEmpty() && mAd == mInterstitialAd.get(0))
                mInterstitialAd.remove(mAd);
            else if(!mVideoInterstitialAd.isEmpty() && mAd == mVideoInterstitialAd.get(0))
                mVideoInterstitialAd.remove(mAd);

            mAd = null;
            mAdListener = null;
        }

        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);

            TMAdError error = buildError(i);
            TMServiceErrorHandler.ServiceError(mCurrentActivity, getName(), mType, mPlacement, error, mAdListener);
            TLog.error(error.getErrorMessage());

            if (!mInterstitialAd.isEmpty() && mAd == mInterstitialAd.get(0))
                mInterstitialAd.remove(mAd);
            else if(!mVideoInterstitialAd.isEmpty() && mAd == mVideoInterstitialAd.get(0))
                mVideoInterstitialAd.remove(mAd);
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
