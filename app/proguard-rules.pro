# Proguard rules for Al-Qur'an Offline
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class com.alquran.offline.data.local.entity.** { *; }
-dontwarn androidx.compose.**
