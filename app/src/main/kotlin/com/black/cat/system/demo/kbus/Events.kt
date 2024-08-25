package com.black.cat.system.demo.kbus

import com.black.cat.system.demo.data.User

const val EVENT_UPDATE_USER_INFO = "update_user_info_event"

class UpdateUserInfoEvent(
  private val eventValue: User,
) : KBaseEvent {
  override fun eventName() = EVENT_UPDATE_USER_INFO
  override fun eventValue() = eventValue
}
