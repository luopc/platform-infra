package com.luopc.platform.web.job.scheduler.misfire.strategy;

import com.luopc.platform.web.job.scheduler.config.XxlJobAdminBootstrap;
import com.luopc.platform.web.job.scheduler.misfire.MisfireHandler;
import com.luopc.platform.web.job.scheduler.trigger.TriggerTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MisfireFireOnceNow extends MisfireHandler {
    protected static Logger logger = LoggerFactory.getLogger(MisfireFireOnceNow.class);

    @Override
    public void handle(int jobId) {
        // FIRE_ONCE_NOW 》 trigger
        XxlJobAdminBootstrap.getInstance().getJobTriggerPoolHelper().trigger(jobId, TriggerTypeEnum.MISFIRE, -1, null, null, null);
        logger.warn(">>>>>>>>>>> xxl-job, schedule MisfireFireOnceNow: jobId = " + jobId );
    }

}
