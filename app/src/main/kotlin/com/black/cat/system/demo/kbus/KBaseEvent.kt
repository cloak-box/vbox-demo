package com.black.cat.system.demo.kbus

interface KBaseEvent {
  fun eventName(): String
  fun eventValue(): Any
}
