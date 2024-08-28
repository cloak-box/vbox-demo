package com.black.cat.system.demo.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
  @Json(name = "code") val code: Int,
  @Json(name = "message") val message: String?,
)

@JsonClass(generateAdapter = true)
data class RootJson<out T>(@Json(name = "meta") val meta: Meta?, @Json(name = "data") val data: T?)

data class BaseResponse<out T>(
  var error: Throwable? = null,
  var meta: Meta? = null,
  var data: @UnsafeVariance T? = null
) {
  fun isSuccess(): Boolean = error == null && data != null
}
