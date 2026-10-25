package com.swastikalabs.adshandler

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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
        initializeAndPreload()
    }

    private fun initializeAndPreload() {
        val activity = activityRef.get() ?: return
        MobileAds.initialize(activity) {
            preload()
        }
    }

    @Synchronized
    private fun preload() {
        val activity = activityRef.get() ?: return
        if (destroyed || loading || interstitialAd != null) return
        loading = true
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            config.interstitialAdUnitId,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    loading = false
                    if (destroyed) return
                    interstitialAd = ad
                    // If showAds() was waiting for this load, display the ad now.
                    val callback = synchronized(this@GoogleInterstitialAds) {
                        pendingCallback.also { pendingCallback = null }
                    }
                    if (callback != null) {
                        showAds(callback)
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    interstitialAd = null
                    Log.w(TAG, "Interstitial failed to load: ${error.message}")
                    // Do not retry in a tight loop. The next showAds() request
                    // can initiate a fresh load.
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
            val ready = interstitialAd
            if (ready != null && !visible) {
                interstitialAd = null
                visible = true
                ready
            } else {
                null
            }
        }

        if (ad == null) {
            val alreadyLoading = synchronized(this) {
                if (loading) {
                    pendingCallback = callback
                    true
                } else {
                    pendingCallback = callback
                    false
                }
            }
            if (!alreadyLoading) preload()
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
        } catch (t: Throwable) {
            visible = false
            finish(InterstitialResult.Failed(t.message))
            preload()
        }
    }

    override fun hideAds() {
        // Ad SDKs generally do not permit programmatically dismissing an
        // already-presented interstitial. This suppresses future displays.
        suppressed = true
    }

    fun allowAds() {
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
        const val TAG = "AdsManager"
    }
}
