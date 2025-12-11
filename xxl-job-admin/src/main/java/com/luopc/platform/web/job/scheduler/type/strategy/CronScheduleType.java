package com.luopc.platform.web.job.scheduler.type.strategy;

import com.luopc.platform.web.job.model.XxlJobInfo;
import com.luopc.platform.web.job.scheduler.cron.CronExpression;
import com.luopc.platform.web.job.scheduler.type.ScheduleType;

import java.util.Date;

public class CronScheduleType extends ScheduleType {

    @Override
    public Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception {
        // generate next trigger time, with cron
        return new CronExpression(jobInfo.getScheduleConf()).getNextValidTimeAfter(fromTime);
    }

}
