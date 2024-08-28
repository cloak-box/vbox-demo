package com.black.cat.system.demo.hook

import android.os.Parcel
import com.black.cat.system.demo.bean.AppInfo
import com.black.cat.system.demo.utils.readParcel
import com.black.cat.system.demo.utils.xPluginsInfoListFile

object PluginManager {

  val plugins by lazy { loadPlugins() }

  fun savePlugins(infos: List<AppInfo>) {
    if (infos.isEmpty()) return
    val parcel = Parcel.obtain()
    runCatching {
      parcel.writeInt(infos.size)
      plugins.clear()
      infos.forEach {
        parcel.writeString(it.applicationInfo!!.packageName)
        it.writeToParcel(parcel, 0)
        plugins[it.applicationInfo.packageName] = it
      }
      val bytes = parcel.marshall()
      val file = xPluginsInfoListFile
      file.writeBytes(bytes)
    }
    parcel.recycle()
  }

  private fun loadPlugins(): MutableMap<String, AppInfo> {
    val parcel = Parcel.obtain()
    val result = mutableMapOf<String, AppInfo>()
    runCatching {
      val file = xPluginsInfoListFile
      file.readParcel(parcel)
      val size = parcel.readInt()
      for (index in 0 until size) {
        result[parcel.readString()!!] = AppInfo.createFromParcel(parcel)
      }
    }
    parcel.recycle()
    return result
  }
}
