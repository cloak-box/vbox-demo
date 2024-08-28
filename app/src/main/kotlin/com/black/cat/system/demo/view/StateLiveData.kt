package com.black.cat.system.demo.view

import androidx.lifecycle.MutableLiveData
import com.black.cat.system.demo.api.models.BaseResponse

class StateLiveData<T> : MutableLiveData<BaseResponse<T>>()
