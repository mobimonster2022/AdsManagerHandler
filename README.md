# AdsManagerHandler

A source-based Android project containing a reusable ad-handler library and a demo app. The library uses a provider abstraction and factory pattern.

## Folder structure

```text
AdsManagerHandler/
├── app/                          # Demo app
├── adsmanager/                   # Reusable Android library
│   └── src/main/java/com/swastikalabs/adshandler/
│       ├── ads/                  # BannerAds, InterstitialAds contracts
│       ├── callback/             # Interstitial callback
│       ├── factory/              # Provider-neutral AdsFactory
│       ├── manager/              # ActivityAdsManager facade
│       ├── model/                # AdsConfig, InterstitialResult
│       ├── provider/             # AdsProvider contract
│       │   └── google/           # Google Mobile Ads implementation
│       └── repository/           # AdsRepository
├── gradle/libs.versions.toml
├── settings.gradle.kts
└── build.gradle.kts
```

## Provider selection

The app chooses the provider. `AdsFactory` never creates a concrete provider:

```kotlin
val provider: AdsProvider = GoogleMobileAdsProvider()
val ads = AdsFactory.createForActivity(
    activity = this,
    config = config,
    provider = provider,
    bannerContainer = bannerContainer
)
```

To add a provider, implement `AdsProvider`, `BannerAds`, and `InterstitialAds`, then inject it into the same factory. `AdsFactory` need not change.

## Interstitial callback

`showAds` reports one of:
- `Dismissed`: user closed the displayed interstitial.
- `NotAvailable`: no ad could be loaded/used.
- `Failed`: showing the ad failed.

After dismissal, the Google implementation attempts to preload the next ad.

## Notes

- Google Mobile Ads SDK version in this template is `25.5.0`, with `minSdk = 24`.
- Demo uses Google's official test app ID and test ad unit IDs. Replace these before production.
- `hideAds()` suppresses future interstitial requests; it cannot force-close an already displayed interstitial.
- The source has been generated but not built in Android Studio in this environment.


## Native in-page ads (blog/article content)

The library now supports native ads placed between content blocks, such as blog paragraphs.
The host app supplies a `ViewGroup` at the placement and a `NativeAdViewFactory` that builds
the visual `NativeAdView` and registers its asset views. This lets the app control layout while
keeping the provider selection out of `AdsFactory`.

Example:
```kotlin
val nativeAds = AdsFactory.createNative(
    activity = this,
    container = nativeAdContainer,
    config = config,
    provider = GoogleMobileAdsProvider(),
    adViewFactory = DemoNativeAdViewFactory(this)
)
nativeAds.setCallback(NativeAdCallback { error -> /* report load error */ })
nativeAds.show()
```

The demo includes Google's official native test ad unit ID. Use test ads while developing.
If blog content is rendered inside a `WebView` and the ads are already part of the website,
those webpage ads are controlled by the website and do not need this native-ad API.
