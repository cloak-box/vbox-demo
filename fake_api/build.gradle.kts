plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
}

apply { from("../common_config.gradle") }

android {
  namespace = "com.black.cat.vsystem.fake.api"
  aidlPackagedList("android/database/IContentObserver.aidl")
  aidlPackagedList("android/content/IContentProvider.aidl")
  aidlPackagedList("android/accounts/IAccountAuthenticator.aidl")
  aidlPackagedList("android/accounts/IAccountAuthenticatorResponse.aidl")
  aidlPackagedList("android/accounts/IAccountManagerResponse.aidl")
  aidlPackagedList("android/content/pm/IPackageStatsObserver.aidl")
  defaultConfig { buildFeatures { aidl = true } }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

