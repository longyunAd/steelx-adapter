package com.ironsource.adapters.custom.udx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.longyun.udx.sdk.UDXAd;
import com.longyun.udx.sdk.UDXError;
import com.longyun.udx.sdk.inters.UDXInterstitialAd;
import com.longyun.udx.sdk.inters.UDXInterstitialAdListener;
import com.longyun.udx.sdk.inters.UDXInterstitialRequest;

public class UDXCustomInterstitial extends BaseInterstitial<UDXCustomAdapter> {

    private final String TAG = UDXCustomInterstitial.class.getSimpleName();
    private UDXAd mAd;
    private boolean isAdAvailable;
    private Activity mActivity;


    public UDXCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {
        mActivity = activity;
        String placementId = (String) adData.getConfiguration().get("placementID");
        Log.i(TAG, "loadAd->placementId:"+placementId);
        new UDXInterstitialAd().loadAd(placementId, null, new UDXInterstitialAdListener(){

            @Override
            public void onAdLoaded(UDXAd ad) {
                mAd = ad;
                isAdAvailable = true;

                if(listener != null){
                    listener.onAdLoadSuccess();
                }
            }

            @Override
            public void onAdFailedToLoad(String adUnitId, UDXError error) {
                if(listener != null) {
                    //ADAPTER_ERROR_TYPE_NO_FILL 无填充
                    //ADAPTER_ERROR_TYPE_AD_EXPIRED 过期
                    //ADAPTER_ERROR_TYPE_INTERNAL 其他
                    listener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayFailed(UDXAd ad, UDXError error) {
                if(listener != null) {
                    listener.onAdShowFailed(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onAdDisplayed(UDXAd ad) {
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClicked(UDXAd ad) {
                if(listener != null)
                    listener.onAdClicked();
            }

            @Override
            public void onAdHidden(UDXAd ad) {
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
