/*
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.hackair.harvester;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.io.task.ScheduledJob;
import org.n52.sos.exception.CodedException;
import org.n52.sos.hackair.ds.HackAIRConfiguration;
import org.n52.sos.hackair.ds.HackAIRConnector;
import org.n52.sos.hackair.util.HackAIRHelper;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class HackAIRDataSourceHarvestJobFactory implements HackAIRHelper {

    private static final Logger log = LoggerFactory.getLogger(HackAIRDataSourceHarvestJobFactory.class);

    private String cronExpression;
    private HackAIRDataSourceHarvesterScheduler scheduler;
    private Set<String> jobs = new HashSet<>();
    private List<ScheduledJob> scheduledJobs = new ArrayList<>();
    private HackAIRConfiguration config;
    private HackAIRConnector connector;

    @Inject
    public void setDataSourceHarvesterScheduler(HackAIRDataSourceHarvesterScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setScheduledJobs(Optional<List<ScheduledJob>> scheduledJobs) {
        this.scheduledJobs.clear();
        if (scheduledJobs.isPresent()) {
            this.scheduledJobs.addAll(scheduledJobs.get());
        }
    }

    public List<ScheduledJob> getScheduledJobs() {
        return scheduledJobs;
    }

    /**
     * @return the updateDefinition
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * @param updateDefinition
     *            the updateDefinition to set
     */
//    @Setting(ScheduledContentCacheControllerSettings.CAPABILITIES_CACHE_UPDATE)
//    public void setCronExpression(String cronExpression) {
//        Validation.notNullOrEmpty("Cron expression for cache update", cronExpression);
//        if (this.cronExpression == null) {
//            this.cronExpression = cronExpression;
//            if (scheduledJobs.isEmpty()) {
//                scheduledJobs.add(createJob());
//            }
//            reschedule();
//        } else if (!this.cronExpression.equalsIgnoreCase(cronExpression)) {
//            this.cronExpression = cronExpression;
//            reschedule();
//        }
//    }
    
    public void startOrUpdate() {
        try {
            config = readConfig("/hackair.json");
        } catch (CodedException e) {
            // TODO Auto-generated catch block
            config = new HackAIRConfiguration();
        }
        String updatedCronExpression = config.getCronExpression();
        Validation.notNullOrEmpty("Cron expression for cache update", updatedCronExpression);
        if (this.cronExpression == null) {
            this.cronExpression = updatedCronExpression;
            if (scheduledJobs.isEmpty()) {
                scheduledJobs.add(createJob());
            }
            reschedule();
        } else if (!this.cronExpression.equalsIgnoreCase(updatedCronExpression)) {
            this.cronExpression = updatedCronExpression;
            reschedule();
        }
    }

    public void stop() {
        this.cronExpression = null;
        for (ScheduledJob job : getScheduledJobs()) {
            try {
                scheduler.removeJob(job);
            } catch (SchedulerException e) {
               log.error("Error while stopping jobs!", e);
            }
        }
        
    }

    private ScheduledJob createJob() {
        HackAIRDataSourceHarvesterJob job = new HackAIRDataSourceHarvesterJob(connector, "/hackair.json");
        job.setEnabled(true);
        job.setCronExpression(getCronExpression());
        job.setTriggerAtStartup(true);
        return job;
    }

    private void reschedule() {
        for (ScheduledJob job : getScheduledJobs()) {
            if (jobs.contains(job.getJobName())) {
                try {
                    job.setCronExpression(getCronExpression());
                    scheduler.updateJob(job);
                } catch (SchedulerException e) {
                    log.error("Error while rescheduling jobs!", e);
                }
            } else {
                scheduler.scheduleJob(job);
            }
            jobs.add(job.getJobName());
        }
    }
    
}
