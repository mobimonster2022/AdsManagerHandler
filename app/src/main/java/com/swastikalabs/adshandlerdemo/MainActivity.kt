package com.swastikalabs.adshandlerdemo

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.swastikalabs.adshandler.ads.NativeAds
import com.swastikalabs.adshandler.callback.InterstitialCallback
import com.swastikalabs.adshandler.callback.NativeAdCallback
import com.swastikalabs.adshandler.factory.AdsFactory
import com.swastikalabs.adshandler.model.AdsConfig
import com.swastikalabs.adshandler.model.InterstitialResult
import com.swastikalabs.adshandler.provider.AdsProvider
import com.swastikalabs.adshandler.provider.google.GoogleMobileAdsProvider

class MainActivity : ComponentActivity() {
    private lateinit var adsObject: com.swastikalabs.adshandler.manager.ActivityAdsManager
    private var nativeAds: NativeAds? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(32, 32, 32, 24)
        }
        val title = TextView(this).apply { text = "AdsManagerHandler Demo"; textSize = 22f }
        val status = TextView(this).apply {
            text = "Ready"
            textSize = 16f
            setPadding(0, 12, 0, 12)
        }
        val bannerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val blogHeading = TextView(this).apply {
            text = "Sample blog article"
            textSize = 20f
            setPadding(0, 20, 0, 8)
        }
        val blogBeforeAd = TextView(this).apply {
            text = "This is sample blog content. A native ad can be placed between article paragraphs."
            textSize = 16f
            setPadding(0, 0, 0, 12)
        }
        val nativeAdContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = android.view.View.GONE
        }
        val blogAfterAd = TextView(this).apply {
            text = "The article continues here, after the in-content ad placement."
            textSize = 16f
            setPadding(0, 12, 0, 12)
        }
        val showInterstitial = Button(this).apply { text = "Show interstitial" }
        val showBanner = Button(this).apply { text = "Show banner" }
        val hideBanner = Button(this).apply { text = "Hide banner" }
        val showNative = Button(this).apply { text = "Load/show in-page native ad" }
        val hideNative = Button(this).apply { text = "Hide in-page native ad" }
        val checkVisibility = Button(this).apply { text = "Check visibility" }

        root.addView(title)
        root.addView(status)
        root.addView(showInterstitial)
        root.addView(showBanner)
        root.addView(hideBanner)
        root.addView(blogHeading)
        root.addView(blogBeforeAd)
        root.addView(nativeAdContainer, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(blogAfterAd)
        root.addView(showNative)
        root.addView(hideNative)
        root.addView(checkVisibility)
        root.addView(bannerContainer, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        setContentView(root)

        val config = AdsConfig(
            bannerAdUnitId = "ca-app-pub-3940256099942544/6300978111",
            interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712",
            nativeAdUnitId = "ca-app-pub-3940256099942544/2247696110"
        )
        val provider: AdsProvider = GoogleMobileAdsProvider()
        adsObject = AdsFactory.createForActivity(
            activity = this,
            config = config,
            provider = provider,
            bannerContainer = bannerContainer
        )
        nativeAds = AdsFactory.createNative(
            activity = this,
            container = nativeAdContainer,
            config = config,
            provider = provider,
            adViewFactory = DemoNativeAdViewFactory(this)
        ).also { native ->
            native.setCallback(NativeAdCallback { message ->
                runOnUiThread {
                    status.text = "Native ad failed: ${message.orEmpty()}"
                    nativeAdContainer.visibility = android.view.View.GONE
                }
            })
        }

        showInterstitial.setOnClickListener {
            status.text = "Waiting for ad result…"
            adsObject.showAds(InterstitialCallback { result ->
                runOnUiThread {
                    status.text = when (result) {
                        InterstitialResult.Dismissed -> "Ad dismissed"
                        InterstitialResult.NotAvailable -> "No ad available"
                        is InterstitialResult.Failed -> "Ad failed: ${result.message.orEmpty()}"
                    }
                    Toast.makeText(this, status.text, Toast.LENGTH_SHORT).show()
                }
            })
        }
        showBanner.setOnClickListener {
            adsObject.showBanner()
            status.text = "Banner shown/requested"
        }
        hideBanner.setOnClickListener {
            adsObject.hideBanner()
            status.text = "Banner hidden"
        }
        showNative.setOnClickListener {
            nativeAdContainer.visibility = android.view.View.VISIBLE
            nativeAds?.show()
            status.text = "Native ad load requested"
        }
        hideNative.setOnClickListener {
            nativeAds?.hide()
            nativeAdContainer.visibility = android.view.View.GONE
            status.text = "Native ad hidden"
        }
        checkVisibility.setOnClickListener {
            status.text =
                "Banner: ${adsObject.isBannerVisible()} | Interstitial: ${adsObject.isInterstitialVisible()} | Native: ${nativeAds?.isVisible() == true}"
        }
    }

    override fun onDestroy() {
        nativeAds?.destroy()
        if (::adsObject.isInitialized) adsObject.destroy()
        super.onDestroy()
    }
}
