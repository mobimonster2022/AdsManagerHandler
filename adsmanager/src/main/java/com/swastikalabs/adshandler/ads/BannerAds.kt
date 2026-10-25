package com.swastikalabs.adshandler.ads

interface BannerAds {
    fun showBanner()
    fun hideBanner()
    fun isBannerVisible(): Boolean
    fun destroy()
}
