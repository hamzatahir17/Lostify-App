// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // Now we use the alias we just created in libs.versions.toml
    alias(libs.plugins.google.services) apply false
}