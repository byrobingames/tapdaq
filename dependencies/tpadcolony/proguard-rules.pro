# Proguard configurations in this file get packaged up in the AAR file and
# merged with the application proguard rules.

#AdColony
# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep ADCNative class members unobfuscated
-keepclassmembers class com.adcolony.sdk.ADCNative** {
    *;
}

# For removing warnings due to lack of Multi-Window support
-dontwarn android.app.Activity

