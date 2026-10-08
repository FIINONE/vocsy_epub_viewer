# R8/ProGuard rules shipped to every app that depends on this plugin (consumerProguardFiles).
# They only matter when the app enables minification (isMinifyEnabled = true).

# Generic signatures and runtime annotations are read by Retrofit, Gson, Jackson and EventBus.
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*, AnnotationDefault

# ---------------------------------------------------------------------------------------------
# FolioReader
# ---------------------------------------------------------------------------------------------

# Models are (de)serialized by Jackson by property name (ReadLocator, dictionary/Wikipedia
# responses) and passed around as Parcelable/Serializable.
-keep class com.folioreader.model.** { *; }

# Retrofit API to the local r2-streamer server. @Gson/@Jackson are custom annotations read at
# runtime by QualifiedTypeConverterFactory to pick a converter.
-keep interface com.folioreader.network.R2StreamerApi { *; }
-keep @interface com.folioreader.network.Gson
-keep @interface com.folioreader.network.Jackson

# Methods called from JavaScript (FolioWebView, FolioPageFragment, WebViewPager, LoadingView).
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Private AndroidX fields looked up by name (FolioPageFragmentAdapter, SearchActivity).
-keepclassmembers class androidx.fragment.app.FragmentStatePagerAdapter {
    *** mSavedState;
}
-keepclassmembers class androidx.fragment.app.Fragment$SavedState {
    *** mState;
}
-keepclassmembers class androidx.appcompat.widget.Toolbar {
    *** mCollapseIcon;
}

# ---------------------------------------------------------------------------------------------
# r2-streamer / r2-shared and the local HTTP server
# ---------------------------------------------------------------------------------------------

# nanohttpd's router instantiates route handlers via Class.newInstance(), and the
# Publication/Locator models are (de)serialized by field name with Jackson and Gson.
-keep class org.readium.r2.** { *; }

# The org.nanohttpd.* shim (plugin sources) that r2-streamer was compiled against, and
# nanohttpd 2.3.1 behind it.
-keep class org.nanohttpd.** { *; }
-keep class fi.iki.elonen.** { *; }

# ---------------------------------------------------------------------------------------------
# Third-party libraries
# ---------------------------------------------------------------------------------------------

# EventBus 3.1: subscriber methods are found by annotation at runtime.
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Jackson databind references JDK classes that do not exist on Android (optional support code).
-dontwarn java.beans.**
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

# Retrofit 2.5 / OkHttp 3 / Okio (these versions do not ship their own consumer rules).
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# spring-core is only used for ReflectionUtils; the rest of it references optional
# dependencies that are not on the classpath.
-dontwarn org.springframework.**

# joda-time references optional org.joda.convert annotations.
-dontwarn org.joda.convert.**

# zt-zip logs through slf4j-api, which looks up an optional binding class at runtime and falls
# back to a no-op logger when none is present (no binding is bundled on purpose).
-dontwarn org.slf4j.impl.StaticLoggerBinder

# koi (used by r2-streamer for HASH.sha1) has helpers for the legacy support library that
# r2 never calls; its support-v4 dependency is excluded in the vendored POM.
# Note: SwipeLayout (com.daimajia.swipelayout) also references android.support.* classes and
# really uses them, so it only works when the app has android.enableJetifier=true, which
# rewrites those references to AndroidX. This rule does not change that.
-dontwarn android.support.**
