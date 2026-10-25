package com.swastikalabs.adshandler.ads

import android.view.ViewGroup
import com.swastikalabs.adshandler.callback.NativeAdCallback

/**
 * Native ad rendered inside app content, such as between blog paragraphs.
 * The host app supplies a container at the position where the ad should appear.
 */
interface NativeAds {
    fun load()
    fun show()
    fun hide()
    fun isVisible(): Boolean
    fun destroy()
    fun setCallback(callback: NativeAdCallback?)
}