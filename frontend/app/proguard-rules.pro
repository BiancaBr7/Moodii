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

# Retrofit / OkHttp / Gson
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep interface okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Keep model/data classes used by Gson (prevent obfuscation of serialized names)
-keep class com.example.moodii.api.ml.** { *; }
-keep class com.example.moodii.data.** { *; }
-keep class com.example.moodii.api.moodlog.** { *; }

# Coroutines debug metadata
-keepclassmembers class kotlinx.coroutines.DebugStringsKt { *; }

# Suppress warnings for Java 11 APIs possibly used by dependencies
-dontwarn java.lang.invoke.*

# TensorFlow / Librosa not directly packaged (server side) so minimal rules here
