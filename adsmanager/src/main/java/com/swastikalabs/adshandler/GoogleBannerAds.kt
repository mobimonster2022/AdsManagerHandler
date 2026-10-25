package com.swastikalabs.adshandler

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

internal class GoogleBannerAds(
    private val activity: Activity,
    private val container: ViewGroup,
    private val config: AdsConfig
) : BannerAds {
    private var adView: AdView? = null
    private var visible = false
    private var destroyed = false

    init {
        createAndLoad()
    }

    private fun createAndLoad() {
        if (destroyed || activity.isFinishing || activity.isDestroyed) return
        val view = AdView(activity).apply {
            adUnitId = config.bannerAdUnitId
            setAdSize(AdSize.BANNER)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    if (!destroyed && visible) visibility = View.VISIBLE
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    visibility = View.GONE
                }
            }
        }
        adView = view
        if (view.parent is ViewGroup) (view.parent as ViewGroup).removeView(view)
        container.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        view.visibility = View.GONE
        view.loadAd(AdRequest.Builder().build())
    }

    override fun showBanner() {
        if (destroyed) return
        visible = true
        val view = adView ?: run {
            createAndLoad()
            adView
        }
        view?.visibility = View.VISIBLE
    }

    override fun hideBanner() {
        visible = false
        adView?.visibility = View.GONE
    }

    override fun isBannerVisible(): Boolean =
        visible && adView?.visibility == View.VISIBLE

    override fun destroy() {
        destroyed = true
        adView?.let { view ->
            (view.parent as? ViewGroup)?.removeView(view)
            view.destroy()
        }
        adView = null
    }
}
