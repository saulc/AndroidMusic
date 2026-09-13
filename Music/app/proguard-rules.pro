# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/saul/Dev/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Prevent WorkManager classes from being stripped or obfuscated
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# Prevent Room database components from being stripped
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Keep SQLite and database support classes
-keep class androidx.sqlite.** { *; }
-dontwarn androidx.sqlite.**
