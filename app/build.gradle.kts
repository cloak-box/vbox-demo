plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
  id("com.black.cat.plugin.ProguardRrule")
}

apply {
  from("signing.gradle")
  from("../common_config.gradle")
  from("agconnect-plugin.gradle")
}

android {
  namespace = "com.black.cat.system.demo"

  defaultConfig {
    applicationId = "com.black.cat.system.demo"
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = 1015
    versionName = "1.0.15"
    ndk {
      abiFilters.add("armeabi-v7a")
      abiFilters.add("arm64-v8a")
    }
    ksp { arg("moshi.generateProguardRules", "false") }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      signingConfig = signingConfigs.maybeCreate("debug")
      buildConfigField("boolean", "enable_pine_hook", "false")
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    debug {
      buildConfigField("boolean", "enable_pine_hook", "true")
      signingConfig = signingConfigs.maybeCreate("debug") }
  }
  buildFeatures {
    viewBinding = true
    buildConfig = true
  }
  packaging { jniLibs { useLegacyPackaging = true } }
}



dependencies {
  implementation(libs.core.ktx)
  implementation(libs.appcompat)
  implementation(libs.material)
  compileOnly(project(":fake_api"))
  implementation(project(":pine-xposed-res"))
  implementation(libs.constraintlayout)
  // coroutines
  implementation(libs.kotlinx.coroutines.android)

  // viewModel liveData  lifecycle
  implementation(libs.lifecycle.viewmodel.ktx)
  implementation(libs.lifecycle.livedata.ktx)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.profileinstaller)
  //  implementation("com.github.xuexiangjys:XUI:1.2.1")
  implementation(project(":xui_lib"))
  // 工具类
  implementation(libs.xutil.core)
  implementation(libs.xutil.sub)
  // 下拉刷新
  implementation(libs.refresh.layout.kernel) // 核心必须依赖
  implementation(libs.refresh.header.classics) // 经典加载
  implementation(libs.agentweb.core)
  implementation(libs.agentweb.filechooser) // (可选)
  implementation(libs.downloader) // (可选)
  implementation(libs.palette.ktx)
  implementation(libs.mmkv)
  implementation("com.github.bumptech.glide:glide:4.11.0") { exclude("com.android.support") }

  implementation(libs.retrofit)
  implementation(libs.moshi)
  implementation("com.squareup.retrofit2:converter-moshi:2.11.0") {
    exclude(group = "com.squareup.moshi", module = "moshi")
  }
  implementation(libs.logging.interceptor)
  implementation(libs.android.cn.oaid)
  implementation(libs.org.greenrobot.eventbus)
  implementation(libs.nestedscrollwebview)
  implementation(libs.cloak.box.library.objectSign.annotation)
  ksp(libs.moshi.kotlin.codegen)
  ksp(libs.cloak.box.ksp.objectSign)
  ksp(libs.cloak.box.ksp.moshi.codeGen)
  implementation(libs.work.runtime)
  implementation(libs.work.runtime.ktx)
  implementation(libs.work.multiprocess)
  implementation(libs.xxpermissions)
  implementation(libs.top.canyie.pine.core)
  implementation(libs.top.canyie.pine.xposed)
  implementation(libs.cloak.box.sdk.core)
  implementation(libs.cloak.box.sdk.dexopt)
}

R8proguardConfig{
  replaceConfig{
    this["/transformed/jetified-core-0.3.0/proguard.txt"]= File("${project.projectDir.absolutePath}/pine.pro").absolutePath
    this["/transformed/core-0.3.0/proguard.txt"]= File("${project.projectDir.absolutePath}/pine.pro").absolutePath
    this["/transformed/rules/lib/META-INF/proguard/moshi.pro"]=File("${project.projectDir.absolutePath}/moshi.pro").absolutePath
  }
}