package com.black.cat.system.demo.hook

import com.black.cat.system.demo.BuildConfig
import com.black.cat.vsystem.api.Vlog
import top.canyie.pine.PineConfig

private const val TAG = "PinedHook"

fun hook(classLoader: ClassLoader) {
  if (!BuildConfig.enable_pine_hook) return
  PineConfig.debug = BuildConfig.DEBUG
  PineConfig.debuggable = BuildConfig.DEBUG
  try {
    doHook(classLoader)
  } catch (th: Throwable) {
    th.printStackTrace()
    Vlog.e(TAG, th, "hook error")
  }
}

private fun doHook(classLoader: ClassLoader) {}
