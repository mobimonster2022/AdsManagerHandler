package com.swastikalabs.adshandler.provider.google

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.swastikalabs.adshandler.ads.InterstitialAds
import com.swastikalabs.adshandler.callback.InterstitialCallback
import com.swastikalabs.adshandler.model.AdsConfig
import com.swastikalabs.adshandler.model.InterstitialResult
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

internal class GoogleInterstitialAds(
    activity: Activity,
    private val config: AdsConfig
) : InterstitialAds {
    private val activityRef = WeakReference(activity)
    @Volatile private var interstitialAd: InterstitialAd? = null
    @Volatile private var loading = false
    @Volatile private var visible = false
    @Volatile private var suppressed = false
    @Volatile private var destroyed = false
    private var pendingCallback: InterstitialCallback? = null

    init {
        val appActivity = activityRef.get()
        if (appActivity != null) {
            MobileAds.initialize(appActivity) { preload() }
        }
    }

    @Synchronized
    private fun preload() {
        val activity = activityRef.get() ?: return
        if (destroyed || loading || interstitialAd != null) return
        loading = true
        InterstitialAd.load(
            activity,
            config.interstitialAdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loading = false
                    if (destroyed) return
                    interstitialAd = ad
                    val callback = synchronized(this@GoogleInterstitialAds) {
                        pendingCallback.also { pendingCallback = null }
                    }
                    if (callback != null) showAds(callback)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    interstitialAd = null
                    Log.w(TAG, "Interstitial load failed: ${error.message}")
                    val callback = synchronized(this@GoogleInterstitialAds) {
                        pendingCallback.also { pendingCallback = null }
                    }
                    callback?.onFinished(InterstitialResult.NotAvailable)
                }
            }
        )
    }

    override fun showAds(callback: InterstitialCallback) {
        val activity = activityRef.get()
        if (activity == null || destroyed || suppressed || activity.isFinishing || activity.isDestroyed) {
            callback.onFinished(InterstitialResult.NotAvailable)
            return
        }

        val ad = synchronized(this) {
            if (interstitialAd != null && !visible) {
                interstitialAd.also {
                    interstitialAd = null
                    visible = true
                }
            } else null
        }

        if (ad == null) {
            val shouldLoad = synchronized(this) {
                if (loading) {
                    pendingCallback = callback
                    false
                } else {
                    pendingCallback = callback
                    true
                }
            }
            if (shouldLoad) preload()
            return
        }

        val callbackDelivered = AtomicBoolean(false)
        fun finish(result: InterstitialResult) {
            if (callbackDelivered.compareAndSet(false, true)) callback.onFinished(result)
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                visible = false
                finish(InterstitialResult.Dismissed)
                preload()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                visible = false
                finish(InterstitialResult.Failed(error.message))
                preload()
            }

            override fun onAdShowedFullScreenContent() {
                visible = true
            }
        }

        try {
            ad.show(activity)
        } catch (error: Exception) {
            visible = false
            finish(InterstitialResult.Failed(error.message))
            preload()
        }
    }

    override fun hideAds() {
        // Google Mobile Ads doesn't expose a supported API to forcibly close an
        // already visible interstitial. Suppress subsequent requests instead.
        suppressed = true
    }

    fun enableAds() {
        suppressed = false
        preload()
    }

    override fun isInterstitialVisible(): Boolean = visible

    @Synchronized
    override fun destroy() {
        destroyed = true
        interstitialAd = null
        pendingCallback = null
        visible = false
    }

    private companion object {
        const val TAG = "AdsManagerHandler"
    }
}
