import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.trojanplanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trojanplanner"
        minSdk = 27
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
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += "mockito-extensions/org.mockito.plugins.MockMaker"
        }
    }
}

dependencies {
    implementation(libs.camera.core)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.zxing.android.embedded)
    implementation(libs.core)
    implementation(libs.camera.camera2)
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")  // OkHttp version
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.activity)


// Test dependencies
    testImplementation ("org.robolectric:robolectric:4.9")
    testImplementation("org.mockito:mockito-core:4.11.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation("org.mockito:mockito-android:4.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude(group = "com.google.protobuf", module = "protobuf-lite") // Exclude protobuf-lite
    }
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.3")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.fragment:fragment-testing:1.3.6")
    androidTestImplementation("org.mockito:mockito-android:4.0.0")
    androidTestImplementation("com.linkedin.dexmaker:dexmaker-mockito:2.28.1")

    // Firebase dependencies
    implementation(libs.barcode.scanning)
    implementation(libs.common)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.appcheck)
    implementation("com.google.firebase:firebase-messaging:23.0.0")

    // UI dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")

    // Unit testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
