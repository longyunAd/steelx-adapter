package com.ironsource.adapters.custom.udx;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.longyun.udx.sdk.SdkInitListener;
import com.longyun.udx.sdk.UDX;

public class UDXCustomAdapter extends BaseAdapter {

    private static final String TAG = UDXCustomAdapter.class.getCanonicalName();

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, @Nullable NetworkInitializationListener networkInitializationListener) {
       String appId = (String) adData.getConfiguration().get("appId");
//        Log.i(TAG, "init->appId:"+appId +" "+ GsonUtils.toJson(adData));

        UDX.init(context, appId, new SdkInitListener() {
            public void onInitSuccess() {
                Log.i(TAG, "udx init success: ");
                if(networkInitializationListener != null)
                    networkInitializationListener.onInitSuccess();
            }

            public void onInitError() {
                Log.i(TAG, "udx init fail: ");
                if(networkInitializationListener != null)
                    networkInitializationListener.onInitFailed(-1, "udx init fail");
            }
        });
    }

    @Nullable
    @Override
    public String getNetworkSDKVersion() {
        return UDX.getSdkVersion();
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return "1.1.0";
    }
}
