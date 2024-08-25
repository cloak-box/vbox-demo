package com.black.cat.system.demo.utils

import android.content.Context
import android.os.Parcel
import com.black.cat.system.demo.App
import java.io.File

val dataRoot = App.instance.getDir("box_data", Context.MODE_PRIVATE)

val systemInstallAppInfoListFile =
  File(dataRoot, "install_app_info.conf").apply { this.parentFile?.mkdirs() }

val xPluginsInfoListFile =
  File(dataRoot, "install_plugin_info.conf").apply { this.parentFile?.mkdirs() }

val tmpApk = File(dataRoot, "apks/tmp.apk").apply { this.parentFile?.mkdirs() }
val mmkvDir = File(dataRoot, "mmkv").apply { this.parentFile?.mkdirs() }

inline fun File.readParcel(parcel: Parcel) {
  val appConfData = readBytes()
  parcel.unmarshall(appConfData, 0, appConfData.size)
  parcel.setDataPosition(0)
}
