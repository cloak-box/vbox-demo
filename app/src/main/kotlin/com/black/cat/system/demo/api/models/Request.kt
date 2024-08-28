package com.black.cat.system.demo.api.models

import com.black.cat.annotation.ObjectSignField
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterUserRequest(
  @ObjectSignField @Json(name = "device_id") val deviceId: String,
  @ObjectSignField @Json(name = "phone_model") val phoneModel: String,
  @ObjectSignField @Json(name = "phone_abi") val phoneAbi: String,
  @ObjectSignField @Json(name = "phone_brand") val phoneBrand: String,
)

@JsonClass(generateAdapter = true)
data class UnregisterUserRequest(
  @ObjectSignField @Json(name = "device_id") val deviceId: String,
  @ObjectSignField @Json(name = "user_id") val userID: Long,
)
