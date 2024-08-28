package com.black.cat.system.demo.utils

import android.app.Application
import android.content.Context
import com.black.cat.system.demo.config.AppConfig
import com.black.cat.system.demo.utils.Md5Utils.md5
import com.black.cat.vsystem.api.Vlog
import com.github.gzuliyujiang.oaid.DeviceID
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.github.gzuliyujiang.oaid.IGetter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay

object OaidHelper {

  fun register(app: Application) {
    DeviceIdentifier.register(app)
  }

  suspend fun getOAIDWithRetry(context: Context): String? {
    val localOaid = AppConfig.getOaidFromLocal()
    if (!localOaid.isNullOrEmpty()) return localOaid
    var oaid: String? = ""
    for (index in 0 until 3) {
      oaid = getOAID(context)
      if (!oaid.isNullOrEmpty()) {
        return oaid.md5().apply { AppConfig.saveOaidToLocal(this) }
      }
      delay(500)
    }
    return null
  }

  private suspend fun getOAID(context: Context): String? = suspendCoroutine { continuation ->
    DeviceID.getOAID(
      context,
      object : IGetter {
        override fun onOAIDGetComplete(result: String?) {
          continuation.resume(result)
        }

        override fun onOAIDGetError(error: Exception) {
          Vlog.d("OaidHelper", "onOAIDGetError $error")
          continuation.resume(null)
        }
      }
    )
  }
}
