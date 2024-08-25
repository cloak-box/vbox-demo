package com.black.cat.system.demo.view

import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.black.cat.system.demo.utils.tmpApk
import com.black.cat.vsystem.api.VPackageManager
import java.io.InputStream

class AppsFragmentViewModel : BaseViewModel() {

  val installedPackage = MutableLiveData<List<ApplicationInfo>>()
  val unInstallPackage = MutableLiveData<ApplicationInfo>()
  val copyApkFromSdcard = MutableLiveData<ApplicationInfo?>()

  fun forceStopApp(applicationInfo: ApplicationInfo) {
    launchOnUI { VPackageManager.forceStopApp(applicationInfo.packageName, 0) }
  }

  fun clearApk(applicationInfo: ApplicationInfo) {
    launchOnUI { VPackageManager.clearApk(applicationInfo.packageName, 0) }
  }
  fun unInstallApk(applicationInfo: ApplicationInfo) {
    launchOnUI {
      VPackageManager.unInstallApk(applicationInfo.packageName, 0)
      unInstallPackage.postValue(applicationInfo)
    }
  }

  fun getInstalledPackage(userID: Int) {
    launchOnUI {
      VPackageManager.getInstalledApplications(0, userID)?.let { applicationInfos ->
        installedPackage.postValue(
          applicationInfos.filter {
            if (it.metaData == null) return@filter true
            return@filter !it.metaData.getBoolean("xposedmodule")
          }
        )
      }
    }
  }

  fun copyApkFromSdcard(context: Context, uri: Uri) {
    launchOnUI {
      var inputStream: InputStream? = null
      try {
        inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream == null) {
          copyApkFromSdcard.postValue(null)
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
      val packageInfo = context.packageManager.getPackageArchiveInfo(tmpApk.absolutePath, 0)
      packageInfo?.applicationInfo?.sourceDir = tmpApk.absolutePath
      packageInfo?.applicationInfo?.publicSourceDir = tmpApk.absolutePath

      copyApkFromSdcard.postValue(packageInfo?.applicationInfo)
    }
  }
}
