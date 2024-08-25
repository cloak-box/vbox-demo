import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false
  alias(libs.plugins.jetbrains.kotlin.jvm) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.androidx.baselineprofile) apply false
}

buildscript {
  dependencies {
    classpath(libs.huawei.agconnect.agcp)
    classpath(libs.android.tools.build.gradle)
    classpath(libs.cloak.box.plugin.proguardConfig)
    classpath(libs.cloak.box.plugin.maven.api.plugin)
  }
}

ext["java_version"] = JavaVersion.VERSION_1_8
ext["vbox_sdk_version"] = vboxSdkVersion()

fun vboxSdkVersion(): String {
  val inputStream = File("${rootProject.projectDir}/app/vbox_sdk_version.properties").inputStream()
  val properties = Properties()
  properties.load(inputStream)
  inputStream.close()
  return properties.getProperty("vbox_sdk_version")
}
