package com.black.cat.system.demo.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import androidx.work.multiprocess.RemoteWorkerService
import com.black.cat.vsystem.dexopt.HostDexOptimizer
import com.tencent.mmkv.MMKV
import java.io.File

class Dex2OatWorker(context: Context, params: WorkerParameters) :
  RemoteCoroutineWorker(context, params) {
  companion object {
    private val appConfigKV = MMKV.mmkvWithID("dex_oat_config.kv")!!
    fun startDexOpt(
      context: Context,
      taskTag: String,
      dexFilePaths: Array<String>,
      optimizedDirPath: String
    ) {
      val hasOpt = appConfigKV.getBoolean(taskTag, false)
      if (hasOpt) return
      WorkManager.getInstance(context).cancelAllWorkByTag(taskTag)
      val data =
        Data.Builder()
          .putString(ARGUMENT_PACKAGE_NAME, context.packageName)
          .putString(ARGUMENT_CLASS_NAME, RemoteWorkerService::class.java.name)
          .putString(PARAM_OPTIMIZED_DIR, optimizedDirPath)
          .putString(PARAM_DEX_TASK_TAG, taskTag)
          .putStringArray(PARAM_DEX_FILE_PATH, dexFilePaths)
          .build()
      val requestBuilder =
        OneTimeWorkRequestBuilder<Dex2OatWorker>().addTag(taskTag).setInputData(data)
      WorkManager.getInstance(context).enqueue(requestBuilder.build())
    }

    private const val PARAM_DEX_FILE_PATH = "param_dex_file_path"
    private const val PARAM_DEX_TASK_TAG = "param_dex_task_tag"
    private const val PARAM_OPTIMIZED_DIR = "param_optimized_dir"
  }

  override suspend fun doRemoteWork(): Result {
    val dexFilePaths = inputData.getStringArray(PARAM_DEX_FILE_PATH) ?: return Result.failure()
    val optimizedDirPath = inputData.getString(PARAM_OPTIMIZED_DIR) ?: return Result.failure()
    val taskTag = inputData.getString(PARAM_DEX_TASK_TAG) ?: return Result.failure()
    val hasOpt = appConfigKV.getBoolean(taskTag, false)
    if (!hasOpt) {
      val success =
        HostDexOptimizer.optimizeAll(
          applicationContext,
          dexFilePaths.map { File(it) },
          File(optimizedDirPath),
          true,
          null
        )
      appConfigKV.putBoolean(taskTag, success)
    }
    return Result.success()
  }
}
