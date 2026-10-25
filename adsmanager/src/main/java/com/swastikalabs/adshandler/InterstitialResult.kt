package com.swastikalabs.adshandler

sealed interface InterstitialResult {
    /** The user dismissed the full-screen ad. */
    data object Dismissed : InterstitialResult

    /** No ad was ready, or the provider reported that no ad could be loaded. */
    data object NotAvailable : InterstitialResult

    /** A provider error occurred while loading or showing the ad. */
    data class Failed(val message: String? = null) : InterstitialResult
}

fun interface InterstitialCallback {
    fun onFinished(result: InterstitialResult)
}
