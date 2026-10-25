package com.swastikalabs.adshandler

import android.app.Activity
import android.view.ViewGroup

/**
 * Provider-neutral interfaces. A future provider can implement these without
 * changing the application's calling code.
 */
interface InterstitialAds {
    fun showAds(callback: InterstitialCallback)
    fun hideAds()
    fun isInterstitialVisible(): Boolean
    fun destroy()
}

interface BannerAds {
    fun showBanner()
    fun hideBanner()
    fun isBannerVisible(): Boolean
    fun destroy()
}

interface AdsProvider {
    fun createInterstitial(activity: Activity, config: AdsConfig): InterstitialAds
    fun createBanner(
        activity: Activity,
        container: ViewGroup,
        config: AdsConfig
    ): BannerAds
}
