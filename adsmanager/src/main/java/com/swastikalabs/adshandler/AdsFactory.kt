package com.swastikalabs.adshandler

import android.app.Activity
import android.view.ViewGroup

/**
 * Factory entry points are deliberately separate so applications can create
 * only the ad type they need, or use an Activity-scoped combined manager.
 */
object AdsFactory {
    fun createInterstitial(
        activity: Activity,
        config: AdsConfig
    ): InterstitialAds = GoogleMobileAdsProvider().createInterstitial(activity, config)

    fun createBanner(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig
    ): BannerAds = GoogleMobileAdsProvider().createBanner(activity, container, config)

    fun createForActivity(
        activity: Activity,
        bannerContainer: ViewGroup? = null,
        config: AdsConfig
    ): ActivityAdsManager {
        val provider = GoogleMobileAdsProvider()
        return ActivityAdsManager(
            interstitial = provider.createInterstitial(activity, config),
            banner = bannerContainer?.let { provider.createBanner(activity, it, config) }
        )
    }
}
