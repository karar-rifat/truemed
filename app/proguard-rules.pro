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

########################################
# keep model classes
 -keep class com.example.pillreminder.model.**{*;}
 -keep class com.example.pillreminder.helper.**{*;}
 -keep class com.example.pillreminder.network.request.**{*;}
 -keep class com.example.pillreminder.network.response.**{*;}
 -keep class com.example.pillreminder.interfaces.**{*;}
 -keep class com.example.pillreminder.database.**{*;}
 -keep class com.example.pillreminder.network.apiservice.**{*;}
 -keep class com.example.pillreminder.network.repository.**{*;}
 -keep class com.example.pillreminder.network.**{*;}
 -keep class com.example.pillreminder.service.**{*;}

 -keepclassmembers class com.example.pillreminder.model.** { <fields>; }

 # Retrofit2
 -keepclasseswithmembers class * {
     @retrofit2.http.* <methods>;
 }
 -keepclassmembernames interface * {
     @retrofit2.http.* <methods>;
 }

 # GSON Annotations
 -keepclassmembers,allowobfuscation class * {
   @com.google.gson.annotations.SerializedName <fields>;
 }

 # room
 -keep class * extends androidx.room.RoomDatabase
 -dontwarn androidx.room.paging.**

 # keep widgets
 -keep class androidx.appcompat.widget.** { *; }

 # Validation lib
 -keep class com.mobsandgeeks.saripaar.** {*;}
 -keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}

 # pdf viewer
 -keep class com.shockwave.**

-dontwarn com.fasterxml.jackson.**
  -dontwarn com.zhihu.matisse.**
  -dontwarn kotlinx.coroutines.debug.**

#jackson
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Uncomment for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#itext pdf
-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.**

-keep class com.itextpdf.text.** { *; }
-dontwarn com.itextpdf.text.**

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembernames class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# These classes are only required by kotlinx.coroutines.debug.AgentPremain, which is only loaded when
# kotlinx-coroutines-core is used as a Java agent, so these are not needed in contexts where ProGuard is used.
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.SignalHandler
-dontwarn java.lang.instrument.Instrumentation
-dontwarn sun.misc.Signa

###### From https://github.com/square/retrofit#r8--proguard:
# Retrofit does reflection on generic parameters and InnerClass is required to use Signature.
-keepattributes Signature, InnerClasses

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions

###### From https://github.com/square/okhttp#r8--proguard:
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform