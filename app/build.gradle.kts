plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.project.aerotrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.aerotrack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    //Retrofit
    implementation (libs.retrofit)
    //Gson
    implementation (libs.converter.gson)
    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Coroutine support for ViewModelScope
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.locationtech.proj4j:proj4j:1.2.2")
    implementation ("org.locationtech.proj4j:proj4j-epsg:1.2.2")
    //Firebase
    implementation (platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-auth:23.2.0")
    implementation ("com.google.firebase:firebase-storage-ktx:21.0.1")
    implementation ("com.google.firebase:firebase-database-ktx:19.2.1")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.google.firebase:firebase-firestore-ktx")
}