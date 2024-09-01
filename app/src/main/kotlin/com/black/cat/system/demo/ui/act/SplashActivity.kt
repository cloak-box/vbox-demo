package com.black.cat.system.demo.ui.act

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.black.cat.system.demo.config.AppConfig
import com.black.cat.system.demo.databinding.ActivitySplashBinding
import com.black.cat.system.demo.hook.PluginManager
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.utils.ViewUtil
import com.xuexiang.xui.widget.progress.CircleProgressView

class SplashActivity : BaseActivity() {
  private val viewBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ViewUtil.hideSystemUI(this)
    setContentView(viewBinding.root)
    viewBinding.progressViewCircleMain.setGraduatedEnabled(true)
    viewBinding.progressViewCircleMain.startProgressAnimation()
    viewBinding.progressViewCircleMain.setProgressViewUpdateListener(
      object : CircleProgressView.CircleProgressUpdateListener {
        override fun onCircleProgressStart(view: View?) {}

        override fun onCircleProgressUpdate(view: View?, progress: Float) {
          viewBinding.progressTextMain.text = progress.toInt().toString()
        }

        override fun onCircleProgressFinished(view: View?) {
          showNextActivity()
        }
      }
    )
    Thread { PluginManager.plugins }.start()
  }

  private fun showNextActivity() {
    if (!AppConfig.hasLogin()) {
      val options =
        ActivityOptionsCompat.makeSceneTransitionAnimation(
          this,
          viewBinding.imgIconLauncher,
          "icon_launcher"
        )
      startActivity(Intent(this@SplashActivity, LoginActivity::class.java), options.toBundle())
    } else if (AppConfig.getAppPassword().isNotEmpty()) {
      CalcActivity.start(this, checkPassword = true, gotoMain = true)
    } else {
      startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }
    finish()
  }
}
