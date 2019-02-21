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

import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.n52.io.task.ScheduledJob;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidAcceptVersionsParameterException;
import org.n52.sos.exception.ows.concrete.InvalidServiceOrVersionException;
import org.n52.sos.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.VersionNotSupportedException;
import org.n52.sos.hackair.converter.InsertObservationConverter;
import org.n52.sos.hackair.converter.InsertSensorConverter;
import org.n52.sos.hackair.data.Data;
import org.n52.sos.hackair.data.PollutantQ;
import org.n52.sos.hackair.data.Response;
import org.n52.sos.hackair.ds.HackAIRConfiguration;
import org.n52.sos.hackair.ds.HackAIRConnector;
import org.n52.sos.hackair.ds.SourceMetadata;
import org.n52.sos.hackair.util.HackAIRHelper;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.operator.ServiceOperator;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.sos.service.operator.ServiceOperatorRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HackAIRDataSourceHarvesterJob extends ScheduledJob implements Job, HackAIRHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(HackAIRDataSourceHarvesterJob.class);

    private HackAIRConnector connector;

    private String configFile = "/hackair.json";
    
    private InsertSensorConverter insertSensorConverter = new InsertSensorConverter();

    private InsertObservationConverter insertObservationConverter = new InsertObservationConverter();

    public HackAIRDataSourceHarvesterJob(HackAIRConnector connector, String configFile) {
        this.connector = connector;
        this.configFile = configFile;
    }
    
    public HackAIRDataSourceHarvesterJob() {
        // TODO Auto-generated constructor stub
    }
    
    @Inject
    public void setHackAIRConnector(HackAIRConnector connector) {
        this.connector = connector;
    }

    public void init() {
        
    }

    @Override
    public JobDetail createJobDetails() {
        return JobBuilder.newJob(HackAIRDataSourceHarvesterJob.class).withIdentity(getJobName()).build();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            HackAIRConfiguration config = readConfig(configFile);
            for (SourceMetadata source : config.getSources()) {
                try {
                    if (source.hasLastDateTime()) {
                        if (source.getLastDateTimeAsDateTime().isBefore(DateTime.now())) {
                            processData(source, source.getLastDateTimeAsDateTime());
                        } else {
                            LOGGER.warn("The last date time of source '" + source.getSource() + "' is in the future '"
                                    + source.getLastDateTime() + "'!");
                        }
                    } else {
                        if (config.getGlobalStartTimeAsDateTime().isBefore(DateTime.now())) {
                            processData(source, config.getGlobalStartTimeAsDateTime());
                        } else {
                            LOGGER.warn("The global date time is in the future '" + source.getLastDateTime() + "'!");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error while processing data for source '" + source.getSource() + "'!", e);
                }
                writeConfig(config);
            }
        } catch (CodedException e1) {
            LOGGER.error("Error while processing configuration file!", e1);
        }
        
    }
    
    private void processData(SourceMetadata source, DateTime startTime) throws OwsExceptionReport {
        processData(source, startTime, startTime.plus(source.getIntervalAsPeriod()));
    }

    private void processData(SourceMetadata source, DateTime startTime, DateTime endTime)
            throws OwsExceptionReport, CodedException {
        Response response = connector.getData(startTime, endTime, source.getSource());
        // process if response not null and count is > 600 or end time is before
        // now
        if ((response != null && response.getCount() > 0) || endTime.isBeforeNow()) {
            
            if (response.getCount() == 600 || (response.getCount() > 0
                    && containsOnlyNewerValues(response, startTime, source.getIntervalAsPeriod()))) {
                // reduce interval if the result count ix > 600 or if the resulting
                // data are only in the last hals of the requested interval and query again
                source.reduceInterval();
                processData(source, startTime);
            } else {
                if (response.getCount() == 0) {
                    // if the result contains no data, increase the interval and
                    // query starting from requested end time
                    source.increaseInterval();
                    source.setLastDateTime(endTime);
                    processData(source, endTime);
                } else {
                    // process data and query next values
                    processInsertSensor(source, response);
                    DateTime lastDateTime = processInsertObservation(response);
                    if (lastDateTime != null
                            && (!source.hasLastDateTime() || source.getLastDateTimeAsDateTime().isBefore(lastDateTime))) {
                        source.setLastDateTime(lastDateTime);
                    }
                    processData(source, endTime); 
                }
            }
        } else {
            LOGGER.debug("Requesting for source {} for time period {}/{} returned an empty response!",
                    source.getSource(), startTime, endTime);
        }
    }
    
    private boolean containsOnlyNewerValues(Response response, DateTime startTime, Period period) {
        return startTime.plus(period.toStandardDuration().dividedBy(2).toPeriod()).isBefore(getFirstTime(response));
    }

    private void processInsertSensor(SourceMetadata source, Response response) throws OwsExceptionReport {
        Set<PollutantQ> pollutants = getPollutants(response);
        if (!isProcedureRegistered(source.getSource())) {
            insertProcedureFromSource(source.getSource(), pollutants, null);
        }
        Set<String> subSources = getSubSources(response);
        if (subSources.isEmpty()) {
            for (Integer sensorId : getSubProcedures(response)) {
                LOGGER.debug("Processing sensor '{}' for source '{}'", sensorId, source.getSource());
                String subProcedure = prepareSubProcedure(source.getSource(), sensorId);
                if (!isProcedureRegistered(subProcedure)) {
                    insertProcedureFromSource(source.getSource(), pollutants, subProcedure);
                }
            }
        } else {
            for (String subSource : subSources) {
                LOGGER.debug("Processing sub-source '{}' for source '{}'", subSource, source.getSource());
                String subSourceProcedureIdentifier = prepareSubProcedure(source.getSource(), subSource);
                if (!isProcedureRegistered(subSourceProcedureIdentifier)) {
                    insertProcedureFromSource(source.getSource(), pollutants, subSourceProcedureIdentifier);
                }
                for (Integer sensorId : getSubProcedures(response)) {
                    LOGGER.debug("Processing sensor '{}' for sub-source '{}' for source '{}'", sensorId, subSource,
                            source.getSource());
                    String subProcedure = prepareSubProcedure(subSourceProcedureIdentifier, sensorId);
                    if (!isProcedureRegistered(subProcedure)) {
                        insertProcedureFromSource(source.getSource(), pollutants, subProcedure);
                    }
                }
            }
        }
    }

    private void insertProcedureFromSource(String source, Set<PollutantQ> pollutants, String subProcedure) throws OwsExceptionReport {
        InsertSensorRequest request = insertSensorConverter.convert(source, pollutants, subProcedure);
        getServiceOperator(request).receiveRequest(request);
    }

    private DateTime processInsertObservation(Response response){
        DateTime latest = null;
        if (response.hasData()) {
            latest = new DateTime().minusYears(100);
            for (Data data : response.getData()) {
                try {
                    InsertObservationRequest request = insertObservationConverter.convert(data);
                    getServiceOperator(request).receiveRequest(request);
                    DateTime current = getDateTime(data.getDateStr());
                    latest = latest.isBefore(current) ? current : latest;
                } catch (OwsExceptionReport e) {
                    LOGGER.error("Error while inserting observations!", e);
                }
            }
        }
        return latest;
    }

    private int calculateDurationSesonds(DateTime startTime, DateTime endTime) throws CodedException {
        if (startTime != null && endTime != null && startTime.isBefore(endTime)) {
            return Math.toIntExact(new Interval(startTime, endTime).toDurationMillis() / 2000);
        }
        throw new NoApplicableCodeException().withMessage(
                "StartTime '%S' or endTime '%' are invalid or startTime is not before endTime!", startTime, endTime);
    }

    private ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

    private boolean isProcedureRegistered(String procedure) {
        return getCache().getProcedures().contains(procedure);
    }

    private ServiceOperator getServiceOperator(ServiceOperatorKey sokt) throws OwsExceptionReport {
        return getServiceOperatorRepository().getServiceOperator(sokt);
    }

    private ServiceOperator getServiceOperator(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        checkServiceOperatorKeyTypes(request);
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            ServiceOperator so = getServiceOperator(sokt);
            if (so != null) {
                return so;
            }
        }
        throw new InvalidServiceOrVersionException(request.getService(), request.getVersion());
    }

    private void checkServiceOperatorKeyTypes(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        for (ServiceOperatorKey sokt : request.getServiceOperatorKeyType()) {
            if (sokt.hasService()) {
                if (sokt.getService().isEmpty()) {
                    exceptions.add(new MissingServiceParameterException());
                } else if (!getServiceOperatorRepository().isServiceSupported(sokt.getService())) {
                    exceptions.add(new InvalidServiceParameterException(sokt.getService()));
                }
            }
            if (request instanceof GetCapabilitiesRequest) {
                GetCapabilitiesRequest gcr = (GetCapabilitiesRequest) request;
                if (gcr.isSetAcceptVersions()) {
                    boolean hasSupportedVersion = false;
                    for (String acceptVersion : gcr.getAcceptVersions()) {
                        if (isVersionSupported(request.getService(), acceptVersion)) {
                            hasSupportedVersion = true;
                        }
                    }
                    if (!hasSupportedVersion) {
                        exceptions.add(new InvalidAcceptVersionsParameterException(gcr.getAcceptVersions()));
                    }
                }
            } else if (sokt.hasVersion()) {
                if (sokt.getVersion().isEmpty()) {
                    exceptions.add(new MissingVersionParameterException());
                } else if (!isVersionSupported(sokt.getService(), sokt.getVersion())) {
                    exceptions.add(new VersionNotSupportedException());
                }
            }
        }
        exceptions.throwIfNotEmpty();
    }

    private boolean isVersionSupported(String service, String acceptVersion) {
        return getServiceOperatorRepository().isVersionSupported(service, acceptVersion);
    }

    private ServiceOperatorRepository getServiceOperatorRepository() {
        return ServiceOperatorRepository.getInstance();
    }
    
}
