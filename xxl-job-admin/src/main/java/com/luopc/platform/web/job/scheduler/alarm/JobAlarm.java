package com.luopc.platform.web.job.scheduler.alarm;

import com.luopc.platform.web.job.model.XxlJobInfo;
import com.luopc.platform.web.job.model.XxlJobLog;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog);

}
