package com.swastikalabs.adshandler.callback

/** Receives native-ad loading/display errors. */
fun interface NativeAdCallback {
    fun onAdFailed(message: String?)
}