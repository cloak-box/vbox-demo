package com.black.cat.system.demo.ui.act.vsys

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.black.cat.vsystem.api.VActivityManager

class AppLauncherActivity : VsysActivity() {
  override fun initAction(applicationInfo: ApplicationInfo) {
    viewModel.preloadApp(applicationInfo.packageName)
    viewModel.preloadAppObserver.observe(this) { intentTmp ->
      if (intentTmp != null) {
        VActivityManager.startActivity(intentTmp, 0)
        finish()
      }
    }
  }
  companion object {
    fun start(context: Context, applicationInfo: ApplicationInfo) {
      context.startActivity(
        Intent(context, AppLauncherActivity::class.java).apply {
          putExtra(KEY_LAUNCHER_APPLICATION_INFO, applicationInfo)
        }
      )
    }
  }
}
