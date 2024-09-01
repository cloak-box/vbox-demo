package com.black.cat.system.demo.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

class AppFrontBackManager private constructor() {
  private val mAppStatusListeners: MutableList<OnAppStatusListener> = ArrayList()

  @Volatile var isBackground = true

  fun init(context: Context) {
    if (context is Application) {
      val activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {
          private var activityStartCount = 0
          override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
          override fun onActivityStarted(activity: Activity) {
            activityStartCount++
            // 数值从0变到1说明是从后台切到前台
            if (activityStartCount == 1) {
              isBackground = false
              for (listener in mAppStatusListeners) {
                listener.onFront(activity)
              }
            }
          }

          override fun onActivityResumed(activity: Activity) {}
          override fun onActivityPaused(activity: Activity) {}
          override fun onActivityStopped(activity: Activity) {
            activityStartCount--
            // 数值从1到0说明是从前台切到后台
            if (activityStartCount == 0) {
              for (listener in mAppStatusListeners) {
                listener.onBack()
              }
              isBackground = true
            }
          }

          override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
          override fun onActivityDestroyed(activity: Activity) {}
        }
      context.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
  }

  companion object {
    @JvmStatic val instance: AppFrontBackManager by lazy { AppFrontBackManager() }
  }

  /** 注册状态监听，仅在Application中使用 */
  fun addListener(listener: OnAppStatusListener) {
    if (!mAppStatusListeners.contains(listener)) {
      mAppStatusListeners.add(listener)
    }
  }

  fun removeListener(listener: OnAppStatusListener) {
    mAppStatusListeners.remove(listener)
  }

  interface OnAppStatusListener {
    fun onFront(activity: Activity)
    fun onBack()
  }
}
