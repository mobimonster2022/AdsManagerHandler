package com.swastikalabs.adshandler.manager

import com.swastikalabs.adshandler.ads.BannerAds
import com.swastikalabs.adshandler.ads.InterstitialAds
import com.swastikalabs.adshandler.callback.InterstitialCallback

class ActivityAdsManager internal constructor(
    private val interstitial: InterstitialAds,
    private val banner: BannerAds?
) {
    fun showAds(callback: InterstitialCallback) = interstitial.showAds(callback)
    fun hideAds() = interstitial.hideAds()
    fun isInterstitialVisible(): Boolean = interstitial.isInterstitialVisible()

    fun showBanner() = banner?.showBanner()
    fun hideBanner() = banner?.hideBanner()
    fun isBannerVisible(): Boolean = banner?.isBannerVisible() ?: false

    fun destroy() {
        interstitial.destroy()
        banner?.destroy()
    }
}
