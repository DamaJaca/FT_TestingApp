buildscript {
    dependencies {
        classpath(libs.google.services)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    //SafeArgs
    id ("androidx.navigation.safeargs.kotlin") version "2.7.7" apply false


    //Dagger Hilt
    id ("com.google.dagger.hilt.android") version "2.51.1" apply false

    
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}
