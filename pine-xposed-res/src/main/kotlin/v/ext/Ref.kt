package v.ext

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

fun loadClass(className: String) = runCatching { Class.forName(className) }.getOrNull()

fun findField(clazz: Class<*>?, fieldName: String): Field? {
  return findFieldInner(clazz, fieldName)?.apply { isAccessible = true }
}

private fun findFieldInner(clazz: Class<*>?, fieldName: String): Field? {
  var clazzTmp = clazz
  while (clazzTmp != null) {
    val result = runCatching { clazzTmp!!.getDeclaredField(fieldName) }
    if (result.isSuccess) return result.getOrNull()
    clazzTmp = clazzTmp.superclass
  }
  return null
}

fun findMethod(clazz: Class<*>?, methodName: String, vararg classes: Class<*>): Method? {
  return findMethodInner(clazz, methodName, *classes)?.apply { isAccessible = true }
}

private fun findMethodInner(
  clazz: Class<*>?,
  methodName: String,
  vararg classes: Class<*>
): Method? {
  var clazzTmp = clazz
  while (clazzTmp != null) {
    val result = runCatching { clazzTmp!!.getDeclaredMethod(methodName, *classes) }
    if (result.isSuccess) return result.getOrNull()
    clazzTmp = clazzTmp.superclass
  }
  return null
}

fun findConstructor(clazz: Class<*>?, vararg classes: Class<*>): Constructor<*>? {
  return findConstructorInner(clazz, *classes)
}

private fun findConstructorInner(clazz: Class<*>?, vararg classes: Class<*>): Constructor<*>? {
  var clazzTmp = clazz
  while (clazzTmp != null) {
    val result = runCatching { clazzTmp!!.getDeclaredConstructor(*classes) }
    if (result.isSuccess) return result.getOrNull()
    clazzTmp = clazzTmp.superclass
  }
  return null
}
