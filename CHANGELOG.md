## 4.0.0 (Unreleased)

Android:
* **Breaking:** `minSdk` is now 24.
* **Breaking:** the app must have `android.enableJetifier=true` (needed by the reader's
  bookmark/highlight lists).
* FolioReader is now built from sources inside the plugin instead of the
  `com.github.FIINONE:vocsy_epub_viewer_android_folioreader` JitPack artifact.
* r2-shared/r2-streamer 1.0.4-2 are bundled in `android/maven`; JitPack and JCenter are no longer
  used.
* Release builds still need code shrinking disabled (`isMinifyEnabled = false`); the bundled
  R8 rules (`android/consumer-rules.pro`) are partial and do not cover R8 full mode.
* Reader activities are no longer exported; unused `READ_MEDIA_*` permissions and
  `requestLegacyExternalStorage` were removed from the manifest.
* Builds with AGP 8 / Kotlin 2.x and relies on Flutter to apply the Kotlin Gradle plugin.
* Removed the unused bundled `3.epub` asset and 12 empty font files.

## 3.0.0
* LATEST ANDROID VERSION SUPPORT!

## 2.0.0
* LATEST ANDROID VERSION SUPPORT!
* ANDROID 13 SUPPORTED !
* STABLE VERSION 3.7.X

## 1.0.1
* IOS ISSUE FIXED!!
* LATEST ANDROID VERSION SUPPORT!
* ANDROID 12 SUPPORTED !
* STABLE VERSION

## 1.0.0
* README UPDATE

## 0.0.8

* Bug Fixed for Locator in android

## 0.0.7

* Bug Fixed for Android

## 0.0.6

* IOS ISSUE FIXED!!
* LATEST IOS VERSION SUPPORT!
* ANDROID 12 SUPPORTED !

## 0.0.5

* Bug Fixed

## 0.0.4

* Bug Fixed

## 0.0.3

* Bug Fixed

## 0.0.2

* Bug Fixed

## 0.0.1

* initial release.
