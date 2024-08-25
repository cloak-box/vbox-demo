package com.black.cat.system.demo.view

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import com.black.cat.system.demo.worker.Dex2OatWorker
import com.black.cat.vsystem.api.InstallFrags
import com.black.cat.vsystem.api.VActivityManager
import com.black.cat.vsystem.api.VPackageManager
import com.black.cat.vsystem.api.pm.InstallResult
import java.io.File
import kotlin.math.roundToInt

class AppLauncherViewModel : BaseViewModel() {
  val bgColor = MutableLiveData<Pair<Int, Int>>()
  val preloadAppObserver = MutableLiveData<Intent?>()
  val installResult = MutableLiveData<InstallResult>()
  private fun drawableToBitmap(drawable: Drawable): Bitmap {
    // 声明将要创建的bitmap
    // 获取图片宽度
    val width = drawable.intrinsicWidth
    // 获取图片高度
    val height = drawable.intrinsicHeight
    // 图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
    val config =
      if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
    // 创建一个空的Bitmap
    val bitmap = Bitmap.createBitmap(width, height, config)
    // 在bitmap上创建一个画布
    val canvas = Canvas(bitmap)
    // 设置画布的范围
    drawable.setBounds(0, 0, width, height)
    // 将drawable绘制在canvas上
    drawable.draw(canvas)
    return bitmap
  }

  fun generateViewColor(drawable: Drawable) {
    launchOnUI {
      val bitmap = drawableToBitmap(drawable)
      Palette.from(bitmap)
        .addFilter { rgb, _ ->
          return@addFilter rgb != Color.parseColor("#FFFFFF")
        }
        .maximumColorCount(24)
        .generate {
          if (it == null) return@generate
          val lightVibrantSwatch =
            it.lightVibrantSwatch
              ?: it.swatches.first { swatch -> swatch != null } ?: return@generate
          val blue = Color.blue(lightVibrantSwatch.rgb)
          val green = Color.green(lightVibrantSwatch.rgb)
          val red = Color.red(lightVibrantSwatch.rgb)
          val alpha = Color.alpha(lightVibrantSwatch.rgb)
          val color = Color.argb((alpha * 0.6f).roundToInt(), red, green, blue)
          bgColor.postValue(color to lightVibrantSwatch.titleTextColor)
        }
    }
  }

  fun preloadApp(packageName: String) {
    launchOnUI {
      val launchIntent = VPackageManager.getLaunchIntentForPackage(packageName, 0)
      if (launchIntent == null) {
        preloadAppObserver.postValue(null)
      } else {
        val result = VActivityManager.preloadAppByLaunchIntent(launchIntent, 0)
        if (result) {
          preloadAppObserver.postValue(launchIntent)
        } else {
          preloadAppObserver.postValue(null)
        }
      }
    }
  }

  fun installApp(context: Context, applicationInfo: ApplicationInfo, dex2oat: Boolean = true) {
    launchOnUI {
      val result =
        VPackageManager.installPackageAsUser(
          applicationInfo.sourceDir,
          applicationInfo.packageName,
          InstallFrags.INSTALL_FLAG_STORAGE,
          0
        )
          ?: InstallResult(applicationInfo.packageName, "install error unknown", false)
      if (result.success) {
        if (dex2oat) {
          val app = VPackageManager.getApplicationInfo(applicationInfo.packageName, 0, 0)
          if (app != null) {
            if (!TextUtils.isEmpty(app.sourceDir)) {
              val apk = File(app.sourceDir)
              val optimizedDir = File(apk.parent, "odex").absolutePath
              val dexFiles = mutableListOf(app.sourceDir)
              app.splitSourceDirs?.forEach { dexFiles.add(it) }
              Dex2OatWorker.startDexOpt(
                context,
                applicationInfo.packageName,
                Array(dexFiles.size) { dexFiles[it] },
                optimizedDir
              )
            }
          }
        }
      }
      installResult.postValue(result)
    }
  }
}
