package com.black.cat.system.demo.bean

import android.content.pm.ApplicationInfo
import android.os.Parcel
import android.os.Parcelable
import com.black.cat.system.demo.App
import com.black.cat.system.demo.utils.HanziToPinyin
import com.black.cat.system.demo.utils.readParcel
import com.black.cat.system.demo.utils.systemInstallAppInfoListFile

class SystemInstallAppInfo(
  val applicationInfo: ApplicationInfo,
  val appName: String = applicationInfo.loadLabel(App.instance.packageManager).toString(),
  val firstLetter: String = HanziToPinyin.getSortLetter(appName).first
) : Parcelable {

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeParcelable(applicationInfo, flags)
    parcel.writeString(appName)
    parcel.writeString(firstLetter)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<SystemInstallAppInfo> {
    override fun createFromParcel(parcel: Parcel): SystemInstallAppInfo {
      val applicationInfo: ApplicationInfo =
        parcel.readParcelable(ApplicationInfo::class.java.classLoader)!!
      val appName = parcel.readString()!!
      val firstLetter = parcel.readString()!!
      return SystemInstallAppInfo(applicationInfo, appName, firstLetter)
    }

    override fun newArray(size: Int): Array<SystemInstallAppInfo?> {
      return arrayOfNulls(size)
    }

    fun saveSystemInstallAppInfoList(infos: List<SystemInstallAppInfo>) {
      if (infos.isEmpty()) return
      val parcel = Parcel.obtain()
      kotlin.runCatching {
        parcel.writeInt(infos.size)
        infos.forEach { it.writeToParcel(parcel, 0) }
        val bytes = parcel.marshall()
        val file = systemInstallAppInfoListFile
        file.writeBytes(bytes)
      }
      parcel.recycle()
    }

    fun readSystemInstallAppInfoList(): List<SystemInstallAppInfo> {
      val parcel = Parcel.obtain()
      val result = mutableListOf<SystemInstallAppInfo>()
      runCatching {
        val file = systemInstallAppInfoListFile
        file.readParcel(parcel)
        val size = parcel.readInt()
        for (index in 0 until size) {
          result.add(createFromParcel(parcel))
        }
      }
      parcel.recycle()
      return result
    }
  }
}
