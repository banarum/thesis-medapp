# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-verbose
-keep class ru.terrakok.** { *; }

-dontwarn org.joda.convert.*

# Bottom navigation reflection
-keepclassmembers class android.support.design.internal.BottomNavigationMenuView {
    boolean mShiftingMode;
}

-dontwarn android.net.http.SslError

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.google.android.gms.**

#jar warnings
-keepattributes EnclosingMethod
#-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.google.android.gms.** { *; }
-dontwarn java.lang.invoke**
-dontwarn java.lang.reflect.Method

# Gson
-keep public class com.google.gson
-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.Unsafe
-keepclassmembers enum ru.burgerking.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.koenigmed.luomanager.data.model.** { *; }
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Crashlytics
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**

# Retrofit2
-dontwarn okio.**
-dontwarn javax.annotation.**

-keepattributes Signature
-keepattributes Exceptions

#SearchView
-keep class android.support.v7.widget.SearchView { *; }

# Google map
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

#WebView
-keepattributes JavascriptInterface

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
-dontwarn com.bumptech.glide.load.resource.bitmap.Downsampler
-dontwarn com.bumptech.glide.load.resource.bitmap.HardwareConfigState
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# To fix warning: InnerClass annotations are missing
# corresponding EnclosingMember annotations.
-keep @**annotation** class * {*;}