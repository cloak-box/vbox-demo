package com.black.cat.system.demo.bean

import android.content.pm.ApplicationInfo
import android.os.Parcel
import android.os.Parcelable

const val TYPE_DEFAULT = 0
const val TYPE_CALC = 1
const val TYPE_APP = 2

class AppInfo(var isDefault: Int = TYPE_APP, val applicationInfo: ApplicationInfo? = null) :
  Parcelable {
  constructor(
    parcel: Parcel
  ) : this(parcel.readInt(), parcel.readParcelable(ApplicationInfo::class.java.classLoader))

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(isDefault)
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
