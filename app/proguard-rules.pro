# Proguard rules for Al-Qur'an Offline
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Room database & DAOs
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.alquran.offline.data.local.entity.** { *; }
-keep class com.alquran.offline.data.local.dao.** { *; }
-keep class com.alquran.offline.model.** { *; }

# Keep Android components
-keep class com.alquran.offline.QuranApplication { *; }
-keep class com.alquran.offline.ui.MainActivity { *; }
-keep class com.alquran.offline.receiver.** { *; }
-keep class com.alquran.offline.notification.** { *; }

# Compose and Coroutines
-dontwarn androidx.compose.**
-keepattributes SourceFile,LineNumberTable
