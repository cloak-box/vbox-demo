package com.black.cat.system.demo.ui.act.webview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import com.black.cat.system.demo.R
import com.xuexiang.xutil.tip.ToastUtils

class JsBridge(
  var act: Activity,
) {

  @JavascriptInterface
  fun jumpCustomerServicePage() {
    val key = "DRaKIsUTy7QVBIG66Ei5NAT2Mc0TM4J6"
    val intent = Intent()
    intent.data =
      Uri.parse(
        "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"
      )
    // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
    // //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
      act.startActivity(intent)
    } catch (e: Exception) {
      ToastUtils.toast(act.getString(R.string.me_not_support))
    }
  }
}
