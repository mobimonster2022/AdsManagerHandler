package com.swastikalabs.adshandler

import android.app.Activity
import android.view.ViewGroup

internal class GoogleMobileAdsProvider : AdsProvider {
    override fun createInterstitial(
        activity: Activity,
        config: AdsConfig
    ): InterstitialAds = GoogleInterstitialAds(activity, config)

    override fun createBanner(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig
    ): BannerAds = GoogleBannerAds(activity, container, config)
}
