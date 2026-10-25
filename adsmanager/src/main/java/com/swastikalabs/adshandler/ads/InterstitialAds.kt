package com.swastikalabs.adshandler.ads

import com.swastikalabs.adshandler.callback.InterstitialCallback

interface InterstitialAds {
    fun showAds(callback: InterstitialCallback)
    fun hideAds()
    fun isInterstitialVisible(): Boolean
    fun destroy()
}
