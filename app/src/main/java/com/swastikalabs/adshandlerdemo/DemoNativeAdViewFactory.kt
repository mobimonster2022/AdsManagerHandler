package com.swastikalabs.adshandlerdemo

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.swastikalabs.adshandler.provider.google.NativeAdViewFactory

/** Minimal programmatic native-ad layout for the demo. Customize for production UI. */
class DemoNativeAdViewFactory(private val context: Context) : NativeAdViewFactory {
     override fun create(ad: NativeAd): NativeAdView {
        val adView = NativeAdView(context)
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 16, 24, 16)
            setBackgroundColor(Color.rgb(245, 245, 245))
        }
        val headline = TextView(context).apply {
            textSize = 18f
            setTextColor(Color.BLACK)
        }
        val body = TextView(context).apply {
            textSize = 14f
            setTextColor(Color.DKGRAY)
        }
        val advertiser = TextView(context).apply {
            textSize = 12f
            setTextColor(Color.DKGRAY)
        }
        val callToAction = TextView(context).apply {
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(25, 95, 180))
            gravity = Gravity.CENTER
            setPadding(18, 10, 18, 10)
        }
        val media = MediaView(context)
        card.addView(TextView(context).apply {
            text = "Ad"
            textSize = 11f
            setTextColor(Color.DKGRAY)
        })
        card.addView(headline)
        card.addView(advertiser)
        card.addView(body)
        card.addView(media, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 180
        ))
        card.addView(callToAction)
        adView.addView(card)

        adView.headlineView = headline
        adView.bodyView = body
        adView.advertiserView = advertiser
        adView.callToActionView = callToAction
        adView.mediaView = media

        headline.text = ad.headline.orEmpty()
        body.text = ad.body.orEmpty()
        body.visibility = if (ad.body.isNullOrBlank()) View.GONE else View.VISIBLE
        advertiser.text = ad.advertiser.orEmpty()
        advertiser.visibility = if (ad.advertiser.isNullOrBlank()) View.GONE else View.VISIBLE
        callToAction.text = ad.callToAction.orEmpty()
        callToAction.visibility = if (ad.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE
        adView.setNativeAd(ad)
        return adView
    }
}