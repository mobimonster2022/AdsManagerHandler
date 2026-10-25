package com.swastikalabs.adshandler.callback

import com.swastikalabs.adshandler.model.InterstitialResult

fun interface InterstitialCallback {
    fun onFinished(result: InterstitialResult)
}
