package com.black.cat.system.demo.widget.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

abstract class BaseHolder<T, out VB : ViewBinding>(itemView: View) : ViewHolder(itemView) {
  val binding: VB
  init {
    binding = initBinding(itemView)
  }

  abstract fun initBinding(itemView: View): VB

  abstract fun bindView(positionData: T, position: Int)
}
