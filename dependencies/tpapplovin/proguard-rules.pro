# Proguard configurations in this file get packaged up in the AAR file and
# merged with the application proguard rules.

#Applovin
-dontwarn com.applovin.**
-keep class com.applovin.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }
