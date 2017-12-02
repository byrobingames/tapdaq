# Proguard configurations in this file get packaged up in the AAR file and
# merged with the application proguard rules.

#HyprMX
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class com.hyprmx.** {*;}
-keep class okhttp3.hyprmx.** { *; }
-keep interface okhttp3.hyprmx.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontwarn okhttp3.hyprmx.**
-dontwarn okio.hyprmx.**
-dontwarn com.google.android.gms.ads.identifier.**
