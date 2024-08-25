package com.black.cat.system.demo.bean

import android.content.pm.ApplicationInfo
import android.os.Parcel
import android.os.Parcelable

class AppInfo(var isDefault: Boolean = false, val applicationInfo: ApplicationInfo? = null) :
  Parcelable {
  constructor(
    parcel: Parcel
  ) : this(
    parcel.readByte() != 0.toByte(),
    parcel.readParcelable(ApplicationInfo::class.java.classLoader)
  )

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeByte(if (isDefault) 1 else 0)
    parcel.writeParcelable(applicationInfo, flags)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<AppInfo> {
    override fun createFromParcel(parcel: Parcel): AppInfo {
      return AppInfo(parcel)
    }

    override fun newArray(size: Int): Array<AppInfo?> {
      return arrayOfNulls(size)
    }
  }
}
