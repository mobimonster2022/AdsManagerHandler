package com.swastikalabs.adshandler.repository

import android.app.Activity
import android.view.ViewGroup
import com.swastikalabs.adshandler.ads.BannerAds
import com.swastikalabs.adshandler.ads.InterstitialAds
import com.swastikalabs.adshandler.ads.NativeAds
import com.swastikalabs.adshandler.model.AdsConfig
import com.swastikalabs.adshandler.provider.AdsProvider
import com.swastikalabs.adshandler.provider.google.NativeAdViewFactory

/** App-facing creation boundary; provider selection is injected. */
class AdsRepository(private val provider: AdsProvider) {
    fun createInterstitial(activity: Activity, config: AdsConfig): InterstitialAds =
        provider.createInterstitial(activity, config)

    fun createBanner(activity: Activity, container: ViewGroup, config: AdsConfig): BannerAds =
        provider.createBanner(activity, container, config)

    fun createNative(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig,
        adViewFactory: NativeAdViewFactory
    ): NativeAds = provider.createNative(activity, container, config, adViewFactory)
}
