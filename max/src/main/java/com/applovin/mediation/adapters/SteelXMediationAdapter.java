package com.applovin.mediation.adapters;

import android.app.Activity;
import android.os.Bundle;

import com.applovin.impl.sdk.utils.BundleUtils;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.MaxSignalProvider;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxSignalCollectionListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterSignalCollectionParameters;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.longyun.steelx.sdk.BidManager;
import com.longyun.steelx.sdk.SXAd;
import com.longyun.steelx.sdk.SXConfig;
import com.longyun.steelx.sdk.SXError;
import com.longyun.steelx.sdk.SXSdk;
import com.longyun.steelx.sdk.inters.SXInterstitialAd;
import com.longyun.steelx.sdk.inters.SXInterstitialAdListener;
import com.longyun.steelx.sdk.inters.SXInterstitialRequest;
import com.longyun.steelx.sdk.reward.SXReward;
import com.longyun.steelx.sdk.reward.SXRewardedAd;
import com.longyun.steelx.sdk.reward.SXRewardedAdListener;
import com.longyun.steelx.sdk.reward.SXRewardedRequest;

import java.util.concurrent.atomic.AtomicBoolean;

public class SteelXMediationAdapter
        extends MediationAdapterBase
        implements MaxSignalProvider, MaxInterstitialAdapter, MaxRewardedAdapter
{

    private static final int ERROR_AD_TYPE = 10001;
    private static final AtomicBoolean        initialized                        = new AtomicBoolean();
    private static InitializationStatus status;

    private SXInterstitialAd interstitialAd;
    private SXRewardedAd rewardedAd;

    private InterstitialAdListener interstitialAdListener;
    private RewardedAdListener rewardedAdListener;

    public SteelXMediationAdapter(AppLovinSdk sdk) {
        super(sdk);
    }

    @Override
    public void initialize(final MaxAdapterInitializationParameters parameters, final Activity activity, final OnCompletionListener onCompletionListener)
    {
        if ( initialized.compareAndSet( false, true ) )
        {
            status = InitializationStatus.INITIALIZING;
            final Bundle serverParameters = parameters.getServerParameters();
            final String appId = serverParameters.getString( "app_id" );
            log( "Initializing SDK with app id: " + appId + "..." );

//            Boolean hasUserConsent = parameters.hasUserConsent();
//            if ( hasUserConsent != null )
//            {
//                builder.setGDPRConsent( hasUserConsent ? 1 : 0 );
//            }
//
//            // NOTE: Adapter / mediated SDK has support for COPPA, but is not approved by Play Store and therefore will be filtered on COPPA traffic
//            // https://support.google.com/googleplay/android-developer/answer/9283445?hl=en
//            Boolean isAgeRestrictedUser = parameters.isAgeRestrictedUser();
//            if ( isAgeRestrictedUser != null )
//            {
//                builder.setChildDirected( isAgeRestrictedUser ? 1 : 0 );
//            }
//
//            Boolean isDoNotSell = parameters.isDoNotSell();
//            if ( isDoNotSell != null )
//            {
//                builder.setDoNotSell( isDoNotSell ? 1 : 0 );
//            }

            SXConfig config = new SXConfig.Builder()
                    .setAppId(appId)
                    .setDebug(parameters.isTesting())
                    .build();
            SXSdk.init(activity.getApplicationContext(), config, new SXSdk.SLInitCallback() {

                @Override
                public void success() {
                    log( "SDK initialized" );

                    status = InitializationStatus.INITIALIZED_SUCCESS;
                    onCompletionListener.onCompletion( status, null );
                }

                @Override
                public void fail(int code, String msg) {
                    log( "SDK failed to initialize with code: " + code + " and message: " + msg );

                    status = InitializationStatus.INITIALIZED_FAILURE;
                    onCompletionListener.onCompletion( status, msg );
                }
            });
        }
        else
        {
            log( "attempted initialization already - marking initialization as completed" );
            onCompletionListener.onCompletion( status, null );
        }
    }

    @Override
    public String getSdkVersion()
    {
        return SXSdk.getSDKVersion();
    }

    @Override
    public String getAdapterVersion()
    {
        return "1.0.0";
    }

    @Override
    public void onDestroy()
    {
        interstitialAdListener = null;
        interstitialAd = null;

        rewardedAdListener = null;
        rewardedAd = null;
    }

    //region Signal Collection

    @Override
    public void collectSignal(final MaxAdapterSignalCollectionParameters parameters, final Activity activity, final MaxSignalCollectionListener callback)
    {
        log( "Collecting signal..." );

        String signal = BidManager.getBiddingToken();
        callback.onSignalCollected( signal );
    }

    //endregion

    //region MaxInterstitialAdapter Methods

    @Override
    public void loadInterstitialAd(final MaxAdapterResponseParameters parameters, final Activity activity, final MaxInterstitialAdapterListener listener)
    {
        String codeId = parameters.getThirdPartyAdPlacementId();
        String bidResponse = parameters.getBidResponse();
        boolean isBidding = AppLovinSdkUtils.isValidString( bidResponse );
        log( "Loading " + ( isBidding ? "bidding " : "" ) + "interstitial ad for code id \"" + codeId + "\"..." );

        SXInterstitialRequest request = new SXInterstitialRequest();
        request.setUserId(getWrappingSdk().getUserIdentifier());
        interstitialAdListener = new InterstitialAdListener( codeId, listener );

        interstitialAd = new SXInterstitialAd();
        interstitialAd.loadAd( codeId, request, interstitialAdListener);
    }

    @Override
    public void showInterstitialAd(final MaxAdapterResponseParameters parameters, final Activity activity, final MaxInterstitialAdapterListener listener)
    {
        String codeId = parameters.getThirdPartyAdPlacementId();
        log( "Showing interstitial ad for code id \"" + codeId + "\"..." );

//        interstitialAd.setAdInteractionListener( interstitialAdListener );
        interstitialAd.show( activity );
    }

    //endregion

    //region MaxRewardedAdapter Methods

    @Override
    public void loadRewardedAd(final MaxAdapterResponseParameters parameters, final Activity activity, final MaxRewardedAdapterListener listener)
    {
        String codeId = parameters.getThirdPartyAdPlacementId();
        String bidResponse = parameters.getBidResponse();
        boolean isBidding = AppLovinSdkUtils.isValidString( bidResponse );
        log( "Loading " + ( isBidding ? "bidding " : "" ) + "rewarded ad for code id \"" + codeId + "\"..." );

//        PAGConfig.setUserData( createAdConfigData( parameters.getServerParameters(), false ) );
//
//        Map<String, Object> extraInfo = new HashMap<>();
//        extraInfo.put( "user_id", getWrappingSdk().getUserIdentifier() );

        SXRewardedRequest request = new SXRewardedRequest();
        request.setUserId(getWrappingSdk().getUserIdentifier());
//        request.setExtraInfo( extraInfo );
//
//        if ( isBidding )
//        {
//            request.setAdString( bidResponse );
//        }

        rewardedAdListener = new RewardedAdListener( codeId, listener );
//        PAGRewardedAd.loadAd( codeId, request, rewardedAdListener );
        rewardedAd = new SXRewardedAd();
        rewardedAd.loadAd( codeId, request, rewardedAdListener);
    }

    @Override
    public void showRewardedAd(final MaxAdapterResponseParameters parameters, final Activity activity, final MaxRewardedAdapterListener listener)
    {
        String codeId = parameters.getThirdPartyAdPlacementId();
        log( "Showing rewarded ad for code id \"" + codeId + "\"..." );

        // Configure userReward from server.
        configureReward( parameters );

//        rewardedAd.setAdInteractionListener( rewardedAdListener );
        rewardedAd.show( activity );
    }

    //endregion

    //region MaxAdViewAdapter Methods

    //endregion

    private static MaxAdapterError toMaxError(final int byteDanceErrorCode, final String byteDanceErrorMessage)
    {
        MaxAdapterError adapterError = MaxAdapterError.UNSPECIFIED;
        switch ( byteDanceErrorCode )
        {
//            case OK: // Success
//                throw new IllegalStateException( "Returned error code for success" );
//            case NO_AD: // NO FILL
//                adapterError = MaxAdapterError.NO_FILL;
//                break;
            case ERROR_AD_TYPE: // Ad type is illegal
                adapterError = MaxAdapterError.INVALID_CONFIGURATION;
                break;
//            case SPLASH_CACHE_EXPIRED_ERROR: // Cache expired
//                adapterError = MaxAdapterError.AD_EXPIRED;
//                break;
//            case NET_ERROR: // Network Error
//                adapterError = MaxAdapterError.NO_CONNECTION;
//                break;
        }

        return new MaxAdapterError( adapterError.getErrorCode(), adapterError.getErrorMessage(), byteDanceErrorCode, byteDanceErrorMessage );
    }

    private String createAdConfigData(Bundle serverParameters, Boolean isInitializing)
    {
        if ( isInitializing )
        {
            return String.format( "[{\"name\":\"mediation\",\"value\":\"MAX\"},{\"name\":\"adapter_version\",\"value\":\"%s\"}]", getAdapterVersion() );
        }
        else
        {
            return String.format( "[{\"name\":\"mediation\",\"value\":\"MAX\"},{\"name\":\"adapter_version\",\"value\":\"%s\"},{\"name\":\"hybrid_id\",\"value\":\"%s\"}]", getAdapterVersion(), BundleUtils.getString( "event_id", serverParameters ) );
        }
    }

    //endregion

    private class InterstitialAdListener
            implements SXInterstitialAdListener
    {
        private final String                         codeId;
        private final MaxInterstitialAdapterListener listener;

        InterstitialAdListener(final String codeId, final MaxInterstitialAdapterListener listener)
        {
            this.codeId = codeId;
            this.listener = listener;
        }

        @Override
        public void onAdLoaded(final SXAd ad)
        {
            if ( ad == null )
            {
                log( "Interstitial ad" + "(" + codeId + ")" + " NO FILL'd" );
                listener.onInterstitialAdLoadFailed( MaxAdapterError.NO_FILL );

                return;
            }

            log( "Interstitial ad loaded: " + codeId );
            interstitialAd = (SXInterstitialAd) ad;

            listener.onInterstitialAdLoaded();
        }

        @Override
        public void onAdFailedToLoad(String adUnitId, SXError error) {
            MaxAdapterError adapterError = toMaxError( error.getCode(), error.getMessage() );
            log( "Interstitial ad (" + codeId + ") failed to load with error: " + adapterError );
            listener.onInterstitialAdLoadFailed( adapterError );
        }

        @Override
        public void onAdDisplayFailed(SXAd ad, SXError error) {
            MaxAdapterError adapterError = toMaxError( error.getCode(), error.getMessage() );
            log( "Interstitial ad (" + codeId + ") failed to load with error: " + adapterError );
            listener.onInterstitialAdLoadFailed( adapterError );
        }

        @Override
        public void onAdDisplayed(SXAd ad) {
            log( "Interstitial ad displayed: " + codeId );
            listener.onInterstitialAdDisplayed();
        }

        @Override
        public void onAdClicked(SXAd ad) {
            log( "Interstitial ad clicked: " + codeId );
            listener.onInterstitialAdClicked();
        }

        @Override
        public void onAdHidden(SXAd ad) {
            log( "Interstitial ad hidden: " + codeId );
            listener.onInterstitialAdHidden();
        }
    }

    private class RewardedAdListener
            implements SXRewardedAdListener
    {
        private final String                     codeId;
        private final MaxRewardedAdapterListener listener;

        private boolean hasGrantedReward;

        RewardedAdListener(final String codeId, final MaxRewardedAdapterListener listener)
        {
            this.codeId = codeId;
            this.listener = listener;
        }

        @Override
        public void onAdLoaded(SXAd ad) {
            if ( ad == null )
            {
                log( "Rewarded ad" + "(" + codeId + ")" + " NO FILL'd" );
                listener.onRewardedAdLoadFailed( MaxAdapterError.NO_FILL );

                return;
            }

            log( "Rewarded ad loaded: " + codeId );
            rewardedAd = (SXRewardedAd) ad;

            listener.onRewardedAdLoaded();
        }

        @Override
        public void onAdFailedToLoad(String adUnitId, SXError error) {
            MaxAdapterError adapterError = toMaxError( error.getCode(), error.getMessage() );
            log( "Rewarded ad (" + codeId + ") failed to load with error: " + adapterError );
            listener.onRewardedAdLoadFailed( adapterError );
        }

        @Override
        public void onAdDisplayFailed(SXAd ad, SXError error) {
            MaxAdapterError adapterError = toMaxError( error.getCode(), error.getMessage() );
            log( "Rewarded ad (" + codeId + ") failed to load with error: " + adapterError );
            listener.onRewardedAdLoadFailed( adapterError );
        }

        @Override
        public void onAdDisplayed(SXAd ad) {
            log( "Rewarded ad displayed: " + codeId );

            listener.onRewardedAdDisplayed();
        }

        @Override
        public void onAdClicked(SXAd ad) {
            log( "Rewarded ad clicked: " + codeId );
            listener.onRewardedAdClicked();
        }

        @Override
        public void onAdHidden(SXAd ad) {
            log( "Rewarded ad hidden: " + codeId );

            listener.onRewardedAdVideoCompleted();

            if ( hasGrantedReward || shouldAlwaysRewardUser() )
            {
                final MaxReward reward = getReward();
                log( "Rewarded user with reward: " + reward );
                listener.onUserRewarded( reward );
            }

            listener.onRewardedAdHidden();
        }

        @Override
        public void onRewardedVideoStarted(SXAd ad) {
            listener.onRewardedAdVideoStarted();
        }

        @Override
        public void onRewardedVideoCompleted(SXAd ad) {

        }

        @Override
        public void onUserRewarded(SXAd ad, SXReward reward) {
            if(reward.isReward()) {
                log( "Rewarded user with reward: ");
                hasGrantedReward = true;
            } else {
                log( "Failed to reward user with error: ");
//                log( "Failed to reward user with error: " + code + " " + message );
                hasGrantedReward = false;
            }
        }
    }
}
