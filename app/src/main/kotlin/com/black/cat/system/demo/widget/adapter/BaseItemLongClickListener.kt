package com.black.cat.system.demo.widget.adapter

import androidx.viewbinding.ViewBinding

interface BaseItemLongClickListener<T> {
  fun onLongClick(binding: ViewBinding, positionData: T, position: Int)
}
