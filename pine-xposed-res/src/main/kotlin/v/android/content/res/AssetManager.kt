package v.android.content.res

import android.content.res.AssetManager
import android.content.res.Configuration
import android.util.DisplayMetrics
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import kotlin.Int
import kotlin.String
import v.ext.findConstructor
import v.ext.findMethod
import v.ext.loadClass

public val realClass: Class<*>? by lazy { loadClass("android.content.res.AssetManager") }

public val method_addAssetPath_resPath: Method by lazy {
  findMethod(realClass, "addAssetPath", String::class.java)!!
}

public val method_getConfiguration_: Method by lazy { findMethod(realClass, "getConfiguration")!! }

public val method_getDisplayMetrics_: Method by lazy {
  findMethod(realClass, "getDisplayMetrics")!!
}

public val constructor_newInstance_: Constructor<*> by lazy { findConstructor(realClass)!! }

public fun addAssetPath(caller: AssetManager, resPath: String): Int =
  method_addAssetPath_resPath.invoke(caller, resPath) as Int

public fun getConfiguration(caller: AssetManager): Configuration =
  method_getConfiguration_.invoke(
    caller,
  ) as Configuration

public fun getDisplayMetrics(caller: AssetManager): DisplayMetrics =
  method_getDisplayMetrics_.invoke(
    caller,
  ) as DisplayMetrics

public fun newInstance(): AssetManager = constructor_newInstance_.newInstance() as AssetManager
