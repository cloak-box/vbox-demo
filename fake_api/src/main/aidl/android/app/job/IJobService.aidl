package android.app.job;

import android.app.job.JobParameters;

oneway interface IJobService {
    void startJob(in JobParameters jobParams);
    void stopJob(in JobParameters jobParams);
    void getTransferredDownloadBytes( in JobParameters jobParameters,in JobWorkItem jobWorkItem);
    void getTransferredUploadBytes(in JobParameters jobParameters,in JobWorkItem jobWorkItem);
    void onNetworkChanged( in JobParameters jobParameters) ;
}
