package com.luopc.platform.web.job.scheduler.type.strategy;

import com.luopc.platform.web.job.model.XxlJobInfo;
import com.luopc.platform.web.job.scheduler.type.ScheduleType;

import java.util.Date;

public class NoneScheduleType extends ScheduleType {

    @Override
    public Date generateNextTriggerTime(XxlJobInfo jobInfo, Date fromTime) throws Exception {
        // generate none trigger-time
        return null;
    }

}
