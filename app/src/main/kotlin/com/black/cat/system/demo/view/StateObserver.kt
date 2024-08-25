package com.black.cat.system.demo.view

import androidx.lifecycle.Observer
import com.black.cat.system.demo.BuildConfig
import com.black.cat.system.demo.R
import com.black.cat.system.demo.api.models.BaseResponse
import com.xuexiang.xutil.tip.ToastUtils

abstract class StateObserver<T> : Observer<BaseResponse<T>> {
  override fun onChanged(t: BaseResponse<T>) {
    if (!t.isSuccess() && t.error != null) {
      if (BuildConfig.DEBUG) {
        ToastUtils.toast(t.error?.stackTraceToString())
        t.error?.printStackTrace()
      } else {
        ToastUtils.toast(R.string.net_work_err)
      }
    }
    changed(t)
  }

  abstract fun changed(t: BaseResponse<T>?)
}
