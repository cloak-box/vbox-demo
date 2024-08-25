pluginManagement {
  repositories {
    google()
    mavenCentral()
    maven ("https://jitpack.io")
    maven ( "https://developer.huawei.com/repo" )
    maven ( "https://developer.hihonor.com/repo" )
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenLocal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven ("https://jitpack.io")
    maven ( "https://developer.huawei.com/repo" )
    maven ( "https://developer.hihonor.com/repo" )
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenLocal()
  }
}

rootProject.name = "VsystemDemo"
include(":app")
include(":fake_api")
include(":pine-xposed-res")
include(":xui_lib")
