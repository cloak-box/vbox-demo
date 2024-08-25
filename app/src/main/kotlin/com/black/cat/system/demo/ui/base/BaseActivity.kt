package com.black.cat.system.demo.ui.base

import android.app.Dialog
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.black.cat.system.demo.R
import com.black.cat.system.demo.databinding.CommonProgressDialogBinding
import com.black.cat.system.demo.ui.base.owner.FragmentOwner

open class BaseActivity : AppCompatActivity(), FragmentOwner {

  val progressDialog: Dialog by lazy {
    val binding = CommonProgressDialogBinding.inflate(layoutInflater)
    val p =
      AlertDialog.Builder(this, R.style.progress_dialog)
        .setView(binding.root)
        .setCancelable(false)
        .create()
    p.setCanceledOnTouchOutside(false)
    return@lazy p
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

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this)
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun hostActivity() = this

  override fun showLoading() {
    if (!progressDialog.isShowing) progressDialog.show()
  }
  override fun hideLoading() {
    if (progressDialog.isShowing) progressDialog.dismiss()
  }
}
