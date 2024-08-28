package com.black.cat.system.demo.api

import com.black.cat.system.demo.BuildConfig
import com.black.cat.system.demo.api.models.RegisterUserRequest
import com.black.cat.system.demo.api.models.RootJson
import com.black.cat.system.demo.api.models.UnregisterUserRequest
import com.black.cat.system.demo.contants.UrlConstant
import com.black.cat.system.demo.data.User
import com.black.cat.system.demo.utils.EncryptUtil
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.UniversalAdapterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

object Api {
  const val BASE_URL = UrlConstant.HOST_URL // http://localhost:8080/

  private val logger =
    HttpLoggingInterceptor().apply {
      level =
        if (BuildConfig.DEBUG) {
          HttpLoggingInterceptor.Level.BODY
        } else {
          HttpLoggingInterceptor.Level.NONE
        }
    }
  private val okHttpClient: OkHttpClient =
    OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).addInterceptor(logger).build()

  private val moshi: Moshi =
    Moshi.Builder()
      .add(Boolean::class.javaObjectType, BOOLEAN_JSON_ADAPTER)
      .add(Boolean::class.java, BOOLEAN_JSON_ADAPTER)
      .add(UniversalAdapterFactory())
      .build()
  private val retrofit =
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .client(okHttpClient)
      .build()
  val httpService: ApiService = retrofit.create(ApiService::class.java)
  const val appID = "6775bb9beb8662ba799e31f57367f49a"
  const val appKey = "48886b5d7c3171b71670bdc24c704b4c"
  inline fun sha256(str: String, time: Long): String =
    EncryptUtil.getSHA256(appID + str + appKey + time)
}

interface ApiService {
  @POST("/user/register")
  suspend fun registerUser(
    @Body request: RegisterUserRequest,
    @Header("request_time") requestTime: Long,
    @Header("request_sign") requestSign: String
  ): RootJson<User>
  @POST("/user/unregister")
  suspend fun unregisterUser(
    @Body request: UnregisterUserRequest,
    @Header("request_time") requestTime: Long,
    @Header("request_sign") requestSign: String
  ): RootJson<Boolean>
}

val BOOLEAN_JSON_ADAPTER: JsonAdapter<Boolean> =
  object : JsonAdapter<Boolean>() {
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): Boolean {
      return when (reader.nextString()) {
        "true",
        "success" -> true
        else -> false
      }
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: Boolean?) {
      writer.value(value)
    }

    override fun toString(): String {
      return "JsonAdapter(Boolean)"
    }
  }
