package com.black.cat.system.demo.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.black.cat.system.demo.api.models.BaseResponse
import com.black.cat.system.demo.api.models.RootJson
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel() {
  fun launchOnUI(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        try {
          block()
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      }
    }
  }

  inline fun <T> httpCall(block: () -> RootJson<T>): BaseResponse<T> {
    val baseResponse = BaseResponse<T>()
    try {
      block().apply {
        baseResponse.data = this.data
        baseResponse.meta = this.meta
      }
    } catch (th: Throwable) {
      baseResponse.error = th
    }
    return baseResponse
  }

  override fun onCleared() {
    super.onCleared()
    viewModelScope.cancel()
  }
}
