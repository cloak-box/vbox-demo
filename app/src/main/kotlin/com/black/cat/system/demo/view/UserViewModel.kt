package com.black.cat.system.demo.view

import android.content.Context
import android.os.Build
import com.black.cat.system.demo.api.Api
import com.black.cat.system.demo.api.models.RegisterUserRequest
import com.black.cat.system.demo.api.models.UnregisterUserRequest
import com.black.cat.system.demo.api.models.generateSign
import com.black.cat.system.demo.data.User
import com.black.cat.system.demo.kbus.UpdateUserInfoEvent
import com.black.cat.system.demo.user.UserManager
import com.black.cat.system.demo.utils.OaidHelper
import com.black.cat.vsystem.api.Vlog
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus

class UserViewModel : BaseViewModel() {

  val registerUserObserver = StateLiveData<User?>()
  val unregisterUserObserver = StateLiveData<Boolean>()

  fun registerUser(context: Context) {
    launchOnUI {
      val oaid = OaidHelper.getOAIDWithRetry(context)
      if (oaid.isNullOrEmpty()) {
        registerUserObserver.postValue(null)
        return@launchOnUI
      }
      val response = httpCall {
        val request =
          RegisterUserRequest(oaid, Build.MODEL, Build.SUPPORTED_ABIS.joinToString(), Build.BRAND)
        val time = System.currentTimeMillis() / 1000
        Api.httpService.registerUser(request, time, Api.sha256(request.generateSign(), time))
      }
      if (response.isSuccess()) {
        UserManager.updateUserInfo(response.data)
      }
      registerUserObserver.postValue(response)
    }
  }

  fun unregisterUser(context: Context, userId: Long) {
    launchOnUI {
      val oaid = OaidHelper.getOAIDWithRetry(context)
      if (oaid.isNullOrEmpty()) {
        registerUserObserver.postValue(null)
        return@launchOnUI
      }
      val request = UnregisterUserRequest(oaid, userId)
      val time = System.currentTimeMillis() / 1000
      val response = httpCall {
        Api.httpService.unregisterUser(request, time, Api.sha256(request.generateSign(), time))
      }
      unregisterUserObserver.postValue(response)
    }
  }

  private suspend fun registerUserIfNeedInner(context: Context): User? {
    val oaid = OaidHelper.getOAIDWithRetry(context) ?: return null
    val request =
      RegisterUserRequest(oaid, Build.MODEL, Build.SUPPORTED_ABIS.joinToString(), Build.BRAND)
    val response = httpCall {
      val time = System.currentTimeMillis() / 1000
      Api.httpService.registerUser(request, time, Api.sha256(request.generateSign(), time))
    }
    Vlog.e("UserViewModel", "registerUser $response")
    return response.data
  }

  fun registerUserIfNeed(context: Context) {
    if (!UserManager.hasUser()) {
      launchOnUI {
        for (count in 0 until 3) {
          val user = registerUserIfNeedInner(context)
          if (user != null) {
            UserManager.updateUserInfo(user)
            EventBus.getDefault().post(UpdateUserInfoEvent(user))
            return@launchOnUI
          }
          delay(500)
        }
      }
    }
  }
}
