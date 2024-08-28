package com.black.cat.system.demo.view

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.black.cat.system.demo.worker.Dex2OatWorker
import com.black.cat.vsystem.api.VPackageManager
import com.black.cat.vsystem.api.Vsystem
import java.io.File

class MeFragmentViewModel : BaseViewModel() {
  val installGmsPackageObserver = MutableLiveData<Boolean>()
  fun installGms(context: Context, dex2oat: Boolean = true) {
    launchOnUI {
      val result = Vsystem.installGms()

      if (dex2oat && result) {

        Vsystem.getGoogleApp().forEach { packageName ->
          val app = VPackageManager.getApplicationInfo(packageName, 0, 0)
          if (app != null) {
            if (!TextUtils.isEmpty(app.sourceDir)) {
              val apk = File(app.sourceDir)
              val optimizedDir = File(apk.parent, "odex").absolutePath
              val dexFiles = mutableListOf(app.sourceDir)
              app.splitSourceDirs?.forEach { dexFiles.add(it) }
              Dex2OatWorker.startDexOpt(
                context.applicationContext,
                packageName,
                Array(dexFiles.size) { dexFiles[it] },
                optimizedDir
              )
            }
          }
        }
      }

      installGmsPackageObserver.postValue(result)
    }
  }
}
