plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}

tasks.register<Exec>("validateDataset") {
    group = "verification"
    description = "Validates the integrity of the Al-Qur'an dataset (114 surahs, 6,236 ayahs, 30 juz)"
    commandLine("python3", "${rootDir}/scripts/validate_dataset.py")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
