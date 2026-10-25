package com.swastikalabs.adshandler.provider.google

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.swastikalabs.adshandler.ads.NativeAds
import com.swastikalabs.adshandler.callback.NativeAdCallback
import com.swastikalabs.adshandler.model.AdsConfig

/**
 * Google Mobile Ads native implementation.
 *
 * A host-provided NativeAdViewFactory builds a compliant NativeAdView using
 * the loaded NativeAd. This keeps the library independent of the app's visual design.
 */
class GoogleNativeAds(
    private val activity: Activity,
    private val container: ViewGroup,
    private val config: AdsConfig,
    private val adViewFactory: NativeAdViewFactory
) : NativeAds {
    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var callback: NativeAdCallback? = null
    private var visible = false
    private var destroyed = false

    override fun setCallback(callback: NativeAdCallback?) {
        this.callback = callback
    }

    override fun load() {
        if (destroyed || activity.isFinishing || activity.isDestroyed) return
        val unitId = config.nativeAdUnitId
        if (unitId.isBlank()) {
            callback?.onAdFailed("Native ad unit ID is empty.")
            return
        }
        val loader = AdLoader.Builder(activity, unitId)
            .forNativeAd { loadedAd ->
                if (destroyed || activity.isFinishing || activity.isDestroyed) {
                    loadedAd.destroy()
                    return@forNativeAd
                }
                nativeAd?.destroy()
                nativeAd = loadedAd
                renderLoadedAd()
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    callback?.onAdFailed(error.message)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
    }

    private fun renderLoadedAd() {
        val ad = nativeAd ?: return
        val view = try {
            adViewFactory.create(ad)
        } catch (e: Exception) {
            callback?.onAdFailed(e.message ?: "Could not create NativeAdView.")
            return
        }
        nativeAdView = view
        container.removeAllViews()
        (view.parent as? ViewGroup)?.removeView(view)
        container.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun show() {
        if (destroyed) return
        visible = true
        nativeAdView?.visibility = View.VISIBLE
        if (nativeAd == null) load()
    }

    override fun hide() {
        visible = false
        nativeAdView?.visibility = View.GONE
    }

    override fun isVisible(): Boolean = visible && nativeAdView?.visibility == View.VISIBLE

    override fun destroy() {
        destroyed = true
        container.removeView(nativeAdView)
        nativeAdView = null
        nativeAd?.destroy()
        nativeAd = null
        callback = null
    }
}

/** Creates the visual layout and registers assets with Google Mobile Ads. */
fun interface NativeAdViewFactory {
    fun create(ad: NativeAd): NativeAdView
}