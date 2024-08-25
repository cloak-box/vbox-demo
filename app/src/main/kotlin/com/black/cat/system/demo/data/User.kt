package com.black.cat.system.demo.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class User(
  @Json(name = "user_id") var userId: Long = -1,
  @Json(name = "create_time") var createTime: Long = -1,
  @Json(name = "nick_name") var nickName: String = ""
) : Serializable
