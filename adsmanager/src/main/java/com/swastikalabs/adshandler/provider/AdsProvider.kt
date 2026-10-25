package com.swastikalabs.adshandler.provider

import android.app.Activity
import android.view.ViewGroup
import com.swastikalabs.adshandler.ads.BannerAds
import com.swastikalabs.adshandler.ads.InterstitialAds
import com.swastikalabs.adshandler.ads.NativeAds
import com.swastikalabs.adshandler.model.AdsConfig
import com.swastikalabs.adshandler.provider.google.NativeAdViewFactory

/**
 * Provider abstraction. The factory depends only on this contract.
 * Native ads require a host-supplied view factory because apps control their visual style.
 */
interface AdsProvider {
    fun createInterstitial(activity: Activity, config: AdsConfig): InterstitialAds
    fun createBanner(activity: Activity, container: ViewGroup, config: AdsConfig): BannerAds

    fun createNative(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig,
        adViewFactory: NativeAdViewFactory
    ): NativeAds
}
