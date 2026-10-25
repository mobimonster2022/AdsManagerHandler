package com.swastikalabs.adshandler.model

sealed interface InterstitialResult {
    data object Dismissed : InterstitialResult
    data object NotAvailable : InterstitialResult
    data class Failed(val message: String? = null) : InterstitialResult
}
