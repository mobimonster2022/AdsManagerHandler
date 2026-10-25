package com.swastikalabs.adshandler

/**
 * Optional convenience facade for an Activity/screen.
 * Use the separate factory methods when you only need banners or interstitials.
 */
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
