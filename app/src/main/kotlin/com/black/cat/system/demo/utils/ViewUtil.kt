package com.black.cat.system.demo.utils

import android.app.Activity
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager

object ViewUtil {

  inline fun View.singleClickListener(crossinline onClickBlock: (v: View) -> Unit) {
    this.setOnClickListener(
      object : OnClickListener {
        var lastClick: Long = 0
        override fun onClick(v: View) {
          if (SystemClock.uptimeMillis() - lastClick > 500) {
            lastClick = SystemClock.uptimeMillis()
            onClickBlock(v)
          }
        }
      }
    )
  }

  fun hideSystemUI(activity: Activity) {
    var systemUiVisibility =
      View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    systemUiVisibility =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        systemUiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
      } else {
        systemUiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
      }
    val window = activity.window
    window.decorView.systemUiVisibility = systemUiVisibility
    // 五要素隐私详情页或五要素弹窗关闭回到开屏广告时，再次设置SystemUi
    window.decorView.setOnSystemUiVisibilityChangeListener { hideSystemUI(activity) }

    // Android P 官方方法
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      val params = window.attributes
      params.layoutInDisplayCutoutMode =
        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
      window.attributes = params
    }
  }
}
