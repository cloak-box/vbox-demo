package android.app.job;

import android.app.job.JobWorkItem;

interface IJobCallback {
    void acknowledgeStartMessage(int jobId, boolean ongoing);
    void acknowledgeStopMessage(int jobId, boolean reschedule);
    JobWorkItem dequeueWork(int jobId);
    boolean completeWork(int jobId, int workId);
    void jobFinished(int jobId, boolean reschedule);
}
