package com.black.cat.system.demo.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.black.cat.system.demo.ui.base.owner.FragmentOwner

open class BaseFragment : Fragment() {
  protected lateinit var fragmentOwner: FragmentOwner

  override fun onAttach(context: Context) {
    if (context is FragmentOwner) {
      fragmentOwner = context
    }
    super.onAttach(context)
  }

  fun <T : ViewModel> createViewModel(clazz: Class<T>): T {
    return ViewModelProvider(
        this,
        object : ViewModelProvider.Factory {
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.newInstance() as T
          }
        }
      )
      .get(clazz)
  }
}
