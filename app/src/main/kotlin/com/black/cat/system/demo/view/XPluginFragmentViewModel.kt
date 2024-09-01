package com.black.cat.system.demo.view

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.black.cat.system.demo.bean.AppInfo
import com.black.cat.system.demo.bean.TYPE_APP
import com.black.cat.system.demo.hook.PluginManager
import com.black.cat.system.demo.utils.tmpApk
import com.black.cat.vsystem.api.VPackageManager
import java.io.InputStream

class XPluginFragmentViewModel : BaseViewModel() {

  val copyXposedPluginObserver = MutableLiveData<ApplicationInfo?>()
  val installedPluginObserver = MutableLiveData<List<AppInfo>>()
  val unInstallPluginObserver = MutableLiveData<ApplicationInfo>()

  fun copyXposedPlugin(context: Context, uri: Uri) {
    launchOnUI {
      var inputStream: InputStream? = null
      try {
        inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream == null) {
          copyXposedPluginObserver.postValue(null)
          return@launchOnUI
        }
        val tmpApkFile = tmpApk
        if (tmpApkFile.exists()) {
          tmpApkFile.delete()
        }
        tmpApkFile.writeBytes(inputStream.readBytes())
      } catch (th: Throwable) {
        inputStream?.close()
      }
      val packageInfo =
        context.packageManager.getPackageArchiveInfo(
          tmpApk.absolutePath,
          PackageManager.GET_META_DATA
        )
      packageInfo?.applicationInfo?.sourceDir = tmpApk.absolutePath
      packageInfo?.applicationInfo?.publicSourceDir = tmpApk.absolutePath
      copyXposedPluginObserver.postValue(packageInfo?.applicationInfo)
    }
  }

  fun getInstalledPlugin(userID: Int) {
    launchOnUI {
      installedPluginObserver.postValue(PluginManager.plugins.map { it.value })
      VPackageManager.getInstalledApplications(0, userID)?.let { applicationInfos ->
        val result =
          applicationInfos
            .filter {
              if (it.metaData == null) return@filter false
              return@filter it.metaData.getBoolean("xposedmodule")
            }
            .map { applicationInfo ->
              val enable = PluginManager.plugins[applicationInfo.packageName]?.isDefault ?: TYPE_APP
              AppInfo(enable, applicationInfo)
            }
        PluginManager.savePlugins(result)
        installedPluginObserver.postValue(result)
      }
    }
  }

  fun savePlugins(infos: List<AppInfo>) {
    launchOnUI { PluginManager.savePlugins(infos) }
  }

  fun unInstallPlugin(applicationInfo: ApplicationInfo) {
    launchOnUI {
      VPackageManager.unInstallApk(applicationInfo.packageName, 0)
      unInstallPluginObserver.postValue(applicationInfo)
    }
  }
}
