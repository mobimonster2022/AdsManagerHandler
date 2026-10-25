package com.swastikalabs.adshandler.model

data class AdsConfig(
    val bannerAdUnitId: String,
    val interstitialAdUnitId: String,
    /** Optional Google Mobile Ads native ad unit ID. */
    val nativeAdUnitId: String = ""
)
