package v.android.app

import android.app.Application
import java.lang.reflect.Method
import v.ext.findMethod
import v.ext.loadClass

val realClass: Class<*>? by lazy { loadClass("android.app.ActivityThread") }

val static_method_currentActivityThread_: Method by lazy {
  findMethod(realClass, "currentActivityThread")!!
}

val static_method_currentPackageName_: Method by lazy {
  findMethod(realClass, "currentPackageName")!!
}

val static_method_currentApplication_: Method by lazy {
  findMethod(realClass, "currentApplication")!!
}

fun currentApplication(): Application =
  static_method_currentApplication_.invoke(null) as Application

fun currentActivityThread(): Any = static_method_currentActivityThread_.invoke(null) as Any

fun currentPackageName(): String = static_method_currentPackageName_.invoke(null) as String
