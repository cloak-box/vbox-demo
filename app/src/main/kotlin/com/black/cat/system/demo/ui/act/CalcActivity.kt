package com.black.cat.system.demo.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.black.cat.system.demo.R
import com.black.cat.system.demo.config.AppConfig
import com.darkempire78.opencalculator.activities.MainActivity
import com.xuexiang.xui.utils.SnackbarUtils

private const val TAG = "CalcActivity"

class CalcActivity : MainActivity() {
  private var checkPassword = false
  private var gotoMain = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    gotoMain = intent.getBooleanExtra(TAG_ACTION_GO_TO_MAIN_ACT, false)
    checkPassword =
      intent.getBooleanExtra(TAG_ACTION_CHECK_PASSWORD, false) &&
        AppConfig.getAppPassword().isNotEmpty()
    if (checkPassword && AppConfig.needShowWarningPasswordTip()) {
      val view = findViewById<View>(com.darkempire78.opencalculator.R.id.input)
      SnackbarUtils.Indefinite(
          view,
          getString(R.string.warning_password_tip, AppConfig.getAppPassword())
        )
        .gravityFrameLayout(Gravity.TOP)
        .setAction(getString(R.string.calc_close_warning_password_tip)) {
          AppConfig.saveShowWarningPasswordTip(false)
        }
        .show()
    }
  }
  override fun afterInputChanged(str: String) {
    super.afterInputChanged(str)
    if (str.length == 5 && checkPassword && AppConfig.getAppPassword() == str.replace(",", "")) {
      if (gotoMain) {
        startActivity(Intent(this, com.black.cat.system.demo.ui.act.MainActivity::class.java))
      }
      finish()
    }
  }

  companion object {
    private const val TAG_ACTION_CHECK_PASSWORD = "action_check_password"
    private const val TAG_ACTION_GO_TO_MAIN_ACT = "action_go_to_main_act"
    fun start(
      context: Context,
      checkPassword: Boolean = false,
      gotoMain: Boolean = false,
    ) {
      val intent = Intent(context, CalcActivity::class.java)
      intent.putExtra(TAG_ACTION_CHECK_PASSWORD, checkPassword)
      intent.putExtra(TAG_ACTION_GO_TO_MAIN_ACT, gotoMain)
      context.startActivity(intent)
    }
  }
}
