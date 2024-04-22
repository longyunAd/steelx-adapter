package com.ironsource.adapters.custom.udx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.longyun.udx.sdk.AdError;
import com.longyun.udx.sdk.UDXInterstitialAd;

public class UDXCustomInterstitial extends BaseInterstitial<UDXCustomAdapter> {

    private final String TAG = UDXCustomInterstitial.class.getSimpleName();
    private UDXInterstitialAd mInterstitialAd;
    private boolean isAdAvailable;
    private Activity mActivity;


    public UDXCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {
        mActivity = activity;
        String placementId = (String) adData.getConfiguration().get("placementId");
        Log.i(TAG, "loadAd->placementId:"+placementId);
        mInterstitialAd = new UDXInterstitialAd(activity, placementId);
        mInterstitialAd.setListener(new UDXInterstitialAd.Listener() {
            @Override
            public void onAdHidden() {
                if(listener != null)
                    listener.onAdClosed();
            }

            @Override
            public void onAdLoaded() {
                isAdAvailable = true;

                if(listener != null){
                    listener.onAdLoadSuccess();
                }
            }

            @Override
            public void onAdLoadFailed() {
                if(listener != null) {
                    //ADAPTER_ERROR_TYPE_NO_FILL 无填充
                    //ADAPTER_ERROR_TYPE_AD_EXPIRED 过期
                    //ADAPTER_ERROR_TYPE_INTERNAL 其他
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, AdError.NO_FILL.getErrorCode(), AdError.NO_FILL.getErrorMessage());
                }
            }

            @Override
            public void onAdDisplayed() {
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                if(listener != null)
                    listener.onAdClicked();
            }

            @Override
            public void onAdError(AdError adError) {
                if(listener != null) {
                    listener.onAdShowFailed(adError.getErrorCode(), adError.getErrorMessage());
                }
            }
        });
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull InterstitialAdListener listener) {
        if (mInterstitialAd != null && mActivity != null) {
            mInterstitialAd.showAd();
            mInterstitialAd = null;
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return isAdAvailable;
    }
}
