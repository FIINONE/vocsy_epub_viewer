## 4.0.1 (2026-10-08)

Android:
* Fixed the reader toolbar showing an overflow menu (three dots) with dead buttons when the app
  also uses `flutter_inappwebview`: its `menu_main` resource replaced the reader's. Reader resources
  with generic names are now prefixed (`folio_menu_main`, `folio_menu_search`, `folio_ic_share`,
  `FolioDialogAnimation`).
* Fixed a crash of the whole app when the WebView renderer process dies (crash or out of memory):
  the reader now handles `onRenderProcessGone` and closes itself.
* Fixed 5-second UI freezes (ANR) when changing the font, adding a bookmark, changing the scroll
  direction or closing the reader: the last read position is no longer awaited on the main thread.
* The local book server now answers 404 for unknown paths instead of throwing a
  `NullPointerException`.
* Security: the local book server now listens on `127.0.0.1` only. Previously it listened on all
  network interfaces, so while the reader was open the book could be downloaded by any device on
  the same network.
* Fixed `epubClosed` reporting the position the book was opened at instead of where the user
  stopped reading. The reader now keeps the position up to date while reading (after scrolling
  settles and after changing chapters) and reports the latest one on close.
* Opening a book without `lastLocation` no longer reuses the previous book's position.
* The Android side now answers every method-channel call, so the Futures returned by
  `setConfig`/`open`/`openAsset`/`closeReader`/`setChannel`/`sendTransAndCheckWord` complete
  instead of hanging forever. `closeReader()` and `sendTransAndCheckWord()` before a book was
  opened are now no-ops instead of failing with a `NullPointerException`.
* Removed leftover sample code that loaded highlights from a bundled `highlights_data.json` on a
  background thread. It never saved anything (the file was empty and the condition inverted), but
  any exception in it crashed the app — e.g. in release builds with R8.

## 4.0.0 (2026-10-08)

Android:
* **Breaking:** `minSdk` is now 24.
* **Breaking:** the app must have `android.enableJetifier=true` (needed by the reader's
  bookmark/highlight lists).
* **Breaking:** the reader activities now declare `android:exported="false"`. Remove any
  override of them from the app's manifest (for example
  `<activity android:name="com.folioreader.ui.activity.SearchActivity" android:exported="true" tools:node="merge" />`),
  otherwise the manifest merge fails.
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
