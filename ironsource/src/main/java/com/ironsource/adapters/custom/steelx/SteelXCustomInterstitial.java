package com.ironsource.adapters.custom.steelx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.longyun.steelx.sdk.SXAd;
import com.longyun.steelx.sdk.SXError;
import com.longyun.steelx.sdk.inters.SXInterstitialAd;
import com.longyun.steelx.sdk.inters.SXInterstitialAdListener;

public class SteelXCustomInterstitial extends BaseInterstitial<SteelXCustomAdapter> {

    private final String TAG = "SteelXCustomInterstitial";
    private SXAd mAd;
    private boolean isAdAvailable;
    private Activity mActivity;


    public SteelXCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {
        mActivity = activity;
        String placementId = (String) adData.getConfiguration().get("placementID");
        Log.i(TAG, "loadAd->placementId:"+placementId);
        new SXInterstitialAd().loadAd(placementId, null, new SXInterstitialAdListener(){

            @Override
            public void onAdLoaded(SXAd ad) {
                mAd = ad;
                isAdAvailable = true;

                if(listener != null){
                    listener.onAdLoadSuccess();
                }
            }

            @Override
            public void onAdFailedToLoad(String adUnitId, SXError error) {
                if(listener != null) {
                    //ADAPTER_ERROR_TYPE_NO_FILL 无填充
                    //ADAPTER_ERROR_TYPE_AD_EXPIRED 过期
                    //ADAPTER_ERROR_TYPE_INTERNAL 其他
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayFailed(SXAd ad, SXError error) {
                if(listener != null) {
                    listener.onAdShowFailed(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayed(SXAd ad) {
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClicked(SXAd ad) {
                if(listener != null)
                    listener.onAdClicked();
            }

            @Override
            public void onAdHidden(SXAd ad) {
                if(listener != null)
                    listener.onAdClosed();
            }
        });
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull InterstitialAdListener listener) {
        if (mAd != null && mActivity != null) {
            mAd.show(mActivity);
            mAd = null;
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return isAdAvailable;
    }
}
