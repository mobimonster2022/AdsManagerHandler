package com.swastikalabs.adshandler

/**
 * Provider configuration. Replace test IDs with your own production ad unit IDs
 * before releasing your application.
 */
data class AdsConfig(
    val bannerAdUnitId: String,
    val interstitialAdUnitId: String,
    val requestTestAds: Boolean = true
)
