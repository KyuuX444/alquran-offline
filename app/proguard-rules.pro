# Proguard rules for Al-Qur'an Offline
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Keep Room database & DAOs
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.alquran.offline.data.local.AppDatabase { *; }
-keep class com.alquran.offline.data.local.AppDatabase_Impl { *; }
-keep class com.alquran.offline.data.local.entity.** { *; }
-keep class com.alquran.offline.data.local.dao.** { *; }
-keep class com.alquran.offline.model.** { *; }

# Keep DataStore Preferences
-keep class androidx.datastore.preferences.core.** { *; }
-keep class com.alquran.offline.data.preferences.** { *; }

# Keep ViewModels and Repositories
-keep class com.alquran.offline.data.repository.** { *; }
-keep class com.alquran.offline.ui.screens.**ViewModel* { *; }
-keepclassmembers class com.alquran.offline.ui.screens.**ViewModel* {
    <init>(...);
}

# Keep Android components
-keep class com.alquran.offline.QuranApplication { *; }
-keep class com.alquran.offline.ui.MainActivity { *; }
-keep class com.alquran.offline.receiver.** { *; }
-keep class com.alquran.offline.notification.** { *; }

# Compose and Coroutines
-dontwarn androidx.compose.**
-dontwarn kotlinx.coroutines.**

# Keep Provenance & Project Identity (watermark preservation under R8)
-keep class com.alquran.offline.provenance.** { *; }
-keepclassmembers class com.alquran.offline.provenance.** { *; }
