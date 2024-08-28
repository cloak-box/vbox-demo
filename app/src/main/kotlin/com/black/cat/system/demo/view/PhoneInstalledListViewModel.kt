package com.black.cat.system.demo.view

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.lifecycle.MutableLiveData
import com.black.cat.system.demo.App
import com.black.cat.system.demo.bean.SystemInstallAppInfo
import com.black.cat.system.demo.utils.SortComparator

class PhoneInstalledListViewModel : BaseViewModel() {

  val installedApps = MutableLiveData<List<SystemInstallAppInfo>>()
  val installedAppsFromCache = MutableLiveData<List<SystemInstallAppInfo>>()

  fun getPhoneInstalledApps(context: Context) {
    // 先读缓存
    launchOnUI {
      installedAppsFromCache.postValue(SystemInstallAppInfo.readSystemInstallAppInfoList())
    }
    launchOnUI {
      val applications =
        context.packageManager
          .getInstalledPackages(0)
          .filter {
            it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
              it.packageName != App.instance.packageName
          }
          .map { SystemInstallAppInfo(it.applicationInfo) }
          .sortedWith(SortComparator())
      SystemInstallAppInfo.saveSystemInstallAppInfoList(applications)
      installedApps.postValue(applications)
    }
  }
}
