package com.ironsource.adapters.custom.udx;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.longyun.udx.sdk.AdError;
import com.longyun.udx.sdk.UDXRewardedAd;

public class UDXCustomRewardedVideo extends BaseRewardedVideo<UDXCustomAdapter>{

    private final String TAG = UDXCustomRewardedVideo.class.getSimpleName();
    private UDXRewardedAd mRewardedAd;
    private boolean isAdAvailable;
    private Activity mActivity;

    public UDXCustomRewardedVideo(@NonNull NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull RewardedVideoAdListener listener) {
        mActivity = activity;
        String placementId = (String) adData.getConfiguration().get("placementId");
        Log.i(TAG, "loadAd->placementId:"+placementId);
        mRewardedAd = new UDXRewardedAd(activity, placementId);
        mRewardedAd.setListener(new UDXRewardedAd.Listener() {
            @Override
            public void onAdHidden() {
                if(listener != null)
                    listener.onAdClosed();
            }

            @Override
            public void onUserRewarded() {
                if(listener != null) {
                    listener.onAdRewarded();
                }
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
    public void showAd(@NonNull AdData adData, @NonNull RewardedVideoAdListener listener) {
        if (mRewardedAd != null && mActivity != null) {
            mRewardedAd.showAd();
            mRewardedAd = null;
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        return isAdAvailable;
    }
}
