package com.black.cat.system.demo.widget.adapter

import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T> : Adapter<BaseHolder<T, ViewBinding>>() {
  private val data = mutableListOf<T>()
  var itemClickListener: BaseItemClickListener<T>? = null
  var itemLongClickListener: BaseItemLongClickListener<T>? = null

  override fun onBindViewHolder(holder: BaseHolder<T, ViewBinding>, position: Int) {
    holder.bindView(data[position], position)
    itemClickListener?.let { tmpItemClickListener ->
      holder.itemView.setOnClickListener {
        tmpItemClickListener.onClick(holder.binding, data[position], position)
      }
    }
    itemLongClickListener?.let { tmpItemLongClickListener ->
      holder.itemView.setOnLongClickListener {
        tmpItemLongClickListener.onLongClick(holder.binding, data[position], position)
        true
      }
    }
  }
  override fun getItemCount(): Int {
    return data.size
  }

  open fun add(item: T) {
    data.add(item)
  }

  open fun addAll(items: List<T>) {
    data.addAll(items)
  }

  open fun clear() {
    data.clear()
  }

  open fun remove(item: T) {
    data.remove(item)
  }

  open fun removeAll(items: List<T>) {
    data.removeAll(items)
  }

  open fun getData(): List<T> {
    return data
  }
}
