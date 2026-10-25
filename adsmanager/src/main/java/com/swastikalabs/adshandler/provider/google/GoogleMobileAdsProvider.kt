package com.swastikalabs.adshandler.provider.google

import android.app.Activity
import android.view.ViewGroup
import com.swastikalabs.adshandler.ads.BannerAds
import com.swastikalabs.adshandler.ads.InterstitialAds
import com.swastikalabs.adshandler.ads.NativeAds
import com.swastikalabs.adshandler.model.AdsConfig
import com.swastikalabs.adshandler.provider.AdsProvider

class GoogleMobileAdsProvider : AdsProvider {
    override fun createInterstitial(activity: Activity, config: AdsConfig): InterstitialAds =
        GoogleInterstitialAds(activity, config)

    override fun createBanner(
        activity: Activity, container: ViewGroup, config: AdsConfig
    ): BannerAds = GoogleBannerAds(activity, container, config)

    override fun createNative(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig,
        adViewFactory: NativeAdViewFactory
    ): NativeAds = GoogleNativeAds(activity, container, config, adViewFactory)
}
