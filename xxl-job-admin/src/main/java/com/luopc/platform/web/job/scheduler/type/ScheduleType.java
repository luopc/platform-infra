package com.luopc.platform.web.job.scheduler.type;

import com.luopc.platform.web.job.model.XxlJobInfo;

import java.util.Date;

/**
 * Schedule Type
 *
 * @author xuxueli 2020-10-29
 */
public abstract class ScheduleType {

    /**
     * generate next trigger time
     *
     * @param jobInfo       job info
     * @param fromTime      from time
     */
    public abstract Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception;

}
