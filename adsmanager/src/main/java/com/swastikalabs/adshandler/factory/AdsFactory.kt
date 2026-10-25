package com.swastikalabs.adshandler.factory

import android.app.Activity
import android.view.ViewGroup
import com.swastikalabs.adshandler.ads.BannerAds
import com.swastikalabs.adshandler.ads.InterstitialAds
import com.swastikalabs.adshandler.ads.NativeAds
import com.swastikalabs.adshandler.manager.ActivityAdsManager
import com.swastikalabs.adshandler.model.AdsConfig
import com.swastikalabs.adshandler.provider.AdsProvider
import com.swastikalabs.adshandler.provider.google.NativeAdViewFactory

/** Provider-neutral factory. Provider selection stays in the consuming app. */
object AdsFactory {
    fun createInterstitial(activity: Activity, config: AdsConfig, provider: AdsProvider): InterstitialAds =
        provider.createInterstitial(activity, config)

    fun createBanner(
        activity: Activity, container: ViewGroup, config: AdsConfig, provider: AdsProvider
    ): BannerAds = provider.createBanner(activity, container, config)

    fun createNative(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig,
        provider: AdsProvider,
        adViewFactory: NativeAdViewFactory
    ): NativeAds = provider.createNative(activity, container, config, adViewFactory)

    fun createForActivity(
        activity: Activity,
        config: AdsConfig,
        provider: AdsProvider,
        bannerContainer: ViewGroup? = null
    ): ActivityAdsManager = ActivityAdsManager(
        interstitial = provider.createInterstitial(activity, config),
        banner = bannerContainer?.let { provider.createBanner(activity, it, config) }
    )
}
