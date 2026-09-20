plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

tasks.register<Exec>("validateHadithDataset") {
    group = "verification"
    description = "Validates the integrity of the Hadith dataset"
    commandLine("python3", "${rootDir}/scripts/validate_hadith_dataset.py")
}

tasks.register<Exec>("validatePrayerDataset") {
    group = "verification"
    description = "Validates the integrity of the Bacaan Sholat dataset"
    commandLine("python3", "${rootDir}/scripts/validate_prayer_dataset.py")
}

tasks.register<Exec>("validateDataset") {
    group = "verification"
    description = "Validates the integrity of the Al-Qur'an dataset (114 surahs, 6,236 ayahs, 30 juz)"
    commandLine("python3", "${rootDir}/scripts/validate_dataset.py")
    dependsOn("validateHadithDataset", "validatePrayerDataset")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
