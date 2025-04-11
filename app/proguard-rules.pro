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

# --- General Android rules ---
-keep class androidx.** { *; }
-keep class com.google.android.material.** { *; }

# --- ViewModel ---
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# --- LiveData ---
-keepclassmembers class androidx.lifecycle.LiveData { *; }

# --- Room (para que pueda generar correctamente las clases) ---
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase

# --- Coroutines ---
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# --- Retrofit (si usás) ---
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# --- GSON (si lo usás para parseo) ---
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# --- Hilt (si estás usando Dagger Hilt) ---
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
-keep class * extends androidx.lifecycle.ViewModel

# --- Tu app (si querés mantener nombres legibles en ciertas clases) ---
# Ejemplo: no ofuscar esta clase
# -keep class com.tuapp.ui.LoginActivity { *; }

# --- Evita remover clases que usás por reflexión (si las hay) ---
# -keepclassmembers class com.tuapp.** {
#     *;
# }

# --- Logging (opcional: remueve los logs en release) ---
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Conservar todas las clases de ViewBinding
-keep class **.databinding.*Binding { *; }
-keepclassmembers class **.databinding.*Binding { *; }

# O si usás ViewBinding sin databinding
-keep class *Binding { *; }

# No ofuscar clases R (resources)
-keepclassmembers class **.R$* {
    public static <fields>;
}

